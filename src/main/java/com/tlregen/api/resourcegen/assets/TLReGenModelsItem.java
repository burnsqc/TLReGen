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
import net.minecraftforge.client.model.generators.ItemModelBuilder;

public class TLReGenModelsItem extends TLReGenAssetProvider {
	private final Map<ResourceLocation, ItemModelBuilder> resources = new HashMap<>();

	public TLReGenModelsItem(Map<ResourceLocation, ItemModelBuilder> resourcesIn) {
		resourcesIn.forEach((k, v) -> this.resources.put(k, v));
	}

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		populate();
		List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
		resources.forEach((key, value) -> {
			JsonObject json = value.toJson();
			list.add(DataProvider.saveStable(cache, json, packOutput.createPathProvider(target, "models").json(extendWithFolder(key))));
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

	@Override
	public final String getName() {
		return super.getName() + ".models.item";
	}

	private ResourceLocation extendWithFolder(ResourceLocation rl) {
		if (rl.getPath().contains("/")) {
			return rl;
		}
		return new ResourceLocation(rl.getNamespace(), "item/" + rl.getPath());
	}

	@Override
	protected void populate() {
	}
}
