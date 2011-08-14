package org.amphiprion.droidvirtualtable.driver;

import java.io.File;

public interface ImportGameDriver {
	boolean accept(File filename);

	void importGame(ImportGameListener listener, File file);
}
