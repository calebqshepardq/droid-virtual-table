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
import org.amphiprion.droidvirtualtable.dao.GameDao;
import org.amphiprion.droidvirtualtable.driver.ImportGameDriver;
import org.amphiprion.droidvirtualtable.driver.ImportGameListener;
import org.amphiprion.droidvirtualtable.entity.Game;
import org.amphiprion.droidvirtualtable.task.LoadGamesTask;
import org.amphiprion.droidvirtualtable.task.LoadGamesTask.LoadGameListener;
import org.amphiprion.droidvirtualtable.util.DialogUtil;
import org.amphiprion.droidvirtualtable.util.DriverManager;
import org.amphiprion.droidvirtualtable.util.Initializer;
import org.amphiprion.droidvirtualtable.view.GameSummaryView;
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

public class Home extends Activity {
	private Game currentGame;
	private GameListContext gameListContext;
	private static boolean init = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Initializer.init(this);

		setContentView(R.layout.main);
		showGameList();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.clear();

		// if (v instanceof GameSummaryView) {
		// currentGame = ((GameSummaryView) v).getGame();
		// menu.add(1, ApplicationConstants.MENU_ID_VIEW_GAME_TRICTRAC, 0,
		// R.string.goto_trictrac_name);
		// menu.add(1, ApplicationConstants.MENU_ID_VIEW_ADVICES_TRICTRAC, 1,
		// R.string.goto_trictrac_advices);
		// menu.add(1, ApplicationConstants.MENU_ID_SYNCHRO_GAME, 2,
		// R.string.synch_game);
		// menu.add(2, ApplicationConstants.MENU_ID_CREATE_PARTY, 3,
		// R.string.add_party);
		// menu.add(2, ApplicationConstants.MENU_ID_VIEW_PARTIES, 4,
		// R.string.view_parties);
		// }
		//
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		// if (item.getItemId() == ApplicationConstants.MENU_ID_SYNCHRO_GAME) {
		// new GameHandler().parse(currentGame);
		// GameDao.getInstance(this).update(currentGame);
		// buildGameList();
		// } else if (item.getItemId() ==
		// ApplicationConstants.MENU_ID_CREATE_PARTY) {
		// Intent i = new Intent(this, EditParty.class);
		// i.putExtra("GAME", currentGame);
		// startActivityForResult(i,
		// ApplicationConstants.ACTIVITY_RETURN_CREATE_PARTY);
		// } else if (item.getItemId() ==
		// ApplicationConstants.MENU_ID_VIEW_PARTIES) {
		// viewParties(currentGame);
		// } else if (item.getItemId() ==
		// ApplicationConstants.MENU_ID_VIEW_GAME_TRICTRAC) {
		// gotoTricTracGame(currentGame.getId());
		// } else if (item.getItemId() ==
		// ApplicationConstants.MENU_ID_VIEW_ADVICES_TRICTRAC) {
		// gotoTricTracAdvice(currentGame.getId());
		// }

		return true;
	}

	public void showGameList() {
		gameListContext = new GameListContext();

		final Rect r = new Rect();
		gameListContext.scrollView = (MyScrollView) findViewById(R.id.scroll_view);
		gameListContext.scrollView.setOnScrollChanged(new OnScrollChangedListener() {
			@Override
			public void onScrollChanged() {
				if (!gameListContext.allLoaded && !gameListContext.loading) {
					LinearLayout ln = (LinearLayout) gameListContext.scrollView.getChildAt(0);
					if (ln.getChildCount() > 3) {
						boolean b = ln.getChildAt(ln.getChildCount() - 3).getLocalVisibleRect(r);
						if (b) {
							gameListContext.loading = true;
							loadGameNextPage();
						}
					}
				}
			}
		});
		initGameList();

	}

	private void initGameList() {
		gameListContext.loadedPage = 0;
		if (gameListContext.games == null) {
			gameListContext.games = new ArrayList<Game>();
		} else {
			gameListContext.games.clear();
		}
		loadGameNextPage();
	}

	private void loadGameNextPage() {
		if (gameListContext.loadedPage == 0) {
			// int nb =
			// GameDao.getInstance(this).getGameCount(gameListContext.collection,
			// gameListContext.search, gameListContext.query);
			// Toast.makeText(this,
			// getResources().getString(R.string.message_nb_result, nb),
			// Toast.LENGTH_LONG).show();
			List<Game> newGames = GameDao.getInstance(this).getGames(gameListContext.loadedPage, GameListContext.PAGE_SIZE);
			importGameEnded(true, newGames);
		} else {
			LoadGameListener l = new LoadGameListener() {

				@Override
				public void importEnded(boolean succeed, List<Game> games) {
					importGameEnded(succeed, games);
				}

				@Override
				public Context getContext() {
					return Home.this;
				}
			};
			gameListContext.task = new LoadGamesTask(l, gameListContext.loadedPage, GameListContext.PAGE_SIZE);
			gameListContext.task.execute();
		}
	}

	public void importGameEnded(boolean succeed, List<Game> newGames) {
		if (succeed) {
			gameListContext.task = null;
			if (newGames != null && newGames.size() > 0) {
				if (newGames.size() == GameListContext.PAGE_SIZE + 1) {
					newGames.remove(GameListContext.PAGE_SIZE);
					gameListContext.allLoaded = false;
				} else {
					gameListContext.allLoaded = true;
				}
			} else {
				gameListContext.allLoaded = true;
			}
			if (gameListContext.loadedPage != 0) {
				addGameElementToList(newGames);
			} else {
				gameListContext.games = newGames;
				buildGameList();
			}
			gameListContext.loadedPage++;
		}
		gameListContext.loading = false;

	}

	private void buildGameList() {

		LinearLayout ln = (LinearLayout) findViewById(R.id.game_list);
		ln.removeAllViews();
		if (gameListContext.games != null && gameListContext.games.size() > 0) {
			addGameElementToList(gameListContext.games);
		} else {
			TextView tv = new TextView(this);
			tv.setText(R.string.empty_game_list);
			ln.addView(tv);
		}
	}

	private void addGameElementToList(List<Game> newGames) {
		LinearLayout ln = (LinearLayout) findViewById(R.id.game_list);

		if (newGames != gameListContext.games) {
			gameListContext.games.addAll(newGames);
			if (ln.getChildCount() > 0) {
				ln.removeViewAt(ln.getChildCount() - 1);
			}
		}
		for (final Game game : newGames) {
			GameSummaryView view = new GameSummaryView(this, game);
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

		if (!gameListContext.allLoaded) {
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
		MenuItem search = menu.add(0, ApplicationConstants.MENU_ID_IMPORT_GAME, 1, R.string.import_game);
		search.setIcon(android.R.drawable.ic_menu_upload);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_IMPORT_GAME) {
			final File importGameDir = new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY_IMPORT_GAMES);
			final String[] files = importGameDir.list();
			if (files == null || files.length == 0) {
				DialogUtil.showConfirmDialog(this, getResources().getString(R.string.empty_import_game_dir, ApplicationConstants.DIRECTORY_IMPORT_GAMES));
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getResources().getString(R.string.import_game));
				builder.setAdapter(new StringAdapter(Home.this, files), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						dialog.dismiss();
						File file = new File(importGameDir, files[item]);
						ImportGameDriver task = DriverManager.getImportGameTask(file);
						if (task != null) {
							task.importGame(new ImportGameListener() {

								@Override
								public void importEnded(boolean succeed, Game game) {
									Log.d(ApplicationConstants.PACKAGE, "import ended");
									if (succeed) {
										Log.d(ApplicationConstants.PACKAGE, "on refresh");
										initGameList();
									}

								}

								@Override
								public Context getContext() {
									return Home.this;
								}
							}, file);
							// Intent i = new Intent(OperationList.this,
							// DefineImportParameter.class);
							// i.putExtra("ACCOUNT", account);
							// i.putExtra("FILE_DRIVER_INDEX", item);
							// startActivityForResult(i,
							// ApplicationConstants.ACTIVITY_RETURN_IMPORT_OPERATION);
						} else {
							DialogUtil.showConfirmDialog(Home.this, getResources().getString(R.string.no_driver, files[item]));
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