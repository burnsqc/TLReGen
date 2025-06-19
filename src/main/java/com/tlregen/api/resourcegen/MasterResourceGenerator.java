package com.tlregen.api.resourcegen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.tlregen.api.registration.DynamicRegister;
import com.tlregen.api.resourcegen.assets.TLReGenAtlases;
import com.tlregen.api.resourcegen.assets.TLReGenBlockstates;
import com.tlregen.api.resourcegen.assets.TLReGenFont;
import com.tlregen.api.resourcegen.assets.TLReGenLang;
import com.tlregen.api.resourcegen.assets.TLReGenModelsBlock;
import com.tlregen.api.resourcegen.assets.TLReGenModelsItem;
import com.tlregen.api.resourcegen.assets.TLReGenParticles;
import com.tlregen.api.resourcegen.assets.TLReGenSounds;
import com.tlregen.api.resourcegen.data.TLReGenDamageType;
import com.tlregen.api.resourcegen.data.TLReGenDimension;
import com.tlregen.api.resourcegen.data.TLReGenDimensionType;
import com.tlregen.api.resourcegen.data.TLReGenForgeBiomeModifier;
import com.tlregen.api.resourcegen.data.tags.TLReGenTagsBlocks;
import com.tlregen.api.resourcegen.data.worldgen.TLReGenWorldgenBiome;
import com.tlregen.api.resourcegen.data.worldgen.TLReGenWorldgenConfiguredFeature;
import com.tlregen.api.resourcegen.data.worldgen.TLReGenWorldgenDensityFunction;
import com.tlregen.api.resourcegen.data.worldgen.TLReGenWorldgenNoise;
import com.tlregen.api.resourcegen.data.worldgen.TLReGenWorldgenNoiseSettings;
import com.tlregen.api.resourcegen.data.worldgen.TLReGenWorldgenPlacedFeature;
import com.tlregen.api.resourcegen.data.worldgen.TLReGenWorldgenStructure;
import com.tlregen.api.resourcegen.data.worldgen.TLReGenWorldgenStructureSet;
import com.tlregen.api.resourcegen.data.worldgen.TLReGenWorldgenTemplatePool;
import com.tlregen.api.resourcegen.util.TLReGenSoundDefinition;

import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
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
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.IGeneratedBlockState;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class MasterResourceGenerator {
	private static DataGenerator generator;
	public static PackOutput packOutput;
	public static ExistingFileHelper helper;
	public static CompletableFuture<HolderLookup.Provider> lookupProvider;
	public static String modID;
	protected final DynamicOps<JsonElement> dynamicOps = JsonOps.INSTANCE;

	public static TLReGenTagsBlocks TagBlocks;

	private Set<Supplier<DataProvider>> dataProviders = Collections.emptySet();

	private Supplier<Map<ResourceLocation, List<SpriteSource>>> atlases;
	private Supplier<Map<ResourceLocation, BlockModelBuilder>> blockModels;
	private Supplier<Map<Block, IGeneratedBlockState>> blockStates;
	private Supplier<Map<ResourceLocation, List<GlyphProviderDefinition>>> font;
	private Supplier<Map<ResourceLocation, ItemModelBuilder>> itemModels;
	private Supplier<Map<String, String>> lang;
	private Supplier<Map<ResourceLocation, List<String>>> particles;
	private Supplier<Map<String, TLReGenSoundDefinition>> sounds;
	private Supplier<DynamicRegister<Biome>> biomes;
	private Supplier<DynamicRegister<BiomeModifier>> biomeModifiers;
	private Supplier<DynamicRegister<ConfiguredFeature<?, ?>>> configuredFeatures;
	private Supplier<DynamicRegister<DamageType>> damageTypes;
	private Supplier<DynamicRegister<DensityFunction>> densityFunctions;
	private Supplier<DynamicRegister<DimensionType>> dimensionTypes;
	private Supplier<DynamicRegister<LevelStem>> dimensions;
	private Supplier<DynamicRegister<NoiseParameters>> noiseParameters;
	private Supplier<DynamicRegister<NoiseGeneratorSettings>> noiseGeneratorSettings;
	private Supplier<DynamicRegister<PlacedFeature>> placedFeatures;
	private Supplier<DynamicRegister<Structure>> structures;
	private Supplier<DynamicRegister<StructureSet>> structureSets;
	private Supplier<DynamicRegister<StructureTemplatePool>> structureTemplatePools;

	public MasterResourceGenerator(String modid) {
		modID = modid;
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	public MasterResourceGenerator() {
	}

	public void addDataProvider(Supplier<DataProvider> provider) {
		if (dataProviders.isEmpty()) {
			dataProviders = new LinkedHashSet<Supplier<DataProvider>>(new ArrayList<Supplier<DataProvider>>(Arrays.asList(provider)));
		} else {
			dataProviders.add(provider);
		}
	}

	public void addAtlases(Supplier<Map<ResourceLocation, List<SpriteSource>>> atlases) {
		this.atlases = atlases;
	}

	public void addBiomes(Supplier<DynamicRegister<Biome>> biomes) {
		this.biomes = biomes;
	}

	public void addBiomeModifiers(Supplier<DynamicRegister<BiomeModifier>> biomeModifiers) {
		this.biomeModifiers = biomeModifiers;
	}

	public void addBlockModels(Supplier<Map<ResourceLocation, BlockModelBuilder>> blockModels) {
		this.blockModels = blockModels;
	}

	public void addBlockStates(Supplier<Map<Block, IGeneratedBlockState>> blockStates) {
		this.blockStates = blockStates;
	}

	public void addConfiguredFeatures(Supplier<DynamicRegister<ConfiguredFeature<?, ?>>> configuredFeatures) {
		this.configuredFeatures = configuredFeatures;
	}

	public void addDamageTypes(Supplier<DynamicRegister<DamageType>> damageTypes) {
		this.damageTypes = damageTypes;
	}

	public void addDensityFunctions(Supplier<DynamicRegister<DensityFunction>> densityFunctions) {
		this.densityFunctions = densityFunctions;
	}

	public void addDimensions(Supplier<DynamicRegister<LevelStem>> dimensions) {
		this.dimensions = dimensions;
	}

	public void addDimensionTypes(Supplier<DynamicRegister<DimensionType>> dimensionTypes) {
		this.dimensionTypes = dimensionTypes;
	}

	public void addFonts(Supplier<Map<ResourceLocation, List<GlyphProviderDefinition>>> font) {
		this.font = font;
	}

	public void addItemModels(Supplier<Map<ResourceLocation, ItemModelBuilder>> itemModels) {
		this.itemModels = itemModels;
	}

	public void addLang(Supplier<Map<String, String>> lang) {
		this.lang = lang;
	}

	public void addNoiseParameters(Supplier<DynamicRegister<NoiseParameters>> noiseParameters) {
		this.noiseParameters = noiseParameters;
	}

	public void addNoiseGeneratorSettings(Supplier<DynamicRegister<NoiseGeneratorSettings>> noiseGeneratorSettings) {
		this.noiseGeneratorSettings = noiseGeneratorSettings;
	}

	public void addParticles(Supplier<Map<ResourceLocation, List<String>>> particles) {
		this.particles = particles;
	}

	public void addPlacedFeatures(Supplier<DynamicRegister<PlacedFeature>> placedFeatures) {
		this.placedFeatures = placedFeatures;
	}

	public void addSounds(Supplier<Map<String, TLReGenSoundDefinition>> sounds) {
		this.sounds = sounds;
	}

	public void addStructures(Supplier<DynamicRegister<Structure>> structures) {
		this.structures = structures;
	}

	public void addStructureSets(Supplier<DynamicRegister<StructureSet>> structureSets) {
		this.structureSets = structureSets;
	}

	public void addStructureTemplatePools(Supplier<DynamicRegister<StructureTemplatePool>> structureTemplatePools) {
		this.structureTemplatePools = structureTemplatePools;
	}

	@SubscribeEvent
	public void addGenerators(final GatherDataEvent eventIn) {
		;
		generator = eventIn.getGenerator();
		packOutput = generator.getPackOutput();
		helper = eventIn.getExistingFileHelper();
		lookupProvider = eventIn.getLookupProvider();

		if (atlases != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenAtlases(atlases.get(), modID, packOutput));
		}
		if (blockStates != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenBlockstates(blockStates.get(), modID, packOutput));
		}
		if (font != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenFont(font.get(), modID, packOutput));
		}
		if (lang != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenLang(lang.get(), modID, packOutput));
		}
		if (blockModels != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenModelsBlock(blockModels.get(), modID, packOutput));
		}
		if (itemModels != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenModelsItem(itemModels.get(), modID, packOutput));
		}
		if (particles != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenParticles(particles.get(), modID, packOutput));
		}
		if (sounds != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenSounds(sounds.get(), modID, packOutput));
		}

		if (damageTypes != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenDamageType(damageTypes.get(), modID, packOutput));
		}
		if (dimensions != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenDimension(dimensions.get(), modID, packOutput));
		}
		if (dimensionTypes != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenDimensionType(dimensionTypes.get(), modID, packOutput));
		}
		if (biomeModifiers != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenForgeBiomeModifier(biomeModifiers.get(), modID, packOutput));
		}
		if (biomes != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenWorldgenBiome(biomes.get(), modID, packOutput));
		}
		if (configuredFeatures != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenWorldgenConfiguredFeature(configuredFeatures.get(), modID, packOutput));
		}
		if (densityFunctions != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenWorldgenDensityFunction(densityFunctions.get(), modID, packOutput));
		}
		if (noiseParameters != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenWorldgenNoise(noiseParameters.get(), modID, packOutput));
		}
		if (noiseGeneratorSettings != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenWorldgenNoiseSettings(noiseGeneratorSettings.get(), modID, packOutput));
		}
		if (placedFeatures != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenWorldgenPlacedFeature(placedFeatures.get(), modID, packOutput));
		}
		if (structures != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenWorldgenStructure(structures.get(), modID, packOutput));
		}
		if (structureSets != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenWorldgenStructureSet(structureSets.get(), modID, packOutput));
		}
		if (structureTemplatePools != null) {
			generator.addProvider(eventIn.includeClient(), new TLReGenWorldgenTemplatePool(structureTemplatePools.get(), modID, packOutput));
		}

		dataProviders.forEach((dataProviderSupplier) -> {
			DataProvider dataProvider = dataProviderSupplier.get();
			generator.addProvider(eventIn.includeServer(), dataProvider);
			if (dataProvider instanceof TLReGenTagsBlocks) {
				TagBlocks = (TLReGenTagsBlocks) dataProvider;
			}
		});
	}
}
