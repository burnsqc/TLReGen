package com.tlregen.api.setup;

import java.util.Map;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.tlregen.util.TextUtil;

import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientModEventListeners {
	protected String modMarker;
	protected static final Logger LOGGER = LogManager.getLogger("TLREGEN");
	protected static final Marker SETUP = MarkerManager.getMarker("SETUP");

	Supplier<Map<SkullBlock.Type, ModelLayerLocation>> skullRenderers;

	ClientModEventListeners(String modid) {
		modMarker = "(" + TextUtil.stringToAllCapsName(modid) + ")";
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent
	public final void onEntityRenderersEvent$CreateSkullModels(final EntityRenderersEvent.CreateSkullModels event) {
		skullRenderers.get().forEach((k, v) -> event.registerSkullModel(k, new SkullModel(event.getEntityModelSet().bakeLayer(v))));
		LOGGER.info(SETUP, modMarker + " SKULLS REGISTERED ");
	}
}
