package com.tlregen.api.resourcegen;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;

import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public abstract class TLReGenDataProvider implements DataProvider {
	protected String modID;
	protected PackOutput packOutput;
	protected ExistingFileHelper helper;
	protected final PackOutput.Target target = PackOutput.Target.DATA_PACK;
	protected final DynamicOps<JsonElement> dynamicOps = JsonOps.INSTANCE;

	@Override
	public String getName() {
		return "data." + modID;
	}

	protected abstract void populate();
}
