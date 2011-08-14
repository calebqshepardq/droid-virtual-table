package org.amphiprion.droidvirtualtable.driver;

import org.amphiprion.droidvirtualtable.entity.GameSet;

import android.content.Context;

public interface ImportSetListener {
	void importEnded(boolean succeed, GameSet set);

	Context getContext();
}
