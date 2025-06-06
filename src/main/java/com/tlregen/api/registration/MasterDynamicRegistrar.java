package com.tlregen.api.registration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.tlregen.util.TextUtil;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class MasterDynamicRegistrar {
	private String modID;
	private String modMarker;
	private IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
	private static final Logger LOGGER = LogManager.getLogger("TLREGEN");
	private static final Marker REGISTRATION = MarkerManager.getMarker("REGISTRATION-DYNAMIC");
	private Map<ResourceKey<? extends Registry<?>>, RegistrationTracker<?>> registries = new HashMap<>();

	public MasterDynamicRegistrar(String modid) {
		modID = modid;
		modMarker = "(" + TextUtil.stringToAllCapsName(modid) + ")";
		LOGGER.info(REGISTRATION, modMarker + " NEW MASTER DYNAMIC REGISTRAR CONSTRUCTED");
		modBus.register(this);
	}

	public <R> DynamicRegister<R> addRegister(ResourceKey<? extends Registry<R>> key, Supplier<ResourceKey<?>> bootstrapRegistryObject) {
		DynamicRegister<R> dynamicRegister = DynamicRegister.create(key, modID);
		registries.put(key, new RegistrationTracker<R>(dynamicRegister, bootstrapRegistryObject, 0));
		LOGGER.info(REGISTRATION, modMarker + " " + TextUtil.stringToAllCapsName(key.location().toString()) + " DYNAMIC REGISTER ADDED");
		return dynamicRegister;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	protected final void bindValues(final GatherDataEvent event) {
		LOGGER.info(REGISTRATION, modMarker + " INITIALIZATION STARTING");
		registries.forEach((reg, counter) -> {
			counter.bootstrap.get();
			counter.initialized = counter.dynamicRegister.getEntries().size();
			LOGGER.info(REGISTRATION, modMarker + " " + TextUtil.stringToAllCapsName(reg.location().toString()) + " INITIALIZED " + counter.initialized);
		});
		LOGGER.info(REGISTRATION, modMarker + " INITIALIZATION COMPLETE");

		// LOGGER.info(REGISTRATION, modMarker + " BINDING STARTING");
		// registries.forEach((reg, counter) -> counter.dynamicRegister.getEntries().forEach((key, value) -> {
		// if (value != null) {
		// value.first.bindValue(value.second.get());
		// } else {
		// LOGGER.error(REGISTRATION, modMarker + " BINDING ERROR" + key);
		// }
		// }));
		// LOGGER.info(REGISTRATION, modMarker + " BINDING COMPLETE");
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
