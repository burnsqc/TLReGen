package com.tlregen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(TLReGen.MOD_ID)
public class TLReGen {
	public static final String MOD_ID = "tlregen";
	public static final Logger LOGGER = LogManager.getLogger("TLREGEN");
	public static final Marker LOADING = MarkerManager.getMarker("LOADING");

	public TLReGen() {
		LOGGER.info(LOADING, "LOAD STARTING");
		LOGGER.info(LOADING, "DISTRIBUTION: " + FMLEnvironment.dist.toString());
	}
}
