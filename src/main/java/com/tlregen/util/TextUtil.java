package com.tlregen.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

public class TextUtil {
	public static String stringToCapsName(String string) {
		String words[] = string.split("_");
		String name = "";

		for (String word : words) {
			String first = word.substring(0, 1);
			String afterFirst = word.substring(1);
			name += first.toUpperCase() + afterFirst + " ";
		}

		return name.trim();
	}

	public static String stringToProperName(String string) {
		String words[] = string.split("_");
		String name = "";

		if (!words[0].equals("the")) {
			name = "The ";
		}

		for (String word : words) {
			String first = word.substring(0, 1);
			String afterFirst = word.substring(1);
			name += first.toUpperCase() + afterFirst + " ";
		}

		return name.trim();
	}

	public static String stringToAllCapsName(String string) {
		String words[] = string.split("_");
		String name = "";

		for (String word : words) {
			name += word.toUpperCase() + " ";
		}

		return name.trim();
	}

	public static String getPath(ResourceKey<?> resourceKey) {
		return resourceKey.location().getPath().toString();
	}

	public static String getPath(Holder<?> holder) {
		return holder.unwrapKey().get().location().getPath().toString();
	}
}