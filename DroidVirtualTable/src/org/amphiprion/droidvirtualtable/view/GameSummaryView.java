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

import java.io.File;

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.DeckListActivity;
import org.amphiprion.droidvirtualtable.R;
import org.amphiprion.droidvirtualtable.SetListActivity;
import org.amphiprion.droidvirtualtable.entity.Game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View used to display a game in the game list.
 * 
 * @author amphiprion
 * 
 */
public class GameSummaryView extends LinearLayout {
	/** the linked game. */
	private Game game;
	private static Interpolator interpolator = new BounceInterpolator();

	/**
	 * Construct an account view.
	 * 
	 * @param context
	 *            the context
	 * @param game
	 *            the game entity
	 */
	public GameSummaryView(Context context, Game game) {
		super(context);
		this.game = game;
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_black_background_states));

		addView(createIcon());

		addView(createAccountLayout());
		addView(createSetIcon());
		addView(createDeckIcon());

	}

	/**
	 * @return the collection
	 */
	public Game getGame() {
		return game;
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

		File f = new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY_GAMES + "/" + game.getId() + "/" + game.getImageName());
		Bitmap bitmap = null;
		if (f.exists()) {
			bitmap = BitmapFactory.decodeFile(f.toString());
		}
		if (bitmap == null) {
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_game_image);
		}

		img.setImageBitmap(bitmap);
		return img;
	}

	private View createSetIcon() {
		final ImageView img = new ImageView(getContext());
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imglp.gravity = Gravity.CENTER_VERTICAL;
		imglp.rightMargin = 5;
		img.setLayoutParams(imglp);
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ScaleAnimation anim = new ScaleAnimation(0.5f, 1, 0.5f, 1, 35f, 35f);
				anim.setDuration(350);
				anim.setInterpolator(interpolator);
				img.startAnimation(anim);
				postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent i = new Intent(getContext(), SetListActivity.class);
						i.putExtra("GAME", game);
						((Activity) getContext()).startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_MANAGE_SET);
					}
				}, 400);
			}
		});
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.edit_set);

		img.setImageBitmap(bitmap);
		return img;
	}

	private View createDeckIcon() {
		final ImageView img = new ImageView(getContext());
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imglp.gravity = Gravity.CENTER_VERTICAL;
		imglp.rightMargin = 5;
		img.setLayoutParams(imglp);
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ScaleAnimation anim = new ScaleAnimation(0.5f, 1, 0.5f, 1, 35f, 35f);
				anim.setDuration(350);
				anim.setInterpolator(interpolator);
				img.startAnimation(anim);
				postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent i = new Intent(getContext(), DeckListActivity.class);
						i.putExtra("GAME", game);
						((Activity) getContext()).startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_MANAGE_DECK);
					}
				}, 400);
			}
		});

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.edit_deck);

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
		t.setText(game.getName());
		t.setTextSize(16);
		t.setTypeface(Typeface.DEFAULT_BOLD);
		t.setTextColor(getContext().getResources().getColor(R.color.white));
		accountLayout.addView(t);

		t = new TextView(getContext());
		tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		t.setLayoutParams(tlp);
		t.setText(getContext().getString(R.string.number_of_sets, game.getGameSetCount()));
		accountLayout.addView(t);

		accountLayout.addView(createParties());
		accountLayout.addView(createDecks());

		return accountLayout;
	}

	private View createParties() {
		LinearLayout accountLayout = new LinearLayout(getContext());
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		accountLayout.setLayoutParams(aclp);
		TextView t = new TextView(getContext());
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		t.setLayoutParams(tlp);
		t.setText(getContext().getString(R.string.number_of_cards, game.getCardCount()));
		t.setTextSize(10);
		accountLayout.addView(t);

		return accountLayout;
	}

	private View createDecks() {
		LinearLayout accountLayout = new LinearLayout(getContext());
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		accountLayout.setLayoutParams(aclp);
		TextView t = new TextView(getContext());
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		t.setLayoutParams(tlp);
		t.setText(getContext().getString(R.string.number_of_decks, game.getDeckCount()));
		t.setTextSize(10);
		accountLayout.addView(t);

		return accountLayout;
	}

}
