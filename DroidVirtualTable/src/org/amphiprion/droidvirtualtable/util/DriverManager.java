package org.amphiprion.droidvirtualtable.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.amphiprion.droidvirtualtable.driver.ImportDeckDriver;
import org.amphiprion.droidvirtualtable.driver.ImportGameDriver;
import org.amphiprion.droidvirtualtable.driver.ImportSetDriver;
import org.amphiprion.droidvirtualtable.driver.ImportTableDriver;

public class DriverManager {
	private static List<ImportGameDriver> importGameDrivers = new ArrayList<ImportGameDriver>();
	private static List<ImportSetDriver> importSetDrivers = new ArrayList<ImportSetDriver>();
	private static List<ImportDeckDriver> importDeckDrivers = new ArrayList<ImportDeckDriver>();
	private static List<ImportTableDriver> importTableDrivers = new ArrayList<ImportTableDriver>();

	public static void register(ImportGameDriver driver) {
		importGameDrivers.add(driver);
	}

	public static void register(ImportSetDriver driver) {
		importSetDrivers.add(driver);
	}

	public static void register(ImportDeckDriver driver) {
		importDeckDrivers.add(driver);
	}

	public static void register(ImportTableDriver driver) {
		importTableDrivers.add(driver);
	}

	public static ImportGameDriver getImportGameDriver(File file) {
		for (ImportGameDriver task : importGameDrivers) {
			if (task.accept(file)) {
				return task;
			}
		}
		return null;
	}

	public static ImportSetDriver getImportSetDriver(File file) {
		for (ImportSetDriver task : importSetDrivers) {
			if (task.accept(file)) {
				return task;
			}
		}
		return null;
	}

	public static ImportDeckDriver getImportDeckDriver(File file) {
		for (ImportDeckDriver task : importDeckDrivers) {
			if (task.accept(file)) {
				return task;
			}
		}
		return null;
	}

	public static ImportTableDriver getImportTableDriver(File file) {
		for (ImportTableDriver task : importTableDrivers) {
			if (task.accept(file)) {
				return task;
			}
		}
		return null;
	}
}
