package com.tlregen.api.resourcegen;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.tlregen.TLReGen;
import com.tlregen.api.resourcegen.assets.TLReGenBlockstates;
import com.tlregen.api.resourcegen.assets.TLReGenLang;
import com.tlregen.api.resourcegen.assets.TLReGenModelsItem;
import com.tlregen.api.resourcegen.assets.TLReGenParticles;
import com.tlregen.api.resourcegen.assets.TLReGenSounds;
import com.tlregen.api.resourcegen.data.TLRGRecipeGenerator;
import com.tlregen.api.resourcegen.data.TLReGenDamageType;
import com.tlregen.api.resourcegen.data.TLReGenDimension;
import com.tlregen.api.resourcegen.data.TLReGenDimensionType;
import com.tlregen.api.resourcegen.data.tags.TLRGTagsItemsGenerator;
import com.tlregen.api.resourcegen.data.tags.TLReGenTagsBlocks;
import com.tlregen.api.resourcegen.data.tags.TLReGenTagsEntityTypes;
import com.tlregen.api.resourcegen.mirrors.TLReGenRegistrySetBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod.EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public abstract class TLReGenMasterResourceGenerator implements DataProvider {
	private static GatherDataEvent event;
	private static DataGenerator generator;
	public static PackOutput packOutput;
	public static ExistingFileHelper helper;
	public static CompletableFuture<HolderLookup.Provider> lookupProvider;
	public static RegistrySetBuilder registrySetBuilder;
	public static TLReGenRegistrySetBuilder registrySetBuilder2;
	public static TLReGenRegistrySetBuilder registrySetBuilder3;
	public static String modid = TLReGen.MOD_ID;
	protected final DynamicOps<JsonElement> dynamicOps = JsonOps.INSTANCE;

	protected static Supplier<TLReGenBlockstates> BlockStateGenerator;
	protected static Supplier<TLReGenLang> LanguageGenerator;
	protected static Supplier<TLReGenModelsItem> ModelItemGenerator;
	protected static Supplier<TLReGenParticles> ParticleGenerator;
	protected static Supplier<TLReGenSounds> SoundsGenerator;

	// protected static Supplier<TLRGAdvancementGenerator> AdvancementGenerator;
	protected static Supplier<TLReGenDamageType> DamageTypeGenerator;
	protected static Supplier<TLReGenDimension> DimensionGenerator;
	protected static Supplier<TLReGenDimensionType> DimensionTypeGenerator;
	protected static Supplier<TLRGRecipeGenerator> RecipeGenerator;
	protected static Supplier<TLReGenTagsBlocks> TagBlocksGenerator;
	public static TLReGenTagsBlocks TagBlocks;
	protected static Supplier<TLReGenTagsEntityTypes> TagEntityTypeGenerator;
	protected static Supplier<TLRGTagsItemsGenerator> TagItemGenerator;

	@SubscribeEvent
	public static void addGenerators(final GatherDataEvent eventIn) {
		setup(eventIn);
		setGenerators();
		addGenerators();
	}

	private static void setup(final GatherDataEvent eventIn) {
		event = eventIn;
		generator = event.getGenerator();
		packOutput = generator.getPackOutput();
		helper = event.getExistingFileHelper();
		lookupProvider = event.getLookupProvider();
		registrySetBuilder = new RegistrySetBuilder();
		registrySetBuilder2 = new TLReGenRegistrySetBuilder();
		registrySetBuilder3 = new TLReGenRegistrySetBuilder();
	}

	private static void setGenerators() {
	}

	private static void addGenerators() {
	}

	private static void addAssetGenerator(DataProvider provider) {
		generator.addProvider(event.includeClient(), provider);
	}

	private static void addDataGenerator(DataProvider provider) {
		generator.addProvider(event.includeServer(), provider);
	}

	/**
	 * OVERRIDE ME TO ADD RESOURCES
	 */
	protected abstract void populate();

	public static enum ValidationLevel {
		MIN("minimum"), MED("medium"), MAX("maximum");

		final String level;

		private ValidationLevel(String level) {
			this.level = level;
		}
	}

	@Override
	public String getName() {
		return modid;
	}
}
