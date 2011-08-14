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
import org.amphiprion.droidvirtualtable.entity.Entity.DbState;
import org.amphiprion.droidvirtualtable.entity.Game;
import org.amphiprion.droidvirtualtable.entity.GameSet;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database game set access.
 * 
 * @author amphiprion
 * 
 */
public class GameSetDao extends AbstractDao {
	/** The singleton. */
	private static GameSetDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private GameSetDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static GameSetDao getInstance(Context context) {
		if (instance == null) {
			instance = new GameSetDao(context);
		}
		return instance;
	}

	/**
	 * Return all existing sets of a given collection.
	 * 
	 * @param collection
	 *            the collection
	 * @return the set list
	 */
	public int getSetCount() {

		String sql = "SELECT count(*) from GAME_SET";

		Cursor cursor = getDatabase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	/**
	 * Return true if the given set exists in android database.
	 * 
	 * @param id
	 *            the set id
	 * @return true if exists
	 */
	public boolean exists(String id) {
		String sql = "SELECT 1 from GAME_SET where " + GameSet.DbField.ID + "=?";

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
	public List<GameSet> getSets(Game game, int pageIndex, int pageSize) {
		String sql = "SELECT " + GameSet.DbField.ID + "," + GameSet.DbField.GAME_ID + "," + GameSet.DbField.NAME + "," + GameSet.DbField.IMAGE;
		sql += ", (select count(ID) FROM CARD c where c." + Card.DbField.GAME_SET_ID + "=s." + GameSet.DbField.ID + ")";
		sql += " FROM GAME_SET s where s." + GameSet.DbField.GAME_ID + "=?";

		sql += " order by " + GameSet.DbField.NAME + " asc limit " + (pageSize + 1) + " offset " + pageIndex * pageSize;

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { game.getId() });
		ArrayList<GameSet> result = new ArrayList<GameSet>();
		if (cursor.moveToFirst()) {
			do {
				GameSet a = new GameSet(cursor.getString(0));
				a.setGame(new Game(cursor.getString(1)));
				a.setName(cursor.getString(2));
				a.setImageName(cursor.getString(3));
				a.setCardCount(cursor.getInt(4));
				result.add(a);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	/**
	 * Return the given set or null if not exists.
	 * 
	 * @param id
	 *            the id
	 * @return the set
	 */
	public GameSet getSet(String id) {
		String sql = "SELECT " + GameSet.DbField.ID + "," + GameSet.DbField.GAME_ID + "," + GameSet.DbField.NAME + "," + GameSet.DbField.IMAGE + " from GAME_SET g where "
				+ GameSet.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		GameSet result = null;
		if (cursor.moveToFirst()) {
			GameSet a = new GameSet(cursor.getString(0));
			a.setGame(new Game(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setImageName(cursor.getString(3));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new set.
	 * 
	 * @param entity
	 *            the new set
	 */
	public void create(GameSet entity) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into GAME_SET (" + GameSet.DbField.ID + "," + GameSet.DbField.GAME_ID + "," + GameSet.DbField.NAME + "," + GameSet.DbField.IMAGE
					+ ") values (?,?,?,?)";
			Object[] params = new Object[4];
			params[0] = entity.getId();
			params[1] = entity.getGame().getId();
			params[2] = entity.getName();
			params[3] = entity.getImageName();

			execSQL(sql, params);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	public void update(GameSet entity) {
		String sql = "update GAME_SET set " + GameSet.DbField.GAME_ID + "=?," + GameSet.DbField.NAME + "=?," + GameSet.DbField.IMAGE + "=? WHERE " + GameSet.DbField.ID + "=?";
		Object[] params = new Object[4];
		params[0] = entity.getGame().getId();
		params[1] = entity.getName();
		params[2] = entity.getImageName();
		params[3] = entity.getId();

		execSQL(sql, params);

	}

	public void persist(GameSet entity) {
		if (entity.getState() == DbState.NEW) {
			create(entity);
		} else if (entity.getState() == DbState.LOADED) {
			update(entity);
		}
	}

}
