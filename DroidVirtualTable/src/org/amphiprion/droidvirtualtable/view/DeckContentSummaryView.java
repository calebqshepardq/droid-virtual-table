/*
 * @copyright 2010 Gerald Jacobson
 * @license GNU General Public License
 * 
 * This file is part of My Accounts.
 *
 * My Accounts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * My Accounts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with My Accounts.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.amphiprion.droidvirtualtable.view;

import org.amphiprion.droidvirtualtable.R;
import org.amphiprion.droidvirtualtable.entity.DeckContent;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View used to display a deck in the deck list.
 * 
 * @author amphiprion
 * 
 */
public class DeckContentSummaryView extends LinearLayout {
	/** the linked deck. */
	private DeckContent deckContent;
	private TextView quantityView;

	/**
	 * Construct an deck view.
	 * 
	 * @param context
	 *            the context
	 * @param deck
	 *            the entity
	 */
	public DeckContentSummaryView(Context context, DeckContent deckContent) {
		super(context);
		this.deckContent = deckContent;
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_black_background_states));

		addView(createAccountLayout());

	}

	/**
	 * @return the deck
	 */
	public DeckContent getDeckContent() {
		return deckContent;
	}

	/**
	 * Create the collection icon view.
	 * 
	 * @return the view
	 */

	/**
	 * Create the account layout view
	 * 
	 * @return the view
	 */
	private View createAccountLayout() {
		LinearLayout accountLayout = new LinearLayout(getContext());
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3);
		accountLayout.setOrientation(VERTICAL);
		accountLayout.setLayoutParams(aclp);
		TextView t = new TextView(getContext());
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		t.setLayoutParams(tlp);
		t.setText(deckContent.getName());
		t.setTextSize(16);
		t.setTypeface(Typeface.DEFAULT_BOLD);
		t.setTextColor(getContext().getResources().getColor(R.color.white));
		accountLayout.addView(t);

		quantityView = new TextView(getContext());
		tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		quantityView.setLayoutParams(tlp);
		updateQuantity();
		accountLayout.addView(quantityView);

		// accountLayout.addView(createParties());

		return accountLayout;
	}

	public void updateQuantity() {
		quantityView.setText(getContext().getString(R.string.quantity, deckContent.getQuantity()));
	}

	// private View createParties() {
	// LinearLayout accountLayout = new LinearLayout(getContext());
	// LayoutParams aclp = new
	// LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
	// android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
	// accountLayout.setLayoutParams(aclp);
	// TextView t = new TextView(getContext());
	// LayoutParams tlp = new
	// LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
	// android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
	// t.setLayoutParams(tlp);
	// t.setText("blabla");
	// t.setTextSize(10);
	// accountLayout.addView(t);
	//
	// return accountLayout;
	// }

}
