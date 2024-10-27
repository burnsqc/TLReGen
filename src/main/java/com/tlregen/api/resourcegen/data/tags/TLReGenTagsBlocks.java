package com.tlregen.api.resourcegen.data.tags;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.tlregen.api.resourcegen.MasterResourceGenerator;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.world.level.block.Block;

public abstract class TLReGenTagsBlocks extends MasterResourceGenerator implements DataProvider {
	private final Map<ResourceLocation, TagBuilder> blockTags = new HashMap<>();

	/*
	 * PENDING CLEANUP
	 */
	private final CompletableFuture<Void> contentsDone = new CompletableFuture<>();
	private final CompletableFuture<TagsProvider.TagLookup<Block>> parentProvider = CompletableFuture.completedFuture(TagsProvider.TagLookup.empty());
	private final ResourceKey<? extends Registry<Block>> registryKey = Registries.BLOCK;
	private final net.minecraftforge.common.data.ExistingFileHelper.IResourceType resourceType = new net.minecraftforge.common.data.ExistingFileHelper.ResourceType(net.minecraft.server.packs.PackType.SERVER_DATA, ".json", TagManager.getTagDir(Registries.BLOCK));
	private final net.minecraftforge.common.data.ExistingFileHelper.IResourceType elementResourceType = new net.minecraftforge.common.data.ExistingFileHelper.ResourceType(net.minecraft.server.packs.PackType.SERVER_DATA, ".json", net.minecraftforge.common.ForgeHooks.prefixNamespace(Registries.BLOCK.location()));
	@SuppressWarnings("deprecation")
	private final Function<Block, ResourceKey<Block>> keyExtractor = block -> block.builtInRegistryHolder().key();

	/**
	 * OVERRIDE ME TO ADD BLOCK TAGS
	 */
	protected abstract void populate(HolderLookup.Provider p_256380_);

	@Override
	public CompletableFuture<?> run(final CachedOutput cache) {
		CompletableFuture<?> completable = CompletableFuture.allOf();

		record CombinedData<T>(HolderLookup.Provider contents, TagsProvider.TagLookup<T> parent) {
		}

		return lookupProvider.thenApply((provider) -> {
			blockTags.clear();
			populate(provider);
			return provider;
		}).thenApply((provider) -> {
			contentsDone.complete((Void) null);
			return provider;
		}).thenCombineAsync(parentProvider, (provider, tagLookup) -> {
			return new CombinedData<>(provider, tagLookup);
		}).thenCompose((combinedData) -> {
			HolderLookup.RegistryLookup<Block> registrylookup = combinedData.contents.lookup(registryKey).get();
			Predicate<ResourceLocation> predicate = (resourceLocation) -> registrylookup.get(ResourceKey.create(registryKey, resourceLocation)).isPresent();
			Predicate<ResourceLocation> predicate1 = (resourceLocation) -> blockTags.containsKey(resourceLocation) || combinedData.parent.contains(TagKey.create(registryKey, resourceLocation));

			return CompletableFuture.allOf(blockTags.entrySet().stream().map((p_255499_) -> {
				ResourceLocation resourcelocation = p_255499_.getKey();
				TagBuilder tagbuilder = p_255499_.getValue();
				List<TagEntry> list = tagbuilder.build();
				List<TagEntry> list1 = Stream.concat(list.stream(), tagbuilder.getRemoveEntries()).filter((p_274771_) -> {
					return !p_274771_.verifyIfPresent(predicate, predicate1);
				}).filter(this::missing).toList();
				if (!list1.isEmpty()) {
					throw new IllegalArgumentException(String.format(Locale.ROOT, "Couldn't define tag %s as it is missing following references: %s", resourcelocation, list1.stream().map(Objects::toString).collect(Collectors.joining(","))));
				} else {
					var removed = tagbuilder.getRemoveEntries().toList();
					JsonObject json = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(list, tagbuilder.isReplace(), removed)).getOrThrow(false, LOGGER::error).getAsJsonObject();
					return DataProvider.saveStable(cache, json, packOutput.createPathProvider(PackOutput.Target.DATA_PACK, TagManager.getTagDir(Registries.BLOCK)).json(resourcelocation));
				}
			}).toArray((p_253442_) -> {
				return new CompletableFuture[p_253442_];
			}));
		});
	}

	@Override
	public final String getName() {
		return "data." + modID + ".tags.blocks";
	}

	/*
	 * PENDING CLEANUP
	 */

	private boolean missing(TagEntry reference) {
		if (reference.isRequired()) {
			return helper == null || !helper.exists(reference.getId(), reference.isTag() ? resourceType : elementResourceType);
		}
		return false;
	}

	protected Path getPath(ResourceLocation id) {
		return packOutput.createPathProvider(PackOutput.Target.DATA_PACK, TagManager.getTagDir(Registries.BLOCK)).json(id);
	}

	protected TLReGenTagsBlocks.IntrinsicTagAppender<Block> tag(TagKey<Block> p_255730_) {
		TagBuilder tagbuilder = this.getOrCreateRawBuilder(p_255730_);
		return new TLReGenTagsBlocks.IntrinsicTagAppender<Block>(tagbuilder, this.keyExtractor, modID);
	}

	protected TagBuilder getOrCreateRawBuilder(TagKey<Block> p_236452_) {
		return this.blockTags.computeIfAbsent(p_236452_.location(), (p_236442_) -> {
			if (helper != null) {
				helper.trackGenerated(p_236442_, resourceType);
			}
			return TagBuilder.create();
		});
	}

	public CompletableFuture<TagsProvider.TagLookup<Block>> contentsGetter() {
		return this.contentsDone.thenApply((p_276016_) -> {
			return (p_274772_) -> {
				return Optional.ofNullable(this.blockTags.get(p_274772_.location()));
			};
		});
	}

	public static class IntrinsicTagAppender<T> extends TagsProvider.TagAppender<T> implements net.minecraftforge.common.extensions.IForgeIntrinsicHolderTagAppender<T> {
		private final Function<T, ResourceKey<T>> keyExtractor;

		IntrinsicTagAppender(TagBuilder p_256108_, Function<T, ResourceKey<T>> p_256433_, String modId) {
			super(p_256108_, modId);
			this.keyExtractor = p_256433_;
		}

		@Override
		public TLReGenTagsBlocks.IntrinsicTagAppender<T> addTag(TagKey<T> p_256311_) {
			super.addTag(p_256311_);
			return this;
		}

		public final TLReGenTagsBlocks.IntrinsicTagAppender<T> add(T p_256557_) {
			this.add(this.keyExtractor.apply(p_256557_));
			return this;
		}

		@SafeVarargs
		public final TLReGenTagsBlocks.IntrinsicTagAppender<T> add(T... p_255868_) {
			Stream.<T>of(p_255868_).map(this.keyExtractor).forEach(this::add);
			return this;
		}

		@Override
		public final ResourceKey<T> getKey(T value) {
			return this.keyExtractor.apply(value);
		}
	}
}
