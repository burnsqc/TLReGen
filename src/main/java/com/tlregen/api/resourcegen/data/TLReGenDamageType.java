package com.tlregen.api.resourcegen.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.tlregen.api.registration.DynamicRegister;
import com.tlregen.api.resourcegen.TLReGenDataProvider;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.world.damagesource.DamageType;

public class TLReGenDamageType extends TLReGenDataProvider {
	private final DynamicRegister<DamageType> dynamicRegister;

	public TLReGenDamageType(DynamicRegister<DamageType> dynamicRegister) {
		this.dynamicRegister = dynamicRegister;
	}

	@Override
	public CompletableFuture<?> run(final CachedOutput cache) {
		List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
		dynamicRegister.getEntries().forEach((key, value) -> {
			JsonObject json = DamageType.CODEC.encodeStart(dynamicOps, value.get()).getOrThrow(false, msg -> LOGGER.error("Failed to encode")).getAsJsonObject();
			list.add(DataProvider.saveStable(cache, json, packOutput.createPathProvider(target, "damage_type").json(key.location())));
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

	@Override
	public final String getName() {
		return "data." + modID + ".damage_type";
	}

	@Override
	protected void populate() {
	}
}
