package org.amphiprion.droidvirtualtable.driver.octgn;

import java.io.File;

import org.amphiprion.droidvirtualtable.driver.ImportDeckDriver;
import org.amphiprion.droidvirtualtable.driver.ImportDeckListener;
import org.amphiprion.droidvirtualtable.entity.Game;

public class ImportOctgnDeckDriver implements ImportDeckDriver {

	@Override
	public boolean accept(File filename) {
		return filename.getName().endsWith(".o8d");
	}

	@Override
	public void importDeck(ImportDeckListener listener, Game game, File file) {
		ImportOctgnDeckTask task = new ImportOctgnDeckTask(listener, game);
		task.execute(file);

	}
}
