package org.amphiprion.droidvirtualtable.driver.droidvirtualtable;

import java.io.File;

import org.amphiprion.droidvirtualtable.driver.ImportTableDriver;
import org.amphiprion.droidvirtualtable.driver.ImportTableListener;
import org.amphiprion.droidvirtualtable.entity.Game;

public class ImportDvtTableDriver implements ImportTableDriver {

	@Override
	public boolean accept(File filename) {
		return filename.getName().endsWith(".zip");
	}

	@Override
	public void importTable(ImportTableListener listener, Game game, File file) {
		ImportDvtTableTask task = new ImportDvtTableTask(listener, game);
		task.execute(file);

	}

}
