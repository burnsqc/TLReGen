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
import com.tlregen.api.resourcegen.TLReGenAssetProvider;
import com.tlregen.api.resourcegen.util.TLReGenConfiguredModel;
import com.tlregen.api.resourcegen.util.TLReGenVariantBlockStateBuilder;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.IGeneratedBlockState;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.registries.ForgeRegistries;

public class TLReGenBlockstates extends TLReGenAssetProvider {
	private final static Map<Block, IGeneratedBlockState> resources = new HashMap<>();

	public TLReGenBlockstates(Map<Block, IGeneratedBlockState> mapIn) {
		mapIn.forEach((k, v) -> resources.put(k, v));
	}

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		populate();
		List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
		resources.forEach((key, value) -> {
			JsonObject json = value.toJson();
			list.add(DataProvider.saveStable(cache, json, packOutput.createPathProvider(target, "blockstates").json(ForgeRegistries.BLOCKS.getKey(key))));
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

	@Override
	public final String getName() {
		return super.getName() + ".blockstates";
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

	public static TLReGenVariantBlockStateBuilder getVariantBuilder(Block b) {
		TLReGenVariantBlockStateBuilder ret = new TLReGenVariantBlockStateBuilder(b);
		resources.put(b, ret);
		return ret;
	}

	public static MultiPartBlockStateBuilder getMultipartBuilder(Block b) {
		MultiPartBlockStateBuilder ret = new MultiPartBlockStateBuilder(b);
		resources.put(b, ret);
		return ret;
	}

	public static ResourceLocation blockTexture(Block block) {
		ResourceLocation name = ForgeRegistries.BLOCKS.getKey(block);
		return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath());
	}

	@Override
	protected void populate() {
	}
}
