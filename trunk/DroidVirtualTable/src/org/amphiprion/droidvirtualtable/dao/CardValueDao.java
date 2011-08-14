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
import org.amphiprion.droidvirtualtable.entity.CardProperty;
import org.amphiprion.droidvirtualtable.entity.CardValue;
import org.amphiprion.droidvirtualtable.entity.Entity.DbState;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database card value access.
 * 
 * @author amphiprion
 * 
 */
public class CardValueDao extends AbstractDao {
	/** The singleton. */
	private static CardValueDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private CardValueDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static CardValueDao getInstance(Context context) {
		if (instance == null) {
			instance = new CardValueDao(context);
		}
		return instance;
	}

	/**
	 * Return the given CardValue or null if not exists.
	 * 
	 * @param id
	 *            the id
	 * @return the CardValue
	 */
	public CardValue getCardValue(String id) {
		String sql = "SELECT " + CardValue.DbField.ID + "," + CardValue.DbField.CARD_ID + "," + CardValue.DbField.CARD_PROP_ID + "," + CardValue.DbField.VALUE
				+ " from CARD_VALUE where " + CardValue.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		CardValue result = null;
		if (cursor.moveToFirst()) {
			CardValue a = new CardValue(cursor.getString(0));
			a.setCard(new Card(cursor.getString(1)));
			a.setProperty(new CardProperty(cursor.getString(2)));
			a.setValue(cursor.getString(3));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new CardValue.
	 * 
	 * @param game
	 *            the new CardValue
	 */
	public void create(CardValue entity) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into CARD_VALUE (" + CardValue.DbField.ID + "," + CardValue.DbField.CARD_ID + "," + CardValue.DbField.CARD_PROP_ID + "," + CardValue.DbField.VALUE
					+ ") values (?,?,?,?)";
			Object[] params = new Object[4];
			params[0] = entity.getId();
			params[1] = entity.getCard().getId();
			params[2] = entity.getProperty().getId();
			params[3] = entity.getValue();

			execSQL(sql, params);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	public void update(CardValue entity) {
		String sql = "update CARD_VALUE set " + CardValue.DbField.CARD_ID + "=?," + CardValue.DbField.CARD_PROP_ID + "=?," + CardValue.DbField.VALUE + "=? WHERE "
				+ CardValue.DbField.ID + "=?";
		Object[] params = new Object[4];
		params[0] = entity.getCard().getId();
		params[1] = entity.getProperty().getId();
		params[2] = entity.getValue();
		params[3] = entity.getId();

		execSQL(sql, params);

	}

	public void persist(CardValue entity) {
		if (entity.getState() == DbState.NEW) {
			create(entity);
		} else if (entity.getState() == DbState.LOADED) {
			update(entity);
		}
	}

	public void deleteAll(String cardId) {
		String sql = "delete from CARD_VALUE where " + CardValue.DbField.CARD_ID + "=?";

		execSQL(sql, new String[] { cardId });
	}

}
