package com.tlregen.api.resourcegen.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.tlregen.api.resourcegen.TLReGenMasterResourceGenerator;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;

public abstract class TLReGenDimensionType extends TLReGenMasterResourceGenerator implements DataProvider {
	private final Map<ResourceKey<DimensionType>, DimensionType> dimensionTypes = new LinkedHashMap<>();
	private final String name = "data." + modid + ".dimension_type";
	private final DynamicOps<JsonElement> dynamicOps = JsonOps.INSTANCE;

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		dimensionTypes.clear();
		populate();
		if (dimensionTypes.isEmpty()) {
			return CompletableFuture.allOf();
		} else {
			List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
			dimensionTypes.forEach((key, value) -> {
				JsonObject json = DimensionType.DIRECT_CODEC.encodeStart(dynamicOps, value).getOrThrow(false, msg -> LOGGER.error("Failed to encode")).getAsJsonObject();
				list.add(DataProvider.saveStable(cache, json, packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "dimension_type").json(key.location())));
			});
			return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
		}
	}

	/**
	 * OVERRIDE ME TO ADD DIMENSION TYPES
	 */
	@Override
	protected abstract void populate();

	@Override
	public final String getName() {
		return name;
	}

	protected final void dimensionType(ResourceKey<DimensionType> name, DimensionType type) {
		dimensionTypes.put(name, type);
	}

	/*
	 * HELPER METHODS
	 */

	/**
	 * Create a new Dimension Type.
	 * 
	 * @param ambientLight
	 * @param bedWorks
	 * @param coordinateScale
	 * @param effects
	 * @param fixedTime
	 * @param hasCeiling
	 * @param hasRaids
	 * @param hasSkylight
	 * @param height
	 * @param infiniburn
	 * @param logicalHeight
	 * @param minY
	 * @param monsterSpawnBlockLightLimit
	 * @param monsterSpawnLightLevel
	 * @param natural
	 * @param piglinSafe
	 * @param respawnAnchorWorks
	 * @param ultraWarm
	 * @return
	 */
	protected static DimensionType dimensionType(float ambientLight, boolean bedWorks, double coordinateScale, ResourceLocation effects, OptionalLong fixedTime, boolean hasCeiling, boolean hasRaids, boolean hasSkylight, int height, TagKey<Block> infiniburn, int logicalHeight, int minY, int monsterSpawnBlockLightLimit, IntProvider monsterSpawnLightLevel, boolean natural, boolean piglinSafe, boolean respawnAnchorWorks, boolean ultraWarm) {
		return new DimensionType(fixedTime, hasSkylight, hasCeiling, ultraWarm, natural, coordinateScale, bedWorks, respawnAnchorWorks, minY, height, logicalHeight, infiniburn, effects, ambientLight, new DimensionType.MonsterSettings(piglinSafe, hasRaids, monsterSpawnLightLevel, monsterSpawnBlockLightLimit));
	}
}
