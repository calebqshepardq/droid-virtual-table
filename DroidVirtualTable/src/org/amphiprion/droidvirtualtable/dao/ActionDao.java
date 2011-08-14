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

import org.amphiprion.droidvirtualtable.entity.Action;
import org.amphiprion.droidvirtualtable.entity.Action.Type;
import org.amphiprion.droidvirtualtable.entity.Entity.DbState;
import org.amphiprion.droidvirtualtable.entity.Group;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database Action access.
 * 
 * @author amphiprion
 * 
 */
public class ActionDao extends AbstractDao {
	/** The singleton. */
	private static ActionDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private ActionDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static ActionDao getInstance(Context context) {
		if (instance == null) {
			instance = new ActionDao(context);
		}
		return instance;
	}

	/**
	 * Return true if the given Action exists in android database.
	 * 
	 * @param gameId
	 *            the linked game id
	 * @param name
	 *            the name
	 * @return the group or null if not exists
	 */
	public Action getAction(String groupId, String name) {

		String sql = "SELECT " + Action.DbField.ID + "," + Action.DbField.GROUP_ID + "," + Action.DbField.NAME + "," + Action.DbField.TYPE + "," + Action.DbField.COMMAND + ","
				+ Action.DbField.IS_DEFAULT + " from ACTION where " + Action.DbField.GROUP_ID + "=? and " + Action.DbField.NAME + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { groupId, name });
		Action result = null;
		if (cursor.moveToFirst()) {
			Action a = new Action(cursor.getString(0));
			a.setGroup(new Group(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setType(Type.valueOf(cursor.getString(3)));
			a.setCommand(cursor.getString(4));
			a.setDefaultAction(cursor.getInt(5) == 1);
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Return the given Action or null if not exists.
	 * 
	 * @param id
	 *            the id
	 * @return the group
	 */
	public Action getAction(String id) {
		String sql = "SELECT " + Action.DbField.ID + "," + Action.DbField.GROUP_ID + "," + Action.DbField.NAME + "," + Action.DbField.TYPE + "," + Action.DbField.COMMAND + ","
				+ Action.DbField.IS_DEFAULT + " from ACTION where " + Action.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		Action result = null;
		if (cursor.moveToFirst()) {
			Action a = new Action(cursor.getString(0));
			a.setGroup(new Group(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setType(Type.valueOf(cursor.getString(3)));
			a.setCommand(cursor.getString(4));
			a.setDefaultAction(cursor.getInt(5) == 1);
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
	public void create(Action entity) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into ACTION (" + Action.DbField.ID + "," + Action.DbField.GROUP_ID + "," + Action.DbField.NAME + "," + Action.DbField.TYPE + ","
					+ Action.DbField.COMMAND + "," + Action.DbField.IS_DEFAULT + ") values (?,?,?,?,?,?)";
			Object[] params = new Object[6];

			params[0] = entity.getId();
			params[1] = entity.getGroup().getId();
			params[2] = entity.getName();
			params[3] = entity.getType().name();
			params[4] = entity.getCommand();
			params[5] = entity.isDefaultAction() ? 1 : 0;

			execSQL(sql, params);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	public void update(Action entity) {
		String sql = "update ACTION set " + Action.DbField.GROUP_ID + "=?," + Action.DbField.NAME + "=?," + Action.DbField.TYPE + "=?," + Action.DbField.COMMAND + "=?,"
				+ Action.DbField.IS_DEFAULT + "=? WHERE " + Action.DbField.ID + "=?";
		Object[] params = new Object[6];
		params[0] = entity.getGroup().getId();
		params[1] = entity.getName();
		params[2] = entity.getType().name();
		params[3] = entity.getCommand();
		params[4] = entity.isDefaultAction() ? 1 : 0;
		params[5] = entity.getId();

		execSQL(sql, params);

	}

	public void persist(Action entity) {
		if (entity.getState() == DbState.NEW) {
			create(entity);
		} else if (entity.getState() == DbState.LOADED) {
			update(entity);
		}
	}

}
