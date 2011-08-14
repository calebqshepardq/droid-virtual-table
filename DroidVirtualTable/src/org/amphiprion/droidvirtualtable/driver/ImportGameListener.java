package org.amphiprion.droidvirtualtable.driver;

import org.amphiprion.droidvirtualtable.entity.Game;

import android.content.Context;

public interface ImportGameListener {
	void importEnded(boolean succeed, Game game);

	Context getContext();
}
