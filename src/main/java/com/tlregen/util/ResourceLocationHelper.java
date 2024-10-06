package com.tlregen.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class ResourceLocationHelper {

	public static String getPath(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block).getPath();
	}

	public static String replace(String originalIn, String toBeReplacedIn, String replacementIn) {
		return new String(originalIn.replaceAll(toBeReplacedIn, replacementIn));
	}

	public static ResourceLocation extend(ResourceLocation rl, String suffix) {
		return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
	}

	public static ResourceLocation replace(ResourceLocation rl, String regex, String replacement) {
		return new ResourceLocation(rl.getNamespace(), rl.getPath().replaceAll(regex, replacement));
	}
}