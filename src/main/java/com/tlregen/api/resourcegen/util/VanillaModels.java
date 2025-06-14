package com.tlregen.api.resourcegen.util;

import com.tlregen.api.resourcegen.MasterResourceGenerator;

import net.minecraft.client.renderer.block.model.BlockModel.GuiLight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.registries.ForgeRegistries;

public class VanillaModels {
	public static final ModelFile GENERATED = new ModelFile.UncheckedModelFile("item/generated");
	public static final ModelFile HANDHELD = new ModelFile.UncheckedModelFile("item/handheld");
	public static final ModelFile SPAWN_EGG = new ModelFile.UncheckedModelFile("item/template_spawn_egg");
	public static final ModelFile SKULL = new ModelFile.UncheckedModelFile("item/template_skull");
	public static final ModelFile ENTITY = new ModelFile.UncheckedModelFile("builtin/entity");

	private static ItemModelBuilder itemModel(String path) {
		ResourceLocation outputLoc = extendWithFolder(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(MasterResourceGenerator.modID, path));
		ItemModelBuilder itemModelBuilder = new ItemModelBuilder(outputLoc, MasterResourceGenerator.helper);
		return itemModelBuilder;
	}

	/*
	 * RESOURCE LOCATION HELPERS
	 */

	private static ResourceLocation extendWithFolder(ResourceLocation rl) {
		return rl.getPath().contains("/") ? rl : new ResourceLocation(rl.getNamespace(), "item/" + rl.getPath());
	}

	private static String nameSpace(Item item) {
		return ForgeRegistries.ITEMS.getKey(item).getNamespace();
	}

	private static String path(Item item) {
		return ForgeRegistries.ITEMS.getKey(item).getPath();
	}

	private static String path(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block).getPath();
	}

	private static ResourceLocation itemTexture(Item item) {
		return new ResourceLocation(nameSpace(item), "item/" + path(item));
	}

	private static ResourceLocation blockTexture(Item item) {
		return new ResourceLocation(nameSpace(item), "block/" + path(item));
	}

	/*
	 * ITEM MODELS
	 */

	public static ItemModelBuilder item2D(Item item) {
		return itemModel(path(item)).parent(GENERATED).texture("layer0", itemTexture(item));
	}

	public static ItemModelBuilder item2DModel(Item item, ModelFile model) {
		return itemModel(path(item)).parent(model).texture("layer0", itemTexture(item));
	}

	public static ItemModelBuilder itemSpawnEgg(Item item) {
		return itemModel(path(item)).parent(SPAWN_EGG);
	}

	public static ItemModelBuilder item2D(Item item, String renderType) {
		return itemModel(path(item)).parent(GENERATED).texture("layer0", itemTexture(item)).renderType(renderType);
	}

	public static ItemModelBuilder itemWithBlockTexture(Item item) {
		return itemModel(path(item)).parent(GENERATED).texture("layer0", blockTexture(item));
	}

	public static ItemModelBuilder itemGlintBase(Item item) {
		return itemModel(path(item) + "_base").parent(GENERATED).texture("layer0", itemTexture(item));
	}

	public static ItemModelBuilder itemGlint(Item item) {
		return itemModel(path(item)).parent(ENTITY).guiLight(GuiLight.FRONT).texture("layer0", itemTexture(item));
	}

	public static ItemModelBuilder item3DModel(Block block, ModelFile model) {
		return itemModel(path(block)).parent(model);
	}
}
