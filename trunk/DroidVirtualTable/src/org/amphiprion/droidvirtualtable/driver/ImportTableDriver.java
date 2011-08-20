package org.amphiprion.droidvirtualtable.driver;

import java.io.File;

import org.amphiprion.droidvirtualtable.entity.Game;

public interface ImportTableDriver {
	boolean accept(File filename);

	void importTable(ImportTableListener listener, Game game, File file);
}
