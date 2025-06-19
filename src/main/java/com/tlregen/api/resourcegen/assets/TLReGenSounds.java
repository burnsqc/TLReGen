package com.tlregen.api.resourcegen.assets;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenResourceGenerator;
import com.tlregen.api.resourcegen.util.TLReGenSoundDefinition;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class TLReGenSounds extends TLReGenResourceGenerator {
	private Map<String, TLReGenSoundDefinition> resources = new HashMap<>();

	public TLReGenSounds(Map<String, TLReGenSoundDefinition> resources, String modID, PackOutput packOutput) {
		super(modID, Types.SOUND, packOutput);
		this.resources = resources;
	}

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		JsonObject json = new JsonObject();
		resources.forEach((key, value) -> json.add(key, value.serialize()));
		return DataProvider.saveStable(cache, json, pathProvider.json(new ResourceLocation(modID, "sounds")));
	}
}
