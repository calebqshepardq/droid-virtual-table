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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.amphiprion.droidvirtualtable.adapter.CardPropertyAdapter;
import org.amphiprion.droidvirtualtable.dao.CardDao;
import org.amphiprion.droidvirtualtable.dao.CardPropertyDao;
import org.amphiprion.droidvirtualtable.dao.CardValueDao;
import org.amphiprion.droidvirtualtable.dto.Criteria;
import org.amphiprion.droidvirtualtable.entity.Card;
import org.amphiprion.droidvirtualtable.entity.CardProperty;
import org.amphiprion.droidvirtualtable.entity.CardValue;
import org.amphiprion.droidvirtualtable.entity.Game;
import org.amphiprion.droidvirtualtable.task.LoadCardsTask;
import org.amphiprion.droidvirtualtable.task.LoadCardsTask.LoadCardListener;
import org.amphiprion.droidvirtualtable.util.Initializer;
import org.amphiprion.droidvirtualtable.view.CardAttributView;
import org.amphiprion.droidvirtualtable.view.CardSummaryView;
import org.amphiprion.droidvirtualtable.view.CriteriaSummaryView;
import org.amphiprion.droidvirtualtable.view.MyScrollView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;

public class CardListActivity extends Activity {
	private Game game;
	private Card currentCard;
	private CardListContext cardListContext;
	private HashMap<String, List<Criteria>> criterias = new HashMap<String, List<Criteria>>();

	private LinearLayout headerSetFilter;
	private LinearLayout headerAttributFilter;
	private SlidingDrawer slidingDrawer;

	private ImageView cardImage;
	private Bitmap oldBitmap;
	private LinearLayout cardAttributesLayout;
	// prop.getId -> prop
	private HashMap<String, CardProperty> cardProperties = new HashMap<String, CardProperty>();
	// prop.getId -> list of existing values for this prop
	private HashMap<String, List<String>> cardPropertyValues = new HashMap<String, List<String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Initializer.init(this);

		Intent i = getIntent();
		game = (Game) i.getSerializableExtra("GAME");

		setContentView(R.layout.card_list);

		cardImage = (ImageView) findViewById(R.id.cardImage);
		cardAttributesLayout = (LinearLayout) findViewById(R.id.attributsCard);

		List<CardProperty> props = CardPropertyDao.getInstance(this).getCardProperties(game.getId());
		for (CardProperty prop : props) {
			cardProperties.put(prop.getId(), prop);
			if ("List".equals(prop.getType())) {
				List<String> propValues = CardValueDao.getInstance(this).getCardValuesByProperty(prop.getId());
				cardPropertyValues.put(prop.getId(), propValues);
			} else if ("MultipleList".equals(prop.getType())) {
				List<String> propValues = CardValueDao.getInstance(this).getCardValuesByProperty(prop.getId());

				List<String> splitedList = new ArrayList<String>();
				for (String str : propValues) {
					String[] strs = str.split("\\.");
					for (String s : strs) {
						s = s.trim();
						if (s.length() > 0 && !splitedList.contains(s)) {
							splitedList.add(s);
						}
					}
				}
				Collections.sort(splitedList);
				cardPropertyValues.put(prop.getId(), splitedList);
			}
		}

		TextView tv = (TextView) findViewById(R.id.card_title);
		tv.setText(getResources().getString(R.string.activity_cards, game.getName()));

		slidingDrawer = (SlidingDrawer) findViewById(R.id.drawer);

		headerSetFilter = (LinearLayout) findViewById(R.id.headerSetFilter);
		headerAttributFilter = (LinearLayout) findViewById(R.id.headerAttributFilter);

		Button btSearch = (Button) findViewById(R.id.btSearch);
		btSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				slidingDrawer.close();
				initCardList();
			}
		});

		ImageView btAddSetFilter = (ImageView) findViewById(R.id.btAddSetFilter);
		btAddSetFilter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

			}
		});
		ImageView btAddAttributFilter = (ImageView) findViewById(R.id.btAddAttributFilter);
		btAddAttributFilter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				chooseCardAttributeFilter();
			}
		});

		showCardList();
	}

	private void updateCardDetailView(Card card) {
		String image = Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY_GAMES + "/";
		image += game.getId() + "/sets/";
		image += card.getGameSet().getId();
		image += card.getImageName();
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
		List<CardValue> cardValues = CardValueDao.getInstance(this).getCardValues(card.getId());
		for (CardValue cardValue : cardValues) {
			// CardProperty prop =
			// cardProperties.get(cardValue.getProperty().getId());
			// if (prop != null) {
			cardValue.setProperty(cardProperties.get(cardValue.getProperty().getId()));
			cardAttributesLayout.addView(new CardAttributView(this, cardValue));
			// }
		}
	}

	private void chooseCardAttributeFilter() {
		final List<CardProperty> cardProperties = CardPropertyDao.getInstance(CardListActivity.this).getCardProperties(game.getId());

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.filter_attribut));
		builder.setAdapter(new CardPropertyAdapter(CardListActivity.this, cardProperties), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();
				addCardAttributeFilter(cardProperties.get(item));
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void addCardAttributeFilter(CardProperty prop) {
		List<Criteria> propCriterias = criterias.get(prop.getName());
		View insertAfter = headerAttributFilter;
		if (propCriterias == null) {
			propCriterias = new ArrayList<Criteria>();
			criterias.put(prop.getName(), propCriterias);
		} else {
			LinearLayout parent = (LinearLayout) insertAfter.getParent();
			for (int i = 0; i < parent.getChildCount(); i++) {
				View o = parent.getChildAt(i);
				if (o instanceof CriteriaSummaryView) {
					if (((CriteriaSummaryView) o).getCriteria().getName().equals(prop.getName())) {
						insertAfter = o;
					}
				}
			}
		}
		final Criteria criteria = new Criteria(prop.getType(), prop.getName(), cardPropertyValues.get(prop.getId()));
		propCriterias.add(criteria);

		final CriteriaSummaryView critView = new CriteriaSummaryView(this, prop.getType(), criteria);
		int pos = ((LinearLayout) insertAfter.getParent()).indexOfChild(insertAfter);
		((LinearLayout) insertAfter.getParent()).addView(critView, pos + 1);

		critView.getDeleteButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				List<Criteria> propCriterias = criterias.get(criteria.getName());
				propCriterias.remove(criteria);
				((LinearLayout) critView.getParent()).removeView(critView);
				if (propCriterias.size() == 0) {
					criterias.remove(criteria.getName());
				}
			}
		});

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.clear();

		currentCard = ((CardSummaryView) v).getCard();
		menu.add(1, ApplicationConstants.MENU_ID_ADD_TO_DECK, 0, R.string.add_to_deck);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		if (item.getItemId() == ApplicationConstants.MENU_ID_ADD_TO_DECK) {
			selectCard(currentCard);
		}

		return true;
	}

	public void showCardList() {
		cardListContext = new CardListContext();

		final Rect r = new Rect();
		cardListContext.scrollView = (MyScrollView) findViewById(R.id.scroll_view);
		cardListContext.scrollView.setOnScrollChanged(new OnScrollChangedListener() {
			@Override
			public void onScrollChanged() {
				if (!cardListContext.allLoaded && !cardListContext.loading) {
					LinearLayout ln = (LinearLayout) cardListContext.scrollView.getChildAt(0);
					if (ln.getChildCount() > 3) {
						boolean b = ln.getChildAt(ln.getChildCount() - 3).getLocalVisibleRect(r);
						if (b) {
							cardListContext.loading = true;
							loadCardNextPage();
						}
					}
				}
			}
		});
		initCardList();

	}

	private void selectCard(final Card card) {
		final Dialog alert = new Dialog(this);

		alert.setTitle(card.getName());

		alert.setContentView(R.layout.edit_quantity);
		// Set an EditText view to get user input
		final EditText input = (EditText) alert.findViewById(R.id.txtQuantity);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setText("1");

		Button btOk = (Button) alert.findViewById(R.id.btOk);
		Button btCancel = (Button) alert.findViewById(R.id.btCancel);
		btOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					int newValue = Integer.parseInt("" + input.getText());
					if (newValue > 0) {
						Intent intent = getIntent();
						intent.putExtra("SELECTED_CARD", card);
						intent.putExtra("CARD_QUANTITY", newValue);
						setResult(RESULT_OK, intent);
						finish();
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

	private void initCardList() {
		cardListContext.loadedPage = 0;
		if (cardListContext.cards == null) {
			cardListContext.cards = new ArrayList<Card>();
		} else {
			cardListContext.cards.clear();
		}
		loadCardNextPage();
	}

	private void loadCardNextPage() {
		if (cardListContext.loadedPage == 0) {
			// int nb =
			// GameDao.getInstance(this).getGameCount(gameListContext.collection,
			// gameListContext.search, gameListContext.query);
			// Toast.makeText(this,
			// getResources().getString(R.string.message_nb_result, nb),
			// Toast.LENGTH_LONG).show();
			List<Card> newCards = CardDao.getInstance(this).getCards(game, cardListContext.loadedPage, CardListContext.PAGE_SIZE, criterias);
			importCardEnded(true, newCards);
		} else {
			LoadCardListener l = new LoadCardListener() {

				@Override
				public void importEnded(boolean succeed, List<Card> cards) {
					importCardEnded(succeed, cards);
				}

				@Override
				public Context getContext() {
					return CardListActivity.this;
				}
			};
			cardListContext.task = new LoadCardsTask(l, game, cardListContext.loadedPage, CardListContext.PAGE_SIZE, criterias);
			cardListContext.task.execute();
		}
	}

	public void importCardEnded(boolean succeed, List<Card> newCards) {
		if (succeed) {
			cardListContext.task = null;
			if (newCards != null && newCards.size() > 0) {
				if (newCards.size() == CardListContext.PAGE_SIZE + 1) {
					newCards.remove(CardListContext.PAGE_SIZE);
					cardListContext.allLoaded = false;
				} else {
					cardListContext.allLoaded = true;
				}
			} else {
				cardListContext.allLoaded = true;
			}
			if (cardListContext.loadedPage != 0) {
				addCardElementToList(newCards);
			} else {
				cardListContext.cards = newCards;
				buildCardList();
			}
			cardListContext.loadedPage++;
		}
		cardListContext.loading = false;

	}

	private void buildCardList() {

		LinearLayout ln = (LinearLayout) findViewById(R.id.card_list);
		ln.removeAllViews();
		if (cardListContext.cards != null && cardListContext.cards.size() > 0) {
			addCardElementToList(cardListContext.cards);
			updateCardDetailView(cardListContext.cards.get(0));
		} else {
			TextView tv = new TextView(this);
			tv.setText(R.string.empty_card_list);
			ln.addView(tv);
		}
	}

	private void addCardElementToList(List<Card> newCards) {
		LinearLayout ln = (LinearLayout) findViewById(R.id.card_list);

		if (newCards != cardListContext.cards) {
			cardListContext.cards.addAll(newCards);
			if (ln.getChildCount() > 0) {
				ln.removeViewAt(ln.getChildCount() - 1);
			}
		}
		for (final Card card : newCards) {
			card.getGameSet().setGame(game);
			final CardSummaryView view = new CardSummaryView(this, card);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					updateCardDetailView(card);
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

		if (!cardListContext.allLoaded) {
			ln.addView(getProgressView());
		}
	}

	private View getProgressView() {
		LinearLayout lnExpand = new LinearLayout(this);
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lnExpand.setLayoutParams(lp);
		lnExpand.setBackgroundColor(getResources().getColor(R.color.grey));

		ProgressBar im = new ProgressBar(this);
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imglp.gravity = Gravity.CENTER_VERTICAL;
		imglp.rightMargin = 5;
		im.setLayoutParams(imglp);
		lnExpand.addView(im);

		TextView tv = new TextView(this);
		tv.setText(getResources().getText(R.string.loading));
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		tlp.gravity = Gravity.CENTER_VERTICAL;

		tv.setLayoutParams(tlp);
		lnExpand.addView(tv);

		return lnExpand;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		// MenuItem search = menu.add(0,
		// ApplicationConstants.MENU_ID_IMPORT_SET, 1, R.string.import_set);
		// search.setIcon(android.R.drawable.ic_menu_upload);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// if (item.getItemId() ==
		// ApplicationConstants.MENU_ID_CHOOSE_EXISTING_SEARCH) {
		// chooseSearchFilter();
		// } else if (item.getItemId() ==
		// ApplicationConstants.MENU_ID_CLEAR_SEARCH) {
		// gameListContext.query = null;
		// gameListContext.search = null;
		// initGameList();
		// } else if (item.getItemId() == ApplicationConstants.MENU_ID_SEARCH) {
		// onSearchRequested();
		// }

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//
		// if (resultCode == RESULT_OK) {
		// if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CREATE_PARTY)
		// {
		// Party party = (Party) data.getSerializableExtra("PARTY");
		// party.setLastUpdateDate(new Date());
		// PartyDao.getInstance(this).persist(party);
		// viewParties(currentGame);
		// }
		// }
		// if (requestCode == ApplicationConstants.ACTIVITY_RETURN_VIEW_PARTIES)
		// {
		// initGameList();
		// }

	}

	@Override
	public void onBackPressed() {
		if (slidingDrawer.isMoving() || slidingDrawer.isOpened()) {
			slidingDrawer.close();
		} else {
			super.onBackPressed();
		}
	}
}