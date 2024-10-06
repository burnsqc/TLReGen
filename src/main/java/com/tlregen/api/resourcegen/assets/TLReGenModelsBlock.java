package com.tlregen.api.resourcegen.assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenAssetGenerator;
import com.tlregen.util.ResourceLocationHelper;
import com.tlregen.util.TechnologicaLocation;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ExistingFileHelper.ResourceType;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class TLReGenModelsBlock extends TLReGenAssetGenerator {
	private final static Map<ResourceLocation, BlockModelBuilder> resources = new HashMap<>();
	private static BiFunction<ResourceLocation, ExistingFileHelper, BlockModelBuilder> bifunc = BlockModelBuilder::new;

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		resources.clear();
		populate();
		if (resources.isEmpty()) {
			return CompletableFuture.allOf();
		} else {
			List<CompletableFuture<?>> list = new ArrayList<CompletableFuture<?>>();
			resources.forEach((key, value) -> {
				JsonObject json = value.toJson();
				list.add(DataProvider.saveStable(cache, json, packOutput.createPathProvider(target, "models").json(key)));
			});
			return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
		}
	}

	@Override
	public final String getName() {
		return super.getName() + ".models.block";
	}

	/*
	 * HELPER METHODS
	 */

	public static BlockModelBuilder getBuilder(String path) {
		ResourceLocation outputLoc = extendWithFolder(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(modid, path));
		helper.trackGenerated(outputLoc, new ResourceType(PackType.CLIENT_RESOURCES, ".json", "models"));
		return resources.computeIfAbsent(outputLoc, loc -> bifunc.apply(loc, helper));
	}

	private static ResourceLocation extendWithFolder(ResourceLocation rl) {
		if (rl.getPath().contains("/")) {
			return rl;
		}
		return new ResourceLocation(rl.getNamespace(), "block/" + rl.getPath());
	}

	/*
	 * MODELS
	 */

	public static ModelBuilder<BlockModelBuilder> withExistingParent(String name, String parent) {
		return withExistingParent(name, new ResourceLocation(parent));
	}

	public static ModelBuilder<BlockModelBuilder> withExistingParent(String name, ResourceLocation parent) {
		return getBuilder(name).parent(getExistingFile(parent));
	}

	private static ModelBuilder<BlockModelBuilder> singleTexture(String name, String parent, ResourceLocation texture) {
		return singleTexture(name, new ResourceLocation(parent), texture);
	}

	public static ModelBuilder<BlockModelBuilder> singleTexture(String name, ResourceLocation parent, ResourceLocation texture) {
		return singleTexture(name, parent, "texture", texture);
	}

	private static ModelBuilder<BlockModelBuilder> singleTexture(String name, String parent, String textureKey, ResourceLocation texture) {
		return singleTexture(name, new ResourceLocation(parent), textureKey, texture);
	}

	public static ModelBuilder<BlockModelBuilder> singleTexture(String name, ResourceLocation parent, String textureKey, ResourceLocation texture) {
		return withExistingParent(name, parent).texture(textureKey, texture);
	}

	public static ModelBuilder<BlockModelBuilder> cubeAll(String name, ResourceLocation texture) {
		return singleTexture(name, "block/cube_all", "all", texture);
	}

	private static ModelBuilder<BlockModelBuilder> sideBottomTop(String name, String parent, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return withExistingParent(name, parent).texture("side", side).texture("bottom", bottom).texture("top", top);
	}

	public static ModelBuilder<BlockModelBuilder> cubeBottomTop(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return sideBottomTop(name, "block/cube_bottom_top", side, bottom, top);
	}

	public static ModelBuilder<BlockModelBuilder> cubeColumn(String name, ResourceLocation side, ResourceLocation end) {
		return withExistingParent(name, "block/cube_column").texture("side", side).texture("end", end);
	}

	static ModelBuilder<BlockModelBuilder> cubeColumnHorizontal(String name, ResourceLocation side, ResourceLocation end) {
		return withExistingParent(name, "block/cube_column_horizontal").texture("side", side).texture("end", end);
	}

	public static ModelBuilder<BlockModelBuilder> orientable(String name, ResourceLocation side, ResourceLocation front, ResourceLocation top) {
		return withExistingParent(name, "block/orientable").texture("side", side).texture("front", front).texture("top", top);
	}

	public static ModelBuilder<BlockModelBuilder> crop(String name, ResourceLocation crop) {
		return singleTexture(name, "block/crop", "crop", crop);
	}

	public static ModelBuilder<BlockModelBuilder> cross(String name, ResourceLocation cross) {
		return singleTexture(name, "block/cross", "cross", cross);
	}

	public static ModelBuilder<BlockModelBuilder> stairs(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return sideBottomTop(name, "block/stairs", side, bottom, top);
	}

	static ModelBuilder<BlockModelBuilder> stairsOuter(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return sideBottomTop(name, "block/outer_stairs", side, bottom, top);
	}

	static ModelBuilder<BlockModelBuilder> stairsInner(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return sideBottomTop(name, "block/inner_stairs", side, bottom, top);
	}

	public static ModelBuilder<BlockModelBuilder> slab(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return sideBottomTop(name, "block/slab", side, bottom, top);
	}

	public static ModelBuilder<BlockModelBuilder> slabTop(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return sideBottomTop(name, "block/slab_top", side, bottom, top);
	}

	public static ModelBuilder<BlockModelBuilder> button(String name, ResourceLocation texture) {
		return singleTexture(name, "block/button", texture);
	}

	public static ModelBuilder<BlockModelBuilder> buttonPressed(String name, ResourceLocation texture) {
		return singleTexture(name, "block/button_pressed", texture);
	}

	public static ModelBuilder<BlockModelBuilder> pressurePlate(String name, ResourceLocation texture) {
		return singleTexture(name, "block/pressure_plate_up", texture);
	}

	public static ModelBuilder<BlockModelBuilder> pressurePlateDown(String name, ResourceLocation texture) {
		return singleTexture(name, "block/pressure_plate_down", texture);
	}

	public static ModelBuilder<BlockModelBuilder> sign(String name, ResourceLocation texture) {
		return getBuilder(name).texture("particle", texture);
	}

	static ModelBuilder<BlockModelBuilder> fencePost(String name, ResourceLocation texture) {
		return singleTexture(name, "block/fence_post", texture);
	}

	static ModelBuilder<BlockModelBuilder> fenceSide(String name, ResourceLocation texture) {
		return singleTexture(name, "block/fence_side", texture);
	}

	public static ModelBuilder<BlockModelBuilder> fenceInventory(String name, ResourceLocation texture) {
		return singleTexture(name, "block/fence_inventory", texture);
	}

	public static ModelBuilder<BlockModelBuilder> fenceGate(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_fence_gate", texture);
	}

	static ModelBuilder<BlockModelBuilder> fenceGateOpen(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_fence_gate_open", texture);
	}

	static ModelBuilder<BlockModelBuilder> fenceGateWall(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_fence_gate_wall", texture);
	}

	static ModelBuilder<BlockModelBuilder> fenceGateWallOpen(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_fence_gate_wall_open", texture);
	}

	private static ModelBuilder<BlockModelBuilder> door(String name, String model, ResourceLocation bottom, ResourceLocation top) {
		return withExistingParent(name, "block/" + model).texture("bottom", bottom).texture("top", top);
	}

	static ModelBuilder<BlockModelBuilder> doorBottomLeft(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_bottom_left", bottom, top);
	}

	static ModelBuilder<BlockModelBuilder> doorBottomLeftOpen(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_bottom_left_open", bottom, top);
	}

	static ModelBuilder<BlockModelBuilder> doorBottomRight(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_bottom_right", bottom, top);
	}

	static ModelBuilder<BlockModelBuilder> doorBottomRightOpen(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_bottom_right_open", bottom, top);
	}

	static ModelBuilder<BlockModelBuilder> doorTopLeft(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_top_left", bottom, top);
	}

	static ModelBuilder<BlockModelBuilder> doorTopLeftOpen(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_top_left_open", bottom, top);
	}

	static ModelBuilder<BlockModelBuilder> doorTopRight(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_top_right", bottom, top);
	}

	static ModelBuilder<BlockModelBuilder> doorTopRightOpen(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_top_right_open", bottom, top);
	}

	static ModelBuilder<BlockModelBuilder> trapdoorBottom(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_trapdoor_bottom", texture);
	}

	static ModelBuilder<BlockModelBuilder> trapdoorTop(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_trapdoor_top", texture);
	}

	static ModelBuilder<BlockModelBuilder> trapdoorOpen(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_trapdoor_open", texture);
	}

	public static ModelBuilder<BlockModelBuilder> trapdoorOrientableBottom(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_orientable_trapdoor_bottom", texture);
	}

	static ModelBuilder<BlockModelBuilder> trapdoorOrientableTop(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_orientable_trapdoor_top", texture);
	}

	static ModelBuilder<BlockModelBuilder> trapdoorOrientableOpen(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_orientable_trapdoor_open", texture);
	}

	public static ModelBuilder<BlockModelBuilder> hollowLogModel(Block block) {
		return withExistingParent(ResourceLocationHelper.getPath(block), new TechnologicaLocation("hollow_log")).texture("side", blockTexture(null)).texture("end", blockTexture(null) + "_top").texture("inside", blockTexture(null) + "_solid").renderType("translucent");
	}

	private static ResourceLocation blockTexture(Block block) {
		ResourceLocation name = key(block);
		return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath());
	}

	private static ResourceLocation key(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block);
	}

	public static ModelFile tableModel(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block), new TechnologicaLocation("table"), "planks", texture).renderType(renderType);
	}

	public static ModelFile displayModel(Block block) {
		return withExistingParent(ResourceLocationHelper.getPath(block), new TechnologicaLocation("display")).texture("case", blockTexture(block)).texture("base", new ResourceLocation("block/black_wool")).renderType("cutout_mipped");
	}

	public static ModelFile buttonInventoryModel(Block block, String renderType) {
		return withExistingParent(ResourceLocationHelper.getPath(block) + "_inventory", "block/button_inventory").renderType(renderType);
	}

	public static ModelFile pressurePlate(Block block, String renderType) {
		return withExistingParent(ResourceLocationHelper.getPath(block), "block/pressure_plate_up").renderType(renderType);
	}

	public static ModelFile basin(String name, String top, String bottom, String side) {
		return withExistingParent(name, new TechnologicaLocation("basin_model")).texture("particle", side).texture("top", top).texture("bottom", bottom).texture("side", side).texture("inside", bottom);
	}

	public static ModelFile leafyCrystalModel(Block block) {
		ResourceLocation location = ForgeRegistries.BLOCKS.getKey(block);
		return singleTexture(ResourceLocationHelper.getPath(block), new TechnologicaLocation("leafy_crystal"), "crystal", blockTexture(block)).texture("crystal", new ResourceLocation(location.getNamespace(), "block/" + location.getPath())).renderType("translucent");
	}

	public static ModelFile hexagonalCrystalModel(Block block) {
		ResourceLocation location = ForgeRegistries.BLOCKS.getKey(block);
		return singleTexture(ResourceLocationHelper.getPath(block), new TechnologicaLocation("hexagonal_crystal"), "crystal", blockTexture(block)).texture("crystal", new ResourceLocation(location.getNamespace(), "block/" + location.getPath())).renderType("translucent");
	}

	public static ModelFile cubicCrystalModel(Block block) {
		ResourceLocation location = ForgeRegistries.BLOCKS.getKey(block);
		return singleTexture(ResourceLocationHelper.getPath(block), new TechnologicaLocation("cubic_crystal"), "crystal", blockTexture(block)).texture("crystal", new ResourceLocation(location.getNamespace(), "block/" + location.getPath())).renderType("translucent");
	}

	public static ModelFile spikyCrystalModel(Block block) {
		ResourceLocation location = ForgeRegistries.BLOCKS.getKey(block);
		return singleTexture(ResourceLocationHelper.getPath(block), new TechnologicaLocation("spiky_crystal"), "crystal", blockTexture(block)).texture("crystal", new ResourceLocation(location.getNamespace(), "block/" + location.getPath())).renderType("translucent");
	}

	public static ModelFile.ExistingModelFile getExistingFile(ResourceLocation path) {
		ModelFile.ExistingModelFile ret = new ModelFile.ExistingModelFile(extendWithFolder(path), helper);
		ret.assertExistence();
		return ret;
	}

	public static String name(Block block) {
		return key(block).getPath();
	}

	/*
	 * KEEP
	 */

	public static void buttonAll(Block block, ResourceLocation texture, String renderType) {
		button(block, texture, renderType);
		buttonPressed(block, texture, renderType);
		buttonInventory(block, texture, renderType);
	}

	private static ModelBuilder<BlockModelBuilder> button(Block block, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block), "block/button").texture("texture", texture).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> buttonPressed(Block block, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_pressed", "block/button_pressed").texture("texture", texture).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> buttonInventory(Block block, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_inventory", "block/button_inventory").texture("texture", texture).renderType(renderType);
	}

	public static ModelBuilder<BlockModelBuilder> chair(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block), new TechnologicaLocation("chair"), "planks", texture).renderType(renderType);
	}

	public static ModelBuilder<BlockModelBuilder> hexagonalCrystal(Block block, ResourceLocation texture) {
		return singleTexture(ResourceLocationHelper.getPath(block), new TechnologicaLocation("block/hexagonal_crystal"), "crystal", texture).renderType("translucent");
	}

	public static ModelBuilder<BlockModelBuilder> leafyCrystal(Block block, ResourceLocation texture) {
		return singleTexture(ResourceLocationHelper.getPath(block), new TechnologicaLocation("block/leafy_crystal"), "crystal", texture).renderType("translucent");
	}

	public static ModelBuilder<BlockModelBuilder> spikyCrystal(Block block, ResourceLocation texture) {
		return singleTexture(ResourceLocationHelper.getPath(block), new TechnologicaLocation("block/spiky_crystal"), "crystal", texture).renderType("translucent");
	}

	public static ModelBuilder<BlockModelBuilder> cubicCrystal(Block block, ResourceLocation texture) {
		return singleTexture(ResourceLocationHelper.getPath(block), new TechnologicaLocation("block/cubic_crystal"), "crystal", texture).renderType("translucent");
	}

	public static void bush4Stage(Block block, ResourceLocation texture, String renderType) {
		bush(block, 0, texture, renderType);
		bush(block, 1, texture, renderType);
		bush(block, 2, texture, renderType);
		bush(block, 3, texture, renderType);
	}

	public static void bushTall8Stage(Block block, ResourceLocation texture, String renderType) {
		bushBottom(block, 0, texture, renderType);
		bushBottom(block, 1, texture, renderType);
		bushBottom(block, 2, texture, renderType);
		bushBottom(block, 3, texture, renderType);
		bushBottom(block, 4, texture, renderType);
		bushBottom(block, 5, texture, renderType);
		bushBottom(block, 6, texture, renderType);
		bushBottom(block, 7, texture, renderType);
		bushTop(block, 0, texture, renderType);
		bushTop(block, 1, texture, renderType);
		bushTop(block, 2, texture, renderType);
		bushTop(block, 3, texture, renderType);
		bushTop(block, 4, texture, renderType);
		bushTop(block, 5, texture, renderType);
		bushTop(block, 6, texture, renderType);
		bushTop(block, 7, texture, renderType);
	}

	private static ModelBuilder<BlockModelBuilder> bush(Block block, int stage, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_stage" + stage, "block/cross").texture("cross", texture + "_stage" + stage).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> bushBottom(Block block, int stage, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_bottom_stage" + stage, "block/cross").texture("cross", texture + "_bottom_stage" + stage).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> bushTop(Block block, int stage, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_top_stage" + stage, "block/cross").texture("cross", texture + "_top_stage" + stage).renderType(renderType);
	}

	public static void crop4Stage(Block block, ResourceLocation texture, String renderType) {
		crop(block, 0, texture, renderType);
		crop(block, 1, texture, renderType);
		crop(block, 2, texture, renderType);
		crop(block, 3, texture, renderType);
	}

	public static void crop8Stage(Block block, ResourceLocation texture, String renderType) {
		crop(block, 0, texture, renderType);
		crop(block, 1, texture, renderType);
		crop(block, 2, texture, renderType);
		crop(block, 3, texture, renderType);
		crop(block, 4, texture, renderType);
		crop(block, 5, texture, renderType);
		crop(block, 6, texture, renderType);
		crop(block, 7, texture, renderType);
	}

	public static void cropTall8Stage(Block block, ResourceLocation texture, String renderType) {
		cropBottom(block, 0, texture, renderType);
		cropBottom(block, 1, texture, renderType);
		cropBottom(block, 2, texture, renderType);
		cropBottom(block, 3, texture, renderType);
		cropBottom(block, 4, texture, renderType);
		cropBottom(block, 5, texture, renderType);
		cropBottom(block, 6, texture, renderType);
		cropBottom(block, 7, texture, renderType);
		cropTop(block, 0, texture, renderType);
		cropTop(block, 1, texture, renderType);
		cropTop(block, 2, texture, renderType);
		cropTop(block, 3, texture, renderType);
		cropTop(block, 4, texture, renderType);
		cropTop(block, 5, texture, renderType);
		cropTop(block, 6, texture, renderType);
		cropTop(block, 7, texture, renderType);
	}

	private static ModelBuilder<BlockModelBuilder> crop(Block block, int stage, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_stage" + stage, "block/crop").texture("crop", texture + "_stage" + stage).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> cropBottom(Block block, int stage, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_bottom_stage" + stage, "block/crop").texture("crop", texture + "_bottom_stage" + stage).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> cropTop(Block block, int stage, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_top_stage" + stage, "block/crop").texture("crop", texture + "_top_stage" + stage).renderType(renderType);
	}

	public static void gourdStem8Stage(Block block, ResourceLocation texture, String renderType) {
		gourdStem(block, 0, texture, renderType);
		gourdStem(block, 1, texture, renderType);
		gourdStem(block, 2, texture, renderType);
		gourdStem(block, 3, texture, renderType);
		gourdStem(block, 4, texture, renderType);
		gourdStem(block, 5, texture, renderType);
		gourdStem(block, 6, texture, renderType);
		gourdStem(block, 7, texture, renderType);
	}

	private static ModelBuilder<BlockModelBuilder> gourdStem(Block block, int stage, ResourceLocation texture, String renderType) {
		return withExistingParent(name(block) + "_stage" + stage, "block/stem_growth" + stage).texture("stem", texture).renderType(renderType);
	}

	public static ModelBuilder<BlockModelBuilder> cubeAll(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(name(block), "block/cube_all", "all", texture).renderType(renderType);
	}

	public static ModelBuilder<BlockModelBuilder> cubeAll(Block block, ResourceLocation texture) {
		return singleTexture(name(block), "block/cube_all", "all", texture);
	}

	public static void logAll(Block block, ResourceLocation side, ResourceLocation end) {
		cubeColumn(block, side, end);
		cubeColumnHorizontal(block, side, end);
	}

	public static void logAll(Block block, ResourceLocation side, ResourceLocation end, String renderType) {
		cubeColumn(block, side, end, renderType);
		cubeColumnHorizontal(block, side, end, renderType);
	}

	public static ModelBuilder<BlockModelBuilder> cubeColumn(Block block, ResourceLocation side, ResourceLocation end) {
		return withExistingParent(name(block), "block/cube_column").texture("side", side).texture("end", end).renderType("solid");
	}

	public static ModelBuilder<BlockModelBuilder> cubeColumn(Block block, ResourceLocation side, ResourceLocation end, String renderType) {
		return withExistingParent(name(block), "block/cube_column").texture("side", side).texture("end", end).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> cubeColumnHorizontal(Block block, ResourceLocation side, ResourceLocation end) {
		return withExistingParent(name(block) + "_horizontal", "block/cube_column_horizontal").texture("side", side).texture("end", end).renderType("solid");
	}

	private static ModelBuilder<BlockModelBuilder> cubeColumnHorizontal(Block block, ResourceLocation side, ResourceLocation end, String renderType) {
		return withExistingParent(name(block) + "_horizontal", "block/cube_column_horizontal").texture("side", side).texture("end", end).renderType(renderType);
	}

	public static void doorAll(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		doorBottomLeft(block, bottom, top, renderType);
		doorBottomLeftOpen(block, bottom, top, renderType);
		doorBottomRight(block, bottom, top, renderType);
		doorBottomRightOpen(block, bottom, top, renderType);
		doorTopLeft(block, bottom, top, renderType);
		doorTopLeftOpen(block, bottom, top, renderType);
		doorTopRight(block, bottom, top, renderType);
		doorTopRightOpen(block, bottom, top, renderType);
	}

	private static ModelBuilder<BlockModelBuilder> doorBottomLeft(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_bottom_left", "block/door_bottom_left").texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> doorBottomLeftOpen(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_bottom_left_open", "block/door_bottom_left_open").texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> doorBottomRight(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_bottom_right", "block/door_bottom_right").texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> doorBottomRightOpen(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_bottom_right_open", "block/door_bottom_right_open").texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> doorTopLeft(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_top_left", "block/door_top_left").texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> doorTopLeftOpen(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_top_left_open", "block/door_top_left_open").texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> doorTopRight(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_top_right", "block/door_top_right").texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> doorTopRightOpen(Block block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_top_right_open", "block/door_top_right_open").texture("bottom", bottom).texture("top", top).renderType(renderType);
	}

	public static void fenceAll(Block block, ResourceLocation texture, String renderType) {
		fencePost(block, texture, renderType);
		fenceSide(block, texture, renderType);
		fenceInventory(block, texture, renderType);
	}

	private static ModelBuilder<BlockModelBuilder> fencePost(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_post", new ResourceLocation("fence_post"), texture).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> fenceSide(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_side", new ResourceLocation("fence_side"), texture).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> fenceInventory(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_inventory", new ResourceLocation("fence_inventory"), texture).renderType(renderType);
	}

	public static void fenceGateAll(Block block, ResourceLocation texture, String renderType) {
		fenceGate(block, texture, renderType);
		fenceGateOpen(block, texture, renderType);
		fenceGateWall(block, texture, renderType);
		fenceGateWallOpen(block, texture, renderType);
	}

	private static ModelBuilder<BlockModelBuilder> fenceGate(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block), new ResourceLocation("template_fence_gate"), texture).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> fenceGateOpen(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_open", new ResourceLocation("template_fence_gate_open"), texture).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> fenceGateWall(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_wall", new ResourceLocation("template_fence_gate_wall"), texture).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> fenceGateWallOpen(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_wall_open", new ResourceLocation("template_fence_gate_wall_open"), texture).renderType(renderType);
	}

	public static ModelBuilder<BlockModelBuilder> fluid(Block block) {
		return getBuilder(name(block)).texture("particle", "minecraft:block/water_still");
	}

	public static void pressurePlateAll(Block block, ResourceLocation texture, String renderType) {
		pressurePlate(block, texture, renderType);
		pressurePlateDown(block, texture, renderType);
	}

	public static ModelBuilder<BlockModelBuilder> pottedSapling(Block block, ResourceLocation plant, String renderType) {
		return withExistingParent(name(block), "block/flower_pot_cross").texture("plant", plant).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> pressurePlate(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block), new ResourceLocation("pressure_plate_up"), texture).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> pressurePlateDown(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_down", new ResourceLocation("pressure_plate_down"), texture).renderType(renderType);
	}

	public static ModelBuilder<BlockModelBuilder> sapling(Block block, ResourceLocation cross, String renderType) {
		return withExistingParent(name(block), "block/cross").texture("cross", cross).renderType(renderType);
	}

	public static ModelBuilder<BlockModelBuilder> sign(Block block, ResourceLocation texture, String renderType) {
		return getBuilder(name(block)).texture("particle", texture).renderType(renderType);
	}

	public static void slabAll(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, String renderType) {
		slab(block, bottom, side, top, renderType);
		slabTop(block, bottom, side, top, renderType);
	}

	private static ModelBuilder<BlockModelBuilder> slab(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, String renderType) {
		return withExistingParent(name(block), "block/slab").texture("bottom", bottom).texture("side", side).texture("top", top).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> slabTop(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_top", "block/slab_top").texture("bottom", bottom).texture("side", side).texture("top", top).renderType(renderType);
	}

	public static void stairsAll(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, String renderType) {
		stairs(block, bottom, side, top, renderType);
		stairsInner(block, bottom, side, top, renderType);
		stairsOuter(block, bottom, side, top, renderType);
	}

	private static ModelBuilder<BlockModelBuilder> stairs(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, String renderType) {
		return withExistingParent(name(block), "block/stairs").texture("bottom", bottom).texture("side", side).texture("top", top).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> stairsInner(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_inner", "block/inner_stairs").texture("bottom", bottom).texture("side", side).texture("top", top).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> stairsOuter(Block block, ResourceLocation bottom, ResourceLocation side, ResourceLocation top, String renderType) {
		return withExistingParent(name(block) + "_outer", "block/outer_stairs").texture("bottom", bottom).texture("side", side).texture("top", top).renderType(renderType);
	}

	public static ModelBuilder<BlockModelBuilder> table(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block), new TechnologicaLocation("table"), "planks", texture).renderType(renderType);
	}

	public static void trapdoorAll(Block block, ResourceLocation texture, String renderType) {
		trapdoorBottom(block, texture, renderType);
		trapdoorOpen(block, texture, renderType);
		trapdoorTop(block, texture, renderType);
	}

	private static ModelBuilder<BlockModelBuilder> trapdoorBottom(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_bottom", new ResourceLocation("template_orientable_trapdoor_bottom"), texture).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> trapdoorOpen(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_open", new ResourceLocation("template_orientable_trapdoor_open"), texture).renderType(renderType);
	}

	private static ModelBuilder<BlockModelBuilder> trapdoorTop(Block block, ResourceLocation texture, String renderType) {
		return singleTexture(ResourceLocationHelper.getPath(block) + "_top", new ResourceLocation("template_orientable_trapdoor_top"), texture).renderType(renderType);
	}
}
