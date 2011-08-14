package org.amphiprion.droidvirtualtable.driver;

import java.io.File;

import org.amphiprion.droidvirtualtable.entity.Game;

public interface ImportSetDriver {
	boolean accept(File filename);

	void importSet(ImportSetListener listener, Game game, File file);
}
