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

import org.amphiprion.droidvirtualtable.entity.Counter;
import org.amphiprion.droidvirtualtable.entity.Entity.DbState;
import org.amphiprion.droidvirtualtable.entity.Game;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database group access.
 * 
 * @author amphiprion
 * 
 */
public class CounterDao extends AbstractDao {
	/** The singleton. */
	private static CounterDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private CounterDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static CounterDao getInstance(Context context) {
		if (instance == null) {
			instance = new CounterDao(context);
		}
		return instance;
	}

	/**
	 * Return true if the given Counter exists in android database.
	 * 
	 * @param gameId
	 *            the linked game id
	 * @param name
	 *            the name
	 * @return the Counter or null if not exists
	 */
	public Counter getCounter(String gameId, String name) {

		String sql = "SELECT " + Counter.DbField.ID + "," + Counter.DbField.GAME_ID + "," + Counter.DbField.NAME + "," + Counter.DbField.IMAGE + "," + Counter.DbField.WIDTH + ","
				+ Counter.DbField.HEIGHT + "," + Counter.DbField.DEFAULT_VALUE + " from COUNTER where " + Counter.DbField.GAME_ID + "=? and " + Counter.DbField.NAME + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { gameId, name });
		Counter result = null;
		if (cursor.moveToFirst()) {
			Counter a = new Counter(cursor.getString(0));
			a.setGame(new Game(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setImageName(cursor.getString(3));
			a.setWidth(Integer.parseInt(cursor.getString(4)));
			a.setHeight(Integer.parseInt(cursor.getString(5)));
			a.setDefaultValue(Integer.parseInt(cursor.getString(6)));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Return the given Group or null if not exists.
	 * 
	 * @param id
	 *            the id
	 * @return the group
	 */
	public Counter getCounter(String id) {
		String sql = "SELECT " + Counter.DbField.ID + "," + Counter.DbField.GAME_ID + "," + Counter.DbField.NAME + "," + Counter.DbField.IMAGE + "," + Counter.DbField.WIDTH + ","
				+ Counter.DbField.HEIGHT + "," + Counter.DbField.DEFAULT_VALUE + " from COUNTER where " + Counter.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		Counter result = null;
		if (cursor.moveToFirst()) {
			Counter a = new Counter(cursor.getString(0));
			a.setGame(new Game(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setImageName(cursor.getString(3));
			a.setWidth(Integer.parseInt(cursor.getString(4)));
			a.setHeight(Integer.parseInt(cursor.getString(5)));
			a.setDefaultValue(Integer.parseInt(cursor.getString(6)));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new Group.
	 * 
	 * @param entity
	 *            the new Group
	 */
	public void create(Counter entity) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into COUNTER (" + Counter.DbField.ID + "," + Counter.DbField.GAME_ID + "," + Counter.DbField.NAME + "," + Counter.DbField.IMAGE + ","
					+ Counter.DbField.WIDTH + "," + Counter.DbField.HEIGHT + "," + Counter.DbField.DEFAULT_VALUE + ") values (?,?,?,?,?,?,?)";
			Object[] params = new Object[7];

			params[0] = entity.getId();
			params[1] = entity.getGame().getId();
			params[2] = entity.getName();
			params[3] = entity.getImageName();
			params[4] = entity.getWidth();
			params[5] = entity.getHeight();
			params[6] = entity.getDefaultValue();

			execSQL(sql, params);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	public void update(Counter entity) {
		String sql = "update COUNTER set " + Counter.DbField.GAME_ID + "=?," + Counter.DbField.NAME + "=?," + Counter.DbField.IMAGE + "=?," + Counter.DbField.WIDTH + "=?,"
				+ Counter.DbField.HEIGHT + "=?," + Counter.DbField.DEFAULT_VALUE + "=? WHERE " + Counter.DbField.ID + "=?";
		Object[] params = new Object[7];
		params[0] = entity.getGame().getId();
		params[1] = entity.getName();
		params[2] = entity.getImageName();
		params[3] = entity.getWidth();
		params[4] = entity.getHeight();
		params[5] = entity.getDefaultValue();
		params[6] = entity.getId();

		execSQL(sql, params);

	}

	public void persist(Counter entity) {
		if (entity.getState() == DbState.NEW) {
			create(entity);
		} else if (entity.getState() == DbState.LOADED) {
			update(entity);
		}
	}

}
