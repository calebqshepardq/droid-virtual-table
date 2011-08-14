package org.amphiprion.droidvirtualtable.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.amphiprion.droidvirtualtable.driver.ImportGameDriver;

public class DriverManager {
	private static List<ImportGameDriver> importGameTasks = new ArrayList<ImportGameDriver>();

	public static void register(ImportGameDriver task) {
		importGameTasks.add(task);
	}

	public static ImportGameDriver getImportGameTask(File file) {
		for (ImportGameDriver task : importGameTasks) {
			if (task.accept(file)) {
				return task;
			}
		}
		return null;
	}

}
