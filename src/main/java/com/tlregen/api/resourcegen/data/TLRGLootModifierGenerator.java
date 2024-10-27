package com.tlregen.api.resourcegen.data;

import com.tlregen.api.resourcegen.MasterResourceGenerator;

import net.minecraftforge.common.data.GlobalLootModifierProvider;

public abstract class TLRGLootModifierGenerator extends GlobalLootModifierProvider {
	public TLRGLootModifierGenerator() {
		super(MasterResourceGenerator.packOutput, MasterResourceGenerator.modID);
	}

	@Override
	public String getName() {
		return "data." + "MODID" + ".loot_modifier";
	}
}
