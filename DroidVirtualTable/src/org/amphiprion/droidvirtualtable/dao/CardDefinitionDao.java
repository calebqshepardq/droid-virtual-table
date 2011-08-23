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

import org.amphiprion.droidvirtualtable.entity.CardDefinition;
import org.amphiprion.droidvirtualtable.entity.Entity.DbState;
import org.amphiprion.droidvirtualtable.entity.Game;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database card definition access.
 * 
 * @author amphiprion
 * 
 */
public class CardDefinitionDao extends AbstractDao {
	/** The singleton. */
	private static CardDefinitionDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private CardDefinitionDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static CardDefinitionDao getInstance(Context context) {
		if (instance == null) {
			instance = new CardDefinitionDao(context);
		}
		return instance;
	}

	/**
	 * Return true if the given CardDefinition exists in android database.
	 * 
	 * @param id
	 *            the CardDefinition id
	 * @return true if exists
	 */
	public boolean exists(String id) {
		String sql = "SELECT 1 from CARD_DEFINITION where " + CardDefinition.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		if (cursor.moveToFirst()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return the given CardDefinition or null if not exists.
	 * 
	 * @param id
	 *            the id
	 * @return the game
	 */
	public CardDefinition getCardDefinition(String id) {
		String sql = "SELECT " + CardDefinition.DbField.ID + "," + CardDefinition.DbField.GAME_ID + "," + CardDefinition.DbField.NAME + "," + CardDefinition.DbField.IS_DEFAULT
				+ "," + CardDefinition.DbField.BACK_IMAGE + "," + CardDefinition.DbField.FRONT_IMAGE + "," + CardDefinition.DbField.WIDTH + "," + CardDefinition.DbField.HEIGHT
				+ " from CARD_DEFINITION where " + CardDefinition.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		CardDefinition result = null;
		if (cursor.moveToFirst()) {
			CardDefinition a = new CardDefinition(cursor.getString(0));
			a.setGame(new Game(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setDefaultDefinition(cursor.getInt(3) != 0);
			a.setBackImageName(cursor.getString(4));
			a.setFrontImageName(cursor.getString(5));
			a.setWidth(cursor.getInt(6));
			a.setHeight(cursor.getInt(7));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new CardDefinition.
	 * 
	 * @param game
	 *            the new CardDefinition
	 */
	public void create(CardDefinition def) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into CARD_DEFINITION (" + CardDefinition.DbField.ID + "," + CardDefinition.DbField.GAME_ID + "," + CardDefinition.DbField.NAME + ","
					+ CardDefinition.DbField.IS_DEFAULT + "," + CardDefinition.DbField.BACK_IMAGE + "," + CardDefinition.DbField.FRONT_IMAGE + "," + CardDefinition.DbField.WIDTH
					+ "," + CardDefinition.DbField.HEIGHT + ") values (?,?,?,?,?,?,?,?)";
			Object[] params = new Object[8];
			params[0] = def.getId();
			params[1] = def.getGame().getId();
			params[2] = def.getName();
			params[3] = def.isDefaultDefinition() ? 1 : 0;
			params[4] = def.getBackImageName();
			params[5] = def.getFrontImageName();
			params[6] = def.getWidth();
			params[7] = def.getHeight();

			execSQL(sql, params);

			getDatabase().setTransactionSuccessful();
			def.setState(DbState.LOADED);

		} finally {
			getDatabase().endTransaction();
		}
	}

	public void update(CardDefinition def) {
		String sql = "update CARD_DEFINITION set " + CardDefinition.DbField.GAME_ID + "=?," + CardDefinition.DbField.NAME + "=?," + CardDefinition.DbField.IS_DEFAULT + "=?,"
				+ CardDefinition.DbField.BACK_IMAGE + "=?," + CardDefinition.DbField.FRONT_IMAGE + "=?," + CardDefinition.DbField.WIDTH + "=?," + CardDefinition.DbField.HEIGHT
				+ "=? WHERE " + CardDefinition.DbField.ID + "=?";
		Object[] params = new Object[8];
		params[0] = def.getGame().getId();
		params[1] = def.getName();
		params[2] = def.isDefaultDefinition() ? 1 : 0;
		params[3] = def.getBackImageName();
		params[4] = def.getFrontImageName();
		params[5] = def.getWidth();
		params[6] = def.getHeight();
		params[7] = def.getId();

		execSQL(sql, params);

	}

	public void persist(CardDefinition def) {
		if (def.getState() == DbState.NEW) {
			create(def);
		} else if (def.getState() == DbState.LOADED) {
			update(def);
		}
	}

}
