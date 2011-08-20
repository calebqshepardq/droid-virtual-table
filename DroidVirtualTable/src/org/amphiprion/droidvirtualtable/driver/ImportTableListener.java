package org.amphiprion.droidvirtualtable.driver;

import org.amphiprion.droidvirtualtable.entity.Table;

import android.content.Context;

public interface ImportTableListener {
	void importEnded(boolean succeed, Table table);

	Context getContext();
}
