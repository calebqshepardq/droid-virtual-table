package org.amphiprion.droidvirtualtable;

import java.util.List;

import org.amphiprion.droidvirtualtable.dao.SectionDao;
import org.amphiprion.droidvirtualtable.entity.Deck;
import org.amphiprion.droidvirtualtable.entity.Section;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class EditDeckTabActivity extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_deck);
		TabHost tabHost = getTabHost(); // The activity TabHost
		tabHost.clearAllTabs();
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Deck deck = (Deck) getIntent().getSerializableExtra("DECK");
		Intent intent; // Reusable Intent for each tab

		List<Section> sections = SectionDao.getInstance(this).getSections(deck.getGame().getId());
		Log.d(ApplicationConstants.PACKAGE, "sections:" + sections.size());
		for (Section s : sections) {
			// Create an Intent to launch an Activity for the tab (to be reused)
			intent = new Intent().setClass(this, DeckContentListActivity.class);
			intent.putExtra("DECK", deck);
			intent.putExtra("SECTION", s);
			// Initialize a TabSpec for each tab and add it to the TabHost
			spec = tabHost.newTabSpec(s.getName()).setIndicator(s.getName()).setContent(intent);
			tabHost.addTab(spec);
			Log.d(ApplicationConstants.PACKAGE, "Add tab:" + s.getName());
		}
		for (int i = 0; i < sections.size(); i++) {
			tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = 40;

		}
	}
}
