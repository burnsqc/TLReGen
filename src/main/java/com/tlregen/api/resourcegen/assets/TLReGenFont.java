package com.tlregen.api.resourcegen.assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenAssetProvider;

import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

public class TLReGenFont extends TLReGenAssetProvider {
	private Map<ResourceLocation, List<GlyphProviderDefinition>> resources = new HashMap<>();

	public TLReGenFont(Map<ResourceLocation, List<GlyphProviderDefinition>> resources) {
		this.resources = resources;
	}

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
		resources.forEach((key, value) -> {
			JsonObject json = GlyphProviderDefinition.CODEC.listOf().fieldOf("providers").codec().encodeStart(dynamicOps, value).getOrThrow(false, msg -> LOGGER.error("Failed to encode")).getAsJsonObject();
			list.add(DataProvider.saveStable(cache, json, packOutput.createPathProvider(target, "font").json(key)));
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

	@Override
	public final String getName() {
		return super.getName() + ".font";
	}

	@Override
	protected void populate() {
		// TODO Auto-generated method stub
	}
}
