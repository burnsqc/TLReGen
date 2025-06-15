package com.tlregen.util;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class ResourceLocationHelper {

	public static String replace(String originalIn, String toBeReplacedIn, String replacementIn) {
		return new String(originalIn.replaceAll(toBeReplacedIn, replacementIn));
	}

	public static ResourceLocation extend(ResourceLocation rl, String suffix) {
		return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
	}

	public static ResourceLocation replace(ResourceLocation rl, String regex, String replacement) {
		return new ResourceLocation(rl.getNamespace(), rl.getPath().replaceAll(regex, replacement));
	}

	public static ResourceLocation blockTexture(Item item) {
		return new ResourceLocation(nameSpace(item), "block/" + path(item));
	}

	public static ResourceLocation blockTexture(Block block) {
		return new ResourceLocation(nameSpace(block), "block/" + path(block));
	}

	public static ResourceLocation blockTexture(Supplier<Block> block) {
		return new ResourceLocation(nameSpace(block.get()), "block/" + path(block.get()));
	}

	public static ResourceLocation itemTexture(Item item) {
		return new ResourceLocation(nameSpace(item), "item/" + path(item));
	}

	public static ResourceLocation key(Item item) {
		return ForgeRegistries.ITEMS.getKey(item);
	}

	public static ResourceLocation key(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block);
	}

	public static String nameSpace(Item item) {
		return key(item).getNamespace();
	}

	public static String nameSpace(Block block) {
		return key(block).getNamespace();
	}

	public static String path(Item item) {
		return key(item).getPath();
	}

	public static String path(Block block) {
		return key(block).getPath();
	}

	public static ResourceLocation extendWithFolderItem(ResourceLocation rl) {
		return rl.getPath().contains("/") ? rl : new ResourceLocation(rl.getNamespace(), "item/" + rl.getPath());
	}

	public static ResourceLocation extendWithFolderBlock(ResourceLocation rl) {
		return rl.getPath().contains("/") ? rl : new ResourceLocation(rl.getNamespace(), "block/" + rl.getPath());
	}
}
