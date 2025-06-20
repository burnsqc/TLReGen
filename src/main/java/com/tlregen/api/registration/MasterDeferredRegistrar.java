package com.tlregen.api.registration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.tlregen.TLReGen;
import com.tlregen.util.TextUtil;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

/**
 * This class handles Deferred Registration. Create an instance of this class in your mod and reference the instance as needed.
 */
public class MasterDeferredRegistrar {
	private static final Marker REGISTRATION = MarkerManager.getMarker("REGISTRATION-DEFERRED");
	private String modID;
	private String modMarker;
	private Map<ResourceKey<? extends Registry<?>>, RegistrationTracker<?>> registries = new HashMap<>();
	private boolean firstRegistrationEvent = true;

	/**
	 * Construct a new instance of MasterDeferredRegistrar.
	 * <p>Example call:<br>
	 * {@code
	 * private static final MasterDeferredRegistrar MY_MASTER_DEFERRED_REGISTRAR = new MasterDeferredRegistrar(MOD_ID); 
	 * }
	 * 
	 * @param modid The mod id for your mod. This is likely already declared in your main mod class for a variety of other purposes.
	 */
	public MasterDeferredRegistrar(String modid) {
		modID = modid;
		modMarker = "(" + TextUtil.stringToAllCapsName(modid) + ")";
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		TLReGen.LOGGER.info(REGISTRATION, modMarker + " NEW MASTER DEFERRED REGISTRAR CONSTRUCTED");
	}

	/**
	 * Add a new {@link DeferredRegister} to an instance of {@link MasterDeferredRegistrar}.
	 * <p>Example call:<br>
	 * {@code
	 * public static final DeferredRegister<Block> MY_BLOCKS = MY_MASTER_DEFERRED_REGISTRAR.addRegister(ForgeRegistries.Keys.BLOCKS, MyBlocks.class);
	 * }
	 * <p>The mod id associated with the Deferred Register is assumed to be the same mod id specified when constructing the Master Deferred Registrar instance.
	 * 
	 * @param <R>                  The type of Deferred Register.
	 * @param key                  The {@link ResourceKey} for the Deferred Register. Recommended keys can be found in {@link ForgeRegistries.Keys} or {@link Registries}.
	 * @param registryObjectsClass The {@link Class} where you declare your Registry Objects which belong to this Deferred Register. This removes the need to bootstrap or otherwise initialize your class containing Registry Objects. The intent of this is to allow you to create classes which literally only contain Registry Object declarations.
	 * 
	 * @return A new Deferred Register to be used throughout your mod.
	 *         <p>Example usage of returned Deferred Register:<br>
	 *         {@code
	 * public static final RegistryObject<Block> BLOCK_OF_ALUMINUM = MY_BLOCKS.register("block_of_aluminum", () -> new Block(BlockBehaviour.Properties.of()));
	 * 		   }
	 */
	public <R> DeferredRegister<R> addRegister(ResourceKey<? extends Registry<R>> key, Class<?> registryObjectsClass) {
		DeferredRegister<R> deferredRegister = DeferredRegister.create(key, modID);
		deferredRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
		registries.put(key, new RegistrationTracker<R>(deferredRegister, registryObjectsClass, 0));
		TLReGen.LOGGER.info(REGISTRATION, modMarker + " " + TextUtil.stringToAllCapsName(key.location().toString()) + " DEFERRED REGISTER ADDED");
		return deferredRegister;
	}

	/**
	 * This event listener method listens for {@link FMLConstructModEvent} and initializes all added Deferred Registry Objects by using the supplied bootstrapRegistryObject in {@link MasterDeferredRegistrar#addRegister}.
	 * <p>There is no need to call this method; it is registered to the Mod Event Bus in {@link MasterDeferredRegistrar#MasterDeferredRegistrar}.
	 * 
	 * @param event The FMLConstructModEvent that will be intercepted.
	 */
	@SubscribeEvent
	protected final void initDeferredRegisters(final FMLConstructModEvent event) {
		TLReGen.LOGGER.info(REGISTRATION, modMarker + " INITIALIZATION STARTING");
		registries.forEach((registry, registrationTracker) -> {
			try {
				@SuppressWarnings("unused")
				Class<?> registryObjectsClass = Class.forName(registrationTracker.registryObjectsClass.getCanonicalName());
			} catch (ClassNotFoundException e) {
				throw new RegistrationException("REGISTRY OBJECTS NOT LOADED: CLASS NOT FOUND");
			}
			registrationTracker.initialized = registrationTracker.deferredRegister.getEntries().size();
			TLReGen.LOGGER.info(REGISTRATION, modMarker + " " + TextUtil.stringToAllCapsName(registry.location().toString()) + " INITIALIZED " + registrationTracker.initialized);
		});
		TLReGen.LOGGER.info(REGISTRATION, modMarker + " INITIALIZATION COMPLETE");
	}

	/**
	 * This event listener method listens for {@link RegisterEvent} and verifies all added initialized Deferred Registry Objects have been registered.
	 * <p>There is no need to call this method; it is registered to the Mod Event Bus in {@link MasterDeferredRegistrar#MasterDeferredRegistrar}.
	 * 
	 * @param event The RegisterEvent that will be intercepted.
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	protected final void validateDeferredRegisters(final RegisterEvent event) {
		if (firstRegistrationEvent) {
			TLReGen.LOGGER.info(REGISTRATION, modMarker + " REGISTRATION STARTING");
			firstRegistrationEvent = false;
		}
		if (registries.containsKey(event.getRegistryKey())) {
			Stream<Entry<ResourceKey<Object>, Object>> stream = event.getForgeRegistry() != null ? event.getForgeRegistry().getEntries().stream() : event.getVanillaRegistry().entrySet().stream();
			long initialized = registries.get(event.getRegistryKey()).initialized;
			long registered = stream.filter((entry) -> entry.getKey().location().getNamespace() == modID).count();
			TLReGen.LOGGER.info(REGISTRATION, modMarker + " " + TextUtil.stringToAllCapsName(event.getRegistryKey().location().toString()) + " REGISTERED " + registered + " OF " + initialized);

			if (registered < initialized) {
				TLReGen.LOGGER.error(REGISTRATION, "ERROR - " + TextUtil.stringToAllCapsName(event.getRegistryKey().location().toString()) + " - MISSING " + (initialized - registered));
				throw new RegistrationException("REGISTERED ENTRIES LESS THAN INITIALIZED ENTRIES");
			} else if (registered > initialized) {
				TLReGen.LOGGER.error(REGISTRATION, "ERROR - " + TextUtil.stringToAllCapsName(event.getRegistryKey().location().toString()) + " - EXTRANEOUS " + (initialized - registered));
				throw new RegistrationException("REGISTERED ENTRIES GREATER THAN INITIALIZED ENTRIES");
			}
		}
	}

	private static class RegistrationTracker<R> {
		DeferredRegister<R> deferredRegister;
		Class<?> registryObjectsClass;
		long initialized;

		public RegistrationTracker(DeferredRegister<R> deferredRegister, Class<?> registryObjectsClass, long initialized) {
			this.deferredRegister = deferredRegister;
			this.registryObjectsClass = registryObjectsClass;
			this.initialized = initialized;
		}
	}
}
