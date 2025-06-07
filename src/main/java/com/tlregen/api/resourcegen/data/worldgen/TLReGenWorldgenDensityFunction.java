package com.tlregen.api.resourcegen.data.worldgen;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.tlregen.api.registration.DynamicRegister;
import com.tlregen.api.resourcegen.MasterResourceGenerator;
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
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraftforge.registries.DataPackRegistriesHooks;

public class TLReGenWorldgenDensityFunction extends MasterResourceGenerator implements DataProvider {
	public static BootstapContext<DensityFunction> bootstrapContext;
	public static DynamicRegister<DensityFunction> dynamicRegister;

	public TLReGenWorldgenDensityFunction(DynamicRegister<DensityFunction> dynReg) {
		dynamicRegister = dynReg;
	}

	@Override
	public CompletableFuture<?> run(final CachedOutput cache) {
		return lookupProvider.thenApply(r -> constructRegistries(r, new TLReGenRegistrySetBuilder().add(Registries.DENSITY_FUNCTION, TLReGenWorldgenDensityFunction::bootstrap))).thenCompose((provider) -> {
			DynamicOps<JsonElement> dynamicops = RegistryOps.create(dynamicOps, provider);
			return CompletableFuture.allOf(DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().flatMap((registryData) -> dumpRegistryCap(cache, provider, dynamicops, registryData).stream()).toArray(CompletableFuture[]::new));
		});
	}

	private <T> Optional<CompletableFuture<?>> dumpRegistryCap(CachedOutput cache, HolderLookup.Provider lookupProvider, DynamicOps<JsonElement> dynamicops, RegistryDataLoader.RegistryData<T> registryData) {
		return lookupProvider.lookup(registryData.key()).map((registryLookup) -> {
			return CompletableFuture.allOf(registryLookup.listElements().map((reference) -> {
				JsonObject json = registryData.elementCodec().encodeStart(dynamicops, reference.value()).getOrThrow(false, msg -> LOGGER.error("Failed to encode")).getAsJsonObject();
				return DataProvider.saveStable(cache, json, packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "worldgen/density_function").json(reference.key().location()));
			}).toArray(CompletableFuture[]::new));
		});
	}

	private static HolderLookup.Provider constructRegistries(HolderLookup.Provider original, TLReGenRegistrySetBuilder datapackEntriesBuilder) {
		return datapackEntriesBuilder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), original);
	}

	private static void bootstrap(final BootstapContext<DensityFunction> bootstrapContextIn) {
		bootstrapContext = bootstrapContextIn;
		dynamicRegister.getEntries().forEach((k, v) -> bootstrapContext.register(k, v.get()));
	}

	@Override
	public final String getName() {
		return "data." + modID + ".worldgen.density_function";
	}
}
