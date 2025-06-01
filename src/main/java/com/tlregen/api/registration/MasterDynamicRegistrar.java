package com.tlregen.api.registration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.tlregen.api.resourcegen.MasterResourceGenerator;
import com.tlregen.api.resourcegen.data.TLReGenForgeBiomeModifier;
import com.tlregen.api.resourcegen.data.worldgen.TLReGenWorldgenBiome;
import com.tlregen.api.resourcegen.util.TLReGenRegistrySetBuilder;
import com.tlregen.api.resourcegen.util.helpers.TLReGenDimensionHelper;
import com.tlregen.util.TextUtil;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

public class MasterDynamicRegistrar {
	private String modID;
	private String modMarker;
	private IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
	private static final Logger LOGGER = LogManager.getLogger("TLREGEN");
	private static final Marker REGISTRATION = MarkerManager.getMarker("REGISTRATION-DYNAMIC");
	private Map<ResourceKey<? extends Registry<?>>, RegistrationTracker<?>> registries = new HashMap<>();
	private TLReGenRegistrySetBuilder regset = new TLReGenRegistrySetBuilder();
	
	public MasterDynamicRegistrar(String modid) {
		modID = modid;
		modMarker = "(" + TextUtil.stringToAllCapsName(modid) + ")";
		LOGGER.info(REGISTRATION, modMarker + " NEW MASTER DYNAMIC REGISTRAR CONSTRUCTED");
		modBus.register(this);
	}

	public <R> DynamicRegister<R> addRegister(ResourceKey<? extends Registry<R>> key, Supplier<ResourceKey<?>> bootstrapRegistryObject) {
		DynamicRegister<R> dynamicRegister = DynamicRegister.create(key, modID);
		registries.put(key, new RegistrationTracker<R>(dynamicRegister, bootstrapRegistryObject, 0, null));
		//regset.add((ResourceKey<Biome>)key, TLReGenWorldgenBiome::bootstrap);
		LOGGER.info(REGISTRATION, modMarker + " " + TextUtil.stringToAllCapsName(key.location().toString()) + " DYNAMIC REGISTER ADDED");
		return dynamicRegister;
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	protected final <R> void setBootstrap(final GatherDataEvent event) {
		LOGGER.info(REGISTRATION, modMarker + " INITIALIZATION STARTING");
		registries.forEach((dynamicRegistry, counter) -> {
			regset.add((ResourceKey<? extends Registry<R>>) dynamicRegistry, s -> bootstrap(s));
			//BootstapContext<?> bc = new BootstapContext<>();
			counter.bootstrap.get();
			counter.initialized = counter.dynamicRegister.getEntries().size();
			LOGGER.info(REGISTRATION, modMarker + " " + TextUtil.stringToAllCapsName(dynamicRegistry.location().toString()) + " INITIALIZED " + counter.initialized);
		});
		LOGGER.info(REGISTRATION, modMarker + " INITIALIZATION COMPLETE");
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	protected final <R> void bindValues(final GatherDataEvent event) {
		LOGGER.info(REGISTRATION, modMarker + " BINDING STARTING");
		registries.forEach((dynamicRegistry, counter) -> {
			//regset.add((ResourceKey<? extends Registry<R>>)dynamicRegistry, (TLReGenRegistrySetBuilder.RegistryBootstrap<R>)s -> bootstrap(s, counter));
			MasterResourceGenerator.lookupProvider.thenApply(holderLookupProvider -> constructRegistries(holderLookupProvider, regset));
		});
		LOGGER.info(REGISTRATION, modMarker + " BINDING COMPLETE");
	}
	
	private static HolderLookup.Provider constructRegistries(HolderLookup.Provider original, TLReGenRegistrySetBuilder datapackEntriesBuilder) {
		var builderKeys = new HashSet<>(datapackEntriesBuilder.getEntryKeys());
		DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().filter(registryData -> !builderKeys.contains(registryData.key())).forEach(data -> datapackEntriesBuilder.add(data.key(), context -> {
		}));
		return datapackEntriesBuilder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), original);
	}
	
	public static <R> void bootstrap(final BootstapContext<?> s) {
		TLReGenDimensionHelper.bootsc = s;
		//s.run();
		//counter.dynamicRegister.getEntries().forEach((k, v) -> ((BootstapContext<R>)s).register((ResourceKey<R>)k, ((Supplier<R>)v).get()));
	}
	
	public static <R> void setBootstrap(final BootstapContext<R> s, RegistrationTracker<R> counter) {
		counter.bootstrapContext = s;
	}


	private static class RegistrationTracker<R> {
		DynamicRegister<R> dynamicRegister;
		Supplier<ResourceKey<?>> bootstrap;
		long initialized;
		public BootstapContext<R> bootstrapContext;
		
		public RegistrationTracker(DynamicRegister<R> dynamicRegister, Supplier<ResourceKey<?>> bootstrap, long initialized, BootstapContext<R> bootstrapContext) {
			this.dynamicRegister = dynamicRegister;
			this.bootstrap = bootstrap;
			this.initialized = initialized;
			this.bootstrapContext = bootstrapContext;
		}
	}
}
