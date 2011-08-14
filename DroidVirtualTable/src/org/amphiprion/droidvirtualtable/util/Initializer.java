package org.amphiprion.droidvirtualtable.util;

import java.io.File;

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.driver.octgn.ImportOctgnGameDriver;

import android.content.Context;
import android.os.Environment;

public class Initializer {
	private static boolean init = false;

	public static void init(Context ctx) {
		if (!init) {
			init = true;
			new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY).mkdirs();
			new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY_GAMES).mkdirs();
			new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY_TABLES).mkdirs();
			new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY_IMPORT_GAMES).mkdirs();
			new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY_IMPORT_SETS).mkdirs();
			new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY_IMPORT_DECKS).mkdirs();

			DriverManager.register(new ImportOctgnGameDriver());
		}
	}
}
