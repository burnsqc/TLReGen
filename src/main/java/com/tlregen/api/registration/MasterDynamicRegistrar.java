package com.tlregen.api.registration;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.tlregen.TLReGen;
import com.tlregen.util.TextUtil;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * This class handles Dynamic Registration. Create an instance of this class in your mod and reference the instance as needed.
 */
public class MasterDynamicRegistrar {
	private static final Marker REGISTRATION = MarkerManager.getMarker("REGISTRATION-DYNAMIC");
	private String modID;
	private String modMarker;
	private Map<ResourceKey<? extends Registry<?>>, RegistrationTracker<?>> registries = new HashMap<>();

	/**
	 * Construct a new instance of MasterDynamicRegistrar.
	 * <p>Example call:<br>
	 * {@code
	 * private static final MasterDynamicRegistrar MY_MASTER_DYNAMIC_REGISTRAR = new MasterDynamicRegistrar(MOD_ID); 
	 * }
	 * 
	 * @param modid The mod id for your mod. This is likely already declared in your main mod class for a variety of other purposes.
	 */
	public MasterDynamicRegistrar(String modid) {
		modID = modid;
		modMarker = "(" + TextUtil.stringToAllCapsName(modid) + ")";
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		TLReGen.LOGGER.info(REGISTRATION, modMarker + " NEW MASTER DYNAMIC REGISTRAR CONSTRUCTED");
	}

	/**
	 * Add a new {@link DynamicRegister} to an instance of {@link MasterDynamicRegistrar}.
	 * <p>Example call:<br>
	 * {@code
	 * public static final DynamicRegister<DamageType> MY_DAMAGE_TYPES = MY_MASTER_DYNAMIC_REGISTRAR.addRegister(Registries.DAMAGE_TYPE, MyDamageTypes.class);
	 * }
	 * <p>The mod id associated with the Dynamic Register is assumed to be the same mod id specified when constructing the Master Dynamic Registrar instance.
	 * 
	 * @param <R>              The type of Dynamic Register.
	 * @param key              The {@link ResourceKey} for the Dynamic Register. Recommended keys can be found in {@link ForgeRegistries.Keys} or {@link Registries}.
	 * @param resourceKeyClass The {@link Class} where you declare your Resource Keys which belong to this Dynamic Register. This removes the need to bootstrap or otherwise initialize your class containing Resource Keys. The intent of this is to allow you to create classes which literally only contain Resource Key declarations.
	 * 
	 * @return A new Dynamic Register to be used throughout your mod.
	 *         <p>Example usage of returned Dynamic Register:<br>
	 *         {@code
	 * public static final ResourceKey<DamageType> BLEED = MY_DAMAGE_TYPES.register("bleed", () -> new DamageType("bleed", 0.1F));
	 * 		   }
	 */
	public <R> DynamicRegister<R> addRegister(ResourceKey<? extends Registry<R>> key, Class<?> resourceKeyClass) {
		DynamicRegister<R> dynamicRegister = DynamicRegister.create(key, modID);
		registries.put(key, new RegistrationTracker<R>(dynamicRegister, resourceKeyClass, 0));
		TLReGen.LOGGER.info(REGISTRATION, modMarker + " " + TextUtil.stringToAllCapsName(key.location().toString()) + " DYNAMIC REGISTER ADDED");
		return dynamicRegister;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	protected final void initDynamicRegisters(final GatherDataEvent event) {
		TLReGen.LOGGER.info(REGISTRATION, modMarker + " INITIALIZATION STARTING");
		registries.forEach((reg, registrationTracker) -> {
			try {
				@SuppressWarnings("unused")
				Class<?> resourceKeyClass = Class.forName(registrationTracker.resourceKeyClass.getCanonicalName());
			} catch (ClassNotFoundException e) {
				throw new RegistrationException("REGISTRY OBJECTS NOT LOADED: CLASS NOT FOUND");
			}
			registrationTracker.initialized = registrationTracker.dynamicRegister.getEntries().size();
			TLReGen.LOGGER.info(REGISTRATION, modMarker + " " + TextUtil.stringToAllCapsName(reg.location().toString()) + " INITIALIZED " + registrationTracker.initialized);
		});
		TLReGen.LOGGER.info(REGISTRATION, modMarker + " INITIALIZATION COMPLETE");
	}

	private static class RegistrationTracker<R> {
		DynamicRegister<R> dynamicRegister;
		Class<?> resourceKeyClass;
		long initialized;

		public RegistrationTracker(DynamicRegister<R> dynamicRegister, Class<?> resourceKeyClass, long initialized) {
			this.dynamicRegister = dynamicRegister;
			this.resourceKeyClass = resourceKeyClass;
			this.initialized = initialized;
		}
	}
}
