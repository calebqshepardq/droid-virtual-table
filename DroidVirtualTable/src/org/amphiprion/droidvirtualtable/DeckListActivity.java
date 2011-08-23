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
import java.util.List;

import org.amphiprion.droidvirtualtable.adapter.StringAdapter;
import org.amphiprion.droidvirtualtable.dao.DeckDao;
import org.amphiprion.droidvirtualtable.driver.ImportDeckDriver;
import org.amphiprion.droidvirtualtable.driver.ImportDeckListener;
import org.amphiprion.droidvirtualtable.entity.Deck;
import org.amphiprion.droidvirtualtable.entity.Game;
import org.amphiprion.droidvirtualtable.task.LoadDecksTask;
import org.amphiprion.droidvirtualtable.task.LoadDecksTask.LoadDeckListener;
import org.amphiprion.droidvirtualtable.util.DialogUtil;
import org.amphiprion.droidvirtualtable.util.DriverManager;
import org.amphiprion.droidvirtualtable.util.Initializer;
import org.amphiprion.droidvirtualtable.view.DeckSummaryView;
import org.amphiprion.droidvirtualtable.view.MyScrollView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DeckListActivity extends Activity {
	private Game game;
	private Deck currentDeck;
	private DeckListContext deckListContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Initializer.init(this);

		Intent i = getIntent();
		game = (Game) i.getSerializableExtra("GAME");

		setContentView(R.layout.deck_list);

		TextView tv = (TextView) findViewById(R.id.deck_title);
		tv.setText(getResources().getString(R.string.activity_decks, game.getName()));
		showDeckList();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.clear();

		// if (v instanceof GameSummaryView) {
		// currentGame = ((GameSummaryView) v).getGame();
		// menu.add(1, ApplicationConstants.MENU_ID_MANAGE_SET, 0,
		// R.string.manage_sets);
		// }

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		// if (item.getItemId() == ApplicationConstants.MENU_ID_MANAGE_SET) {
		// Intent i = new Intent(this, EditParty.class);
		// i.putExtra("GAME", currentGame);
		// startActivityForResult(i,
		// ApplicationConstants.ACTIVITY_RETURN_CREATE_PARTY);
		// }

		return true;
	}

	public void showDeckList() {
		deckListContext = new DeckListContext();

		final Rect r = new Rect();
		deckListContext.scrollView = (MyScrollView) findViewById(R.id.scroll_view);
		deckListContext.scrollView.setOnScrollChanged(new OnScrollChangedListener() {
			@Override
			public void onScrollChanged() {
				if (!deckListContext.allLoaded && !deckListContext.loading) {
					LinearLayout ln = (LinearLayout) deckListContext.scrollView.getChildAt(0);
					if (ln.getChildCount() > 3) {
						boolean b = ln.getChildAt(ln.getChildCount() - 3).getLocalVisibleRect(r);
						if (b) {
							deckListContext.loading = true;
							loadDeckNextPage();
						}
					}
				}
			}
		});
		initDeckList();

	}

	private void initDeckList() {
		deckListContext.loadedPage = 0;
		if (deckListContext.decks == null) {
			deckListContext.decks = new ArrayList<Deck>();
		} else {
			deckListContext.decks.clear();
		}
		loadDeckNextPage();
	}

	private void loadDeckNextPage() {
		if (deckListContext.loadedPage == 0) {
			// int nb =
			// GameDao.getInstance(this).getGameCount(gameListContext.collection,
			// gameListContext.search, gameListContext.query);
			// Toast.makeText(this,
			// getResources().getString(R.string.message_nb_result, nb),
			// Toast.LENGTH_LONG).show();
			List<Deck> newDecks = DeckDao.getInstance(this).getDecks(game, deckListContext.loadedPage, DeckListContext.PAGE_SIZE);
			importDeckEnded(true, newDecks);
		} else {
			LoadDeckListener l = new LoadDeckListener() {

				@Override
				public void importEnded(boolean succeed, List<Deck> decks) {
					importDeckEnded(succeed, decks);
				}

				@Override
				public Context getContext() {
					return DeckListActivity.this;
				}
			};
			deckListContext.task = new LoadDecksTask(l, game, deckListContext.loadedPage, DeckListContext.PAGE_SIZE);
			deckListContext.task.execute();
		}
	}

	public void importDeckEnded(boolean succeed, List<Deck> newDecks) {
		if (succeed) {
			deckListContext.task = null;
			if (newDecks != null && newDecks.size() > 0) {
				if (newDecks.size() == DeckListContext.PAGE_SIZE + 1) {
					newDecks.remove(DeckListContext.PAGE_SIZE);
					deckListContext.allLoaded = false;
				} else {
					deckListContext.allLoaded = true;
				}
			} else {
				deckListContext.allLoaded = true;
			}
			if (deckListContext.loadedPage != 0) {
				addDeckElementToList(newDecks);
			} else {
				deckListContext.decks = newDecks;
				buildDeckList();
			}
			deckListContext.loadedPage++;
		}
		deckListContext.loading = false;

	}

	private void buildDeckList() {

		LinearLayout ln = (LinearLayout) findViewById(R.id.deck_list);
		ln.removeAllViews();
		if (deckListContext.decks != null && deckListContext.decks.size() > 0) {
			addDeckElementToList(deckListContext.decks);
		} else {
			TextView tv = new TextView(this);
			tv.setText(R.string.empty_deck_list);
			ln.addView(tv);
		}
	}

	private void addDeckElementToList(List<Deck> newDecks) {
		LinearLayout ln = (LinearLayout) findViewById(R.id.deck_list);

		if (newDecks != deckListContext.decks) {
			deckListContext.decks.addAll(newDecks);
			if (ln.getChildCount() > 0) {
				ln.removeViewAt(ln.getChildCount() - 1);
			}
		}
		for (final Deck deck : newDecks) {
			deck.setGame(game);
			DeckSummaryView view = new DeckSummaryView(this, deck);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

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

		if (!deckListContext.allLoaded) {
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
		MenuItem search = menu.add(0, ApplicationConstants.MENU_ID_IMPORT_DECK, 1, R.string.import_deck);
		search.setIcon(android.R.drawable.ic_menu_upload);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_IMPORT_DECK) {
			final File importDeckDir = new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY_IMPORT_DECKS);
			final String[] files = importDeckDir.list();
			if (files == null || files.length == 0) {
				DialogUtil.showConfirmDialog(this, getResources().getString(R.string.empty_import_deck_dir, ApplicationConstants.DIRECTORY_IMPORT_DECKS));
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getResources().getString(R.string.import_deck));
				builder.setAdapter(new StringAdapter(DeckListActivity.this, files), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						dialog.dismiss();
						File file = new File(importDeckDir, files[item]);
						ImportDeckDriver task = DriverManager.getImportDeckDriver(file);
						if (task != null) {
							task.importDeck(new ImportDeckListener() {

								@Override
								public void importEnded(boolean succeed, Deck deck, Exception exception) {
									Log.d(ApplicationConstants.PACKAGE, "import ended");
									if (exception != null) {
										DialogUtil.showErrorDialog(DeckListActivity.this, getResources().getString(R.string.an_error_occurs), exception);
									} else if (succeed) {
										Log.d(ApplicationConstants.PACKAGE, "on refresh");
										initDeckList();
									}

								}

								@Override
								public Context getContext() {
									return DeckListActivity.this;
								}
							}, game, file);
							// Intent i = new Intent(OperationList.this,
							// DefineImportParameter.class);
							// i.putExtra("ACCOUNT", account);
							// i.putExtra("FILE_DRIVER_INDEX", item);
							// startActivityForResult(i,
							// ApplicationConstants.ACTIVITY_RETURN_IMPORT_OPERATION);
						} else {
							DialogUtil.showConfirmDialog(DeckListActivity.this, getResources().getString(R.string.no_driver, files[item]));
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}

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

}