package com.tlregen.api.resourcegen.util.helpers;

import java.util.List;
import java.util.OptionalInt;

import com.mojang.datafixers.util.Pair;
import com.tlregen.api.resourcegen.data.worldgen.TLReGenWorldgenBiome;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class TLReGenBiomeHelper {

	public static Biome biome(TLReGenCarvers carvers, float downfall, TLReGenEffects effects, TLReGenFeatures features, boolean hasPrecipitation, TLReGenSpawners spawners, float temperature) {
		BiomeGenerationSettings.Builder settings = new BiomeGenerationSettings.Builder(TLReGenWorldgenBiome.bootstrapContext.lookup(Registries.PLACED_FEATURE), TLReGenWorldgenBiome.bootstrapContext.lookup(Registries.CONFIGURED_CARVER));
		BiomeSpecialEffects.Builder effects2 = new BiomeSpecialEffects.Builder().fogColor(effects.fogColor).foliageColorOverride(effects.foliageColor).grassColorModifier(effects.grassColorModifier).ambientMoodSound(effects.moodSound).skyColor(effects.skyColor).waterColor(effects.waterColor).waterFogColor(effects.waterFogColor);
		MobSpawnSettings.Builder spawners2 = new MobSpawnSettings.Builder();

		if (effects.grassColorOverride.isPresent()) {
			effects2.grassColorOverride(effects.grassColorOverride.getAsInt());
		}

		carvers.air.forEach(carver -> settings.addCarver(GenerationStep.Carving.AIR, carver));
		carvers.liquid.forEach(carver -> settings.addCarver(GenerationStep.Carving.LIQUID, carver));

		features.rawGeneration.forEach(feature -> settings.addFeature(GenerationStep.Decoration.RAW_GENERATION, feature));
		features.lakes.forEach(feature -> settings.addFeature(GenerationStep.Decoration.LAKES, feature));
		features.localModifications.forEach(feature -> settings.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, feature));
		features.undergroundStructures.forEach(feature -> settings.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, feature));
		features.surfaceStructures.forEach(feature -> settings.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, feature));
		features.strongholds.forEach(feature -> settings.addFeature(GenerationStep.Decoration.STRONGHOLDS, feature));
		features.undergroundOres.forEach(feature -> settings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, feature));
		features.undergroundDecoration.forEach(feature -> settings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, feature));
		features.vegetalDecoration.forEach(feature -> settings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, feature));
		features.fluidSprings.forEach(feature -> settings.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, feature));
		features.topLayerModification.forEach(feature -> settings.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, feature));

		for (MobSpawnSettings.SpawnerData spawner : spawners.ambient) {
			spawners2.addSpawn(MobCategory.AMBIENT, spawner);
		}
		for (MobSpawnSettings.SpawnerData spawner : spawners.axolotls) {
			spawners2.addSpawn(MobCategory.AXOLOTLS, spawner);
		}
		for (MobSpawnSettings.SpawnerData spawner : spawners.creature) {
			spawners2.addSpawn(MobCategory.CREATURE, spawner);
		}
		for (MobSpawnSettings.SpawnerData spawner : spawners.misc) {
			spawners2.addSpawn(MobCategory.MISC, spawner);
		}
		for (MobSpawnSettings.SpawnerData spawner : spawners.monster) {
			spawners2.addSpawn(MobCategory.MONSTER, spawner);
		}
		for (MobSpawnSettings.SpawnerData spawner : spawners.undergroundWaterCreature) {
			spawners2.addSpawn(MobCategory.UNDERGROUND_WATER_CREATURE, spawner);
		}
		for (MobSpawnSettings.SpawnerData spawner : spawners.waterAmbient) {
			spawners2.addSpawn(MobCategory.WATER_AMBIENT, spawner);
		}
		for (MobSpawnSettings.SpawnerData spawner : spawners.waterCreature) {
			spawners2.addSpawn(MobCategory.WATER_CREATURE, spawner);
		}
		for (Pair<MobCategory, List<MobSpawnSettings.SpawnerData>> custom : spawners.custom) {
			for (MobSpawnSettings.SpawnerData spawner : custom.getSecond()) {
				spawners2.addSpawn(custom.getFirst(), spawner);
			}
		}

		return new Biome.BiomeBuilder().downfall(downfall).specialEffects(effects2.build()).generationSettings(settings.build()).hasPrecipitation(hasPrecipitation).mobSpawnSettings(spawners2.build()).temperature(temperature).build();
	}

	/**
	 * @param air
	 * @param liquid
	 */
	public record TLReGenCarvers(List<ResourceKey<ConfiguredWorldCarver<?>>> air, List<ResourceKey<ConfiguredWorldCarver<?>>> liquid) {
		/**
		 * @param air
		 */
		public TLReGenCarvers(List<ResourceKey<ConfiguredWorldCarver<?>>> air) {
			this(air, List.of());
		}
	}

	/**
	 * @param fogColor
	 * @param foliageColor
	 * @param grassColorOverride
	 * @param grassColorModifier
	 * @param moodSound
	 * @param skyColor
	 * @param waterColor
	 * @param waterFogColor
	 */
	public record TLReGenEffects(int fogColor, int foliageColor, OptionalInt grassColorOverride, BiomeSpecialEffects.GrassColorModifier grassColorModifier, AmbientMoodSettings moodSound, int skyColor, int waterColor, int waterFogColor) {
		/**
		 * @param fogColor
		 * @param foliageColor
		 * @param grassColorOverride
		 * @param moodSound
		 * @param skyColor
		 * @param waterColor
		 * @param waterFogColor
		 */
		public TLReGenEffects(int fogColor, int foliageColor, int grassColorOverride, AmbientMoodSettings moodSound, int skyColor, int waterColor, int waterFogColor) {
			this(fogColor, foliageColor, OptionalInt.of(grassColorOverride), BiomeSpecialEffects.GrassColorModifier.NONE, moodSound, skyColor, waterColor, waterFogColor);
		}

		/**
		 * @param fogColor
		 * @param foliageColor
		 * @param grassColorModifier
		 * @param moodSound
		 * @param skyColor
		 * @param waterColor
		 * @param waterFogColor
		 */
		public TLReGenEffects(int fogColor, int foliageColor, BiomeSpecialEffects.GrassColorModifier grassColorModifier, AmbientMoodSettings moodSound, int skyColor, int waterColor, int waterFogColor) {
			this(fogColor, foliageColor, OptionalInt.empty(), grassColorModifier, moodSound, skyColor, waterColor, waterFogColor);
		}
	}

	/**
	 * @param rawGeneration
	 * @param lakes
	 * @param localModifications
	 * @param undergroundStructures
	 * @param surfaceStructures
	 * @param strongholds
	 * @param undergroundOres
	 * @param undergroundDecoration
	 * @param fluidSprings
	 * @param vegetalDecoration
	 * @param topLayerModification
	 */
	public record TLReGenFeatures(List<ResourceKey<PlacedFeature>> rawGeneration, List<ResourceKey<PlacedFeature>> lakes, List<ResourceKey<PlacedFeature>> localModifications, List<ResourceKey<PlacedFeature>> undergroundStructures, List<ResourceKey<PlacedFeature>> surfaceStructures, List<ResourceKey<PlacedFeature>> strongholds, List<ResourceKey<PlacedFeature>> undergroundOres, List<ResourceKey<PlacedFeature>> undergroundDecoration, List<ResourceKey<PlacedFeature>> fluidSprings, List<ResourceKey<PlacedFeature>> vegetalDecoration, List<ResourceKey<PlacedFeature>> topLayerModification) {
	}

	/**
	 * @param ambient
	 * @param axolotls
	 * @param creature
	 * @param misc
	 * @param monster
	 * @param undergroundWaterCreature
	 * @param waterAmbient
	 * @param waterCreature
	 * @param custom
	 */
	public record TLReGenSpawners(List<MobSpawnSettings.SpawnerData> ambient, List<MobSpawnSettings.SpawnerData> axolotls, List<MobSpawnSettings.SpawnerData> creature, List<MobSpawnSettings.SpawnerData> misc, List<MobSpawnSettings.SpawnerData> monster, List<MobSpawnSettings.SpawnerData> undergroundWaterCreature, List<MobSpawnSettings.SpawnerData> waterAmbient, List<MobSpawnSettings.SpawnerData> waterCreature, List<Pair<MobCategory, List<MobSpawnSettings.SpawnerData>>> custom) {
	}
}
