package com.tlregen.api.resourcegen.util;

import com.tlregen.api.resourcegen.MasterResourceGenerator;
import com.tlregen.util.ResourceLocationHelper;

import net.minecraft.client.renderer.block.model.BlockModel.GuiLight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

public class VanillaModels {
	public static final ModelFile GENERATED = new ModelFile.ExistingModelFile(new ResourceLocation("item/generated"), MasterResourceGenerator.helper);
	public static final ModelFile HANDHELD = new ModelFile.UncheckedModelFile("item/handheld");
	public static final ModelFile SPAWN_EGG = new ModelFile.UncheckedModelFile("item/template_spawn_egg");
	public static final ModelFile SKULL = new ModelFile.UncheckedModelFile("item/template_skull");
	public static final ModelFile ENTITY = new ModelFile.UncheckedModelFile("builtin/entity");
	public static final ModelFile CUBE_ALL = new ModelFile.UncheckedModelFile("block/cube_all");
	public static final ModelFile CUBE_COLUMN = new ModelFile.UncheckedModelFile("block/cube_column");
	public static final ModelFile CUBE_COLUMN_HORIZONTAL = new ModelFile.UncheckedModelFile("block/cube_column_horizontal");
	public static final ModelFile CUBE_BOTTOM_TOP = new ModelFile.ExistingModelFile(new ResourceLocation("block/cube_bottom_top"), MasterResourceGenerator.helper);
	public static final ModelFile ORIENTABLE = new ModelFile.ExistingModelFile(new ResourceLocation("block/orientable"), MasterResourceGenerator.helper);
	public static final ModelFile SLAB = new ModelFile.UncheckedModelFile("block/slab");
	public static final ModelFile SLAB_TOP = new ModelFile.UncheckedModelFile("block/slab_top");
	public static final ModelFile STAIRS = new ModelFile.UncheckedModelFile("block/stairs");
	public static final ModelFile INNER_STAIRS = new ModelFile.UncheckedModelFile("block/inner_stairs");
	public static final ModelFile OUTER_STAIRS = new ModelFile.UncheckedModelFile("block/outer_stairs");
	public static final ModelFile FENCE_POST = new ModelFile.UncheckedModelFile("block/fence_post");
	public static final ModelFile FENCE_SIDE = new ModelFile.UncheckedModelFile("block/fence_side");
	public static final ModelFile FENCE_INVENTORY = new ModelFile.UncheckedModelFile("block/fence_inventory");
	public static final ModelFile FENCE_GATE = new ModelFile.UncheckedModelFile("block/template_fence_gate");
	public static final ModelFile FENCE_GATE_OPEN = new ModelFile.UncheckedModelFile("block/template_fence_gate_open");
	public static final ModelFile FENCE_GATE_WALL = new ModelFile.UncheckedModelFile("block/template_fence_gate_wall");
	public static final ModelFile FENCE_GATE_WALL_OPEN = new ModelFile.UncheckedModelFile("block/template_fence_gate_wall_open");
	public static final ModelFile BUTTON = new ModelFile.UncheckedModelFile("block/button");
	public static final ModelFile BUTTON_PRESSED = new ModelFile.UncheckedModelFile("block/button_pressed");
	public static final ModelFile BUTTON_INVENTORY = new ModelFile.UncheckedModelFile("block/button_inventory");
	public static final ModelFile PRESSURE_PLATE_UP = new ModelFile.UncheckedModelFile("block/pressure_plate_up");
	public static final ModelFile PRESSURE_PLATE_DOWN = new ModelFile.UncheckedModelFile("block/pressure_plate_down");
	public static final ModelFile DOOR_BOTTOM_LEFT = new ModelFile.UncheckedModelFile("block/door_bottom_left");
	public static final ModelFile DOOR_BOTTOM_LEFT_OPEN = new ModelFile.UncheckedModelFile("block/door_bottom_left_open");
	public static final ModelFile DOOR_BOTTOM_RIGHT = new ModelFile.UncheckedModelFile("block/door_bottom_right");
	public static final ModelFile DOOR_BOTTOM_RIGHT_OPEN = new ModelFile.UncheckedModelFile("block/door_bottom_right_open");
	public static final ModelFile DOOR_TOP_LEFT = new ModelFile.UncheckedModelFile("block/door_top_left");
	public static final ModelFile DOOR_TOP_LEFT_OPEN = new ModelFile.UncheckedModelFile("block/door_top_left_open");
	public static final ModelFile DOOR_TOP_RIGHT = new ModelFile.UncheckedModelFile("block/door_top_right");
	public static final ModelFile DOOR_TOP_RIGHT_OPEN = new ModelFile.UncheckedModelFile("block/door_top_right_open");
	public static final ModelFile TRAPDOOR_BOTTOM = new ModelFile.UncheckedModelFile("block/template_orientable_trapdoor_bottom");
	public static final ModelFile TRAPDOOR_OPEN = new ModelFile.UncheckedModelFile("block/template_orientable_trapdoor_open");
	public static final ModelFile TRAPDOOR_TOP = new ModelFile.UncheckedModelFile("block/template_orientable_trapdoor_top");
	public static final ModelFile CROSS = new ModelFile.UncheckedModelFile("block/cross");
	public static final ModelFile CROP = new ModelFile.UncheckedModelFile("block/crop");
	public static final ModelFile FLOWER_POT_CROSS = new ModelFile.UncheckedModelFile("block/flower_pot_cross");
	public static final ModelFile CHAIN = new ModelFile.UncheckedModelFile("block/chain");
	public static final ModelFile[] SEA_PICKLE = { new ModelFile.UncheckedModelFile("block/sea_pickle"), new ModelFile.UncheckedModelFile("block/two_sea_pickles"), new ModelFile.UncheckedModelFile("block/three_sea_pickles"), new ModelFile.UncheckedModelFile("block/four_sea_pickles") };
	public static final ModelFile[] DEAD_SEA_PICKLE = { new ModelFile.UncheckedModelFile("block/dead_sea_pickle"), new ModelFile.UncheckedModelFile("block/two_dead_sea_pickles"), new ModelFile.UncheckedModelFile("block/three_dead_sea_pickles"), new ModelFile.UncheckedModelFile("block/four_dead_sea_pickles") };
	public static final ModelFile[] STEM_GROWTH = { new ModelFile.UncheckedModelFile("block/stem_growth0"), new ModelFile.UncheckedModelFile("block/stem_growth1"), new ModelFile.UncheckedModelFile("block/stem_growth2"), new ModelFile.UncheckedModelFile("block/stem_growth3"), new ModelFile.UncheckedModelFile("block/stem_growth4"), new ModelFile.UncheckedModelFile("block/stem_growth5"), new ModelFile.UncheckedModelFile("block/stem_growth6"), new ModelFile.UncheckedModelFile("block/stem_growth7") };

	/*
	 * RENDER TYPES
	 */

	public static final ResourceLocation CUTOUT = new ResourceLocation("cutout");
	public static final ResourceLocation CUTOUT_MIPPED = new ResourceLocation("cutout_mipped");
	public static final ResourceLocation SOLID = new ResourceLocation("solid");
	public static final ResourceLocation TRANSLUCENT = new ResourceLocation("translucent");

	private static ItemModelBuilder itemModel(String path) {
		ResourceLocation outputLoc = ResourceLocationHelper.extendWithFolderItem(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(MasterResourceGenerator.modID, path));
		return new ItemModelBuilder(outputLoc, MasterResourceGenerator.helper);
	}

	private static BlockModelBuilder blockModel() {
		return new BlockModelBuilder(new ResourceLocation(""), MasterResourceGenerator.helper);
	}

	/*
	 * ITEM MODELS
	 */

	public static ItemModelBuilder item2D(Item item) {
		return itemModel(ResourceLocationHelper.path(item)).parent(GENERATED).texture("layer0", ResourceLocationHelper.itemTexture(item));
	}

	public static ItemModelBuilder item2DModel(Item item, ModelFile model) {
		return itemModel(ResourceLocationHelper.path(item)).parent(model).texture("layer0", ResourceLocationHelper.itemTexture(item));
	}

	public static ItemModelBuilder itemSpawnEgg(Item item) {
		return itemModel(ResourceLocationHelper.path(item)).parent(SPAWN_EGG);
	}

	public static ItemModelBuilder item2D(Item item, String renderType) {
		return itemModel(ResourceLocationHelper.path(item)).parent(GENERATED).texture("layer0", ResourceLocationHelper.itemTexture(item)).renderType(renderType);
	}

	public static ItemModelBuilder itemWithBlockTexture(Item item) {
		return itemModel(ResourceLocationHelper.path(item)).parent(GENERATED).texture("layer0", ResourceLocationHelper.blockTexture(item));
	}

	public static ItemModelBuilder itemGlintBase(Item item) {
		return itemModel(ResourceLocationHelper.path(item) + "_base").parent(GENERATED).texture("layer0", ResourceLocationHelper.itemTexture(item));
	}

	public static ItemModelBuilder itemGlint(Item item) {
		return itemModel(ResourceLocationHelper.path(item)).parent(ENTITY).guiLight(GuiLight.FRONT).texture("layer0", ResourceLocationHelper.itemTexture(item));
	}

	public static ItemModelBuilder item3DModel(Block block, ModelFile model) {
		return itemModel(ResourceLocationHelper.path(block)).parent(model);
	}

	/*
	 * BLOCK MODELS
	 */

	public static BlockModelBuilder blockCubeAll(Block block) {
		return blockModel().parent(CUBE_ALL).texture("all", ResourceLocationHelper.blockTexture(block));
	}

	public static BlockModelBuilder blockCubeAllRenderType(Block block, ResourceLocation renderType) {
		return blockModel().parent(CUBE_ALL).texture("all", ResourceLocationHelper.blockTexture(block)).renderType(renderType);
	}

	public static BlockModelBuilder cubeColumn(Block block, ResourceLocation side, ResourceLocation end, ResourceLocation renderType) {
		return blockModel().parent(CUBE_COLUMN).texture("side", side).texture("end", end).renderType(renderType);
	}

	public static BlockModelBuilder cubeColumnHorizontal(Block block, ResourceLocation side, ResourceLocation end, ResourceLocation renderType) {
		return blockModel().parent(CUBE_COLUMN_HORIZONTAL).texture("side", side).texture("end", end).renderType(renderType);
	}

	public static BlockModelBuilder cubeBottomTop(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top) {
		return blockModel().parent(CUBE_BOTTOM_TOP).texture("bottom", bottom).texture("side", side).texture("top", top);
	}

	public static BlockModelBuilder orientable(Block block, ResourceLocation front, ResourceLocation side, ResourceLocation top) {
		return blockModel().parent(ORIENTABLE).texture("front", front).texture("side", side).texture("top", top);
	}

	public static BlockModelBuilder slab(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, ResourceLocation renderType) {
		return blockModel().parent(SLAB).texture("bottom", bottom).texture("side", side).texture("top", top).renderType(renderType);
	}

	public static BlockModelBuilder slabTop(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, ResourceLocation renderType) {
		return blockModel().parent(SLAB_TOP).texture("bottom", bottom).texture("side", side).texture("top", top).renderType(renderType);
	}

	public static BlockModelBuilder stairs(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, ResourceLocation renderType) {
		return blockModel().parent(STAIRS).texture("bottom", bottom).texture("side", side).texture("top", top).renderType(renderType);
	}

	public static BlockModelBuilder stairsInner(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, ResourceLocation renderType) {
		return blockModel().parent(INNER_STAIRS).texture("bottom", bottom).texture("side", side).texture("top", top).renderType(renderType);
	}

	public static BlockModelBuilder stairsOuter(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, ResourceLocation renderType) {
		return blockModel().parent(OUTER_STAIRS).texture("bottom", bottom).texture("side", side).texture("top", top).renderType(renderType);
	}

	public static BlockModelBuilder fencePost(Block block, ResourceLocation texture, ResourceLocation renderType) {
		return blockModel().parent(FENCE_POST).texture("texture", texture).renderType(renderType);
	}

	public static BlockModelBuilder fenceSide(Block block, ResourceLocation texture, ResourceLocation renderType) {
		return blockModel().parent(FENCE_SIDE).texture("texture", texture).renderType(renderType);
	}

	public static BlockModelBuilder fenceInventory(Block block, ResourceLocation texture, ResourceLocation renderType) {
		return blockModel().parent(FENCE_INVENTORY).texture("texture", texture).renderType(renderType);
	}

	public static BlockModelBuilder fenceGate(Block block, ResourceLocation texture, ResourceLocation renderType) {
		return blockModel().parent(FENCE_GATE).texture("texture", texture).renderType(renderType);
	}

	public static BlockModelBuilder fenceGateOpen(Block block, ResourceLocation texture, ResourceLocation renderType) {
		return blockModel().parent(FENCE_GATE_OPEN).texture("texture", texture).renderType(renderType);
	}

	public static BlockModelBuilder fenceGateWall(Block block, ResourceLocation texture, ResourceLocation renderType) {
		return blockModel().parent(FENCE_GATE_WALL).texture("texture", texture).renderType(renderType);
	}

	public static BlockModelBuilder fenceGateWallOpen(Block block, ResourceLocation texture, ResourceLocation renderType) {
		return blockModel().parent(FENCE_GATE_WALL_OPEN).texture("texture", texture).renderType(renderType);
	}

	public static BlockModelBuilder button(Block block, ResourceLocation texture, ResourceLocation renderType) {
		return blockModel().parent(BUTTON).texture("texture", texture).renderType(renderType);
	}

	public static BlockModelBuilder buttonPressed(Block block, ResourceLocation texture, ResourceLocation renderType) {
		return blockModel().parent(BUTTON_PRESSED).texture("texture", texture).renderType(renderType);
	}

	public static BlockModelBuilder buttonInventory(Block block, ResourceLocation texture, ResourceLocation renderType) {
		return blockModel().parent(BUTTON_INVENTORY).texture("texture", texture).renderType(renderType);
	}

	public static BlockModelBuilder pressurePlate(Block block, ResourceLocation texture, ResourceLocation renderType) {
		return blockModel().parent(PRESSURE_PLATE_UP).texture("texture", texture).renderType(renderType);
	}

	public static BlockModelBuilder pressurePlateDown(Block block, ResourceLocation texture, ResourceLocation renderType) {
		return blockModel().parent(PRESSURE_PLATE_DOWN).texture("texture", texture).renderType(renderType);
	}

	public static BlockModelBuilder doorBottomLeft(Block block, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
		return blockModel().parent(DOOR_BOTTOM_LEFT).texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public static BlockModelBuilder doorBottomLeftOpen(Block block, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
		return blockModel().parent(DOOR_BOTTOM_LEFT_OPEN).texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public static BlockModelBuilder doorBottomRight(Block block, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
		return blockModel().parent(DOOR_BOTTOM_RIGHT).texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public static BlockModelBuilder doorBottomRightOpen(Block block, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
		return blockModel().parent(DOOR_BOTTOM_RIGHT_OPEN).texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public static BlockModelBuilder doorTopLeft(Block block, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
		return blockModel().parent(DOOR_TOP_LEFT).texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public static BlockModelBuilder doorTopLeftOpen(Block block, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
		return blockModel().parent(DOOR_TOP_LEFT_OPEN).texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public static BlockModelBuilder doorTopRight(Block block, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
		return blockModel().parent(DOOR_TOP_RIGHT).texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public static BlockModelBuilder doorTopRightOpen(Block block, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
		return blockModel().parent(DOOR_TOP_RIGHT_OPEN).texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public static BlockModelBuilder trapdoorBottom(Block block, ResourceLocation texture, ResourceLocation renderType) {
		return blockModel().parent(TRAPDOOR_BOTTOM).texture("texture", texture).renderType(renderType);
	}

	public static BlockModelBuilder trapdoorOpen(Block block, ResourceLocation texture, ResourceLocation renderType) {
		return blockModel().parent(TRAPDOOR_OPEN).texture("texture", texture).renderType(renderType);
	}

	public static BlockModelBuilder trapdoorTop(Block block, ResourceLocation texture, ResourceLocation renderType) {
		return blockModel().parent(TRAPDOOR_TOP).texture("texture", texture).renderType(renderType);
	}

	public static BlockModelBuilder sign(Block block, ResourceLocation texture, ResourceLocation renderType) {
		return blockModel().texture("particle", texture).renderType(renderType);
	}

	public static BlockModelBuilder sapling(Block block) {
		return blockModel().parent(CROSS).texture("cross", ResourceLocationHelper.blockTexture(block)).renderType(CUTOUT_MIPPED);
	}

	public static BlockModelBuilder pottedSapling(Block block, ResourceLocation texture) {
		return blockModel().parent(FLOWER_POT_CROSS).texture("plant", texture).renderType(CUTOUT_MIPPED);
	}

	public static BlockModelBuilder crop(Block block, int stage) {
		return blockModel().parent(CROP).texture("crop", ResourceLocationHelper.blockTexture(block) + "_stage" + stage).renderType(CUTOUT);
	}

	public static BlockModelBuilder bush(Block block, int stage) {
		return blockModel().parent(CROSS).texture("cross", ResourceLocationHelper.blockTexture(block) + "_stage" + stage).renderType(CUTOUT_MIPPED);
	}

	public static BlockModelBuilder stemGrowth(Block block, int stage) {
		return blockModel().parent(STEM_GROWTH[stage]).texture("stem", ResourceLocationHelper.blockTexture(block)).renderType(CUTOUT);
	}

	public static BlockModelBuilder fluid(Block block) {
		return blockModel().texture("particle", new ResourceLocation("block/water_still"));
	}

	public static BlockModelBuilder seaPickle(Block block, int count) {
		return blockModel().parent(SEA_PICKLE[count - 1]).texture("all", ResourceLocationHelper.blockTexture(block)).renderType(CUTOUT);
	}

	public static BlockModelBuilder deadSeaPickle(Block block, int count) {
		return blockModel().parent(DEAD_SEA_PICKLE[count - 1]).texture("all", ResourceLocationHelper.blockTexture(block)).renderType(CUTOUT);
	}
}
