package com.tlregen.util;

/**
 * This enum contains the Validation Levels used throughout TLReGen.
 * <p>MIN: Simply log information at the debug level.
 * <p>MED: Give detailed logging on errors.
 * <p>MAX: Throw exceptions.
 */
public enum ValidationLevel {
	MIN("minimum"), MED("medium"), MAX("maximum");

	final String level;

	private ValidationLevel(String level) {
		this.level = level;
	}
}
