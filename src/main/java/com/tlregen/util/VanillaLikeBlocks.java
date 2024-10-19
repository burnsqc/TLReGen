package com.tlregen.util;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PotatoBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.BlockHitResult;

/**
 * <p>This class contains helper methods for mimicking vanilla blocks. This class is primarily intended to avoid creation of new block classes to handle stuff like flammability, such as {@link #leavesBlock}. In some cases these methods allow for minor flexibility of the subclass, including allowing overriding of methods that are hard-coded to return a specific value, such as {@link #grainCropBlock}.</p>
 */
public final class VanillaLikeBlocks {

	public static final CropBlock grainCropBlock(Supplier<Item> seeds) {
		return new CropBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP)) {
			@Override
			protected ItemLike getBaseSeedId() {
				return seeds.get();
			}
		};
	}

	public static final PotatoBlock replantableCropBlock(Supplier<Item> seeds) {
		return new PotatoBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP)) {
			@Override
			protected ItemLike getBaseSeedId() {
				return seeds.get();
			}
		};
	}

	public static final BeetrootBlock seededCropBlock(Supplier<Item> seeds) {
		return new BeetrootBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP)) {
			@Override
			protected ItemLike getBaseSeedId() {
				return seeds.get();
			}
		};
	}

	public static final SweetBerryBushBlock bushCropBlock(Supplier<Item> clone) {
		return new SweetBerryBushBlock(BlockBehaviour.Properties.of().randomTicks().noCollission().sound(SoundType.SWEET_BERRY_BUSH)) {
			@Override
			public ItemStack getCloneItemStack(BlockGetter p_57256_, BlockPos p_57257_, BlockState p_57258_) {
				return new ItemStack(clone.get());
			}

			@Override
			public InteractionResult use(BlockState p_57275_, Level p_57276_, BlockPos p_57277_, Player p_57278_, InteractionHand p_57279_, BlockHitResult p_57280_) {
				int i = p_57275_.getValue(AGE);
				boolean flag = i == 3;
				if (!flag && p_57278_.getItemInHand(p_57279_).is(Items.BONE_MEAL)) {
					return InteractionResult.PASS;
				} else if (i > 1) {
					int j = 1 + p_57276_.random.nextInt(2);
					popResource(p_57276_, p_57277_, new ItemStack(clone.get(), j + (flag ? 1 : 0)));
					p_57276_.playSound((Player) null, p_57277_, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + p_57276_.random.nextFloat() * 0.4F);
					p_57276_.setBlock(p_57277_, p_57275_.setValue(AGE, Integer.valueOf(1)), 2);
					return InteractionResult.sidedSuccess(p_57276_.isClientSide);
				} else {
					return super.use(p_57275_, p_57276_, p_57277_, p_57278_, p_57279_, p_57280_);
				}
			}

			@Override
			public BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
				return BlockPathTypes.DAMAGE_OTHER;
			}

			@Override
			public BlockPathTypes getAdjacentBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, BlockPathTypes originalType) {
				return BlockPathTypes.DANGER_OTHER;
			}

			@Override
			public int getFlammability(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 60;
			}

			@Override
			public int getFireSpreadSpeed(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 100;
			}
		};
	}

	public static final LeavesBlock leavesBlock() {
		return new LeavesBlock(BlockBehaviour.Properties.of().strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion()) {
			@Override
			public int getFlammability(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 30;
			}

			@Override
			public int getFireSpreadSpeed(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 60;
			}
		};
	}

	public static final RotatedPillarBlock logBlock() {
		return new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)) {
			@Override
			public int getFlammability(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 5;
			}

			@Override
			public int getFireSpreadSpeed(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 5;
			}
		};
	}

	public static final Block planksBlock() {
		return new Block(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)) {
			@Override
			public int getFlammability(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 5;
			}

			@Override
			public int getFireSpreadSpeed(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 20;
			}
		};
	}

	public static final SlabBlock slabBlock() {
		return new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)) {
			@Override
			public int getFlammability(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 5;
			}

			@Override
			public int getFireSpreadSpeed(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 20;
			}
		};
	}

	public static final StairBlock stairBlock(Supplier<BlockState> state) {
		return new StairBlock(state, BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)) {
			@Override
			public int getFlammability(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 5;
			}

			@Override
			public int getFireSpreadSpeed(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 20;
			}
		};
	}

	public static final Block bookshelfBlock() {
		return new Block(BlockBehaviour.Properties.of().strength(1.5F).sound(SoundType.WOOD)) {
			@Override
			public int getFlammability(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 30;
			}

			@Override
			public int getFireSpreadSpeed(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 20;
			}
		};
	}

	public static final FenceBlock fenceBlock() {
		return new FenceBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)) {
			@Override
			public int getFlammability(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 5;
			}

			@Override
			public int getFireSpreadSpeed(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 20;
			}
		};
	}

	public static final FenceGateBlock fenceGateBlock() {
		return new FenceGateBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), SoundEvents.FENCE_GATE_CLOSE, SoundEvents.FENCE_GATE_OPEN) {
			@Override
			public int getFlammability(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 5;
			}

			@Override
			public int getFireSpreadSpeed(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, Direction faceIn) {
				return 20;
			}
		};
	}

	public static final ButtonBlock woodenButton(BlockSetType p_278239_, FeatureFlag... p_278229_) {
		BlockBehaviour.Properties blockbehaviour$properties = BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY);
		if (p_278229_.length > 0) {
			blockbehaviour$properties = blockbehaviour$properties.requiredFeatures(p_278229_);
		}

		return new ButtonBlock(blockbehaviour$properties, p_278239_, 30, true);
	}

	public static final Boolean never(BlockState p_50779_, BlockGetter p_50780_, BlockPos p_50781_, EntityType<?> p_50782_) {
		return false;
	}
}