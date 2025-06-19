package com.tlregen.api.resourcegen.assets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenResourceGenerator;
import com.tlregen.api.resourcegen.util.TLReGenConfiguredModel;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.IGeneratedBlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class TLReGenBlockstates extends TLReGenResourceGenerator {
	private Map<Block, IGeneratedBlockState> resources = new HashMap<>();

	public TLReGenBlockstates(Map<Block, IGeneratedBlockState> resources, String modID, PackOutput packOutput) {
		super(modID, Types.BLOCKSTATE, packOutput);
		this.resources = resources;
	}

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
		resources.forEach((key, value) -> {
			JsonObject json = value.toJson();
			list.add(DataProvider.saveStable(cache, json, pathProvider.json(ForgeRegistries.BLOCKS.getKey(key))));
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

	/*
	 * HELPER METHODS
	 */

	public static class ConfiguredModelList {
		private final List<TLReGenConfiguredModel> models;

		private ConfiguredModelList(List<TLReGenConfiguredModel> models) {
			Preconditions.checkArgument(!models.isEmpty());
			this.models = models;
		}

		public ConfiguredModelList(TLReGenConfiguredModel... models) {
			this(Arrays.asList(models));
		}

		public JsonElement toJSON() {
			if (models.size() == 1) {
				return models.get(0).toJSON(false);
			} else {
				JsonArray ret = new JsonArray();
				for (TLReGenConfiguredModel m : models) {
					ret.add(m.toJSON(true));
				}
				return ret;
			}
		}

		public ConfiguredModelList append(TLReGenConfiguredModel... models) {
			return new ConfiguredModelList(ImmutableList.<TLReGenConfiguredModel>builder().addAll(this.models).add(models).build());
		}
	}
}
