package org.amphiprion.droidvirtualtable.driver.octgn;

import java.io.File;

import org.amphiprion.droidvirtualtable.driver.ImportSetDriver;
import org.amphiprion.droidvirtualtable.driver.ImportSetListener;
import org.amphiprion.droidvirtualtable.entity.Game;

public class ImportOctgnSetDriver implements ImportSetDriver {

	@Override
	public boolean accept(File filename) {
		return filename.getName().endsWith(".o8s");
	}

	@Override
	public void importSet(ImportSetListener listener, Game game, File file) {
		ImportOctgnSetTask task = new ImportOctgnSetTask(listener, game);
		task.execute(file);

	}

}
