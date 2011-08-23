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
import org.amphiprion.droidvirtualtable.dao.GroupDao;
import org.amphiprion.droidvirtualtable.dao.TableDao;
import org.amphiprion.droidvirtualtable.driver.ImportTableDriver;
import org.amphiprion.droidvirtualtable.driver.ImportTableListener;
import org.amphiprion.droidvirtualtable.dto.CardGroup;
import org.amphiprion.droidvirtualtable.dto.GameSession;
import org.amphiprion.droidvirtualtable.dto.GameTable;
import org.amphiprion.droidvirtualtable.dto.Player;
import org.amphiprion.droidvirtualtable.engine3d.GameSessionActivity;
import org.amphiprion.droidvirtualtable.entity.Game;
import org.amphiprion.droidvirtualtable.entity.Group;
import org.amphiprion.droidvirtualtable.entity.Table;
import org.amphiprion.droidvirtualtable.task.LoadTablesTask;
import org.amphiprion.droidvirtualtable.task.LoadTablesTask.LoadTableListener;
import org.amphiprion.droidvirtualtable.util.DialogUtil;
import org.amphiprion.droidvirtualtable.util.DriverManager;
import org.amphiprion.droidvirtualtable.util.Initializer;
import org.amphiprion.droidvirtualtable.view.MyScrollView;
import org.amphiprion.droidvirtualtable.view.TableSummaryView;

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

public class TableListActivity extends Activity {
	private Game game;
	private Table currentTable;
	private TableListContext tableListContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Initializer.init(this);

		Intent i = getIntent();
		game = (Game) i.getSerializableExtra("GAME");

		setContentView(R.layout.table_list);

		TextView tv = (TextView) findViewById(R.id.table_title);
		tv.setText(getResources().getString(R.string.activity_tables, game.getName()));
		showTableList();
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

	public void showTableList() {
		tableListContext = new TableListContext();

		final Rect r = new Rect();
		tableListContext.scrollView = (MyScrollView) findViewById(R.id.scroll_view);
		tableListContext.scrollView.setOnScrollChanged(new OnScrollChangedListener() {
			@Override
			public void onScrollChanged() {
				if (!tableListContext.allLoaded && !tableListContext.loading) {
					LinearLayout ln = (LinearLayout) tableListContext.scrollView.getChildAt(0);
					if (ln.getChildCount() > 3) {
						boolean b = ln.getChildAt(ln.getChildCount() - 3).getLocalVisibleRect(r);
						if (b) {
							tableListContext.loading = true;
							loadTableNextPage();
						}
					}
				}
			}
		});
		initTableList();

	}

	private void initTableList() {
		tableListContext.loadedPage = 0;
		if (tableListContext.tables == null) {
			tableListContext.tables = new ArrayList<Table>();
		} else {
			tableListContext.tables.clear();
		}
		loadTableNextPage();
	}

	private void loadTableNextPage() {
		if (tableListContext.loadedPage == 0) {
			// int nb =
			// GameDao.getInstance(this).getGameCount(gameListContext.collection,
			// gameListContext.search, gameListContext.query);
			// Toast.makeText(this,
			// getResources().getString(R.string.message_nb_result, nb),
			// Toast.LENGTH_LONG).show();
			List<Table> newTables = TableDao.getInstance(this).getTables(game, tableListContext.loadedPage, TableListContext.PAGE_SIZE);
			importTableEnded(true, newTables);
		} else {
			LoadTableListener l = new LoadTableListener() {

				@Override
				public void importEnded(boolean succeed, List<Table> tables) {
					importTableEnded(succeed, tables);
				}

				@Override
				public Context getContext() {
					return TableListActivity.this;
				}
			};
			tableListContext.task = new LoadTablesTask(l, game, tableListContext.loadedPage, TableListContext.PAGE_SIZE);
			tableListContext.task.execute();
		}
	}

	public void importTableEnded(boolean succeed, List<Table> newTables) {
		if (succeed) {
			tableListContext.task = null;
			if (newTables != null && newTables.size() > 0) {
				if (newTables.size() == TableListContext.PAGE_SIZE + 1) {
					newTables.remove(TableListContext.PAGE_SIZE);
					tableListContext.allLoaded = false;
				} else {
					tableListContext.allLoaded = true;
				}
			} else {
				tableListContext.allLoaded = true;
			}
			if (tableListContext.loadedPage != 0) {
				addTableElementToList(newTables);
			} else {
				tableListContext.tables = newTables;
				buildTableList();
			}
			tableListContext.loadedPage++;
		}
		tableListContext.loading = false;

	}

	private void buildTableList() {

		LinearLayout ln = (LinearLayout) findViewById(R.id.table_list);
		ln.removeAllViews();
		if (tableListContext.tables != null && tableListContext.tables.size() > 0) {
			addTableElementToList(tableListContext.tables);
		} else {
			TextView tv = new TextView(this);
			tv.setText(R.string.empty_table_list);
			ln.addView(tv);
		}
	}

	private void addTableElementToList(List<Table> newTables) {
		LinearLayout ln = (LinearLayout) findViewById(R.id.table_list);

		if (newTables != tableListContext.tables) {
			tableListContext.tables.addAll(newTables);
			if (ln.getChildCount() > 0) {
				ln.removeViewAt(ln.getChildCount() - 1);
			}
		}
		for (final Table table : newTables) {
			table.setGame(game);
			TableSummaryView view = new TableSummaryView(this, table);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(TableListActivity.this, GameSessionActivity.class);
					GameSession gameSession = new GameSession();

					List<Group> groups = GroupDao.getInstance(TableListActivity.this).getGroups(game.getId());
					Player p = new Player();
					p.setName("Gerald");
					p.setLocationName("A");
					String deckId = DeckDao.getInstance(TableListActivity.this).getDeck(game.getId(), "Chaos Starter Deck").getId();
					p.setDeck(DeckDao.getInstance(TableListActivity.this).buidGameDeck(deckId));
					for (Group group : groups) {
						Log.d(ApplicationConstants.PACKAGE, "group=" + group.getName());

						p.addCardGroup(new CardGroup(group));
					}
					gameSession.getPlayers().add(p);

					p = new Player();
					p.setName("Emma");
					p.setLocationName("B");
					deckId = DeckDao.getInstance(TableListActivity.this).getDeck(game.getId(), "Dwarf Starter Deck").getId();
					p.setDeck(DeckDao.getInstance(TableListActivity.this).buidGameDeck(deckId));
					for (Group group : groups) {
						p.addCardGroup(new CardGroup(group));
					}
					gameSession.getPlayers().add(p);

					gameSession.setGameTable(new GameTable(table));

					i.putExtra("GAME_SESSION", gameSession);
					startActivity(i);
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

		if (!tableListContext.allLoaded) {
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
		MenuItem search = menu.add(0, ApplicationConstants.MENU_ID_IMPORT_TABLE, 1, R.string.import_table);
		search.setIcon(android.R.drawable.ic_menu_upload);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_IMPORT_TABLE) {
			final File importTableDir = new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY_IMPORT_TABLES);
			final String[] files = importTableDir.list();
			if (files == null || files.length == 0) {
				DialogUtil.showConfirmDialog(this, getResources().getString(R.string.empty_import_table_dir, ApplicationConstants.DIRECTORY_IMPORT_TABLES));
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getResources().getString(R.string.import_table));
				builder.setAdapter(new StringAdapter(TableListActivity.this, files), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						dialog.dismiss();
						File file = new File(importTableDir, files[item]);
						ImportTableDriver task = DriverManager.getImportTableDriver(file);
						if (task != null) {
							task.importTable(new ImportTableListener() {

								@Override
								public void importEnded(boolean succeed, Table table, Exception exception) {
									Log.d(ApplicationConstants.PACKAGE, "import ended");
									if (exception != null) {
										DialogUtil.showErrorDialog(TableListActivity.this, getResources().getString(R.string.an_error_occurs), exception);
									} else if (succeed) {
										Log.d(ApplicationConstants.PACKAGE, "on refresh");
										initTableList();
									}

								}

								@Override
								public Context getContext() {
									return TableListActivity.this;
								}
							}, game, file);
							// Intent i = new Intent(OperationList.this,
							// DefineImportParameter.class);
							// i.putExtra("ACCOUNT", account);
							// i.putExtra("FILE_DRIVER_INDEX", item);
							// startActivityForResult(i,
							// ApplicationConstants.ACTIVITY_RETURN_IMPORT_OPERATION);
						} else {
							DialogUtil.showConfirmDialog(TableListActivity.this, getResources().getString(R.string.no_driver, files[item]));
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