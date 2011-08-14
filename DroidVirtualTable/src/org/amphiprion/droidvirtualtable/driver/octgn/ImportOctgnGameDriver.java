package org.amphiprion.droidvirtualtable.driver.octgn;

import java.io.File;

import org.amphiprion.droidvirtualtable.driver.ImportGameDriver;
import org.amphiprion.droidvirtualtable.driver.ImportGameListener;

public class ImportOctgnGameDriver implements ImportGameDriver {

	@Override
	public boolean accept(File filename) {
		return filename.getName().endsWith(".o8g");
	}

	@Override
	public void importGame(ImportGameListener listener, File file) {
		ImportOctgnGameTask task = new ImportOctgnGameTask(listener);
		task.execute(file);

	}

}
