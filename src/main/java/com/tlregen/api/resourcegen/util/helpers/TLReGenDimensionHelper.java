package com.tlregen.api.resourcegen.util.helpers;

import java.util.ArrayList;

import com.mojang.datafixers.util.Pair;
import com.tlregen.api.resourcegen.data.TLReGenDimension;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class TLReGenDimensionHelper {
	public static BootstapContext<?> bootsc;
	
	public static LevelStem dimension(ResourceKey<DimensionType> type, ChunkGenerator generator) {
		return new LevelStem(bootsc.lookup(Registries.DIMENSION_TYPE).getOrThrow(type), generator);
	}

	public static ChunkGenerator chunkGenerator(String type, BiomeSource biomeSource, ResourceKey<NoiseGeneratorSettings> settings) {
		return new NoiseBasedChunkGenerator(biomeSource, bootsc.lookup(Registries.NOISE_SETTINGS).getOrThrow(settings));
	}

	public static class BiomeSourceBuilder {
		private ArrayList<Pair<Climate.ParameterPoint, Holder<Biome>>> biomes;

		public BiomeSourceBuilder(String type) {
			biomes = new ArrayList<>();
		}

		public BiomeSourceBuilder add(Pair<Climate.ParameterPoint, Holder<Biome>> biomes) {
			this.biomes.add(biomes);
			return this;
		}

		public BiomeSource build() {
			return MultiNoiseBiomeSource.createFromList(new Climate.ParameterList<Holder<Biome>>(biomes));
		}
	}

	public static Pair<Climate.ParameterPoint, Holder<Biome>> biome(ResourceKey<Biome> biome, Climate.ParameterPoint parameters) {
		final HolderGetter<Biome> biomesGetter = bootsc.lookup(Registries.BIOME);
		return Pair.of(parameters, biomesGetter.getOrThrow(biome));
	}

	public static Climate.ParameterPoint parameters(float continentalness, float depth, float erosion, float humidity, float offset, float temperature, float weirdness) {
		return Climate.parameters(temperature, humidity, continentalness, erosion, depth, weirdness, offset);
	}
}
