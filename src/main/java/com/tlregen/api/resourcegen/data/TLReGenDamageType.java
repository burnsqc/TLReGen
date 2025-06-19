package com.tlregen.api.resourcegen.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.tlregen.api.registration.DynamicRegister;
import com.tlregen.api.resourcegen.TLReGenResourceGenerator;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageType;

public class TLReGenDamageType extends TLReGenResourceGenerator {
	private final DynamicRegister<DamageType> dynamicRegister;

	public TLReGenDamageType(DynamicRegister<DamageType> dynamicRegister, String modID, PackOutput packOutput) {
		super(modID, Types.DAMAGE_TYPE, packOutput);
		this.dynamicRegister = dynamicRegister;
	}

	@Override
	public CompletableFuture<?> run(final CachedOutput cache) {
		List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
		dynamicRegister.getEntries().forEach((key, value) -> {
			JsonObject json = DamageType.CODEC.encodeStart(dynamicOps, value.get()).getOrThrow(false, msg -> LOGGER.error("Failed to encode")).getAsJsonObject();
			list.add(DataProvider.saveStable(cache, json, pathProvider.json(key.location())));
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}
}
