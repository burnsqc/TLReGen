package com.tlregen.api.resourcegen.assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenResourceGenerator;

import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class TLReGenFont extends TLReGenResourceGenerator {
	private Map<ResourceLocation, List<GlyphProviderDefinition>> resources = new HashMap<>();

	public TLReGenFont(Map<ResourceLocation, List<GlyphProviderDefinition>> resources, String modID, PackOutput packOutput) {
		super(modID, Types.FONT, packOutput);
		this.resources = resources;
	}

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
		resources.forEach((key, value) -> {
			JsonObject json = GlyphProviderDefinition.CODEC.listOf().fieldOf("providers").codec().encodeStart(dynamicOps, value).getOrThrow(false, msg -> LOGGER.error("Failed to encode")).getAsJsonObject();
			list.add(DataProvider.saveStable(cache, json, pathProvider.json(key)));
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}
}
