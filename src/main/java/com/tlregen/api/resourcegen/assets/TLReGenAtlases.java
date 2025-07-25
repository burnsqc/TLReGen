package com.tlregen.api.resourcegen.assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenResourceGenerator;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class TLReGenAtlases extends TLReGenResourceGenerator {
	private Map<ResourceLocation, List<SpriteSource>> resources = new HashMap<>();

	public TLReGenAtlases(Map<ResourceLocation, List<SpriteSource>> resources, String modID, PackOutput packOutput) {
		super(modID, Types.ATLAS, packOutput);
		this.resources = resources;
	}

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
		resources.forEach((key, value) -> {
			JsonObject json = SpriteSources.FILE_CODEC.encodeStart(dynamicOps, value).getOrThrow(false, msg -> LOGGER.error("Failed to encode")).getAsJsonObject();
			list.add(DataProvider.saveStable(cache, json, pathProvider.json(key)));
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

	public class VanillaAtlases {
		public final static ResourceLocation ARMOR_TRIMS = new ResourceLocation("armor_trims");
		public final static ResourceLocation BANNER_PATTERNS = new ResourceLocation("banner_patterns");
		public final static ResourceLocation BEDS = new ResourceLocation("beds");
		public final static ResourceLocation BLOCKS = new ResourceLocation("blocks");
		public final static ResourceLocation CHESTS = new ResourceLocation("chests");
		public final static ResourceLocation DECORATED_POT = new ResourceLocation("decorated_pot");
		public final static ResourceLocation MOB_EFFECTS = new ResourceLocation("mob_effects");
		public final static ResourceLocation PAINTINGS = new ResourceLocation("paintings");
		public final static ResourceLocation PARTICLES = new ResourceLocation("particles");
		public final static ResourceLocation SHIELD_PATTERNS = new ResourceLocation("shield_patterns");
		public final static ResourceLocation SHULKER_BOXES = new ResourceLocation("shulker_boxes");
		public final static ResourceLocation SIGNS = new ResourceLocation("signs");
	}
}
