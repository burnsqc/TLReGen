package com.tlregen.util;

import com.tlregen.TLReGen;

import net.minecraft.resources.ResourceLocation;

/**
 * Simple extension of ResourceLocation to keep Technologica Resource Locations concise and obvious. 
 */
public class TechnologicaLocation extends ResourceLocation {
	public TechnologicaLocation(String path) {
		super(TLReGen.MOD_ID, path);
	}
}