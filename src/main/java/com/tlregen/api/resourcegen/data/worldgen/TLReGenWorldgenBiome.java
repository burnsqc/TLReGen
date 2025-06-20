package com.tlregen.api.resourcegen.data.worldgen;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.tlregen.api.registration.DynamicRegister;
import com.tlregen.api.resourcegen.MasterResourceGenerator;
import com.tlregen.api.resourcegen.TLReGenResourceGenerator;
import com.tlregen.api.resourcegen.util.TLReGenRegistrySetBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.DataPackRegistriesHooks;

public class TLReGenWorldgenBiome extends TLReGenResourceGenerator {
	public static BootstapContext<Biome> bootstrapContext;
	public static DynamicRegister<Biome> dynamicRegister;
	public TLReGenRegistrySetBuilder regset = new TLReGenRegistrySetBuilder().add(Registries.BIOME, TLReGenWorldgenBiome::bootstrap);

	public TLReGenWorldgenBiome(DynamicRegister<Biome> dynReg, String modID, PackOutput packOutput) {
		super(modID, Types.BIOME, packOutput);
		dynamicRegister = dynReg;
	}

	@Override
	public CompletableFuture<?> run(final CachedOutput cache) {
		return MasterResourceGenerator.lookupProvider.thenApply(r -> constructRegistries(r, regset)).thenCompose((provider) -> {
			DynamicOps<JsonElement> dynamicops = RegistryOps.create(dynamicOps, provider);
			return CompletableFuture.allOf(DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().flatMap((registryData) -> dumpRegistryCap(cache, provider, dynamicops, registryData).stream()).toArray(CompletableFuture[]::new));
		});
	}

	private <T> Optional<CompletableFuture<?>> dumpRegistryCap(CachedOutput cache, HolderLookup.Provider lookupProvider, DynamicOps<JsonElement> dynamicops, RegistryDataLoader.RegistryData<T> registryData) {
		return lookupProvider.lookup(registryData.key()).map((registryLookup) -> {
			return CompletableFuture.allOf(registryLookup.listElements().map((reference) -> {
				JsonObject json = registryData.elementCodec().encodeStart(dynamicops, reference.value()).getOrThrow(false, msg -> LOGGER.error("Failed to encode")).getAsJsonObject();
				return DataProvider.saveStable(cache, json, pathProvider.json(reference.key().location()));
			}).toArray(CompletableFuture[]::new));
		});
	}

	private static HolderLookup.Provider constructRegistries(HolderLookup.Provider original, TLReGenRegistrySetBuilder datapackEntriesBuilder) {
		return datapackEntriesBuilder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), original);
	}

	private static void bootstrap(final BootstapContext<Biome> bootstrapContextIn) {
		bootstrapContext = bootstrapContextIn;
		dynamicRegister.bootstrapContext = bootstrapContextIn;
		dynamicRegister.getEntries().forEach((k, v) -> bootstrapContext.register(k, v.get()));
	}
}
