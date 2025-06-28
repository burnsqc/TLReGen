package com.tlregen.api.setup;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.tlregen.TLReGen;
import com.tlregen.util.TextUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

class ClientModEventListeners {
	protected String modMarker;

	Supplier<Map<SkullBlock.Type, ModelLayerLocation>> skullModels;
	Supplier<Map<ModelLayerLocation, LayerDefinition>> layerDefinitions;
	Supplier<Map<EntityType<? extends Entity>, EntityRendererProvider<Entity>>> entityRenderers;
	Supplier<Map<BlockEntityType<? extends BlockEntity>, BlockEntityRendererProvider<BlockEntity>>> blockEntityRenderers;
	Supplier<List<ResourceLocation>> additionalModels;
	Supplier<Map<Integer, Block>> blockColorHandlers;
	Supplier<Map<ResourceLocation, DimensionSpecialEffects>> dimensionSpecialEffects;
	Supplier<Map<ParticleType<ParticleOptions>, ParticleProvider.Sprite<ParticleOptions>>> particleSprites;
	Supplier<Map<ParticleType<ParticleOptions>, ParticleEngine.SpriteParticleRegistration<ParticleOptions>>> particleSpriteSets;
	Supplier<Map<MenuType<? extends AbstractContainerMenu>, MenuScreens.ScreenConstructor<AbstractContainerMenu, ?>>> screens;
	Supplier<Map<SkullBlock.Type, ResourceLocation>> skullTextures;
	Supplier<Map<Fluid, RenderType>> fluidRenderTypes;
	Supplier<Map<RenderType, BufferBuilder>> renderTypes;

	ClientModEventListeners(String modid) {
		modMarker = "(" + TextUtil.stringToAllCapsName(modid) + ")";
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent
	protected final void onEntityRenderersEvent$CreateSkullModels(final EntityRenderersEvent.CreateSkullModels event) {
		if (skullModels != null) {
			skullModels.get().forEach((k, v) -> event.registerSkullModel(k, new SkullModel(event.getEntityModelSet().bakeLayer(v))));
			TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " SKULL MODELS CREATED ");
		}
	}

	@SubscribeEvent
	protected final void onEntityRenderersEvent$RegisterLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
		if (layerDefinitions != null) {
			layerDefinitions.get().forEach((k, v) -> event.registerLayerDefinition(k, () -> v));
			TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " LAYER DEFINITIONS REGISTERED ");
		}
	}

	@SubscribeEvent
	protected final void onEntityRenderersEvent$RegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		if (entityRenderers != null) {
			entityRenderers.get().forEach((k, v) -> event.registerEntityRenderer(k, v));
			TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " ENTITY RENDERERS REGISTERED ");
		}
		if (blockEntityRenderers != null) {
			blockEntityRenderers.get().forEach((k, v) -> event.registerBlockEntityRenderer(k, v));
			TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " BLOCK ENTITY RENDERERS REGISTERED ");
		}
	}

	@SubscribeEvent
	protected final void onModelEvent$RegisterAdditional(final ModelEvent.RegisterAdditional event) {
		if (additionalModels != null) {
			additionalModels.get().forEach((model) -> event.register(model));
			TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " ADDITIONAL MODELS REGISTERED ");
		}
	}

	@SubscribeEvent
	protected final void onRegisterColorHandlersEvent$Block(final RegisterColorHandlersEvent.Block event) {
		if (blockColorHandlers != null) {
			blockColorHandlers.get().forEach((k, v) -> event.register((blockState, blockAndTintGetter, blockPos, tintIndex) -> blockAndTintGetter != null && blockPos != null ? k : -1, v));
			TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " BLOCK COLOR HANDLERS REGISTERED ");
		}
	}

	@SubscribeEvent
	protected final void onRegisterDimensionSpecialEffectsEvent(final RegisterDimensionSpecialEffectsEvent event) {
		if (dimensionSpecialEffects != null) {
			dimensionSpecialEffects.get().forEach((k, v) -> event.register(k, v));
			TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " DIMENSION SPECIAL EFFECTS REGISTERED ");
		}
	}

	@SubscribeEvent
	protected final void onRegisterParticleProvidersEvent(final RegisterParticleProvidersEvent event) {
		if (particleSprites != null) {
			particleSprites.get().forEach((k, v) -> event.registerSprite(k, v));
			TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " PARTICLE PROVIDERS REGISTERED ");
		}
		if (particleSpriteSets != null) {
			particleSpriteSets.get().forEach((k, v) -> event.registerSpriteSet(k, v));
			TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " PARTICLE PROVIDERS REGISTERED ");
		}
	}

	@SubscribeEvent
	protected final void onFMLClientSetupEvent(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			if (screens != null) {
				screens.get().forEach((k, v) -> MenuScreens.register(k, v));
				TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " SCREENS REGISTERED ");
			}
			if (skullTextures != null) {
				skullTextures.get().forEach((k, v) -> SkullBlockRenderer.SKIN_BY_TYPE.put(k, v));
				TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " SKULL TEXTURES REGISTERED ");
			}
			if (fluidRenderTypes != null) {
				fluidRenderTypes.get().forEach((k, v) -> ItemBlockRenderTypes.setRenderLayer(k, v));
				TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " FLUID RENDER TYPES SET ");
			}
			if (renderTypes != null) {
				SortedMap<RenderType, BufferBuilder> fixedBuffers = ObfuscationReflectionHelper.getPrivateValue(RenderBuffers.class, Minecraft.getInstance().renderBuffers(), "f_110093_");
				renderTypes.get().forEach((k, v) -> fixedBuffers.put(k, v));
				TLReGen.LOGGER.info(MasterSetupExecutor.SETUP, modMarker + " RENDER TYPES ADDED ");
			}
		});
	}
}
