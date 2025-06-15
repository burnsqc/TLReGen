package com.tlregen.api.resourcegen.assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenAssetProvider;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

public class TLReGenModelsBlock extends TLReGenAssetProvider {
	private Map<ResourceLocation, BlockModelBuilder> resources = new HashMap<>();

	public TLReGenModelsBlock(Map<ResourceLocation, BlockModelBuilder> resources) {
		this.resources = resources;
	}

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
		resources.forEach((key, value) -> {
			JsonObject json = value.toJson();
			list.add(DataProvider.saveStable(cache, json, packOutput.createPathProvider(target, "models/block").json(key)));
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

	@Override
	public final String getName() {
		return super.getName() + ".models.block";
	}

	@Override
	protected void populate() {
	}
}
