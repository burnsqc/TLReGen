package com.tlregen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.tlregen.api.registration.MasterDeferredRegistrar;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(TLReGen.MOD_ID)
public class TLReGen {
	public static final String MOD_ID = "tlregen";
	public static final Logger LOGGER = LogManager.getLogger("TLREGEN");
	public static final Marker LOADING = MarkerManager.getMarker("LOADING");
	private static final MasterDeferredRegistrar mdr = new MasterDeferredRegistrar(MOD_ID);
	public static final DeferredRegister<Attribute> ATTRIBUTES = mdr.addRegister(ForgeRegistries.Keys.ATTRIBUTES, () -> null);

	public TLReGen() {
		LOGGER.info(LOADING, "LOAD STARTING");
		LOGGER.info(LOADING, "DISTRIBUTION: " + FMLEnvironment.dist.toString());
	}
}
