package com.tlregen.api.resourcegen.assets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenAssetProvider;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

public abstract class TLReGenModelsBlock extends TLReGenAssetProvider {

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		models.modelBuilders.clear();
		populate();
		if (models.modelBuilders.isEmpty()) {
			return CompletableFuture.allOf();
		} else {
			List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
			models.modelBuilders.forEach((key, value) -> {
				JsonObject json = value.toJson();
				list.add(DataProvider.saveStable(cache, json, packOutput.createPathProvider(target, "models").json(key)));
			});
			return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
		}
	}

	@Override
	public final String getName() {
		return super.getName() + ".models.block";
	}
}
