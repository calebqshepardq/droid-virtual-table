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
import org.amphiprion.droidvirtualtable.entity.CardProperty;
import org.amphiprion.droidvirtualtable.entity.Entity.DbState;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database card property access.
 * 
 * @author amphiprion
 * 
 */
public class CardPropertyDao extends AbstractDao {
	/** The singleton. */
	private static CardPropertyDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private CardPropertyDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static CardPropertyDao getInstance(Context context) {
		if (instance == null) {
			instance = new CardPropertyDao(context);
		}
		return instance;
	}

	/**
	 * Return true if the given CardProperty exists in android database.
	 * 
	 * @param defId
	 *            the linked card definition id
	 * @param name
	 *            the name
	 * @return the card property or null if not exists
	 */
	public CardProperty getCardProperty(String defId, String name) {

		String sql = "SELECT " + CardProperty.DbField.ID + "," + CardProperty.DbField.CARD_DEF_ID + "," + CardProperty.DbField.NAME + "," + CardProperty.DbField.TYPE
				+ " from CARD_PROPERTY where " + CardProperty.DbField.CARD_DEF_ID + "=? and " + CardProperty.DbField.NAME + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { defId, name });
		CardProperty result = null;
		if (cursor.moveToFirst()) {
			CardProperty a = new CardProperty(cursor.getString(0));
			a.setDefinition(new CardDefinition(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setType(cursor.getString(3));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Return the given CardProperty or null if not exists.
	 * 
	 * @param id
	 *            the id
	 * @return the CardProperty
	 */
	public CardProperty getCardProperty(String id) {
		String sql = "SELECT " + CardProperty.DbField.ID + "," + CardProperty.DbField.CARD_DEF_ID + "," + CardProperty.DbField.NAME + "," + CardProperty.DbField.TYPE
				+ " from CARD_PROPERTY where " + CardProperty.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		CardProperty result = null;
		if (cursor.moveToFirst()) {
			CardProperty a = new CardProperty(cursor.getString(0));
			a.setDefinition(new CardDefinition(cursor.getString(1)));
			a.setName(cursor.getString(2));
			a.setType(cursor.getString(3));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new CardProperty.
	 * 
	 * @param game
	 *            the new CardProperty
	 */
	public void create(CardProperty prop) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into CARD_PROPERTY (" + CardProperty.DbField.ID + "," + CardProperty.DbField.CARD_DEF_ID + "," + CardProperty.DbField.NAME + ","
					+ CardProperty.DbField.TYPE + ") values (?,?,?,?)";
			Object[] params = new Object[4];
			params[0] = prop.getId();
			params[1] = prop.getDefinition().getId();
			params[2] = prop.getName();
			params[3] = prop.getType();

			execSQL(sql, params);

			getDatabase().setTransactionSuccessful();
			prop.setState(DbState.LOADED);

		} finally {
			getDatabase().endTransaction();
		}
	}

	public void update(CardProperty prop) {
		String sql = "update CARD_PROPERTY set " + CardProperty.DbField.CARD_DEF_ID + "=?," + CardProperty.DbField.NAME + "=?," + CardProperty.DbField.TYPE + "=? WHERE "
				+ CardProperty.DbField.ID + "=?";
		Object[] params = new Object[4];
		params[0] = prop.getDefinition().getId();
		params[1] = prop.getName();
		params[2] = prop.getType();
		params[3] = prop.getId();

		execSQL(sql, params);

	}

	public void persist(CardProperty prop) {
		if (prop.getState() == DbState.NEW) {
			create(prop);
		} else if (prop.getState() == DbState.LOADED) {
			update(prop);
		}
	}

}
