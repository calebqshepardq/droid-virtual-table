package org.amphiprion.droidvirtualtable.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.amphiprion.droidvirtualtable.driver.ImportGameDriver;
import org.amphiprion.droidvirtualtable.driver.ImportSetDriver;

public class DriverManager {
	private static List<ImportGameDriver> importGameTasks = new ArrayList<ImportGameDriver>();
	private static List<ImportSetDriver> importSetTasks = new ArrayList<ImportSetDriver>();

	public static void register(ImportGameDriver task) {
		importGameTasks.add(task);
	}

	public static void register(ImportSetDriver task) {
		importSetTasks.add(task);
	}

	public static ImportGameDriver getImportGameTask(File file) {
		for (ImportGameDriver task : importGameTasks) {
			if (task.accept(file)) {
				return task;
			}
		}
		return null;
	}

	public static ImportSetDriver getImportSetTask(File file) {
		for (ImportSetDriver task : importSetTasks) {
			if (task.accept(file)) {
				return task;
			}
		}
		return null;
	}

}
