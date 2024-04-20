package edu.redwoods.cis18.scam.remotegardenproject2.be;

public class MoistureAlertSystem {
	private static int DRY_THRESHOLD = 20;
	private static int WET_THRESHOLD = 80;

	public static int getDryThreshold() {
		return DRY_THRESHOLD;
	}

	public static void setDryThreshold(int dryThreshold) {
		DRY_THRESHOLD = dryThreshold;
	}

	public static int getWetThreshold() {
		return WET_THRESHOLD;
	}

	public static void setWetThreshold(int wetThreshold) {
		WET_THRESHOLD = wetThreshold;
	}
}