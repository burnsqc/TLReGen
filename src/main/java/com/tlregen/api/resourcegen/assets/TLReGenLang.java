package com.tlregen.api.resourcegen.assets;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenResourceGenerator;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class TLReGenLang extends TLReGenResourceGenerator {
	private Map<String, String> resources = new HashMap<>();

	public TLReGenLang(Map<String, String> resources, String modID, PackOutput packOutput) {
		super(modID, Types.LANG, packOutput);
		this.resources = resources;
	}

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		JsonObject json = new JsonObject();
		resources.forEach(json::addProperty);
		return DataProvider.saveStable(cache, json, pathProvider.json(new ResourceLocation(modID, "en_us")));
	}
}
