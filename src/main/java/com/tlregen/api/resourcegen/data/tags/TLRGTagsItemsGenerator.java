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
import com.tlregen.api.resourcegen.TLReGenMasterResourceGenerator;

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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public abstract class TLRGTagsItemsGenerator extends TLReGenMasterResourceGenerator implements DataProvider {
	private final CompletableFuture<TagsProvider.TagLookup<Block>> blockTags = TagBlocks.contentsGetter();
	private final Map<ResourceLocation, TagBuilder> itemTags = new HashMap<>();
	private final Map<TagKey<Block>, TagKey<Item>> tagsToCopy = new HashMap<>();

	/*
	 * PENDING CLEANUP
	 */
	private final CompletableFuture<Void> contentsDone = new CompletableFuture<>();
	private final CompletableFuture<TagsProvider.TagLookup<Item>> parentProvider = CompletableFuture.completedFuture(TagsProvider.TagLookup.empty());
	protected final ResourceKey<? extends Registry<Item>> registryKey = Registries.ITEM;
	private final net.minecraftforge.common.data.ExistingFileHelper.IResourceType resourceType = new net.minecraftforge.common.data.ExistingFileHelper.ResourceType(net.minecraft.server.packs.PackType.SERVER_DATA, ".json", TagManager.getTagDir(Registries.ITEM));
	private final net.minecraftforge.common.data.ExistingFileHelper.IResourceType elementResourceType = new net.minecraftforge.common.data.ExistingFileHelper.ResourceType(net.minecraft.server.packs.PackType.SERVER_DATA, ".json", net.minecraftforge.common.ForgeHooks.prefixNamespace(Registries.ITEM.location()));
	@SuppressWarnings("deprecation")
	private final Function<Item, ResourceKey<Item>> keyExtractor = block -> block.builtInRegistryHolder().key();

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
			itemTags.clear();
			populate(provider);
			return provider;
		}).thenApply((provider) -> {
			contentsDone.complete((Void) null);
			return provider;
		}).thenCombineAsync(parentProvider, (provider, tagLookup) -> {
			return new CombinedData<>(provider, tagLookup);
		}).thenCombineAsync(this.blockTags, (p_274766_, p_274767_) -> {
			this.tagsToCopy.forEach((p_274763_, p_274764_) -> {
				TagBuilder tagbuilder = this.getOrCreateRawBuilder(p_274764_);
				Optional<TagBuilder> optional = p_274767_.apply(p_274763_);
				optional.orElseThrow(() -> {
					return new IllegalStateException("Missing block tag " + p_274764_.location());
				}).build().forEach(tagbuilder::add);
			});
			return p_274766_;
		}).thenCompose((combinedData) -> {
			HolderLookup.RegistryLookup<Item> registrylookup = combinedData.contents.lookup(registryKey).get();
			Predicate<ResourceLocation> predicate = (resourceLocation) -> registrylookup.get(ResourceKey.create(registryKey, resourceLocation)).isPresent();
			Predicate<ResourceLocation> predicate1 = (resourceLocation) -> itemTags.containsKey(resourceLocation) || combinedData.parent.contains(TagKey.create(registryKey, resourceLocation));

			return CompletableFuture.allOf(itemTags.entrySet().stream().map((p_255499_) -> {
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
					return DataProvider.saveStable(cache, json, packOutput.createPathProvider(PackOutput.Target.DATA_PACK, TagManager.getTagDir(Registries.ITEM)).json(resourcelocation));
				}
			}).toArray((p_253442_) -> {
				return new CompletableFuture[p_253442_];
			}));
		});
	}

	@Override
	public final String getName() {
		return "data." + modid + ".tags.items";
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
		return packOutput.createPathProvider(PackOutput.Target.DATA_PACK, TagManager.getTagDir(Registries.ITEM)).json(id);
	}

	protected TLRGTagsItemsGenerator.IntrinsicTagAppender<Item> tag(TagKey<Item> p_255730_) {
		TagBuilder tagbuilder = getOrCreateRawBuilder(p_255730_);
		return new TLRGTagsItemsGenerator.IntrinsicTagAppender<Item>(tagbuilder, keyExtractor, modid);
	}

	protected TagBuilder getOrCreateRawBuilder(TagKey<Item> p_236452_) {
		return itemTags.computeIfAbsent(p_236452_.location(), (p_236442_) -> {
			if (helper != null) {
				helper.trackGenerated(p_236442_, resourceType);
			}
			return TagBuilder.create();
		});
	}

	public CompletableFuture<TagsProvider.TagLookup<Item>> contentsGetter() {
		return contentsDone.thenApply((p_276016_) -> {
			return (p_274772_) -> {
				return Optional.ofNullable(itemTags.get(p_274772_.location()));
			};
		});
	}

	public static class IntrinsicTagAppender<T> extends TagsProvider.TagAppender<T> implements net.minecraftforge.common.extensions.IForgeIntrinsicHolderTagAppender<T> {
		private final Function<T, ResourceKey<T>> keyExtractor;

		IntrinsicTagAppender(TagBuilder p_256108_, Function<T, ResourceKey<T>> p_256433_, String modId) {
			super(p_256108_, modId);
			keyExtractor = p_256433_;
		}

		@Override
		public TLRGTagsItemsGenerator.IntrinsicTagAppender<T> addTag(TagKey<T> p_256311_) {
			super.addTag(p_256311_);
			return this;
		}

		public final TLRGTagsItemsGenerator.IntrinsicTagAppender<T> add(T p_256557_) {
			add(keyExtractor.apply(p_256557_));
			return this;
		}

		@SafeVarargs
		public final TLRGTagsItemsGenerator.IntrinsicTagAppender<T> add(T... p_255868_) {
			Stream.<T>of(p_255868_).map(keyExtractor).forEach(this::add);
			return this;
		}

		@Override
		public final ResourceKey<T> getKey(T value) {
			return keyExtractor.apply(value);
		}
	}

	protected void copy(TagKey<Block> p_206422_, TagKey<Item> p_206423_) {
		tagsToCopy.put(p_206422_, p_206423_);
	}
}
