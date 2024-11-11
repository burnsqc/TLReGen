package com.tlregen.api.setup.util;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent.Operation;

/**
 * Helper class created as a workaround for the package-private access modifier on SpawnPlacements.Data
 */
public class TLReGenSpawnPlacements {
	public static final Map<EntityType<? extends Entity>, TLReGenSpawnPlacements.Data> DATA_BY_TYPE = Maps.newHashMap();

	public static class Data {
		public final SpawnPlacements.Type placement;
		public final Heightmap.Types heightMap;
		public final SpawnPredicate<? extends Entity> predicate;
		public final Operation operation;

		public Data(SpawnPlacements.Type placement, Heightmap.Types heightMap, SpawnPlacements.SpawnPredicate<? extends Entity> predicate, Operation operation) {
			this.placement = placement;
			this.heightMap = heightMap;
			this.predicate = predicate;
			this.operation = operation;
		}
	}
}
