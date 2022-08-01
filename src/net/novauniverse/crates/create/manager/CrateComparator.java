package net.novauniverse.crates.create.manager;

import java.util.Comparator;

import net.novauniverse.crates.create.CrateData;

public class CrateComparator implements Comparator<CrateData> {
	@Override
	public int compare(CrateData a, CrateData b) {
		if (a.getSortingNumber() < b.getSortingNumber()) {
			return -1;
		}
		if (a.getSortingNumber() > b.getSortingNumber()) {
			return 1;
		}
		return 0;
	}
}