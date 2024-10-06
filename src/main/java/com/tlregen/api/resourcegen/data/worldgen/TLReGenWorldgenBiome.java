package com.tlregen.api.resourcegen.data.worldgen;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.tlregen.api.resourcegen.TLReGenMasterResourceGenerator;
import com.tlregen.api.resourcegen.mirrors.TLReGenRegistrySetBuilder;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
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
import net.minecraftforge.registries.DataPackRegistriesHooks;

public abstract class TLReGenWorldgenBiome extends TLReGenMasterResourceGenerator implements DataProvider {
	private final CompletableFuture<HolderLookup.Provider> damageTypes = lookupProvider.thenApply(r -> constructRegistries(r, new TLReGenRegistrySetBuilder().add(Registries.BIOME, null)));
	private final java.util.function.Predicate<String> namespacePredicate = Set.of(modid) == null ? namespace -> true : Set.of(modid)::contains;
	protected static HolderGetter<PlacedFeature> placedFeature;
	protected static HolderGetter<ConfiguredWorldCarver<?>> configuredWorldCarver;

	@Override
	public CompletableFuture<?> run(CachedOutput p_255785_) {
		return damageTypes.thenCompose((p_256533_) -> {
			DynamicOps<JsonElement> dynamicops = RegistryOps.create(JsonOps.INSTANCE, p_256533_);
			return CompletableFuture.allOf(net.minecraftforge.registries.DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().flatMap((p_256552_) -> {
				return dumpRegistryCap(p_255785_, p_256533_, dynamicops, p_256552_).stream();
			}).toArray((p_255809_) -> {
				return new CompletableFuture[p_255809_];
			}));
		});
	}

	private <T> Optional<CompletableFuture<?>> dumpRegistryCap(CachedOutput p_256502_, HolderLookup.Provider p_256492_, DynamicOps<JsonElement> p_256000_, RegistryDataLoader.RegistryData<T> p_256449_) {
		ResourceKey<? extends Registry<T>> resourcekey = p_256449_.key();
		return p_256492_.lookup(resourcekey).map((p_255847_) -> {
			PackOutput.PathProvider packoutput$pathprovider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, net.minecraftforge.common.ForgeHooks.prefixNamespace(resourcekey.location()));
			return CompletableFuture.allOf(p_255847_.listElements().filter(holder -> namespacePredicate.test(holder.key().location().getNamespace())).map((p_256105_) -> {
				return dumpValue(packoutput$pathprovider.json(p_256105_.key().location()), p_256502_, p_256000_, p_256449_.elementCodec(), p_256105_.value());
			}).toArray((p_256279_) -> {
				return new CompletableFuture[p_256279_];
			}));
		});
	}

	private static <E> CompletableFuture<?> dumpValue(Path p_255678_, CachedOutput p_256438_, DynamicOps<JsonElement> p_256127_, Encoder<E> p_255938_, E p_256590_) {
		Optional<JsonElement> optional = p_255938_.encodeStart(p_256127_, p_256590_).resultOrPartial((p_255999_) -> {
			LOGGER.error("Couldn't serialize element {}: {}", p_255678_, p_255999_);
		});
		return optional.isPresent() ? DataProvider.saveStable(p_256438_, optional.get(), p_255678_) : CompletableFuture.completedFuture((Object) null);
	}

	@Override
	public String getName() {
		return "data." + modid + ".worldgen.biome";
	}

	private static HolderLookup.Provider constructRegistries(HolderLookup.Provider original, TLReGenRegistrySetBuilder datapackEntriesBuilder) {
		var builderKeys = new HashSet<>(datapackEntriesBuilder.getEntryKeys());
		DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().filter(data -> !builderKeys.contains(data.key())).forEach(data -> datapackEntriesBuilder.add(data.key(), context -> {
		}));
		return datapackEntriesBuilder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), original);
	}

	/*
	 * HELPER METHODS
	 */

	protected static Biome biome(TLReGenCarvers carvers, float downfall, TLReGenEffects effects, TLReGenFeatures features, boolean hasPrecipitation, TLReGenSpawners spawners, float temperature) {
		BiomeGenerationSettings.Builder settings = new BiomeGenerationSettings.Builder(placedFeature, configuredWorldCarver);
		BiomeSpecialEffects.Builder effects2 = new BiomeSpecialEffects.Builder().fogColor(effects.fogColor).foliageColorOverride(effects.foliageColor).grassColorModifier(effects.grassColorModifier).ambientMoodSound(effects.moodSound).skyColor(effects.skyColor).waterColor(effects.waterColor).waterFogColor(effects.waterFogColor);
		MobSpawnSettings.Builder spawners2 = new MobSpawnSettings.Builder();

		if (effects.grassColorOverride.isPresent()) {
			effects2.grassColorOverride(effects.grassColorOverride.getAsInt());
		}

		for (ResourceKey<ConfiguredWorldCarver<?>> carver : carvers.air) {
			settings.addCarver(GenerationStep.Carving.AIR, carver);
		}
		for (ResourceKey<ConfiguredWorldCarver<?>> carver : carvers.liquid) {
			settings.addCarver(GenerationStep.Carving.LIQUID, carver);
		}

		for (ResourceKey<PlacedFeature> feature : features.rawGeneration) {
			settings.addFeature(GenerationStep.Decoration.RAW_GENERATION, feature);
		}
		for (ResourceKey<PlacedFeature> feature : features.lakes) {
			settings.addFeature(GenerationStep.Decoration.LAKES, feature);
		}
		for (ResourceKey<PlacedFeature> feature : features.localModifications) {
			settings.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, feature);
		}
		for (ResourceKey<PlacedFeature> feature : features.undergroundStructures) {
			settings.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, feature);
		}
		for (ResourceKey<PlacedFeature> feature : features.surfaceStructures) {
			settings.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, feature);
		}
		for (ResourceKey<PlacedFeature> feature : features.strongholds) {
			settings.addFeature(GenerationStep.Decoration.STRONGHOLDS, feature);
		}
		for (ResourceKey<PlacedFeature> feature : features.undergroundOres) {
			settings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, feature);
		}
		for (ResourceKey<PlacedFeature> feature : features.undergroundDecoration) {
			settings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, feature);
		}
		for (ResourceKey<PlacedFeature> feature : features.vegetalDecoration) {
			settings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, feature);
		}
		for (ResourceKey<PlacedFeature> feature : features.fluidSprings) {
			settings.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, feature);
		}
		for (ResourceKey<PlacedFeature> feature : features.topLayerModification) {
			settings.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, feature);
		}

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

	/*
	 * TEMPLATES
	 */

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
