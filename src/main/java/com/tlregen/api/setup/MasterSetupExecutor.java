package com.tlregen.api.setup;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tlregen.TLReGen;
import com.tlregen.api.setup.util.TLReGenSpawnPlacements.Data;
import com.tlregen.util.TextUtil;
import com.tlregen.util.ValidationLevel;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * This class handles various setup activities that follow registration. Create an instance of this class in your mod and reference the instance as needed.
 */
public class MasterSetupExecutor {
	protected static final Marker SETUP = MarkerManager.getMarker("SETUP");
	protected String modMarker;
	private CommonModEventListeners commonModEventListeners;
	private CommonForgeEventListeners commonForgeEventListeners;
	private ClientModEventListeners clientModEventListeners;

	/**
	 * Construct a new instance of MasterSetupExecutor.
	 * <p>Example call:<br>
	 * {@code
	 * private static final MasterSetupExecutor MY_MASTER_SETUP_EXECUTOR = new MasterSetupExecutor(MOD_ID); 
	 * }
	 * 
	 * @param modid The mod id for your mod. This is likely already declared in your main mod class for a variety of other purposes.
	 */
	public MasterSetupExecutor(String modid) {
		modMarker = "(" + TextUtil.stringToAllCapsName(modid) + ")";
		commonModEventListeners = new CommonModEventListeners(modid);
		commonForgeEventListeners = new CommonForgeEventListeners(modid);
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners = new ClientModEventListeners(modid);
		}
		TLReGen.LOGGER.info(SETUP, modMarker + " NEW MASTER SETUP EXECUTOR CONSTRUCTED");
	}

	/**
	 * Change the validation level of MasterSetupExecutor. Default is ValidationLevel.MAX.
	 * 
	 * @param validationLevel
	 * 
	 * @return
	 */
	public MasterSetupExecutor validationLevel(ValidationLevel validationLevel) {
		commonModEventListeners.validationLevel = validationLevel;
		commonForgeEventListeners.validationLevel = validationLevel;
		return this;
	}

	/**
	 * Add attributes to entities.
	 * 
	 * @param entityAttributes A supplier of a Map of Entity Types and their respective Attribute Suppliers.
	 */
	public void addEntityAttributes(Supplier<Map<EntityType<? extends LivingEntity>, AttributeSupplier>> entityAttributes) {
		commonModEventListeners.entityAttributes = entityAttributes;
	}

	public void addCapabilities(List<Class<?>> capabilities) {
		commonModEventListeners.capabilities = capabilities;
	}

	public void registerSpawnPlacements(Supplier<Map<EntityType<? extends Entity>, Data>> spawnPlacements) {
		commonModEventListeners.spawnPlacements = spawnPlacements;
	}

	public void addCommands(Supplier<List<LiteralArgumentBuilder<CommandSourceStack>>> commands) {
		commonForgeEventListeners.commands = commands;
	}

	public void addToCompostables(Supplier<Map<Item, Float>> compostables) {
		commonModEventListeners.compostables = compostables;
	}

	public void addToFlowerPot(Supplier<Map<Block, Block>> plants) {
		commonModEventListeners.plants = plants;
	}

	public void addToVillagerWantedItems(Supplier<Set<Item>> wantedItems) {
		commonModEventListeners.wantedItems = wantedItems;
	}

	public void addToVillageButcherTrades(Supplier<List<ItemListing>> butcherTrades) {
		commonForgeEventListeners.butcherTrades = butcherTrades;
	}

	public void addToVillageFarmerTrades(Supplier<List<ItemListing>> farmerTrades) {
		commonForgeEventListeners.farmerTrades = farmerTrades;
	}

	public void addToWanderingTraderGenericTrades(Supplier<List<ItemListing>> genericTrades) {
		commonForgeEventListeners.genericTrades = genericTrades;
	}

	public void addSkullModels(Supplier<Map<SkullBlock.Type, ModelLayerLocation>> skullModels) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.skullModels = skullModels;
		}
	}

	public void addLayerDefinitions(Supplier<Map<ModelLayerLocation, LayerDefinition>> layerDefinitions) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.layerDefinitions = layerDefinitions;
		}
	}

	public void registerEntityRenderers(Supplier<Map<EntityType<? extends Entity>, EntityRendererProvider<Entity>>> entityRenderers) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.entityRenderers = entityRenderers;
		}
	}

	public void registerBlockEntityRenderers(Supplier<Map<BlockEntityType<? extends BlockEntity>, BlockEntityRendererProvider<BlockEntity>>> blockEntityRenderers) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.blockEntityRenderers = blockEntityRenderers;
		}
	}

	public void registerAdditionalModels(Supplier<List<ResourceLocation>> additionalModels) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.additionalModels = additionalModels;
		}
	}

	public void registerColorHandlersBlock(Supplier<Map<Integer, Block>> blockColorHandlers) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.blockColorHandlers = blockColorHandlers;
		}
	}

	public void registerDimensionSpecialEffects(Supplier<Map<ResourceLocation, DimensionSpecialEffects>> dimensionSpecialEffects) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.dimensionSpecialEffects = dimensionSpecialEffects;
		}
	}

	public void registerParticleProvidersSprites(Supplier<Map<ParticleType<ParticleOptions>, ParticleProvider.Sprite<ParticleOptions>>> particleSprites) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.particleSprites = particleSprites;
		}
	}

	public void registerParticleProvidersSpriteSets(Supplier<Map<ParticleType<ParticleOptions>, ParticleEngine.SpriteParticleRegistration<ParticleOptions>>> particleSpriteSets) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.particleSpriteSets = particleSpriteSets;
		}
	}

	public void registerScreens(Supplier<Map<MenuType<? extends AbstractContainerMenu>, MenuScreens.ScreenConstructor<AbstractContainerMenu, ?>>> screens) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.screens = screens;
		}
	}

	public void registerSkullTextures(Supplier<Map<SkullBlock.Type, ResourceLocation>> skullTextures) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.skullTextures = skullTextures;
		}
	}

	public void setFluidRenderTypes(Supplier<Map<Fluid, RenderType>> fluidRenderTypes) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.fluidRenderTypes = fluidRenderTypes;
		}
	}

	public void addRenderTypes(Supplier<Map<RenderType, BufferBuilder>> renderTypes) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientModEventListeners.renderTypes = renderTypes;
		}
	}

	public void addConditionSerializers(Supplier<Set<IConditionSerializer<?>>> conditionSerializers) {
		commonModEventListeners.conditionSerializers = conditionSerializers;
	}
}
