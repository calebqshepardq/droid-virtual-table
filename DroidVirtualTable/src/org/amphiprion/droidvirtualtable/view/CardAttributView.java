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
import org.amphiprion.droidvirtualtable.entity.CardValue;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View used to display a deck in the deck list.
 * 
 * @author amphiprion
 * 
 */
public class CardAttributView extends LinearLayout {
	private CardValue cardValue;

	/**
	 * Construct an deck view.
	 * 
	 * @param context
	 *            the context
	 * @param deck
	 *            the entity
	 */
	public CardAttributView(Context context, CardValue cardValue) {
		super(context);
		this.cardValue = cardValue;
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT);
		setLayoutParams(lp);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_black_background_states));

		addView(createPropertyName());

		// addView(createPropertyValue());

	}

	/**
	 * Create the collection icon view.
	 * 
	 * @return the view
	 */
	private View createPropertyName() {
		TextView tv = new TextView(getContext());
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imglp.gravity = Gravity.CENTER_VERTICAL;
		tv.setLayoutParams(imglp);
		tv.setBackgroundColor(getContext().getResources().getColor(R.color.darkGrey));
		// tv.setTextSize(14);
		// tv.setTypeface(Typeface.DEFAULT_BOLD);
		tv.setTextColor(getContext().getResources().getColor(R.color.white));
		tv.setText(cardValue.getProperty().getName() + ": " + cardValue.getValue());
		tv.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		return tv;
	}

	/**
	 * Create the account layout view
	 * 
	 * @return the view
	 */
	private View createPropertyValue() {
		TextView t = new TextView(getContext());
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		t.setLayoutParams(tlp);
		t.setTextSize(16);
		t.setTextColor(getContext().getResources().getColor(R.color.white));
		t.setText("" + cardValue.getValue());
		return t;
	}
}
