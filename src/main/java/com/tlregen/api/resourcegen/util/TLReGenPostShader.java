package com.tlregen.api.resourcegen.util;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public final class TLReGenPostShader {
	private final List<String> targets;
	private final List<Pass> passes;

	public TLReGenPostShader(final List<String> targets, final List<Pass> passes) {
		this.targets = targets;
		this.passes = passes;
	}

	public record Pass(String name, String intarget, String outtarget, String auxtargets, List<Uniform> uniforms) {

		public Pass(String name, String intarget, String outtarget) {
			this(name, intarget, outtarget, null, null);
		}

		public Pass(String name, String intarget, String outtarget, List<Uniform> uniforms) {
			this(name, intarget, outtarget, null, uniforms);
		}

		public static class Uniform {
			public String name;
			public List<Float> values;

			public Uniform(String name, float value1) {
				this.name = name;
				this.values = List.of(value1);
			}

			public Uniform(String name, float value1, float value2) {
				this.name = name;
				this.values = List.of(value1, value2);
			}

			public Uniform(String name, float value1, float value2, float value3) {
				this.name = name;
				this.values = List.of(value1, value2, value3);
			}

			public Uniform(String name, float value1, float value2, float value3, float value4) {
				this.name = name;
				this.values = List.of(value1, value2, value3, value4);
			}
		}
	}

	public JsonObject serialize() {
		JsonObject json = new JsonObject();
		JsonArray targetArray = new JsonArray();
		JsonArray passesArray = new JsonArray();
		targets.forEach(targetArray::add);
		json.add("targets", targetArray);
		passes.forEach((pass) -> {
			JsonObject passObject = new JsonObject();
			passObject.addProperty("name", pass.name);
			passObject.addProperty("intarget", pass.intarget);
			passObject.addProperty("outtarget", pass.outtarget);
			if (pass.uniforms != null) {
				JsonArray uniformArray = new JsonArray();
				pass.uniforms.forEach((uniform) -> {
					JsonObject uniformObject = new JsonObject();
					uniformObject.addProperty("name", uniform.name);
					JsonArray valueArray = new JsonArray();
					uniform.values.forEach((value) -> {
						valueArray.add(value);
					});
					uniformObject.add("values", valueArray);
					uniformArray.add(uniformObject);
				});
				passObject.add("uniforms", uniformArray);
			}
			passesArray.add(passObject);
		});
		json.add("passes", passesArray);
		return json;
	}
}
