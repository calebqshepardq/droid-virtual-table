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
import org.amphiprion.droidvirtualtable.entity.Table;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View used to display a table in the table list.
 * 
 * @author amphiprion
 * 
 */
public class TableSummaryView extends LinearLayout {
	/** the linked deck. */
	private Table table;

	/**
	 * Construct an deck view.
	 * 
	 * @param context
	 *            the context
	 * @param table
	 *            the entity
	 */
	public TableSummaryView(Context context, Table table) {
		super(context);
		this.table = table;
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_black_background_states));

		addView(createIcon());

		addView(createAccountLayout());

	}

	/**
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Create the collection icon view.
	 * 
	 * @return the view
	 */
	private View createIcon() {
		ImageView img = new ImageView(getContext());
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imglp.gravity = Gravity.CENTER_VERTICAL;
		imglp.rightMargin = 5;
		img.setLayoutParams(imglp);

		// File f = new File(Environment.getExternalStorageDirectory() + "/" +
		// ApplicationConstants.DIRECTORY_GAMES + "/" + deck.getGame().getId() +
		// "/"
		// + deck.getGame().getImageName());
		// Bitmap bitmap = null;
		// if (f.exists()) {
		// bitmap = BitmapFactory.decodeFile(f.toString());
		// }
		// if (bitmap == null) {
		// bitmap = BitmapFactory.decodeResource(getResources(),
		// R.drawable.no_deck_image);
		// }
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_game_image);

		img.setImageBitmap(bitmap);
		return img;
	}

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
		t.setText(table.getName());
		t.setTextSize(16);
		t.setTypeface(Typeface.DEFAULT_BOLD);
		t.setTextColor(getContext().getResources().getColor(R.color.white));
		accountLayout.addView(t);

		t = new TextView(getContext());
		tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		t.setLayoutParams(tlp);
		t.setText(getContext().getString(R.string.number_of_locations, table.getMaxLocation()));
		accountLayout.addView(t);

		// accountLayout.addView(createParties());

		return accountLayout;
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
