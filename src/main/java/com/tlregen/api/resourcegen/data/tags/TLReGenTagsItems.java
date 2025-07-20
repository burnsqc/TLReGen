package com.tlregen.api.resourcegen.data.tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.tlregen.api.resourcegen.TLReGenResourceGenerator;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TLReGenTagsItems extends TLReGenResourceGenerator {
	private Map<TagKey<Item>, TagBuilder> resources = new HashMap<>();

	public TLReGenTagsItems(Map<TagKey<Item>, TagBuilder> resources, String modID, PackOutput packOutput) {
		super(modID, Types.TAGS_ITEMS, packOutput);
		this.resources = resources;
	}

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
		resources.forEach((key, value) -> {
			JsonObject json = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(value.build(), false)).getOrThrow(false, msg -> LOGGER.error("Failed to encode")).getAsJsonObject();
			list.add(DataProvider.saveStable(cache, json, pathProvider.json(key.location())));
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}
}
