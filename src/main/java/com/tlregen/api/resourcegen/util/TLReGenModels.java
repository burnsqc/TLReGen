package com.tlregen.api.resourcegen.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

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

	public void slabAll(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, String renderType) {
		slab(block, bottom, side, top, renderType);
		slabTop(block, bottom, side, top, renderType);
	}

	public void stairsAll(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, String renderType) {
		stairs(block, bottom, side, top, renderType);
		stairsInner(block, bottom, side, top, renderType);
		stairsOuter(block, bottom, side, top, renderType);
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

	public ModelBuilder<BlockModelBuilder> button(Block block, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block), "block/button").texture("texture", texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> buttonInventory(Block block, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_inventory", "block/button_inventory").texture("texture", texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> buttonPressed(Block block, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_pressed", "block/button_pressed").texture("texture", texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> cubeAll(Block block, ResourceLocation texture) {
		return singleTexture(name(block), "block/cube_all", "all", texture);
	}

	public ModelBuilder<BlockModelBuilder> cubeAll(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(name(block), "block/cube_all", "all", texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> cubeColumn(Block block, ResourceLocation side, ResourceLocation end) {
		return withExistingParent(name(block), "block/cube_column").texture("side", side).texture("end", end).renderType("solid");
	}

	public ModelBuilder<BlockModelBuilder> cubeColumn(Block block, ResourceLocation side, ResourceLocation end, String renderType) {
		return withExistingParent(name(block), "block/cube_column").texture("side", side).texture("end", end).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> cubeColumnHorizontal(Block block, ResourceLocation side, ResourceLocation end) {
		return withExistingParent(name(block) + "_horizontal", "block/cube_column_horizontal").texture("side", side).texture("end", end).renderType("solid");
	}

	private ModelBuilder<BlockModelBuilder> cubeColumnHorizontal(Block block, ResourceLocation side, ResourceLocation end, String renderType) {
		return withExistingParent(name(block) + "_horizontal", "block/cube_column_horizontal").texture("side", side).texture("end", end).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> crop(Block block, int stage, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_stage" + stage, "block/crop").texture("crop", texture + "_stage" + stage).renderType(renderType);
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

	private ModelBuilder<BlockModelBuilder> gourdStem(Block block, int stage, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_stage" + stage, "block/stem_growth" + stage).texture("stem", texture).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> pottedSapling(Block block, ResourceLocation plant, String renderType) {
		return withExistingParent(name(block), "block/flower_pot_cross").texture("plant", plant).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> sapling(Block block, ResourceLocation cross, String renderType) {
		return withExistingParent(name(block), "block/cross").texture("cross", cross).renderType(renderType);
	}

	public ModelBuilder<BlockModelBuilder> sign(Block block, ResourceLocation texture, String renderType) {
		return getBuilder(name(block)).texture("particle", texture).renderType(renderType);
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
}
