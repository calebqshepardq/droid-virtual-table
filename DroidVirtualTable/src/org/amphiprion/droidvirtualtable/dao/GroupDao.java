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
import org.amphiprion.droidvirtualtable.entity.Group;
import org.amphiprion.droidvirtualtable.entity.Group.Type;
import org.amphiprion.droidvirtualtable.entity.Group.Visibility;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database group access.
 * 
 * @author amphiprion
 * 
 */
public class GroupDao extends AbstractDao {
	/** The singleton. */
	private static GroupDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private GroupDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static GroupDao getInstance(Context context) {
		if (instance == null) {
			instance = new GroupDao(context);
		}
		return instance;
	}

	/**
	 * Return true if the given Group exists in android database.
	 * 
	 * @param gameId
	 *            the linked game id
	 * @param name
	 *            the name
	 * @return the group or null if not exists
	 */
	public Group getGroup(String gameId, String name) {

		String sql = "SELECT " + Group.DbField.ID + "," + Group.DbField.GAME_ID + "," + Group.DbField.NAME + "," + Group.DbField.TYPE + "," + Group.DbField.VISIBILITY + ","
				+ Group.DbField.VISIBILITY_VALUE + "," + Group.DbField.WIDTH + "," + Group.DbField.HEIGHT + " from ZONE where " + Group.DbField.GAME_ID + "=? and "
				+ Group.DbField.NAME + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { gameId, name });
		Group result = null;
		if (cursor.moveToFirst()) {
			Group a = new Group(cursor.getString(0));
			a.setGame(new Game(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setType(Type.valueOf(cursor.getString(3)));
			a.setVisibility(Visibility.valueOf(cursor.getString(4)));
			a.setVisibilityValue(cursor.getString(5));
			a.setWidth(Integer.parseInt(cursor.getString(6)));
			a.setHeight(Integer.parseInt(cursor.getString(7)));
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
	public Group getGroup(String id) {
		String sql = "SELECT " + Group.DbField.ID + "," + Group.DbField.GAME_ID + "," + Group.DbField.NAME + "," + Group.DbField.TYPE + "," + Group.DbField.VISIBILITY + ","
				+ Group.DbField.VISIBILITY_VALUE + "," + Group.DbField.WIDTH + "," + Group.DbField.HEIGHT + " from ZONE where " + Group.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		Group result = null;
		if (cursor.moveToFirst()) {
			Group a = new Group(cursor.getString(0));
			a.setGame(new Game(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setType(Type.valueOf(cursor.getString(3)));
			a.setVisibility(Visibility.valueOf(cursor.getString(4)));
			a.setVisibilityValue(cursor.getString(5));
			a.setWidth(Integer.parseInt(cursor.getString(6)));
			a.setHeight(Integer.parseInt(cursor.getString(7)));
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
	public List<Group> getGroups(String gameId) {
		String sql = "SELECT " + Group.DbField.ID + "," + Group.DbField.GAME_ID + "," + Group.DbField.NAME + "," + Group.DbField.TYPE + "," + Group.DbField.VISIBILITY + ","
				+ Group.DbField.VISIBILITY_VALUE + "," + Group.DbField.WIDTH + "," + Group.DbField.HEIGHT + " from ZONE where " + Group.DbField.GAME_ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { gameId });
		ArrayList<Group> result = new ArrayList<Group>();
		if (cursor.moveToFirst()) {
			do {
				Group a = new Group(cursor.getString(0));
				a.setGame(new Game(cursor.getString(1)));
				a.setName(cursor.getString(2));
				a.setType(Type.valueOf(cursor.getString(3)));
				a.setVisibility(Visibility.valueOf(cursor.getString(4)));
				a.setVisibilityValue(cursor.getString(5));
				a.setWidth(Integer.parseInt(cursor.getString(6)));
				a.setHeight(Integer.parseInt(cursor.getString(7)));
				result.add(a);
			} while (cursor.moveToNext());
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
	public void create(Group entity) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into ZONE (" + Group.DbField.ID + "," + Group.DbField.GAME_ID + "," + Group.DbField.NAME + "," + Group.DbField.IMAGE + "," + Group.DbField.TYPE
					+ "," + Group.DbField.VISIBILITY + "," + Group.DbField.VISIBILITY_VALUE + "," + Group.DbField.WIDTH + "," + Group.DbField.HEIGHT
					+ ") values (?,?,?,?,?,?,?,?,?)";
			Object[] params = new Object[9];

			params[0] = entity.getId();
			params[1] = entity.getGame().getId();
			params[2] = entity.getName();
			params[3] = entity.getImageName();
			params[4] = entity.getType().name();
			params[5] = entity.getVisibility().name();
			params[6] = entity.getVisibilityValue();
			params[7] = entity.getWidth();
			params[8] = entity.getHeight();

			execSQL(sql, params);

			getDatabase().setTransactionSuccessful();
			entity.setState(DbState.LOADED);

		} finally {
			getDatabase().endTransaction();
		}
	}

	public void update(Group entity) {
		String sql = "update ZONE set " + Group.DbField.GAME_ID + "=?," + Group.DbField.NAME + "=?," + Group.DbField.IMAGE + "=?," + Group.DbField.TYPE + "=?,"
				+ Group.DbField.VISIBILITY + "=?," + Group.DbField.VISIBILITY_VALUE + "=?," + Group.DbField.WIDTH + "=?," + Group.DbField.HEIGHT + "=? WHERE " + Group.DbField.ID
				+ "=?";
		Object[] params = new Object[9];
		params[0] = entity.getGame().getId();
		params[1] = entity.getName();
		params[2] = entity.getImageName();
		params[3] = entity.getType().name();
		params[4] = entity.getVisibility().name();
		params[5] = entity.getVisibilityValue();
		params[6] = entity.getWidth();
		params[7] = entity.getHeight();
		params[8] = entity.getId();

		execSQL(sql, params);

	}

	public void persist(Group entity) {
		if (entity.getState() == DbState.NEW) {
			create(entity);
		} else if (entity.getState() == DbState.LOADED) {
			update(entity);
		}
	}

}
