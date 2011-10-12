/*
 * @copyright 2010 Gerald Jacobson
 * @license GNU General Public License
 * 
 * This file is part of DroidVirtualTable.
 *
 * DroidVirtualTable is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DroidVirtualTable is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DroidVirtualTable.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.amphiprion.droidvirtualtable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.amphiprion.droidvirtualtable.dao.CardPropertyDao;
import org.amphiprion.droidvirtualtable.dao.CardValueDao;
import org.amphiprion.droidvirtualtable.dao.DeckContentDao;
import org.amphiprion.droidvirtualtable.entity.Card;
import org.amphiprion.droidvirtualtable.entity.CardProperty;
import org.amphiprion.droidvirtualtable.entity.CardValue;
import org.amphiprion.droidvirtualtable.entity.Deck;
import org.amphiprion.droidvirtualtable.entity.DeckContent;
import org.amphiprion.droidvirtualtable.entity.Section;
import org.amphiprion.droidvirtualtable.util.Initializer;
import org.amphiprion.droidvirtualtable.view.CardAttributView;
import org.amphiprion.droidvirtualtable.view.DeckContentSummaryView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DeckContentListActivity extends Activity {
	private Section section;
	private Deck deck;
	private DeckContent currentDeckContent;
	private DeckContentSummaryView currentDeckContentView;
	private List<DeckContent> deckContents = new ArrayList<DeckContent>();
	private ImageView cardImage;
	private Bitmap oldBitmap;
	private LinearLayout cardAttributesLayout;
	private HashMap<String, CardProperty> cardProperties = new HashMap<String, CardProperty>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Initializer.init(this);

		Intent i = getIntent();
		section = (Section) i.getSerializableExtra("SECTION");
		deck = (Deck) i.getSerializableExtra("DECK");

		setContentView(R.layout.deck_section_content_list);

		cardImage = (ImageView) findViewById(R.id.cardImage);
		cardAttributesLayout = (LinearLayout) findViewById(R.id.attributsCard);

		List<CardProperty> props = CardPropertyDao.getInstance(this).getCardProperties(deck.getGame().getId());
		for (CardProperty prop : props) {
			cardProperties.put(prop.getId(), prop);
		}

		showDeckContentList();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.clear();
		currentDeckContentView = (DeckContentSummaryView) v;
		currentDeckContent = currentDeckContentView.getDeckContent();
		menu.add(1, ApplicationConstants.MENU_ID_SET_QUANTITY, 0, R.string.update_quantity);
		menu.add(2, ApplicationConstants.MENU_ID_REMOVE_CARD, 1, R.string.remove_from_deck);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_SET_QUANTITY) {
			editQuantity();
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_REMOVE_CARD) {
			DeckContentDao.getInstance(this).delete(currentDeckContent);
			initDeckContentList();
		}
		// if (item.getItemId() == ApplicationConstants.MENU_ID_MANAGE_SET) {
		// Intent i = new Intent(this, EditParty.class);
		// i.putExtra("GAME", currentGame);
		// startActivityForResult(i,
		// ApplicationConstants.ACTIVITY_RETURN_CREATE_PARTY);
		// }

		return true;
	}

	private void editQuantity() {
		final Dialog alert = new Dialog(this);

		alert.setTitle(currentDeckContent.getName());

		alert.setContentView(R.layout.edit_quantity);
		// Set an EditText view to get user input
		final EditText input = (EditText) alert.findViewById(R.id.txtQuantity);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setText("" + currentDeckContent.getQuantity());

		Button btOk = (Button) alert.findViewById(R.id.btOk);
		Button btCancel = (Button) alert.findViewById(R.id.btCancel);
		btOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					int newValue = Integer.parseInt("" + input.getText());
					if (newValue > 0) {
						currentDeckContent.setQuantity(newValue);
						DeckContentDao.getInstance(DeckContentListActivity.this).persist(currentDeckContent);
						currentDeckContentView.updateQuantity();
					}
					alert.dismiss();
				} catch (Exception e) {
				}
			}
		});

		btCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				alert.dismiss();
			}
		});

		Button btMinus = (Button) alert.findViewById(R.id.btMinus);
		btMinus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					int newValue = Integer.parseInt("" + input.getText());
					if (newValue > 1) {
						newValue--;
						input.setText("" + newValue);
					}
				} catch (Exception e) {
				}
			}
		});
		Button btPlus = (Button) alert.findViewById(R.id.btPlus);
		btPlus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					int newValue = Integer.parseInt("" + input.getText());
					newValue++;
					input.setText("" + newValue);
				} catch (Exception e) {
				}
			}
		});

		alert.show();
	}

	public void showDeckContentList() {

		final Rect r = new Rect();
		initDeckContentList();

	}

	private void initDeckContentList() {
		// int nb =
		// GameDao.getInstance(this).getGameCount(gameListContext.collection,
		// gameListContext.search, gameListContext.query);
		// Toast.makeText(this,
		// getResources().getString(R.string.message_nb_result, nb),
		// Toast.LENGTH_LONG).show();
		deckContents = DeckContentDao.getInstance(this).getDeckContents(deck.getId(), section.getId());
		buildDeckList();

	}

	private void buildDeckList() {

		LinearLayout ln = (LinearLayout) findViewById(R.id.deck_list);
		ln.removeAllViews();
		if (deckContents != null && deckContents.size() > 0) {
			addDeckElementToList(deckContents);
			updateCardDetailView(deckContents.get(0));
		} else {
			TextView tv = new TextView(this);
			tv.setText(R.string.empty_deck_content_list);
			ln.addView(tv);
		}
	}

	private void addDeckElementToList(List<DeckContent> newDeckContents) {
		LinearLayout ln = (LinearLayout) findViewById(R.id.deck_list);

		for (final DeckContent deckContent : newDeckContents) {
			deckContent.setDeck(deck);
			final DeckContentSummaryView view = new DeckContentSummaryView(this, deckContent);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					updateCardDetailView(deckContent);
				}
			});
			view.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					registerForContextMenu(v);
					openContextMenu(v);
					unregisterForContextMenu(v);
					return true;
				}
			});

			ln.addView(view);
		}

	}

	private void updateCardDetailView(DeckContent deckContent) {
		String image = Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY_GAMES + "/";
		image += deckContent.getDeck().getGame().getId() + "/sets/";
		image += deckContent.getCard().getGameSet().getId();
		image += deckContent.getCard().getImageName();
		File f = new File(image);
		Bitmap bitmap = null;
		if (f.exists()) {
			bitmap = BitmapFactory.decodeFile(f.toString());
		}
		if (bitmap == null) {
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_game_image);
		}

		cardImage.setImageBitmap(bitmap);
		if (oldBitmap != null) {
			oldBitmap.recycle();
		}
		oldBitmap = bitmap;

		// attributs
		cardAttributesLayout.removeAllViews();
		List<CardValue> cardValues = CardValueDao.getInstance(this).getCardValues(deckContent.getCard().getId());
		for (CardValue cardValue : cardValues) {
			cardValue.setProperty(cardProperties.get(cardValue.getProperty().getId()));
			cardAttributesLayout.addView(new CardAttributView(this, cardValue));
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		// if (gameListContext.search != null || gameListContext.query != null)
		// {
		// MenuItem clearSearch = menu.add(0,
		// ApplicationConstants.MENU_ID_CLEAR_SEARCH, 0, R.string.clear_filter);
		// clearSearch.setIcon(R.drawable.search_cleared);
		// }
		// MenuItem addAccount = menu.add(0,
		// ApplicationConstants.MENU_ID_CHOOSE_EXISTING_SEARCH, 1,
		// R.string.apply_existing_filter);
		// addAccount.setIcon(R.drawable.search);
		//
		MenuItem search = menu.add(0, ApplicationConstants.MENU_ID_ADD_CARD, 1, R.string.add_card);
		search.setIcon(android.R.drawable.ic_menu_add);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_ADD_CARD) {
			Intent i = new Intent(this, CardListActivity.class);
			i.putExtra("GAME", deck.getGame());
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_ADD_CARD_TO_DECK);
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//
		if (resultCode == RESULT_OK) {
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_ADD_CARD_TO_DECK) {
				Card c = (Card) data.getSerializableExtra("SELECTED_CARD");
				int quantity = data.getIntExtra("CARD_QUANTITY", 1);
				DeckContent dc = new DeckContent();
				dc.setCard(c);
				dc.setDeck(deck);
				dc.setName(c.getName());
				dc.setSection(section);
				dc.setQuantity(quantity);
				DeckContentDao.getInstance(this).persist(dc);
				initDeckContentList();
			}
		}
		// if (requestCode == ApplicationConstants.ACTIVITY_RETURN_VIEW_PARTIES)
		// {
		// initGameList();
		// }

	}

}