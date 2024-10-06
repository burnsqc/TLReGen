package com.tlregen.api.resourcegen.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.tlregen.TLReGen;
import com.tlregen.api.resourcegen.TLReGenMasterResourceGenerator;
import com.tlregen.api.resourcegen.mirrors.TLReGenRegistrySetBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
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
import net.minecraftforge.registries.DataPackRegistriesHooks;

public abstract class TLReGenDimension extends TLReGenMasterResourceGenerator implements DataProvider {
	private final CompletableFuture<HolderLookup.Provider> damageTypes = lookupProvider.thenApply(r -> constructRegistries(r, new TLReGenRegistrySetBuilder().add(Registries.LEVEL_STEM, null)));
	private final java.util.function.Predicate<String> namespacePredicate = Set.of(modid) == null ? namespace -> true : Set.of(modid)::contains;
	private static BootstapContext<LevelStem> dimension;

	@Override
	public CompletableFuture<?> run(final CachedOutput cache) {
		return damageTypes.thenCompose((p_256533_) -> {
			DynamicOps<JsonElement> dynamicops = RegistryOps.create(JsonOps.INSTANCE, p_256533_);
			return CompletableFuture.allOf(net.minecraftforge.registries.DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().flatMap((p_256552_) -> {
				return dumpRegistryCap(cache, p_256533_, dynamicops, p_256552_).stream();
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
	public final String getName() {
		return "data." + TLReGen.MOD_ID + ".dimension";
	}

	private static HolderLookup.Provider constructRegistries(HolderLookup.Provider original, TLReGenRegistrySetBuilder datapackEntriesBuilder) {
		var builderKeys = new HashSet<>(datapackEntriesBuilder.getEntryKeys());
		DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().filter(data -> !builderKeys.contains(data.key())).forEach(data -> datapackEntriesBuilder.add(data.key(), context -> {
		}));
		return datapackEntriesBuilder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), original);
	}

	protected static void setBootstrapContext(BootstapContext<LevelStem> bootstrapContextIn) {
		dimension = bootstrapContextIn;
	}

	/*
	 * HELPER METHODS
	 */

	protected static LevelStem dimension(ResourceKey<DimensionType> type, ChunkGenerator generator) {
		return new LevelStem(dimension.lookup(Registries.DIMENSION_TYPE).getOrThrow(type), generator);
	}

	/*
	 * TEMPLATES
	 */

	protected static ChunkGenerator chunkGenerator(String type, BiomeSource biomeSource, ResourceKey<NoiseGeneratorSettings> settings) {
		return new NoiseBasedChunkGenerator(biomeSource, dimension.lookup(Registries.NOISE_SETTINGS).getOrThrow(settings));
	}

	protected static class BiomeSourceBuilder {
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

	protected static Pair<Climate.ParameterPoint, Holder<Biome>> biome(ResourceKey<Biome> biome, Climate.ParameterPoint parameters) {
		final HolderGetter<Biome> biomesGetter = dimension.lookup(Registries.BIOME);
		return Pair.of(parameters, biomesGetter.getOrThrow(biome));
	}

	protected static Climate.ParameterPoint parameters(float continentalness, float depth, float erosion, float humidity, float offset, float temperature, float weirdness) {
		return Climate.parameters(temperature, humidity, continentalness, erosion, depth, weirdness, offset);
	}
}