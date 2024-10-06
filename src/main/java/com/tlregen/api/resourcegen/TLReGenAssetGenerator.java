package com.tlregen.api.resourcegen;

import net.minecraft.data.PackOutput;

public abstract class TLReGenAssetGenerator extends TLReGenMasterResourceGenerator {
	protected final PackOutput.Target target = PackOutput.Target.RESOURCE_PACK;

	@Override
	public String getName() {
		return "assets." + super.getName();
	}
}
