package com.tlregen.api.resourcegen.data;

import com.tlregen.TLReGen;
import com.tlregen.api.resourcegen.TLReGenMasterResourceGenerator;

import net.minecraftforge.common.data.GlobalLootModifierProvider;

public abstract class TLRGLootModifierGenerator extends GlobalLootModifierProvider {
	public TLRGLootModifierGenerator() {
		super(TLReGenMasterResourceGenerator.packOutput, TLReGen.MOD_ID);
	}

	@Override
	public String getName() {
		return "data." + TLReGen.MOD_ID + ".loot_modifier";
	}
}
