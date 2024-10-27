package com.tlregen.api.resourcegen.assets;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.google.gson.JsonObject;
import com.tlregen.api.resourcegen.TLReGenAssetProvider;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public abstract class TLReGenLang extends TLReGenAssetProvider {
	private final Map<String, String> resources = new TreeMap<>();

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		resources.clear();
		populate();
		if (resources.isEmpty()) {
			return CompletableFuture.allOf();
		} else {
			JsonObject json = new JsonObject();
			resources.forEach(json::addProperty);
			return DataProvider.saveStable(cache, json, packOutput.createPathProvider(target, "lang").json(new ResourceLocation(modID, "en_us")));
		}
	}

	@Override
	public final String getName() {
		return super.getName() + ".lang";
	}

	/*
	 * HELPER METHODS
	 */

	protected final void addDeath(ResourceKey<DamageType> damageType, String translation) {
		add("death.attack." + damageType.location().getPath(), "%1$s " + translation);
		add("death.attack." + damageType.location().getPath() + ".player", "%1$s " + translation + " whilst fighting %2$s");
	}

	protected final void addEffect(Supplier<? extends MobEffect> mobEffect, String translation) {
		add(mobEffect.get().getDescriptionId(), translation);
	}

	protected final void addEntityType(Supplier<? extends EntityType<?>> entity, String translation) {
		add(entity.get().getDescriptionId(), translation);
	}

	protected final void addItem(Supplier<? extends Item> item, String translation) {
		add(item.get().getDescriptionId(), translation);
	}

	protected final void addItemGroup(Supplier<? extends CreativeModeTab> itemGroup, String translation) {
		add(itemGroup.get().getDisplayName().getString(), translation);
	}

	protected final void add(String key, String value) {
		if (resources.put(key, value) != null) {
			throw new IllegalStateException("Duplicate translation key " + key);
		}
	}
}
