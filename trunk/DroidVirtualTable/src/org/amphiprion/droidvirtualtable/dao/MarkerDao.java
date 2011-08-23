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
import org.amphiprion.droidvirtualtable.entity.Marker;
import org.amphiprion.droidvirtualtable.entity.Marker.Shape;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database marker access.
 * 
 * @author amphiprion
 * 
 */
public class MarkerDao extends AbstractDao {
	/** The singleton. */
	private static MarkerDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private MarkerDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static MarkerDao getInstance(Context context) {
		if (instance == null) {
			instance = new MarkerDao(context);
		}
		return instance;
	}

	/**
	 * Return true if the given marker exists in android database.
	 * 
	 * @param gameId
	 *            the linked game id
	 * @param name
	 *            the name
	 * @return the marker or null if not exists
	 */
	public Marker getMarker(String gameId, String name) {

		String sql = "SELECT " + Marker.DbField.ID + "," + Marker.DbField.GAME_ID + "," + Marker.DbField.NAME + "," + Marker.DbField.IMAGE + "," + Marker.DbField.SHAPE + ","
				+ Marker.DbField.WIDTH + "," + Marker.DbField.HEIGHT + " from MARKER where " + Marker.DbField.GAME_ID + "=? and " + Marker.DbField.NAME + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { gameId, name });
		Marker result = null;
		if (cursor.moveToFirst()) {
			Marker a = new Marker(cursor.getString(0));
			a.setGame(new Game(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setImageName(cursor.getString(3));
			a.setShape(Shape.valueOf(cursor.getString(4)));
			a.setWidth(Integer.parseInt(cursor.getString(5)));
			a.setHeight(Integer.parseInt(cursor.getString(6)));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Return the given Marker or null if not exists.
	 * 
	 * @param id
	 *            the id
	 * @return the Marker
	 */
	public Marker getMarker(String id) {
		String sql = "SELECT " + Marker.DbField.ID + "," + Marker.DbField.GAME_ID + "," + Marker.DbField.NAME + "," + Marker.DbField.IMAGE + "," + Marker.DbField.SHAPE + ","
				+ Marker.DbField.WIDTH + "," + Marker.DbField.HEIGHT + " from MARKER where " + Marker.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		Marker result = null;
		if (cursor.moveToFirst()) {
			Marker a = new Marker(cursor.getString(0));
			a.setGame(new Game(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setImageName(cursor.getString(3));
			a.setShape(Shape.valueOf(cursor.getString(4)));
			a.setWidth(Integer.parseInt(cursor.getString(5)));
			a.setHeight(Integer.parseInt(cursor.getString(6)));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new Marker.
	 * 
	 * @param entity
	 *            the new Marker
	 */
	public void create(Marker entity) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into MARKER (" + Marker.DbField.ID + "," + Marker.DbField.GAME_ID + "," + Marker.DbField.NAME + "," + Marker.DbField.IMAGE + ","
					+ Marker.DbField.SHAPE + "," + Marker.DbField.WIDTH + "," + Marker.DbField.HEIGHT + ") values (?,?,?,?,?,?,?)";
			Object[] params = new Object[7];

			params[0] = entity.getId();
			params[1] = entity.getGame().getId();
			params[2] = entity.getName();
			params[3] = entity.getImageName();
			params[4] = entity.getShape().name();
			params[5] = entity.getWidth();
			params[6] = entity.getHeight();

			execSQL(sql, params);

			getDatabase().setTransactionSuccessful();
			entity.setState(DbState.LOADED);

		} finally {
			getDatabase().endTransaction();
		}
	}

	public void update(Marker entity) {
		String sql = "update MARKER set " + Marker.DbField.GAME_ID + "=?," + Marker.DbField.NAME + "=?," + Marker.DbField.IMAGE + "=?," + Marker.DbField.SHAPE + "=?,"
				+ Marker.DbField.WIDTH + "=?," + Marker.DbField.HEIGHT + "=? WHERE " + Marker.DbField.ID + "=?";
		Object[] params = new Object[7];
		params[0] = entity.getGame().getId();
		params[1] = entity.getName();
		params[2] = entity.getImageName();
		params[3] = entity.getShape().name();
		params[4] = entity.getWidth();
		params[5] = entity.getHeight();
		params[6] = entity.getId();

		execSQL(sql, params);

	}

	public void persist(Marker entity) {
		if (entity.getState() == DbState.NEW) {
			create(entity);
		} else if (entity.getState() == DbState.LOADED) {
			update(entity);
		}
	}

}
