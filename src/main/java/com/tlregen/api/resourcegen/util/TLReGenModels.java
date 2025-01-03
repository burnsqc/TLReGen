package com.tlregen.api.resourcegen.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.tlregen.util.ResourceLocationHelper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ExistingFileHelper.ResourceType;
import net.minecraftforge.registries.ForgeRegistries;

public class TLReGenModels {
	public String modID;
	public ExistingFileHelper helper;
	public final Map<ResourceLocation, BlockModelBuilder> modelBuilders = new HashMap<>();
	private BiFunction<ResourceLocation, ExistingFileHelper, BlockModelBuilder> bifunc = BlockModelBuilder::new;

	public BlockModelBuilder getBuilder(String path) {
		ResourceLocation outputLoc = extendWithFolder(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(modID, path));
		helper.trackGenerated(outputLoc, new ResourceType(PackType.CLIENT_RESOURCES, ".json", "models"));
		return modelBuilders.computeIfAbsent(outputLoc, loc -> bifunc.apply(loc, helper));
	}

	public ResourceLocation extendWithFolder(ResourceLocation rl) {
		if (rl.getPath().contains("/")) {
			return rl;
		}
		return new ResourceLocation(rl.getNamespace(), "block/" + rl.getPath());
	}

	public ModelFile.ExistingModelFile getExistingFile(ResourceLocation path) {
		ModelFile.ExistingModelFile ret = new ModelFile.ExistingModelFile(extendWithFolder(path), helper);
		ret.assertExistence();
		return ret;
	}

	/*
	 * RESOURCE LOCATION HELPERS
	 */
	public String name(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block).getPath();
	}

	public ResourceLocation blockTexture(Block block) {
		ResourceLocation name = ForgeRegistries.BLOCKS.getKey(block);
		return new ResourceLocation(name.getNamespace(), "block/" + name.getPath());
	}

	/*
	 * WITH EXISTING PARENT
	 */
	public ModelBuilder<BlockModelBuilder> withExistingParent(String name, String parent) {
		return withExistingParent(name, new ResourceLocation(parent));
	}

	public ModelBuilder<BlockModelBuilder> withExistingParent(String name, ResourceLocation parent) {
		return getBuilder(name).parent(getExistingFile(parent));
	}

	/*
	 * SINGLE TEXTURE
	 */
	public ModelBuilder<BlockModelBuilder> singleTexture(String name, String parent, String textureKey, ResourceLocation texture) {
		return singleTexture(name, new ResourceLocation(parent), textureKey, texture);
	}

	public ModelBuilder<BlockModelBuilder> singleTexture(String name, String parent, ResourceLocation texture) {
		return singleTexture(name, new ResourceLocation(parent), texture);
	}

	public ModelBuilder<BlockModelBuilder> singleTexture(String name, ResourceLocation parent, ResourceLocation texture) {
		return singleTexture(name, parent, "texture", texture);
	}

	public ModelBuilder<BlockModelBuilder> singleTexture(String name, ResourceLocation parent, String textureKey, ResourceLocation texture) {
		return withExistingParent(name, parent).texture(textureKey, texture);
	}

	/*
	 * SHORTCUTS
	 */

	public void bush4Stage(Block block, ResourceLocation texture, String renderType) {
		bush(block, 0, texture, renderType);
		bush(block, 1, texture, renderType);
		bush(block, 2, texture, renderType);
		bush(block, 3, texture, renderType);
	}

	public void buttonAll(Block block, ResourceLocation texture, String renderType) {
		button(block, texture, renderType);
		buttonPressed(block, texture, renderType);
		buttonInventory(block, texture, renderType);
	}

	public void crop4Stage(Block block, ResourceLocation texture, String renderType) {
		crop(block, 0, texture, renderType);
		crop(block, 1, texture, renderType);
		crop(block, 2, texture, renderType);
		crop(block, 3, texture, renderType);
	}

	public void crop8Stage(Block block, ResourceLocation texture, String renderType) {
		crop(block, 0, texture, renderType);
		crop(block, 1, texture, renderType);
		crop(block, 2, texture, renderType);
		crop(block, 3, texture, renderType);
		crop(block, 4, texture, renderType);
		crop(block, 5, texture, renderType);
		crop(block, 6, texture, renderType);
		crop(block, 7, texture, renderType);
	}

	public void doorAll(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		doorBottomLeft(block, bottom, top, renderType);
		doorBottomLeftOpen(block, bottom, top, renderType);
		doorBottomRight(block, bottom, top, renderType);
		doorBottomRightOpen(block, bottom, top, renderType);
		doorTopLeft(block, bottom, top, renderType);
		doorTopLeftOpen(block, bottom, top, renderType);
		doorTopRight(block, bottom, top, renderType);
		doorTopRightOpen(block, bottom, top, renderType);
	}

	public void fenceAll(Block block, ResourceLocation texture, String renderType) {
		fencePost(block, texture, renderType);
		fenceSide(block, texture, renderType);
		fenceInventory(block, texture, renderType);
	}

	public void fenceGateAll(Block block, ResourceLocation texture, String renderType) {
		fenceGate(block, texture, renderType);
		fenceGateOpen(block, texture, renderType);
		fenceGateWall(block, texture, renderType);
		fenceGateWallOpen(block, texture, renderType);
	}

	public void gourdStem8Stage(Block block, ResourceLocation texture, String renderType) {
		gourdStem(block, 0, texture, renderType);
		gourdStem(block, 1, texture, renderType);
		gourdStem(block, 2, texture, renderType);
		gourdStem(block, 3, texture, renderType);
		gourdStem(block, 4, texture, renderType);
		gourdStem(block, 5, texture, renderType);
		gourdStem(block, 6, texture, renderType);
		gourdStem(block, 7, texture, renderType);
	}

	public void logAll(Block block, ResourceLocation side, ResourceLocation end) {
		cubeColumn(block, side, end);
		cubeColumnHorizontal(block, side, end);
	}

	public void logAll(Block block, ResourceLocation side, ResourceLocation end, String renderType) {
		cubeColumn(block, side, end, renderType);
		cubeColumnHorizontal(block, side, end, renderType);
	}

	public void pressurePlateAll(Block block, ResourceLocation texture, String renderType) {
		pressurePlate(block, texture, renderType);
		pressurePlateDown(block, texture, renderType);
	}

	public void slabAll(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, String renderType) {
		slab(block, bottom, side, top, renderType);
		slabTop(block, bottom, side, top, renderType);
	}

	public void stairsAll(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, String renderType) {
		stairs(block, bottom, side, top, renderType);
		stairsInner(block, bottom, side, top, renderType);
		stairsOuter(block, bottom, side, top, renderType);
	}

	public void trapdoorAll(Block block, ResourceLocation texture, String renderType) {
		trapdoorBottom(block, texture, renderType);
		trapdoorOpen(block, texture, renderType);
		trapdoorTop(block, texture, renderType);
	}

	/*
	 * VANILLA MODELS
	 */

	public ModelBuilder<BlockModelBuilder> fluid(Block block) {
		return getBuilder(ForgeRegistries.BLOCKS.getKey(block).getPath()).texture("particle", "minecraft:block/water_still");
	}

	private ModelBuilder<BlockModelBuilder> bush(Block block, int stage, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_stage" + stage, "block/cross").texture("cross", texture + "_stage" + stage).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> button(String name, ResourceLocation texture) {
		return singleTexture(name, "block/button", texture);
	}

	public ModelBuilder<BlockModelBuilder> button(Block block, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block), "block/button").texture("texture", texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> buttonInventory(Block block, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_inventory", "block/button_inventory").texture("texture", texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> buttonPressed(String name, ResourceLocation texture) {
		return singleTexture(name, "block/button_pressed", texture);
	}

	public ModelBuilder<BlockModelBuilder> buttonPressed(Block block, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_pressed", "block/button_pressed").texture("texture", texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> cubeAll(String name, ResourceLocation texture) {
		return singleTexture(name, "block/cube_all", "all", texture);
	}

	public ModelBuilder<BlockModelBuilder> cubeAll(Block block, ResourceLocation texture) {
		return singleTexture(name(block), "block/cube_all", "all", texture);
	}

	public ModelBuilder<BlockModelBuilder> cubeAll(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(name(block), "block/cube_all", "all", texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> cubeBottomTop(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return withExistingParent(name, "block/cube_bottom_top").texture("side", side).texture("bottom", bottom).texture("top", top);
	}

	public ModelBuilder<BlockModelBuilder> cubeColumn(String name, ResourceLocation side, ResourceLocation end) {
		return withExistingParent(name, "block/cube_column").texture("side", side).texture("end", end);
	}

	public ModelBuilder<BlockModelBuilder> cubeColumn(Block block, ResourceLocation side, ResourceLocation end) {
		return withExistingParent(name(block), "block/cube_column").texture("side", side).texture("end", end).renderType("solid");
	}

	public ModelBuilder<BlockModelBuilder> cubeColumn(Block block, ResourceLocation side, ResourceLocation end, String renderType) {
		return withExistingParent(name(block), "block/cube_column").texture("side", side).texture("end", end).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> cubeColumnHorizontal(String name, ResourceLocation side, ResourceLocation end) {
		return withExistingParent(name, "block/cube_column_horizontal").texture("side", side).texture("end", end);
	}

	public ModelBuilder<BlockModelBuilder> cubeColumnHorizontal(Block block, ResourceLocation side, ResourceLocation end) {
		return withExistingParent(name(block) + "_horizontal", "block/cube_column_horizontal").texture("side", side).texture("end", end).renderType("solid");
	}

	private ModelBuilder<BlockModelBuilder> cubeColumnHorizontal(Block block, ResourceLocation side, ResourceLocation end, String renderType) {
		return withExistingParent(name(block) + "_horizontal", "block/cube_column_horizontal").texture("side", side).texture("end", end).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> crop(String name, ResourceLocation crop) {
		return singleTexture(name, "block/crop", "crop", crop);
	}

	public ModelBuilder<BlockModelBuilder> crop(Block block, int stage, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_stage" + stage, "block/crop").texture("crop", texture + "_stage" + stage).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> cross(String name, ResourceLocation cross) {
		return singleTexture(name, "block/cross", "cross", cross);
	}

	public ModelBuilder<BlockModelBuilder> doorBottomLeft(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_bottom_left", "block/door_bottom_left").texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> doorBottomLeftOpen(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_bottom_left_open", "block/door_bottom_left_open").texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> doorBottomRight(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_bottom_right", "block/door_bottom_right").texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> doorBottomRightOpen(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_bottom_right_open", "block/door_bottom_right_open").texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> doorTopLeft(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_top_left", "block/door_top_left").texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> doorTopLeftOpen(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_top_left_open", "block/door_top_left_open").texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> doorTopRight(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_top_right", "block/door_top_right").texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> doorTopRightOpen(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_top_right_open", "block/door_top_right_open").texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> door(String name, String model, ResourceLocation bottom, ResourceLocation top) {
		return withExistingParent(name, "block/" + model).texture("bottom", bottom).texture("top", top);
	}

	public ModelBuilder<BlockModelBuilder> doorBottomLeft(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_bottom_left", bottom, top);
	}

	public ModelBuilder<BlockModelBuilder> doorBottomLeftOpen(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_bottom_left_open", bottom, top);
	}

	public ModelBuilder<BlockModelBuilder> doorBottomRight(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_bottom_right", bottom, top);
	}

	public ModelBuilder<BlockModelBuilder> doorBottomRightOpen(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_bottom_right_open", bottom, top);
	}

	public ModelBuilder<BlockModelBuilder> doorTopLeft(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_top_left", bottom, top);
	}

	public ModelBuilder<BlockModelBuilder> doorTopLeftOpen(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_top_left_open", bottom, top);
	}

	public ModelBuilder<BlockModelBuilder> doorTopRight(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_top_right", bottom, top);
	}

	public ModelBuilder<BlockModelBuilder> doorTopRightOpen(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_top_right_open", bottom, top);
	}

	public ModelBuilder<BlockModelBuilder> fencePost(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_post", new ResourceLocation("fence_post"), texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> fenceSide(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_side", new ResourceLocation("fence_side"), texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> fenceInventory(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_inventory", new ResourceLocation("fence_inventory"), texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> fencePost(String name, ResourceLocation texture) {
		return singleTexture(name, "block/fence_post", texture);
	}

	public ModelBuilder<BlockModelBuilder> fenceSide(String name, ResourceLocation texture) {
		return singleTexture(name, "block/fence_side", texture);
	}

	public ModelBuilder<BlockModelBuilder> fenceGate(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_fence_gate", texture);
	}

	public ModelBuilder<BlockModelBuilder> fenceGateOpen(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_fence_gate_open", texture);
	}

	public ModelBuilder<BlockModelBuilder> fenceGateWall(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_fence_gate_wall", texture);
	}

	public ModelBuilder<BlockModelBuilder> fenceGateWallOpen(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_fence_gate_wall_open", texture);
	}

	public ModelBuilder<BlockModelBuilder> fenceGate(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block), new ResourceLocation("template_fence_gate"), texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> fenceGateOpen(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_open", new ResourceLocation("template_fence_gate_open"), texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> fenceGateWall(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_wall", new ResourceLocation("template_fence_gate_wall"), texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> fenceGateWallOpen(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_wall_open", new ResourceLocation("template_fence_gate_wall_open"), texture).renderType(renderType);
	}

	private ModelBuilder<BlockModelBuilder> gourdStem(Block block, int stage, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_stage" + stage, "block/stem_growth" + stage).texture("stem", texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> orientable(String name, ResourceLocation side, ResourceLocation front, ResourceLocation top) {
		return withExistingParent(name, "block/orientable").texture("side", side).texture("front", front).texture("top", top);
	}

	public ModelBuilder<BlockModelBuilder> pottedSapling(Block block, ResourceLocation plant, String renderType) {
		return withExistingParent(name(block), "block/flower_pot_cross").texture("plant", plant).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> pressurePlate(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block), new ResourceLocation("pressure_plate_up"), texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> pressurePlateDown(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_down", new ResourceLocation("pressure_plate_down"), texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> pressurePlateDown(String name, ResourceLocation texture) {
		return singleTexture(name, "block/pressure_plate_down", texture);
	}

	public ModelBuilder<BlockModelBuilder> pressurePlateUp(String name, ResourceLocation texture) {
		return singleTexture(name, "block/pressure_plate_up", texture);
	}

	public ModelBuilder<BlockModelBuilder> sapling(Block block, ResourceLocation cross, String renderType) {
		return withExistingParent(name(block), "block/cross").texture("cross", cross).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> sign(Block block, ResourceLocation texture, String renderType) {
		return getBuilder(name(block)).texture("particle", texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> skull() {
		return getBuilder("minecraft:block/skull");
	}

	public ModelBuilder<BlockModelBuilder> slab(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, String renderType) {
		return withExistingParent(name(block), "block/slab").texture("bottom", bottom).texture("side", side).texture("top", top).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> slabTop(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_top", "block/slab_top").texture("bottom", bottom).texture("side", side).texture("top", top).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> stairs(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, String renderType) {
		return withExistingParent(name(block), "block/stairs").texture("bottom", bottom).texture("side", side).texture("top", top).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> stairsInner(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_inner", "block/inner_stairs").texture("bottom", bottom).texture("side", side).texture("top", top).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> stairsOuter(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_outer", "block/outer_stairs").texture("bottom", bottom).texture("side", side).texture("top", top).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> stairs(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return withExistingParent(name, "block/stairs").texture("side", side).texture("bottom", bottom).texture("top", top);
	}

	public ModelBuilder<BlockModelBuilder> trapdoorBottom(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_bottom", new ResourceLocation("template_orientable_trapdoor_bottom"), texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> trapdoorOpen(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_open", new ResourceLocation("template_orientable_trapdoor_open"), texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> trapdoorTop(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_top", new ResourceLocation("template_orientable_trapdoor_top"), texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> trapdoorOrientableBottom(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_orientable_trapdoor_bottom", texture);
	}

	public ModelBuilder<BlockModelBuilder> trapdoorOrientableTop(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_orientable_trapdoor_top", texture);
	}

	public ModelBuilder<BlockModelBuilder> trapdoorOrientableOpen(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_orientable_trapdoor_open", texture);
	}

	public ModelBuilder<BlockModelBuilder> trapdoorBottom(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_trapdoor_bottom", texture);
	}

	public ModelBuilder<BlockModelBuilder> trapdoorOpen(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_trapdoor_open", texture);
	}

	public ModelBuilder<BlockModelBuilder> trapdoorTop(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_trapdoor_top", texture);
	}
}
