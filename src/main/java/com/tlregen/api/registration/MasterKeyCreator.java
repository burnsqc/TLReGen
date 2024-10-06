package com.tlregen.api.registration;

import com.tlregen.util.TechnologicaLocation;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * <p>
 * This class contains "cheater" methods for Resource Keys and Tag Keys.
 * This class is to be extended by any subclasses that solely define keys for use by dynamic registries.
 * </p>
 * 
 * @tl.status GREEN
 */
public abstract class MasterKeyCreator {
	protected static ResourceKey<Biome> biome(String path) {
		return ResourceKey.create(Registries.BIOME, new TechnologicaLocation(path));
	}

	protected static ResourceKey<BiomeModifier> biomeModifier(String path) {
		return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new TechnologicaLocation(path));
	}

	protected static ResourceKey<ConfiguredFeature<?, ?>> configuredFeature(String name) {
		return ResourceKey.create(Registries.CONFIGURED_FEATURE, new TechnologicaLocation(name));
	}

	protected static ResourceKey<DamageType> damageType(String path) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, new TechnologicaLocation(path));
	}

	protected static ResourceKey<DensityFunction> densityFunction(String path) {
		return ResourceKey.create(Registries.DENSITY_FUNCTION, new TechnologicaLocation(path));
	}

	protected static ResourceKey<Level> dimension(String path) {
		return ResourceKey.create(Registries.DIMENSION, new TechnologicaLocation(path));
	}

	protected static ResourceKey<LevelStem> dimensionStem(String path) {
		return ResourceKey.create(Registries.LEVEL_STEM, new TechnologicaLocation(path));
	}

	protected static ResourceKey<DimensionType> dimensionType(String path) {
		return ResourceKey.create(Registries.DIMENSION_TYPE, new TechnologicaLocation(path));
	}

	protected static ResourceKey<NoiseParameters> noise(String path) {
		return ResourceKey.create(Registries.NOISE, new TechnologicaLocation(path));
	}

	protected static ResourceKey<NoiseGeneratorSettings> noiseGeneratorSettings(String path) {
		return ResourceKey.create(Registries.NOISE_SETTINGS, new TechnologicaLocation(path));
	}

	protected static ResourceKey<PaintingVariant> paintingVariant(String path) {
		return ResourceKey.create(Registries.PAINTING_VARIANT, new TechnologicaLocation(path));
	}

	protected static ResourceKey<PlacedFeature> placedFeature(String name) {
		return ResourceKey.create(Registries.PLACED_FEATURE, new TechnologicaLocation(name));
	}

	protected static ResourceKey<Structure> structure(String path) {
		return ResourceKey.create(Registries.STRUCTURE, new TechnologicaLocation(path));
	}

	protected static ResourceKey<StructureSet> structureSet(String path) {
		return ResourceKey.create(Registries.STRUCTURE_SET, new TechnologicaLocation(path));
	}

	protected static ResourceKey<StructureTemplatePool> structureTemplatePool(String path) {
		return ResourceKey.create(Registries.TEMPLATE_POOL, new TechnologicaLocation(path));
	}

	protected static TagKey<Biome> biomeTag(String path) {
		return TagKey.create(Registries.BIOME, new TechnologicaLocation(path));
	}

	protected static TagKey<Block> blockTag(String path) {
		return TagKey.create(Registries.BLOCK, new TechnologicaLocation(path));
	}

	protected static TagKey<Item> itemTag(String path) {
		return TagKey.create(Registries.ITEM, new TechnologicaLocation(path));
	}
}
