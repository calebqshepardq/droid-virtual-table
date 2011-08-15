package org.amphiprion.droidvirtualtable.driver;

import java.io.File;

import org.amphiprion.droidvirtualtable.entity.Game;

public interface ImportDeckDriver {
	boolean accept(File filename);

	void importDeck(ImportDeckListener listener, Game game, File file);
}
