package com.tlregen.api.resourcegen.data.worldgen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.tlregen.api.resourcegen.TLReGenMasterResourceGenerator;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;

public abstract class TLReGenWorldgenNoise extends TLReGenMasterResourceGenerator implements DataProvider {
	private final Map<ResourceKey<NoiseParameters>, NoiseParameters> noises = new LinkedHashMap<>();
	private final String name = "data." + modid + ".worldgen.noise";
	private final DynamicOps<JsonElement> dynamicOps = JsonOps.INSTANCE;

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		noises.clear();
		populate();
		if (noises.isEmpty()) {
			return CompletableFuture.allOf();
		} else {
			List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
			noises.forEach((key, value) -> {
				JsonObject json = NoiseParameters.DIRECT_CODEC.encodeStart(dynamicOps, value).getOrThrow(false, msg -> LOGGER.error("Failed to encode")).getAsJsonObject();
				list.add(DataProvider.saveStable(cache, json, packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "worldgen/noise").json(key.location())));
			});
			return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
		}
	}

	/**
	 * OVERRIDE ME TO ADD NOISES
	 */
	@Override
	protected abstract void populate();

	@Override
	public final String getName() {
		return name;
	}

	protected final void noise(ResourceKey<NoiseParameters> name, NoiseParameters type) {
		noises.put(name, type);
	}

	/*
	 * HELPER METHODS
	 */
}
