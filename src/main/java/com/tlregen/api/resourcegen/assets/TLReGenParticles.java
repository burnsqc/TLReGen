package com.tlregen.api.resourcegen.assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenResourceGenerator;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class TLReGenParticles extends TLReGenResourceGenerator {
	private Map<ResourceLocation, List<String>> resources = new HashMap<>();

	public TLReGenParticles(Map<ResourceLocation, List<String>> resources, String modID, PackOutput packOutput) {
		super(modID, Types.PARTICLE, packOutput);
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
			list.add(DataProvider.saveStable(cache, json, pathProvider.json(key)));
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}
}
