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
package org.amphiprion.droidvirtualtable.dao;

import java.util.ArrayList;
import java.util.List;

import org.amphiprion.droidvirtualtable.entity.Card;
import org.amphiprion.droidvirtualtable.entity.Deck;
import org.amphiprion.droidvirtualtable.entity.Entity.DbState;
import org.amphiprion.droidvirtualtable.entity.Game;
import org.amphiprion.droidvirtualtable.entity.GameSet;
import org.amphiprion.droidvirtualtable.entity.Table;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database game access.
 * 
 * @author amphiprion
 * 
 */
public class GameDao extends AbstractDao {
	/** The singleton. */
	private static GameDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private GameDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static GameDao getInstance(Context context) {
		if (instance == null) {
			instance = new GameDao(context);
		}
		return instance;
	}

	/**
	 * Return all existing games of a given collection.
	 * 
	 * @param collection
	 *            the collection
	 * @return the game list
	 */
	public int getGameCount() {

		String sql = "SELECT count(*) from GAME";

		Cursor cursor = getDatabase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	/**
	 * Return true if the given game exists in android database.
	 * 
	 * @param id
	 *            the game id
	 * @return true if exists
	 */
	public boolean exists(String id) {
		String sql = "SELECT 1 from GAME where " + Game.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		if (cursor.moveToFirst()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return all existing games of a given collection.
	 * 
	 * @param collection
	 *            the collection
	 * @return the game list
	 */
	public List<Game> getGames(int pageIndex, int pageSize) {
		String sql = "SELECT " + Game.DbField.ID + "," + Game.DbField.NAME + "," + Game.DbField.IMAGE_NAME + "," + Game.DbField.PLAYER_SUMMARY;
		sql += ", (select count(1) from GAME_SET s where s." + GameSet.DbField.GAME_ID + "=g." + Game.DbField.ID + ")";
		sql += ", (select count(1) from GAME_SET s, CARD c where s." + GameSet.DbField.GAME_ID + "=g." + Game.DbField.ID + " and c." + Card.DbField.GAME_SET_ID + "=s."
				+ GameSet.DbField.ID + ")";
		sql += ", (select count(1) from DECK d where d." + Deck.DbField.GAME_ID + "=g." + Game.DbField.ID + ")";
		sql += ", (select count(1) from BOARD_TABLE t where t." + Table.DbField.GAME_ID + "=g." + Game.DbField.ID + ")";
		sql += " FROM GAME g";

		sql += " order by " + Game.DbField.NAME + " asc limit " + (pageSize + 1) + " offset " + pageIndex * pageSize;

		Cursor cursor = getDatabase().rawQuery(sql, null);
		ArrayList<Game> result = new ArrayList<Game>();
		if (cursor.moveToFirst()) {
			do {
				Game a = new Game(cursor.getString(0));
				a.setName(cursor.getString(1));
				a.setImageName(cursor.getString(2));
				a.setPlayerSummary(cursor.getString(3));
				a.setGameSetCount(cursor.getInt(4));
				a.setCardCount(cursor.getInt(5));
				a.setDeckCount(cursor.getInt(6));
				a.setTableCount(cursor.getInt(7));
				result.add(a);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	/**
	 * Return the given game or null if not exists.
	 * 
	 * @param id
	 *            the id
	 * @return the game
	 */
	public Game getGame(String id) {
		String sql = "SELECT " + Game.DbField.ID + "," + Game.DbField.NAME + "," + Game.DbField.IMAGE_NAME + "," + Game.DbField.PLAYER_SUMMARY + " from GAME g where "
				+ Game.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		Game result = null;
		if (cursor.moveToFirst()) {
			Game a = new Game(cursor.getString(0));
			a.setName(cursor.getString(1));
			a.setImageName(cursor.getString(2));
			a.setPlayerSummary(cursor.getString(3));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new game.
	 * 
	 * @param game
	 *            the new game
	 */
	public void createGame(Game game) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into GAME (" + Game.DbField.ID + "," + Game.DbField.NAME + "," + Game.DbField.IMAGE_NAME + "," + Game.DbField.PLAYER_SUMMARY
					+ ") values (?,?,?,?)";
			Object[] params = new Object[4];
			params[0] = game.getId();
			params[1] = game.getName();
			params[2] = game.getImageName();
			params[3] = game.getPlayerSummary();

			execSQL(sql, params);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	public void update(Game game) {
		String sql = "update GAME set " + Game.DbField.NAME + "=?," + Game.DbField.IMAGE_NAME + "=?," + Game.DbField.PLAYER_SUMMARY + "=? WHERE " + Game.DbField.ID + "=?";
		Object[] params = new Object[4];
		params[0] = game.getName();
		params[1] = game.getImageName();
		params[2] = game.getPlayerSummary();
		params[3] = game.getId();

		execSQL(sql, params);

	}

	public void persist(Game game) {
		if (game.getState() == DbState.NEW) {
			createGame(game);
		} else if (game.getState() == DbState.LOADED) {
			update(game);
		}
	}

}
