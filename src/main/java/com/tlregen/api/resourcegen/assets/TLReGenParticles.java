package com.tlregen.api.resourcegen.assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenAssetProvider;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

public class TLReGenParticles extends TLReGenAssetProvider {
	private Map<ResourceLocation, List<String>> resources = new HashMap<>();

	public TLReGenParticles(Map<ResourceLocation, List<String>> resources) {
		this.resources = resources;
	}

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
		resources.forEach((key, value) -> {
			JsonObject json = new JsonObject();
			JsonArray textures = new JsonArray();
			value.forEach(textures::add);
			json.add("textures", textures);
			list.add(DataProvider.saveStable(cache, json, packOutput.createPathProvider(target, "particles").json(key)));
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

	@Override
	public final String getName() {
		return super.getName() + ".particles";
	}

	@Override
	protected void populate() {
		// TODO Auto-generated method stub

	}
}
