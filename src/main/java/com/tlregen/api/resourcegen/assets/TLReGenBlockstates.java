package com.tlregen.api.resourcegen.assets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenAssetProvider;
import com.tlregen.api.resourcegen.util.TLReGenConfiguredModel;
import com.tlregen.api.resourcegen.util.TLReGenVariantBlockStateBuilder;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.IGeneratedBlockState;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ExistingFileHelper.ResourceType;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class TLReGenBlockstates extends TLReGenAssetProvider {
	private final static Map<Block, IGeneratedBlockState> resources = new HashMap<>();
	private final Map<ResourceLocation, BlockModelBuilder> resources2 = new HashMap<>();
	private BiFunction<ResourceLocation, ExistingFileHelper, BlockModelBuilder> bifunc = BlockModelBuilder::new;

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
				list.add(DataProvider.saveStable(cache, json, packOutput.createPathProvider(target, "blockstates").json(ForgeRegistries.BLOCKS.getKey(key))));
			});
			return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
		}
	}

	@Override
	public final String getName() {
		return super.getName() + ".blockstates";
	}

	/*
	 * HELPER METHODS
	 */

	public static class ConfiguredModelList {
		private final List<TLReGenConfiguredModel> models;

		private ConfiguredModelList(List<TLReGenConfiguredModel> models) {
			Preconditions.checkArgument(!models.isEmpty());
			this.models = models;
		}

		public ConfiguredModelList(TLReGenConfiguredModel... models) {
			this(Arrays.asList(models));
		}

		public JsonElement toJSON() {
			if (models.size() == 1) {
				return models.get(0).toJSON(false);
			} else {
				JsonArray ret = new JsonArray();
				for (TLReGenConfiguredModel m : models) {
					ret.add(m.toJSON(true));
				}
				return ret;
			}
		}

		public ConfiguredModelList append(TLReGenConfiguredModel... models) {
			return new ConfiguredModelList(ImmutableList.<TLReGenConfiguredModel>builder().addAll(this.models).add(models).build());
		}
	}

	public static TLReGenVariantBlockStateBuilder getVariantBuilder(Block b) {
		if (resources.containsKey(b)) {
			IGeneratedBlockState old = resources.get(b);
			Preconditions.checkState(old instanceof TLReGenVariantBlockStateBuilder);
			return (TLReGenVariantBlockStateBuilder) old;
		} else {
			TLReGenVariantBlockStateBuilder ret = new TLReGenVariantBlockStateBuilder(b);
			resources.put(b, ret);
			return ret;
		}
	}

	public MultiPartBlockStateBuilder getMultipartBuilder(Block b) {
		if (resources.containsKey(b)) {
			IGeneratedBlockState old = resources.get(b);
			Preconditions.checkState(old instanceof MultiPartBlockStateBuilder);
			return (MultiPartBlockStateBuilder) old;
		} else {
			MultiPartBlockStateBuilder ret = new MultiPartBlockStateBuilder(b);
			resources.put(b, ret);
			return ret;
		}
	}

	private static ResourceLocation key(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block);
	}

	private static String name(Block block) {
		return key(block).getPath();
	}

	public static ResourceLocation blockTexture(Block block) {
		ResourceLocation name = key(block);
		return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath());
	}

	private ResourceLocation extend(ResourceLocation rl, String suffix) {
		return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
	}

	public static void simpleBlock(Block block, ModelFile model) {
		simpleBlock(block, new TLReGenConfiguredModel(model));
	}

	public ModelFile cubeAll(Block block) {
		return cubeAll(name(block), blockTexture(block));
	}

	public void simpleBlock(Block block) {
		simpleBlock(block, cubeAll(block));
	}

	private static void simpleBlock(Block block, TLReGenConfiguredModel... models) {
		getVariantBuilder(block).partialState().setModels(models);
	}

	public void logBlockWithRenderType(RotatedPillarBlock block, String renderType) {
		axisBlockWithRenderType(block, blockTexture(block), extend(blockTexture(block), "_top"), renderType);
	}

	public void axisBlockWithRenderType(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end, String renderType) {
		axisBlock(block, cubeColumn(name(block), side, end).renderType(renderType), cubeColumnHorizontal(name(block) + "_horizontal", side, end).renderType(renderType));
	}

	public void axisBlock(RotatedPillarBlock block, ModelFile vertical, ModelFile horizontal) {
		getVariantBuilder(block).partialState().with(RotatedPillarBlock.AXIS, Axis.Y).modelForState().modelFile(vertical).addModel().partialState().with(RotatedPillarBlock.AXIS, Axis.Z).modelForState().modelFile(horizontal).rotationX(90).addModel().partialState().with(RotatedPillarBlock.AXIS, Axis.X).modelForState().modelFile(horizontal).rotationX(90).rotationY(90).addModel();
	}

	public void stairsBlockWithRenderType(StairBlock block, ResourceLocation texture, String renderType) {
		stairsBlockWithRenderType(block, texture, texture, texture, renderType);
	}

	private void stairsBlockWithRenderType(StairBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, String renderType) {
		stairsBlockInternalWithRenderType(block, key(block).toString(), side, bottom, top, ResourceLocation.tryParse(renderType));
	}

	private void stairsBlockInternalWithRenderType(StairBlock block, String baseName, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
		ModelFile stairs = stairs(baseName, side, bottom, top).renderType(renderType);
		ModelFile stairsInner = stairsInner(baseName + "_inner", side, bottom, top).renderType(renderType);
		ModelFile stairsOuter = stairsOuter(baseName + "_outer", side, bottom, top).renderType(renderType);
		stairsBlock(block, stairs, stairsInner, stairsOuter);
	}

	private void stairsBlock(StairBlock block, ModelFile stairs, ModelFile stairsInner, ModelFile stairsOuter) {
		getVariantBuilder(block).forAllStatesExcept(state -> {
			Direction facing = state.getValue(StairBlock.FACING);
			Half half = state.getValue(StairBlock.HALF);
			StairsShape shape = state.getValue(StairBlock.SHAPE);
			int yRot = (int) facing.getClockWise().toYRot(); // Stairs model is rotated 90 degrees clockwise for some reason
			if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
				yRot += 270; // Left facing stairs are rotated 90 degrees clockwise
			}
			if (shape != StairsShape.STRAIGHT && half == Half.TOP) {
				yRot += 90; // Top stairs are rotated 90 degrees clockwise
			}
			yRot %= 360;
			boolean uvlock = yRot != 0 || half == Half.TOP; // Don't set uvlock for states that have no rotation
			return TLReGenConfiguredModel.builder().modelFile(shape == StairsShape.STRAIGHT ? stairs : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? stairsInner : stairsOuter).rotationX(half == Half.BOTTOM ? 0 : 180).rotationY(yRot).uvLock(uvlock).build();
		}, StairBlock.WATERLOGGED);
	}

	public void slabBlock(SlabBlock block, ModelFile bottom, ModelFile top, ModelFile doubleslab) {
		getVariantBuilder(block).partialState().with(SlabBlock.TYPE, SlabType.BOTTOM).addModels(new TLReGenConfiguredModel(bottom)).partialState().with(SlabBlock.TYPE, SlabType.TOP).addModels(new TLReGenConfiguredModel(top)).partialState().with(SlabBlock.TYPE, SlabType.DOUBLE).addModels(new TLReGenConfiguredModel(doubleslab));
	}

	public void buttonBlock(ButtonBlock block, ModelFile button, ModelFile buttonPressed) {
		getVariantBuilder(block).forAllStates(state -> {
			Direction facing = state.getValue(ButtonBlock.FACING);
			AttachFace face = state.getValue(ButtonBlock.FACE);
			boolean powered = state.getValue(ButtonBlock.POWERED);
			return TLReGenConfiguredModel.builder().modelFile(powered ? buttonPressed : button).rotationX(face == AttachFace.FLOOR ? 0 : (face == AttachFace.WALL ? 90 : 180)).rotationY((int) (face == AttachFace.CEILING ? facing : facing.getOpposite()).toYRot()).uvLock(face == AttachFace.WALL).build();
		});
	}

	public void pressurePlateBlock(PressurePlateBlock block, ModelFile pressurePlate, ModelFile pressurePlateDown) {
		getVariantBuilder(block).partialState().with(PressurePlateBlock.POWERED, true).addModels(new TLReGenConfiguredModel(pressurePlateDown)).partialState().with(PressurePlateBlock.POWERED, false).addModels(new TLReGenConfiguredModel(pressurePlate));
	}

	public void pressurePlateBlockWithRenderType(PressurePlateBlock block, ResourceLocation texture, String renderType) {
		ModelFile pressurePlate = pressurePlate(name(block), texture).renderType(renderType);
		ModelFile pressurePlateDown = pressurePlateDown(name(block) + "_down", texture).renderType(renderType);
		pressurePlateBlock(block, pressurePlate, pressurePlateDown);
	}

	public void signBlock(StandingSignBlock signBlock, WallSignBlock wallSignBlock, ModelFile sign) {
		simpleBlock(signBlock, sign);
		simpleBlock(wallSignBlock, sign);
	}

	private void fourWayBlock(CrossCollisionBlock block, ModelFile post, ModelFile side) {
		MultiPartBlockStateBuilder builder = getMultipartBuilder(block).part().modelFile(post).addModel().end();
		fourWayMultipart(builder, side);
	}

	private void fourWayMultipart(MultiPartBlockStateBuilder builder, ModelFile side) {
		PipeBlock.PROPERTY_BY_DIRECTION.entrySet().forEach(e -> {
			Direction dir = e.getKey();
			if (dir.getAxis().isHorizontal()) {
				builder.part().modelFile(side).rotationY((((int) dir.toYRot()) + 180) % 360).uvLock(true).addModel().condition(e.getValue(), true);
			}
		});
	}

	public void fenceBlockWithRenderType(FenceBlock block, ResourceLocation texture, String renderType) {
		String baseName = key(block).toString();
		fourWayBlock(block, fencePost(baseName + "_post", texture).renderType(renderType), fenceSide(baseName + "_side", texture).renderType(renderType));
	}

	public void fenceGateBlockWithRenderType(FenceGateBlock block, ResourceLocation texture, String renderType) {
		fenceGateBlockInternalWithRenderType(block, key(block).toString(), texture, ResourceLocation.tryParse(renderType));
	}

	private void fenceGateBlockInternalWithRenderType(FenceGateBlock block, String baseName, ResourceLocation texture, ResourceLocation renderType) {
		ModelFile gate = fenceGate(baseName, texture).renderType(renderType);
		ModelFile gateOpen = fenceGateOpen(baseName + "_open", texture).renderType(renderType);
		ModelFile gateWall = fenceGateWall(baseName + "_wall", texture).renderType(renderType);
		ModelFile gateWallOpen = fenceGateWallOpen(baseName + "_wall_open", texture).renderType(renderType);
		fenceGateBlock(block, gate, gateOpen, gateWall, gateWallOpen);
	}

	private void fenceGateBlock(FenceGateBlock block, ModelFile gate, ModelFile gateOpen, ModelFile gateWall, ModelFile gateWallOpen) {
		getVariantBuilder(block).forAllStatesExcept(state -> {
			ModelFile model = gate;
			if (state.getValue(FenceGateBlock.IN_WALL)) {
				model = gateWall;
			}
			if (state.getValue(FenceGateBlock.OPEN)) {
				model = model == gateWall ? gateWallOpen : gateOpen;
			}
			return TLReGenConfiguredModel.builder().modelFile(model).rotationY((int) state.getValue(FenceGateBlock.FACING).toYRot()).uvLock(true).build();
		}, FenceGateBlock.POWERED);
	}

	public void doorBlockWithRenderType(DoorBlock block, ResourceLocation bottom, ResourceLocation top, String renderType) {
		doorBlockInternalWithRenderType(block, key(block).toString(), bottom, top, ResourceLocation.tryParse(renderType));
	}

	private void doorBlockInternalWithRenderType(DoorBlock block, String baseName, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
		ModelFile bottomLeft = doorBottomLeft(baseName + "_bottom_left", bottom, top).renderType(renderType);
		ModelFile bottomLeftOpen = doorBottomLeftOpen(baseName + "_bottom_left_open", bottom, top).renderType(renderType);
		ModelFile bottomRight = doorBottomRight(baseName + "_bottom_right", bottom, top).renderType(renderType);
		ModelFile bottomRightOpen = doorBottomRightOpen(baseName + "_bottom_right_open", bottom, top).renderType(renderType);
		ModelFile topLeft = doorTopLeft(baseName + "_top_left", bottom, top).renderType(renderType);
		ModelFile topLeftOpen = doorTopLeftOpen(baseName + "_top_left_open", bottom, top).renderType(renderType);
		ModelFile topRight = doorTopRight(baseName + "_top_right", bottom, top).renderType(renderType);
		ModelFile topRightOpen = doorTopRightOpen(baseName + "_top_right_open", bottom, top).renderType(renderType);
		doorBlock(block, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen);
	}

	private void doorBlock(DoorBlock block, ModelFile bottomLeft, ModelFile bottomLeftOpen, ModelFile bottomRight, ModelFile bottomRightOpen, ModelFile topLeft, ModelFile topLeftOpen, ModelFile topRight, ModelFile topRightOpen) {
		getVariantBuilder(block).forAllStatesExcept(state -> {
			int yRot = ((int) state.getValue(DoorBlock.FACING).toYRot()) + 90;
			boolean right = state.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
			boolean open = state.getValue(DoorBlock.OPEN);
			boolean lower = state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
			if (open) {
				yRot += 90;
			}
			if (right && open) {
				yRot += 180;
			}
			yRot %= 360;

			ModelFile model = null;
			if (lower && right && open) {
				model = bottomRightOpen;
			} else if (lower && !right && open) {
				model = bottomLeftOpen;
			}
			if (lower && right && !open) {
				model = bottomRight;
			} else if (lower && !right && !open) {
				model = bottomLeft;
			}
			if (!lower && right && open) {
				model = topRightOpen;
			} else if (!lower && !right && open) {
				model = topLeftOpen;
			}
			if (!lower && right && !open) {
				model = topRight;
			} else if (!lower && !right && !open) {
				model = topLeft;
			}

			return TLReGenConfiguredModel.builder().modelFile(model).rotationY(yRot).build();
		}, DoorBlock.POWERED);
	}

	public void trapdoorBlockWithRenderType(TrapDoorBlock block, ResourceLocation texture, boolean orientable, String renderType) {
		trapdoorBlockInternalWithRenderType(block, key(block).toString(), texture, orientable, ResourceLocation.tryParse(renderType));
	}

	private void trapdoorBlockInternalWithRenderType(TrapDoorBlock block, String baseName, ResourceLocation texture, boolean orientable, ResourceLocation renderType) {
		ModelFile bottom = orientable ? trapdoorOrientableBottom(baseName + "_bottom", texture).renderType(renderType) : trapdoorBottom(baseName + "_bottom", texture).renderType(renderType);
		ModelFile top = orientable ? trapdoorOrientableTop(baseName + "_top", texture).renderType(renderType) : trapdoorTop(baseName + "_top", texture).renderType(renderType);
		ModelFile open = orientable ? trapdoorOrientableOpen(baseName + "_open", texture).renderType(renderType) : trapdoorOpen(baseName + "_open", texture).renderType(renderType);
		trapdoorBlock(block, bottom, top, open, orientable);
	}

	private void trapdoorBlock(TrapDoorBlock block, ModelFile bottom, ModelFile top, ModelFile open, boolean orientable) {
		getVariantBuilder(block).forAllStatesExcept(state -> {
			int xRot = 0;
			int yRot = ((int) state.getValue(TrapDoorBlock.FACING).toYRot()) + 180;
			boolean isOpen = state.getValue(TrapDoorBlock.OPEN);
			if (orientable && isOpen && state.getValue(TrapDoorBlock.HALF) == Half.TOP) {
				xRot += 180;
				yRot += 180;
			}
			if (!orientable && !isOpen) {
				yRot = 0;
			}
			yRot %= 360;
			return TLReGenConfiguredModel.builder().modelFile(isOpen ? open : state.getValue(TrapDoorBlock.HALF) == Half.TOP ? top : bottom).rotationX(xRot).rotationY(yRot).build();
		}, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED);
	}

	ModelBuilder<BlockModelBuilder> fencePost(String name, ResourceLocation texture) {
		return singleTexture(name, "block/fence_post", texture);
	}

	private ModelBuilder<BlockModelBuilder> singleTexture(String name, String parent, ResourceLocation texture) {
		return singleTexture(name, new ResourceLocation(parent), texture);
	}

	public ModelBuilder<BlockModelBuilder> singleTexture(String name, ResourceLocation parent, ResourceLocation texture) {
		return singleTexture(name, parent, "texture", texture);
	}

	private ModelBuilder<BlockModelBuilder> singleTexture(String name, String parent, String textureKey, ResourceLocation texture) {
		return singleTexture(name, new ResourceLocation(parent), textureKey, texture);
	}

	public ModelBuilder<BlockModelBuilder> singleTexture(String name, ResourceLocation parent, String textureKey, ResourceLocation texture) {
		return withExistingParent(name, parent).texture(textureKey, texture);
	}

	public ModelBuilder<BlockModelBuilder> withExistingParent(String name, ResourceLocation parent) {
		return getBuilder(name).parent(getExistingFile(parent));
	}

	public ModelBuilder<BlockModelBuilder> withExistingParent(String name, String parent) {
		return withExistingParent(name, new ResourceLocation(parent));
	}

	public BlockModelBuilder getBuilder(String path) {
		ResourceLocation outputLoc = extendWithFolder(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(modID, path));
		helper.trackGenerated(outputLoc, new ResourceType(PackType.CLIENT_RESOURCES, ".json", "models"));
		return resources2.computeIfAbsent(outputLoc, loc -> bifunc.apply(loc, helper));
	}

	private ResourceLocation extendWithFolder(ResourceLocation rl) {
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

	public ModelBuilder<BlockModelBuilder> cubeAll(String name, ResourceLocation texture) {
		return singleTexture(name, "block/cube_all", "all", texture);
	}

	public ModelBuilder<BlockModelBuilder> cubeColumn(String name, ResourceLocation side, ResourceLocation end) {
		return withExistingParent(name, "block/cube_column").texture("side", side).texture("end", end);
	}

	ModelBuilder<BlockModelBuilder> cubeColumnHorizontal(String name, ResourceLocation side, ResourceLocation end) {
		return withExistingParent(name, "block/cube_column_horizontal").texture("side", side).texture("end", end);
	}

	public ModelBuilder<BlockModelBuilder> stairs(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return sideBottomTop(name, "block/stairs", side, bottom, top);
	}

	ModelBuilder<BlockModelBuilder> stairsOuter(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return sideBottomTop(name, "block/outer_stairs", side, bottom, top);
	}

	ModelBuilder<BlockModelBuilder> stairsInner(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return sideBottomTop(name, "block/inner_stairs", side, bottom, top);
	}

	private ModelBuilder<BlockModelBuilder> sideBottomTop(String name, String parent, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return withExistingParent(name, parent).texture("side", side).texture("bottom", bottom).texture("top", top);
	}

	public ModelBuilder<BlockModelBuilder> pressurePlate(String name, ResourceLocation texture) {
		return singleTexture(name, "block/pressure_plate_up", texture);
	}

	public ModelBuilder<BlockModelBuilder> pressurePlateDown(String name, ResourceLocation texture) {
		return singleTexture(name, "block/pressure_plate_down", texture);
	}

	ModelBuilder<BlockModelBuilder> fenceSide(String name, ResourceLocation texture) {
		return singleTexture(name, "block/fence_side", texture);
	}

	public ModelBuilder<BlockModelBuilder> fenceGate(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_fence_gate", texture);
	}

	ModelBuilder<BlockModelBuilder> fenceGateOpen(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_fence_gate_open", texture);
	}

	ModelBuilder<BlockModelBuilder> fenceGateWall(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_fence_gate_wall", texture);
	}

	ModelBuilder<BlockModelBuilder> fenceGateWallOpen(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_fence_gate_wall_open", texture);
	}

	ModelBuilder<BlockModelBuilder> doorBottomLeft(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_bottom_left", bottom, top);
	}

	ModelBuilder<BlockModelBuilder> doorBottomLeftOpen(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_bottom_left_open", bottom, top);
	}

	ModelBuilder<BlockModelBuilder> doorBottomRight(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_bottom_right", bottom, top);
	}

	ModelBuilder<BlockModelBuilder> doorBottomRightOpen(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_bottom_right_open", bottom, top);
	}

	ModelBuilder<BlockModelBuilder> doorTopLeft(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_top_left", bottom, top);
	}

	ModelBuilder<BlockModelBuilder> doorTopLeftOpen(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_top_left_open", bottom, top);
	}

	ModelBuilder<BlockModelBuilder> doorTopRight(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_top_right", bottom, top);
	}

	ModelBuilder<BlockModelBuilder> doorTopRightOpen(String name, ResourceLocation bottom, ResourceLocation top) {
		return door(name, "door_top_right_open", bottom, top);
	}

	private ModelBuilder<BlockModelBuilder> door(String name, String model, ResourceLocation bottom, ResourceLocation top) {
		return withExistingParent(name, "block/" + model).texture("bottom", bottom).texture("top", top);
	}

	public ModelBuilder<BlockModelBuilder> trapdoorOrientableBottom(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_orientable_trapdoor_bottom", texture);
	}

	ModelBuilder<BlockModelBuilder> trapdoorOrientableTop(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_orientable_trapdoor_top", texture);
	}

	ModelBuilder<BlockModelBuilder> trapdoorOrientableOpen(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_orientable_trapdoor_open", texture);
	}

	ModelBuilder<BlockModelBuilder> trapdoorBottom(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_trapdoor_bottom", texture);
	}

	ModelBuilder<BlockModelBuilder> trapdoorTop(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_trapdoor_top", texture);
	}

	ModelBuilder<BlockModelBuilder> trapdoorOpen(String name, ResourceLocation texture) {
		return singleTexture(name, "block/template_trapdoor_open", texture);
	}
}
