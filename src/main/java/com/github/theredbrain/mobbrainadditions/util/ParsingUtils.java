package com.github.theredbrain.mobbrainadditions.util;

public class ParsingUtils {

	public static int parseInt(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException numberFormatException) {
			return 0;
		}
	}

}
