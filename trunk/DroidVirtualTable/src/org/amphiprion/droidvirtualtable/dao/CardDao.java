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

import org.amphiprion.droidvirtualtable.entity.Card;
import org.amphiprion.droidvirtualtable.entity.CardDefinition;
import org.amphiprion.droidvirtualtable.entity.Entity.DbState;
import org.amphiprion.droidvirtualtable.entity.GameSet;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database card access.
 * 
 * @author amphiprion
 * 
 */
public class CardDao extends AbstractDao {
	/** The singleton. */
	private static CardDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private CardDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static CardDao getInstance(Context context) {
		if (instance == null) {
			instance = new CardDao(context);
		}
		return instance;
	}

	/**
	 * Return the given Card or null if not exists.
	 * 
	 * @param id
	 *            the id
	 * @return the Card
	 */
	public Card getCard(String id) {
		String sql = "SELECT " + Card.DbField.ID + "," + Card.DbField.GAME_SET_ID + "," + Card.DbField.NAME + "," + Card.DbField.IMAGE + "," + Card.DbField.CARD_DEF_ID
				+ " from CARD where " + Card.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		Card result = null;
		if (cursor.moveToFirst()) {
			Card a = new Card(cursor.getString(0));
			a.setGameSet(new GameSet(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setImageName(cursor.getString(3));
			a.setDefinition(new CardDefinition(cursor.getString(4)));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Return the given Card or null if not exists.
	 * 
	 * @param setId
	 *            the id of the set
	 * @param name
	 *            the name
	 * @return the Card
	 */
	public Card getCard(String setId, String name) {
		String sql = "SELECT " + Card.DbField.ID + "," + Card.DbField.GAME_SET_ID + "," + Card.DbField.NAME + "," + Card.DbField.IMAGE + "," + Card.DbField.CARD_DEF_ID
				+ " from CARD where " + Card.DbField.GAME_SET_ID + "=? and " + Card.DbField.NAME + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { setId, name });
		Card result = null;
		if (cursor.moveToFirst()) {
			Card a = new Card(cursor.getString(0));
			a.setGameSet(new GameSet(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setImageName(cursor.getString(3));
			a.setDefinition(new CardDefinition(cursor.getString(6)));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new Card.
	 * 
	 * @param entity
	 *            the new Card
	 */
	public void create(Card entity) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into CARD (" + Card.DbField.ID + "," + Card.DbField.GAME_SET_ID + "," + Card.DbField.NAME + "," + Card.DbField.IMAGE + ","
					+ Card.DbField.CARD_DEF_ID + ") values (?,?,?,?,?)";
			Object[] params = new Object[5];
			params[0] = entity.getId();
			params[1] = entity.getGameSet().getId();
			params[2] = entity.getName();
			params[3] = entity.getImageName();
			params[4] = entity.getDefinition().getId();

			execSQL(sql, params);

			getDatabase().setTransactionSuccessful();
			entity.setState(DbState.LOADED);

		} finally {
			getDatabase().endTransaction();
		}
	}

	public void update(Card entity) {
		String sql = "update CARD set " + Card.DbField.GAME_SET_ID + "=?," + Card.DbField.NAME + "=?," + Card.DbField.IMAGE + "=?," + Card.DbField.CARD_DEF_ID + "=? WHERE "
				+ Card.DbField.ID + "=?";
		Object[] params = new Object[5];
		params[0] = entity.getGameSet().getId();
		params[1] = entity.getName();
		params[2] = entity.getImageName();
		params[3] = entity.getDefinition().getId();
		params[4] = entity.getId();
		execSQL(sql, params);

	}

	public void persist(Card entity) {
		if (entity.getState() == DbState.NEW) {
			create(entity);
		} else if (entity.getState() == DbState.LOADED) {
			update(entity);
		}
	}

}
