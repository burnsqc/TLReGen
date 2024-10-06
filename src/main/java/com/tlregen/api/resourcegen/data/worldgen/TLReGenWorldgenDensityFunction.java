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
import net.minecraft.world.level.levelgen.DensityFunction;

public abstract class TLReGenWorldgenDensityFunction extends TLReGenMasterResourceGenerator implements DataProvider {
	private final Map<ResourceKey<DensityFunction>, DensityFunction> densityFunctions = new LinkedHashMap<>();
	private final String name = "data." + modid + ".worldgen.density_function";
	private final DynamicOps<JsonElement> dynamicOps = JsonOps.INSTANCE;

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		densityFunctions.clear();
		populate();
		if (densityFunctions.isEmpty()) {
			return CompletableFuture.allOf();
		} else {
			List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
			densityFunctions.forEach((key, value) -> {
				JsonObject json = DensityFunction.DIRECT_CODEC.encodeStart(dynamicOps, value).getOrThrow(false, msg -> LOGGER.error("Failed to encode")).getAsJsonObject();
				list.add(DataProvider.saveStable(cache, json, packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "worldgen/density_function").json(key.location())));
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

	protected final void densityFunction(ResourceKey<DensityFunction> name, DensityFunction type) {
		densityFunctions.put(name, type);
	}

	/*
	 * HELPER METHODS
	 */
}
