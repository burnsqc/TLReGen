package com.tlregen.api.resourcegen.util.helpers;

import com.tlregen.api.resourcegen.util.TLReGenSoundDefinition;

import net.minecraft.resources.ResourceLocation;

public interface TLReGenSoundHelper {
	/**
	 * Single Sound
	 * 
	 * @param name
	 * 
	 * @return
	 */
	default TLReGenSoundDefinition sounds(ResourceLocation name) {
		return new TLReGenSoundDefinition().with(sound(name));
	}

	/**
	 * Single Sound with attenuation_distance
	 * 
	 * @param attenuationDistance distance
	 * @param name                ResourceLocation of the path to your .ogg file
	 * 
	 * @return
	 */
	default TLReGenSoundDefinition sounds(int attenuationDistance, ResourceLocation name) {
		return new TLReGenSoundDefinition().with(sound(name).attenuationDistance(attenuationDistance));
	}

	/**
	 * Single Sound with volume
	 * 
	 * @param name
	 * @param volume
	 * 
	 * @return
	 */
	default TLReGenSoundDefinition sounds(ResourceLocation name, float volume) {
		return new TLReGenSoundDefinition().with(sound(name).volume(volume));
	}

	default TLReGenSoundDefinition sounds(ResourceLocation... sounds) {
		TLReGenSoundDefinition definition = new TLReGenSoundDefinition();
		for (ResourceLocation sound : sounds) {
			definition.with(sound(sound));
		}
		return definition;
	}

	static TLReGenSoundDefinition.Sound sound(final ResourceLocation name) {
		return TLReGenSoundDefinition.Sound.sound(name, TLReGenSoundDefinition.SoundType.SOUND);
	}
}
