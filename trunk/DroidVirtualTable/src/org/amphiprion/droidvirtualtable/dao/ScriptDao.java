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

import org.amphiprion.droidvirtualtable.entity.Entity.DbState;
import org.amphiprion.droidvirtualtable.entity.Game;
import org.amphiprion.droidvirtualtable.entity.Script;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database Script access.
 * 
 * @author amphiprion
 * 
 */
public class ScriptDao extends AbstractDao {
	/** The singleton. */
	private static ScriptDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private ScriptDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static ScriptDao getInstance(Context context) {
		if (instance == null) {
			instance = new ScriptDao(context);
		}
		return instance;
	}

	/**
	 * Return true if the given Script exists in android database.
	 * 
	 * @param gameId
	 *            the linked game id
	 * @param name
	 *            the name
	 * @return the Script or null if not exists
	 */
	public Script getScript(String gameId, String name) {

		String sql = "SELECT " + Script.DbField.ID + "," + Script.DbField.GAME_ID + "," + Script.DbField.FILENAME + " from SCRIPT where " + Script.DbField.GAME_ID + "=? and "
				+ Script.DbField.FILENAME + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { gameId, name });
		Script result = null;
		if (cursor.moveToFirst()) {
			Script a = new Script(cursor.getString(0));
			a.setGame(new Game(cursor.getString(1)));
			a.setFilename(cursor.getString(2));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Return the given Script or null if not exists.
	 * 
	 * @param id
	 *            the id
	 * @return the Script
	 */
	public Script getScript(String id) {
		String sql = "SELECT " + Script.DbField.ID + "," + Script.DbField.GAME_ID + "," + Script.DbField.FILENAME + " from SCRIPT where " + Script.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		Script result = null;
		if (cursor.moveToFirst()) {
			Script a = new Script(cursor.getString(0));
			a.setGame(new Game(cursor.getString(1)));
			a.setFilename(cursor.getString(2));
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
	public void create(Script entity) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into SCRIPT (" + Script.DbField.ID + "," + Script.DbField.GAME_ID + "," + Script.DbField.FILENAME + ") values (?,?,?)";
			Object[] params = new Object[3];

			params[0] = entity.getId();
			params[1] = entity.getGame().getId();
			params[2] = entity.getFilename();

			execSQL(sql, params);

			getDatabase().setTransactionSuccessful();
			entity.setState(DbState.LOADED);
		} finally {
			getDatabase().endTransaction();
		}
	}

	public void update(Script entity) {
		String sql = "update SCRIPT set " + Script.DbField.GAME_ID + "=?," + Script.DbField.FILENAME + "=? WHERE " + Script.DbField.ID + "=?";
		Object[] params = new Object[3];
		params[0] = entity.getGame().getId();
		params[1] = entity.getFilename();
		params[2] = entity.getId();

		execSQL(sql, params);

	}

	public void persist(Script entity) {
		if (entity.getState() == DbState.NEW) {
			create(entity);
		} else if (entity.getState() == DbState.LOADED) {
			update(entity);
		}
	}

}
