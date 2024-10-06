package com.tlregen.api.resourcegen.data.worldgen;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.tlregen.api.resourcegen.TLReGenMasterResourceGenerator;
import com.tlregen.api.resourcegen.mirrors.TLReGenRegistrySetBuilder;

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
import net.minecraftforge.registries.DataPackRegistriesHooks;

public abstract class TLReGenWorldgenConfiguredFeature extends TLReGenMasterResourceGenerator implements DataProvider {
	private final CompletableFuture<HolderLookup.Provider> damageTypes = lookupProvider.thenApply(r -> constructRegistries(r, new TLReGenRegistrySetBuilder().add(Registries.CONFIGURED_FEATURE, (TLReGenRegistrySetBuilder.RegistryBootstrap) null)));
	private final java.util.function.Predicate<String> namespacePredicate = Set.of(modid) == null ? namespace -> true : Set.of(modid)::contains;

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
		return "data." + modid + ".worldgen.configured_feature";
	}

	private static HolderLookup.Provider constructRegistries(HolderLookup.Provider original, TLReGenRegistrySetBuilder datapackEntriesBuilder) {
		var builderKeys = new HashSet<>(datapackEntriesBuilder.getEntryKeys());
		DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().filter(data -> !builderKeys.contains(data.key())).forEach(data -> datapackEntriesBuilder.add(data.key(), context -> {
		}));
		return datapackEntriesBuilder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), original);
	}
}