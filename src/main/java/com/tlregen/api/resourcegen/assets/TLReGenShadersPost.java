package com.tlregen.api.resourcegen.assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenResourceGenerator;
import com.tlregen.api.resourcegen.util.TLReGenPostShader;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class TLReGenShadersPost extends TLReGenResourceGenerator {
	private Map<ResourceLocation, TLReGenPostShader> resources = new HashMap<>();

	public TLReGenShadersPost(Map<ResourceLocation, TLReGenPostShader> resources, String modID, PackOutput packOutput) {
		super(modID, Types.POST_SHADER, packOutput);
		this.resources = resources;
	}

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
		resources.forEach((key, value) -> {
			JsonObject json = value.serialize();
			list.add(DataProvider.saveStable(cache, json, pathProvider.json(key)));
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}
}
