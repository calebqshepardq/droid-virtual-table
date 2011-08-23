package org.amphiprion.droidvirtualtable.driver;

import org.amphiprion.droidvirtualtable.entity.Deck;

import android.content.Context;

public interface ImportDeckListener {
	void importEnded(boolean succeed, Deck deck, Exception e);

	Context getContext();
}
