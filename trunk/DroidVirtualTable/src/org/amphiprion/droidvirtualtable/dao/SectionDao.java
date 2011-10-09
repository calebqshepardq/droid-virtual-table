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
import org.amphiprion.droidvirtualtable.entity.Section;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database Section access.
 * 
 * @author amphiprion
 * 
 */
public class SectionDao extends AbstractDao {
	/** The singleton. */
	private static SectionDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private SectionDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static SectionDao getInstance(Context context) {
		if (instance == null) {
			instance = new SectionDao(context);
		}
		return instance;
	}

	/**
	 * Return true if the given Section exists in android database.
	 * 
	 * @param gameId
	 *            the linked game id
	 * @param name
	 *            the name
	 * @return the Section or null if not exists
	 */
	public Section getSection(String gameId, String name) {

		String sql = "SELECT " + Section.DbField.ID + "," + Section.DbField.GAME_ID + "," + Section.DbField.NAME + "," + Section.DbField.GROUP_ID + " from SECTION where "
				+ Section.DbField.GAME_ID + "=? and " + Section.DbField.NAME + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { gameId, name });
		Section result = null;
		if (cursor.moveToFirst()) {
			Section a = new Section(cursor.getString(0));
			a.setGame(new Game(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setStartupGroup(new Group(cursor.getString(3)));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Return the given Section or null if not exists.
	 * 
	 * @param id
	 *            the id
	 * @return the Section
	 */
	public Section getSection(String id) {
		String sql = "SELECT " + Section.DbField.ID + "," + Section.DbField.GAME_ID + "," + Section.DbField.NAME + "," + Section.DbField.GROUP_ID + " from SECTION where "
				+ Section.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		Section result = null;
		if (cursor.moveToFirst()) {
			Section a = new Section(cursor.getString(0));
			a.setGame(new Game(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setStartupGroup(new Group(cursor.getString(3)));
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
	public void create(Section entity) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into SECTION (" + Section.DbField.ID + "," + Section.DbField.GAME_ID + "," + Section.DbField.NAME + "," + Section.DbField.GROUP_ID
					+ ") values (?,?,?,?)";
			Object[] params = new Object[4];

			params[0] = entity.getId();
			params[1] = entity.getGame().getId();
			params[2] = entity.getName();
			params[3] = entity.getStartupGroup().getId();

			execSQL(sql, params);

			getDatabase().setTransactionSuccessful();
			entity.setState(DbState.LOADED);

		} finally {
			getDatabase().endTransaction();
		}
	}

	public void update(Section entity) {
		String sql = "update SECTION set " + Section.DbField.GAME_ID + "=?," + Section.DbField.NAME + "=?," + Section.DbField.GROUP_ID + "=? WHERE " + Section.DbField.ID + "=?";
		Object[] params = new Object[4];
		params[0] = entity.getGame().getId();
		params[1] = entity.getName();
		params[2] = entity.getStartupGroup().getId();
		params[3] = entity.getId();

		execSQL(sql, params);

	}

	public void persist(Section entity) {
		if (entity.getState() == DbState.NEW) {
			create(entity);
		} else if (entity.getState() == DbState.LOADED) {
			update(entity);
		}
	}

	public List<Section> getSections(String gameId) {
		String sql = "SELECT " + Section.DbField.ID + "," + Section.DbField.GAME_ID + "," + Section.DbField.NAME + "," + Section.DbField.GROUP_ID + " from SECTION where "
				+ Section.DbField.GAME_ID + "=?";

		sql += " order by " + Section.DbField.NAME;

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { gameId });
		ArrayList<Section> result = new ArrayList<Section>();
		if (cursor.moveToFirst()) {
			do {
				Section a = new Section(cursor.getString(0));
				a.setGame(new Game(cursor.getString(1)));
				a.setName(cursor.getString(2));
				a.setStartupGroup(new Group(cursor.getString(3)));
				result.add(a);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

}
