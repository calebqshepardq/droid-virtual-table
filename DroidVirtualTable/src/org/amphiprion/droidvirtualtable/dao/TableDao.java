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

import org.amphiprion.droidvirtualtable.entity.Entity.DbState;
import org.amphiprion.droidvirtualtable.entity.Game;
import org.amphiprion.droidvirtualtable.entity.Table;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database table access.
 * 
 * @author amphiprion
 * 
 */
public class TableDao extends AbstractDao {
	/** The singleton. */
	private static TableDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private TableDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static TableDao getInstance(Context context) {
		if (instance == null) {
			instance = new TableDao(context);
		}
		return instance;
	}

	/**
	 * Return all existing table of a given game.
	 * 
	 * @param game
	 *            the game
	 * @return the table list
	 */
	public List<Table> getTables(Game game, int pageIndex, int pageSize) {
		String sql = "SELECT " + Table.DbField.ID + "," + Table.DbField.GAME_ID + "," + Table.DbField.NAME + "," + Table.DbField.LOCATION_COUNT;
		sql += " FROM BOARD_TABLE where " + Table.DbField.GAME_ID + "=?";
		sql += " order by " + Table.DbField.NAME + " asc limit " + (pageSize + 1) + " offset " + pageIndex * pageSize;

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { game.getId() });
		ArrayList<Table> result = new ArrayList<Table>();
		if (cursor.moveToFirst()) {
			do {
				Table a = new Table(cursor.getString(0));
				a.setGame(new Game(cursor.getString(1)));
				a.setName(cursor.getString(2));
				a.setMaxLocation(cursor.getInt(3));
				result.add(a);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	/**
	 * Return the given table in android database.
	 * 
	 * @param gameId
	 *            the linked game id
	 * @param name
	 *            the name
	 * @return the Table or null if not exists
	 */
	public Table getTable(String gameId, String name) {

		String sql = "SELECT " + Table.DbField.ID + "," + Table.DbField.GAME_ID + "," + Table.DbField.NAME + "," + Table.DbField.LOCATION_COUNT;
		sql += " FROM BOARD_TABLE where " + Table.DbField.GAME_ID + "=? and " + Table.DbField.NAME + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { gameId, name });
		Table result = null;
		if (cursor.moveToFirst()) {
			Table a = new Table(cursor.getString(0));
			a.setGame(new Game(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setMaxLocation(cursor.getInt(3));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Return the given Table or null if not exists.
	 * 
	 * @param id
	 *            the id
	 * @return the Table
	 */
	public Table getTable(String id) {
		String sql = "SELECT " + Table.DbField.ID + "," + Table.DbField.GAME_ID + "," + Table.DbField.NAME + "," + Table.DbField.LOCATION_COUNT;
		sql += " FROM BOARD_TABLE where " + Table.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		Table result = null;
		if (cursor.moveToFirst()) {
			Table a = new Table(cursor.getString(0));
			a.setGame(new Game(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setMaxLocation(cursor.getInt(3));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new Table.
	 * 
	 * @param entity
	 *            the new Table
	 */
	public void create(Table entity) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into BOARD_TABLE (" + Table.DbField.ID + "," + Table.DbField.GAME_ID + "," + Table.DbField.NAME + "," + Table.DbField.LOCATION_COUNT
					+ ") values (?,?,?,?)";
			Object[] params = new Object[4];

			params[0] = entity.getId();
			params[1] = entity.getGame().getId();
			params[2] = entity.getName();
			params[3] = entity.getMaxLocation();
			execSQL(sql, params);
			getDatabase().setTransactionSuccessful();
			entity.setState(DbState.LOADED);
		} finally {
			getDatabase().endTransaction();
		}
	}

	public void update(Table entity) {
		String sql = "update BOARD_TABLE set " + Table.DbField.GAME_ID + "=?," + Table.DbField.NAME + "=?," + Table.DbField.LOCATION_COUNT + "=? WHERE " + Table.DbField.ID + "=?";
		Object[] params = new Object[4];
		params[0] = entity.getGame().getId();
		params[1] = entity.getName();
		params[2] = entity.getMaxLocation();
		params[3] = entity.getId();

		execSQL(sql, params);

	}

	public void persist(Table entity) {
		if (entity.getState() == DbState.NEW) {
			create(entity);
		} else if (entity.getState() == DbState.LOADED) {
			update(entity);
		}
	}

}
