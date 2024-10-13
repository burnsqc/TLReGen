package com.tlregen.api.registration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.tlregen.TLReGen;
import com.tlregen.util.TextUtil;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

public class MasterDeferredRegistrar {
	private String modID;
	private String modMarker;
	private static final Logger LOGGER = LogManager.getLogger("TLREGEN");
	private static final Marker REGISTRATION = MarkerManager.getMarker("REGISTRATION");
	private Map<ResourceKey<? extends Registry<?>>, RegistrationTracker<?>> registries = new HashMap<>();
	private boolean firstRegistrationEvent = true;

	public MasterDeferredRegistrar(String modid) {
		modID = modid;
		modMarker = "(" + TextUtil.stringToAllCapsName(modid) + ")";
		LOGGER.info(REGISTRATION, modMarker + " NEW MASTER DEFERRED REGISTRAR CONSTRUCTED");
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLConstructModEvent);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterEvent);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLLoadCompleteEvent);
	}

	/**
	 * Add a new {@link DeferredRegister} to an instance of {@link MasterDeferredRegistrar}.
	 * <p>Example call:<br>
	 * {@code
	 * public static final DeferredRegister<Block> MY_BLOCKS = myMasterDeferredRegistrar.addRegister(ForgeRegistries.Keys.BLOCKS, () -> MyBlocks.MY_BLOCK);
	 * }
	 * <p>The mod id associated with the Deferred Register is assumed to be the same mod id specified when constructing the Master Deferred Registrar instance.
	 * 
	 * @param <R>       The type of Deferred Register.
	 * @param key       The {@link ResourceKey} for the Deferred Register. Recommended keys can be found in {@link ForgeRegistries.Keys}.
	 * @param bootstrap A supplier of a {@link RegistryObject}. The bootstrap will later be used to load your class where you store Registry Objects and initialize said objects. This removes the need for the "init" method commonly found in classes with Registry Objects. The intent of this is to allow you to create classes which literally only contain Registry Object declarations.
	 * 
	 * @return A new Deferred Register to be used throughout your mod.
	 *         <p>Example usage of returned Deferred Register:<br>
	 *         {@code
	 * public static final RegistryObject<Block> BLOCK_OF_ALUMINUM = MY_BLOCKS.register("block_of_aluminum", () -> new Block(BlockBehaviour.Properties.of()));
	 * 		   }
	 */
	public <R> DeferredRegister<R> addRegister(ResourceKey<? extends Registry<R>> key, Supplier<RegistryObject<?>> bootstrap) {
		DeferredRegister<R> deferredRegister = DeferredRegister.create(key, modMarker);
		deferredRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
		registries.put(key, new RegistrationTracker<R>(deferredRegister, bootstrap, 0, 0));
		LOGGER.info(REGISTRATION, modMarker + " " + TextUtil.stringToAllCapsName(key.location().toString()) + " DEFERRED REGISTER ADDED");
		return deferredRegister;
	}

	private void initDeferredRegisters() {
		LOGGER.info(REGISTRATION, modMarker + " INITIALIZATION STARTING");
		registries.forEach((reg, counter) -> {
			counter.bootstrap.get();
			counter.initialized = counter.deferredRegister.getEntries().size();
			LOGGER.info(REGISTRATION, modMarker + " " + TextUtil.stringToAllCapsName(reg.location().toString()) + " INITIALIZED " + counter.initialized);
		});
		LOGGER.info(REGISTRATION, modMarker + " INITIALIZATION COMPLETE");
	}

	@SubscribeEvent
	public final void onRegisterEvent(final RegisterEvent event) {
		if (firstRegistrationEvent) {
			LOGGER.info(REGISTRATION, modMarker + " REGISTRATION STARTING");
			firstRegistrationEvent = false;
		}
		try {
			LOGGER.info(REGISTRATION, TextUtil.stringToAllCapsName(event.getRegistryKey().location().toString()));
			if (registries.containsKey(event.getRegistryKey())) {
				Stream<Entry<ResourceKey<Object>, Object>> stream = event.getForgeRegistry() != null ? event.getForgeRegistry().getEntries().stream() : event.getVanillaRegistry().entrySet().stream();
				long initialized = registries.get(event.getRegistryKey()).initialized;
				long registered = stream.filter((entry) -> entry.getKey().location().getNamespace() == modID).count();
				LOGGER.info(REGISTRATION, modMarker + " " + TextUtil.stringToAllCapsName(event.getRegistryKey().location().toString()) + " REGISTERED " + registered + " OF " + initialized);

				if (registered < initialized) {
					LOGGER.error(REGISTRATION, "ERROR - " + TextUtil.stringToAllCapsName(event.getRegistryKey().location().toString()) + " - MISSING " + (initialized - registered));
					throw new RegistrationException("REGISTERED ENTRIES LESS THAN INITIALIZED ENTRIES");
				} else if (registered > initialized) {
					LOGGER.error(REGISTRATION, "ERROR - " + TextUtil.stringToAllCapsName(event.getRegistryKey().location().toString()) + " - EXTRANEOUS " + (initialized - registered));
					throw new RegistrationException("REGISTERED ENTRIES GREATER THAN INITIALIZED ENTRIES");
				}
			}
		} catch (RegistrationException e) {
			LOGGER.info(REGISTRATION, "UNREGISTERING REGISTEREVENT LISTENER DUE TO ERRORS");
			FMLJavaModLoadingContext.get().getModEventBus().unregister(MasterDeferredRegistrar.class);
			// throw new RegistrationException("REGISTRATION ERROR", e);
		}
	}

	@SubscribeEvent
	public final void onFMLConstructModEvent(final FMLConstructModEvent event) {
		LOGGER.info(TLReGen.LOADING, "TLREGEN CONSTRUCT");
		initDeferredRegisters();
	}

	@SubscribeEvent
	public final void onFMLLoadCompleteEvent(final FMLLoadCompleteEvent event) {
		LOGGER.info(TLReGen.LOADING, "LOAD COMPLETE");
	}

	public static class RegistrationTracker<R> {
		DeferredRegister<R> deferredRegister;
		Supplier<RegistryObject<?>> bootstrap;
		long initialized;
		long registered;

		public RegistrationTracker(DeferredRegister<R> deferredRegister, Supplier<RegistryObject<?>> bootstrap, long initialized, long registered) {
			this.deferredRegister = deferredRegister;
			this.bootstrap = bootstrap;
			this.initialized = initialized;
			this.registered = registered;
		}
	}
}
