package com.tlregen.api.resourcegen.assets;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenAssetProvider;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

public class TLReGenLang extends TLReGenAssetProvider {
	private Map<String, String> resources = new HashMap<>();

	public TLReGenLang(Map<String, String> resources) {
		this.resources = resources;
	}

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		JsonObject json = new JsonObject();
		resources.forEach(json::addProperty);
		return DataProvider.saveStable(cache, json, packOutput.createPathProvider(target, "lang").json(new ResourceLocation(modID, "en_us")));
	}

	@Override
	public final String getName() {
		return super.getName() + ".lang";
	}

	@Override
	protected void populate() {
		// TODO Auto-generated method stub
	}
}
