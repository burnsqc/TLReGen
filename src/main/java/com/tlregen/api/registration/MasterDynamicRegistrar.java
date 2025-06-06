package com.tlregen.api.registration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.tlregen.TLReGen;
import com.tlregen.util.TextUtil;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class MasterDynamicRegistrar {
	private static final Marker REGISTRATION = MarkerManager.getMarker("REGISTRATION-DYNAMIC");
	private String modID;
	private String modMarker;
	private Map<ResourceKey<? extends Registry<?>>, RegistrationTracker<?>> registries = new HashMap<>();

	public MasterDynamicRegistrar(String modid) {
		modID = modid;
		modMarker = "(" + TextUtil.stringToAllCapsName(modid) + ")";
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		TLReGen.LOGGER.info(REGISTRATION, modMarker + " NEW MASTER DYNAMIC REGISTRAR CONSTRUCTED");
	}

	public <R> DynamicRegister<R> addRegister(ResourceKey<? extends Registry<R>> key, Supplier<ResourceKey<?>> bootstrapRegistryObject) {
		DynamicRegister<R> dynamicRegister = DynamicRegister.create(key, modID);
		registries.put(key, new RegistrationTracker<R>(dynamicRegister, bootstrapRegistryObject, 0));
		TLReGen.LOGGER.info(REGISTRATION, modMarker + " " + TextUtil.stringToAllCapsName(key.location().toString()) + " DYNAMIC REGISTER ADDED");
		return dynamicRegister;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	protected final void bindValues(final GatherDataEvent event) {
		TLReGen.LOGGER.info(REGISTRATION, modMarker + " INITIALIZATION STARTING");
		registries.forEach((reg, counter) -> {
			counter.bootstrap.get();
			counter.initialized = counter.dynamicRegister.getEntries().size();
			TLReGen.LOGGER.info(REGISTRATION, modMarker + " " + TextUtil.stringToAllCapsName(reg.location().toString()) + " INITIALIZED " + counter.initialized);
		});
		TLReGen.LOGGER.info(REGISTRATION, modMarker + " INITIALIZATION COMPLETE");
	}

	private static class RegistrationTracker<R> {
		DynamicRegister<R> dynamicRegister;
		Supplier<ResourceKey<?>> bootstrap;
		long initialized;

		public RegistrationTracker(DynamicRegister<R> dynamicRegister, Supplier<ResourceKey<?>> bootstrap, long initialized) {
			this.dynamicRegister = dynamicRegister;
			this.bootstrap = bootstrap;
			this.initialized = initialized;
		}
	}
}
