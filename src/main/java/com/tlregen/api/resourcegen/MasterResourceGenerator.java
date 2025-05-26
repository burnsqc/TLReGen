package com.tlregen.api.resourcegen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.tlregen.api.resourcegen.data.tags.TLReGenTagsBlocks;
import com.tlregen.api.resourcegen.util.TLReGenModels;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class MasterResourceGenerator {
	private static GatherDataEvent event;
	private static DataGenerator generator;
	public static PackOutput packOutput;
	public static ExistingFileHelper helper;
	public static CompletableFuture<HolderLookup.Provider> lookupProvider;
	public static String modID;
	protected final DynamicOps<JsonElement> dynamicOps = JsonOps.INSTANCE;

	public static TLReGenTagsBlocks TagBlocks;
	public TLReGenModels models;

	private Set<Supplier<TLReGenAssetProvider>> assetProviders = Collections.emptySet();
	private Set<Supplier<DataProvider>> dataProviders = Collections.emptySet();

	public MasterResourceGenerator(String modid) {
		modID = modid;
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		models = new TLReGenModels();
	}

	public MasterResourceGenerator() {
	}

	public void addAssetProvider(Supplier<TLReGenAssetProvider> provider) {
		if (assetProviders.isEmpty()) {
			assetProviders = new LinkedHashSet<Supplier<TLReGenAssetProvider>>(new ArrayList<Supplier<TLReGenAssetProvider>>(Arrays.asList(provider)));
		} else {
			assetProviders.add(provider);
		}
	}

	public void addDataProvider(Supplier<DataProvider> provider) {
		if (dataProviders.isEmpty()) {
			dataProviders = new LinkedHashSet<Supplier<DataProvider>>(new ArrayList<Supplier<DataProvider>>(Arrays.asList(provider)));
		} else {
			dataProviders.add(provider);
		}
	}

	@SubscribeEvent
	public void addGenerators(final GatherDataEvent eventIn) {
		event = eventIn;
		generator = event.getGenerator();
		packOutput = generator.getPackOutput();
		helper = event.getExistingFileHelper();
		lookupProvider = event.getLookupProvider();

		models.modID = modID;
		models.helper = helper;

		assetProviders.forEach((assetProviderSupplier) -> {
			TLReGenAssetProvider assetProvider = assetProviderSupplier.get();
			assetProvider.modID = modID;
			assetProvider.packOutput = packOutput;
			assetProvider.helper = helper;
			assetProvider.models = models;
			generator.addProvider(event.includeClient(), assetProvider);
		});
		dataProviders.forEach((dataProviderSupplier) -> {
			DataProvider dataProvider = dataProviderSupplier.get();
			if (dataProvider instanceof TLReGenDataProvider) {
				TLReGenDataProvider assetProvider = (TLReGenDataProvider) dataProvider;
				assetProvider.modID = modID;
				assetProvider.packOutput = packOutput;
				assetProvider.helper = helper;
			}
			generator.addProvider(event.includeServer(), dataProvider);
			if (dataProvider instanceof TLReGenTagsBlocks) {
				TagBlocks = (TLReGenTagsBlocks) dataProvider;
			}
		});
	}
}
