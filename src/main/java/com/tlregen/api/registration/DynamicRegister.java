package com.tlregen.api.registration;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.core.Holder.Reference;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class DynamicRegister<T> {
	private final ResourceKey<? extends Registry<T>> registryKey;
	private final String modid;
	private final Map<ResourceKey<T>, Supplier<T>> entries = new LinkedHashMap<>();
	public BootstapContext<T> bootstrapContext;

	private DynamicRegister(ResourceKey<? extends Registry<T>> registryKey, String modid) {
		this.registryKey = registryKey;
		this.modid = modid;
	}

	public static <B> DynamicRegister<B> create(ResourceKey<? extends Registry<B>> key, String modid) {
		return new DynamicRegister<>(key, modid);
	}

	public Map<ResourceKey<T>, Supplier<T>> getEntries() {
		return entries;
	}

	public ResourceKey<T> register(final String name) {
		ResourceKey<T> ret;
		if (this.registryKey != null)
			ret = ResourceKey.create(registryKey, new ResourceLocation(modid, name));
		else
			throw new IllegalStateException("Could not create RegistryObject in DynamicRegister");
		if (entries.putIfAbsent(ret, null) != null) {
			throw new IllegalArgumentException("Duplicate registration " + name);
		}
		return ret;
	}

	public ResourceKey<T> register(final String name, Supplier<T> entry) {
		ResourceKey<T> ret;
		if (this.registryKey != null) {
			ret = ResourceKey.create(registryKey, new ResourceLocation(modid, name));
		} else {
			throw new IllegalStateException("Could not create RegistryObject in DynamicRegister");
		}
		if (entries.putIfAbsent(ret, entry) != null) {
			throw new IllegalArgumentException("Duplicate registration " + name);
		}
		return ret;
	}

	public Reference<T> asHolder(ResourceKey<T> key) {
		return bootstrapContext.lookup(registryKey).getOrThrow(key);
	}
}
