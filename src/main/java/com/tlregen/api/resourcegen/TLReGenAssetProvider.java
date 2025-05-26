package com.tlregen.api.resourcegen;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.tlregen.api.resourcegen.util.TLReGenModels;

import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public abstract class TLReGenAssetProvider implements DataProvider {
	protected String modID;
	protected PackOutput packOutput;
	protected ExistingFileHelper helper;
	protected TLReGenModels models;
	protected final PackOutput.Target target = PackOutput.Target.RESOURCE_PACK;
	protected final DynamicOps<JsonElement> dynamicOps = JsonOps.INSTANCE;

	@Override
	public String getName() {
		return "assets" + modID;
	}

	protected abstract void populate();
}
