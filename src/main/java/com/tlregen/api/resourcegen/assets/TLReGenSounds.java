package com.tlregen.api.resourcegen.assets;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenAssetGenerator;
import com.tlregen.api.resourcegen.mirrors.TLReGenSoundDefinition;
import com.tlregen.api.resourcegen.util.helpers.TLReGenSoundHelper;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public abstract class TLReGenSounds extends TLReGenAssetGenerator implements TLReGenSoundHelper {
	private final Map<String, TLReGenSoundDefinition> resources = new HashMap<>();

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		resources.clear();
		populate();
		if (resources.isEmpty()) {
			return CompletableFuture.allOf();
		} else {
			JsonObject json = new JsonObject();
			resources.forEach((key, value) -> json.add(key, value.serialize()));
			return DataProvider.saveStable(cache, json, packOutput.createPathProvider(target, "").json(new ResourceLocation(modid, "sounds")));
		}
	}

	@Override
	public final String getName() {
		return super.getName() + ".sounds";
	}

	/*
	 * HELPER METHODS
	 */

	protected void add(final Supplier<SoundEvent> soundEventSupplier, final TLReGenSoundDefinition definition) {
		addSounds(soundEventSupplier.get().getLocation().getPath(), definition);
	}

	private void addSounds(final String soundEvent, final TLReGenSoundDefinition definition) {
		if (resources.put(soundEvent, definition) != null) {
			throw new IllegalStateException("Sound '" + modid + ":" + soundEvent + "' already exists");
		}
	}
}
