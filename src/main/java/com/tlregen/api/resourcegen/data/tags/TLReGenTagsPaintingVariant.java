package com.tlregen.api.resourcegen.data.tags;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
import net.minecraft.world.entity.decoration.PaintingVariant;

public abstract class TLReGenTagsPaintingVariant extends TLReGenMasterResourceGenerator implements DataProvider {
	private final Map<ResourceLocation, TagBuilder> blockTags = new HashMap<>();

	/*
	 * PENDING CLEANUP
	 */
	private final CompletableFuture<Void> contentsDone = new CompletableFuture<>();
	private final CompletableFuture<TagsProvider.TagLookup<PaintingVariant>> parentProvider = CompletableFuture.completedFuture(TagsProvider.TagLookup.empty());
	protected final ResourceKey<? extends Registry<PaintingVariant>> registryKey = Registries.PAINTING_VARIANT;
	private final net.minecraftforge.common.data.ExistingFileHelper.IResourceType resourceType = new net.minecraftforge.common.data.ExistingFileHelper.ResourceType(net.minecraft.server.packs.PackType.SERVER_DATA, ".json", TagManager.getTagDir(Registries.PAINTING_VARIANT));
	private final net.minecraftforge.common.data.ExistingFileHelper.IResourceType elementResourceType = new net.minecraftforge.common.data.ExistingFileHelper.ResourceType(net.minecraft.server.packs.PackType.SERVER_DATA, ".json", net.minecraftforge.common.ForgeHooks.prefixNamespace(Registries.PAINTING_VARIANT.location()));

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
			HolderLookup.RegistryLookup<PaintingVariant> registrylookup = combinedData.contents.lookup(registryKey).get();
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
					return DataProvider.saveStable(cache, json, packOutput.createPathProvider(PackOutput.Target.DATA_PACK, TagManager.getTagDir(Registries.PAINTING_VARIANT)).json(resourcelocation));
				}
			}).toArray((p_253442_) -> {
				return new CompletableFuture[p_253442_];
			}));
		});
	}

	@Override
	public final String getName() {
		return "data." + modid + ".tags.painting_variant";
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
		return packOutput.createPathProvider(PackOutput.Target.DATA_PACK, TagManager.getTagDir(Registries.PAINTING_VARIANT)).json(id);
	}

	protected TLReGenTagsPaintingVariant.TagAppender<PaintingVariant> tag(TagKey<PaintingVariant> p_255730_) {
		TagBuilder tagbuilder = getOrCreateRawBuilder(p_255730_);
		return new TLReGenTagsPaintingVariant.TagAppender<PaintingVariant>(tagbuilder, modid);
	}

	protected TagBuilder getOrCreateRawBuilder(TagKey<PaintingVariant> p_236452_) {
		return this.blockTags.computeIfAbsent(p_236452_.location(), (p_236442_) -> {
			if (helper != null) {
				helper.trackGenerated(p_236442_, resourceType);
			}
			return TagBuilder.create();
		});
	}

	public CompletableFuture<TagsProvider.TagLookup<PaintingVariant>> contentsGetter() {
		return this.contentsDone.thenApply((p_276016_) -> {
			return (p_274772_) -> {
				return Optional.ofNullable(this.blockTags.get(p_274772_.location()));
			};
		});
	}

	public static class TagAppender<T> implements net.minecraftforge.common.extensions.IForgeTagAppender<T> {
		private final TagBuilder builder;
		private final String modId;

		protected TagAppender(TagBuilder p_236454_, String modId) {
			this.builder = p_236454_;
			this.modId = modId;
		}

		public final TagAppender<T> add(ResourceKey<T> p_256138_) {
			this.builder.addElement(p_256138_.location());
			return this;
		}

		@SafeVarargs
		public final TagAppender<T> add(ResourceKey<T>... p_211102_) {
			for (ResourceKey<T> resourcekey : p_211102_) {
				this.builder.addElement(resourcekey.location());
			}

			return this;
		}

		public TagAppender<T> addOptional(ResourceLocation p_176840_) {
			this.builder.addOptionalElement(p_176840_);
			return this;
		}

		public TagAppender<T> addTag(TagKey<T> p_206429_) {
			this.builder.addTag(p_206429_.location());
			return this;
		}

		public TagAppender<T> addOptionalTag(ResourceLocation p_176842_) {
			this.builder.addOptionalTag(p_176842_);
			return this;
		}

		public TagAppender<T> add(TagEntry tag) {
			builder.add(tag);
			return this;
		}

		public TagBuilder getInternalBuilder() {
			return builder;
		}

		public String getModID() {
			return modId;
		}
	}
}
