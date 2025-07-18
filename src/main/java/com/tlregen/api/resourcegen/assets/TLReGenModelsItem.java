package com.tlregen.api.resourcegen.assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenResourceGenerator;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;

public class TLReGenModelsItem extends TLReGenResourceGenerator {
	private Map<ResourceLocation, ItemModelBuilder> resources = new HashMap<>();

	public TLReGenModelsItem(Map<ResourceLocation, ItemModelBuilder> resources, String modID, PackOutput packOutput) {
		super(modID, Types.ITEM_MODEL, packOutput);
		this.resources = resources;
	}

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
		resources.forEach((key, value) -> {
			JsonObject json = value.toJson();
			list.add(DataProvider.saveStable(cache, json, pathProvider.json(key)));
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}
}
