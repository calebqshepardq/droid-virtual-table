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

import org.amphiprion.droidvirtualtable.entity.Card;
import org.amphiprion.droidvirtualtable.entity.Deck;
import org.amphiprion.droidvirtualtable.entity.DeckContent;
import org.amphiprion.droidvirtualtable.entity.Entity.DbState;
import org.amphiprion.droidvirtualtable.entity.Section;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database deck content access.
 * 
 * @author amphiprion
 * 
 */
public class DeckContentDao extends AbstractDao {
	/** The singleton. */
	private static DeckContentDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private DeckContentDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static DeckContentDao getInstance(Context context) {
		if (instance == null) {
			instance = new DeckContentDao(context);
		}
		return instance;
	}

	/**
	 * Return true if the given DeckContent exists in android database.
	 * 
	 * @param deckId
	 *            the linked deck id
	 * @param sectionId
	 *            the linked section id
	 * @param name
	 *            the name
	 * @return the DeckContent or null if not exists
	 */
	public DeckContent getDeckContent(String deckId, String sectionId, String name) {

		String sql = "SELECT " + DeckContent.DbField.ID + "," + DeckContent.DbField.DECK_ID + "," + DeckContent.DbField.SECTION_ID + "," + DeckContent.DbField.NAME + ","
				+ DeckContent.DbField.CARD_ID + "," + DeckContent.DbField.QUANTITY + " from DECK_CONTENT where " + DeckContent.DbField.DECK_ID + "=? and "
				+ DeckContent.DbField.SECTION_ID + "=? and " + DeckContent.DbField.NAME + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { deckId, sectionId, name });
		DeckContent result = null;
		if (cursor.moveToFirst()) {
			DeckContent a = new DeckContent(cursor.getString(0));
			a.setDeck(new Deck(cursor.getString(1)));
			a.setSection(new Section(cursor.getString(2)));
			a.setName(cursor.getString(3));
			a.setCard(new Card(cursor.getString(4)));
			a.setQuantity(cursor.getInt(5));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Return true if the given DeckContent exists in android database.
	 * 
	 * @param deckId
	 *            the linked deck id
	 * @param sectionId
	 *            the linked section id
	 * @param name
	 *            the name
	 * @return the DeckContent or null if not exists
	 */
	public List<DeckContent> getDeckContents(String deckId) {

		String sql = "SELECT " + DeckContent.DbField.ID + "," + DeckContent.DbField.DECK_ID + "," + DeckContent.DbField.SECTION_ID + "," + DeckContent.DbField.NAME + ","
				+ DeckContent.DbField.CARD_ID + "," + DeckContent.DbField.QUANTITY + " from DECK_CONTENT where " + DeckContent.DbField.DECK_ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { deckId });
		ArrayList<DeckContent> result = new ArrayList<DeckContent>();
		if (cursor.moveToFirst()) {
			do {
				DeckContent a = new DeckContent(cursor.getString(0));
				a.setDeck(new Deck(cursor.getString(1)));
				a.setSection(new Section(cursor.getString(2)));
				a.setName(cursor.getString(3));
				a.setCard(new Card(cursor.getString(4)));
				a.setQuantity(cursor.getInt(5));
				result.add(a);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	/**
	 * Return the given DeckContent or null if not exists.
	 * 
	 * @param id
	 *            the id
	 * @return the DeckContent
	 */
	public DeckContent getDeckContent(String id) {
		String sql = "SELECT " + DeckContent.DbField.ID + "," + DeckContent.DbField.DECK_ID + "," + DeckContent.DbField.SECTION_ID + "," + DeckContent.DbField.NAME + ","
				+ DeckContent.DbField.CARD_ID + "," + DeckContent.DbField.QUANTITY + " from DECK_CONTENT where " + DeckContent.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		DeckContent result = null;
		if (cursor.moveToFirst()) {
			DeckContent a = new DeckContent(cursor.getString(0));
			a.setDeck(new Deck(cursor.getString(1)));
			a.setSection(new Section(cursor.getString(2)));
			a.setName(cursor.getString(3));
			a.setCard(new Card(cursor.getString(4)));
			a.setQuantity(cursor.getInt(5));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new DeckContent.
	 * 
	 * @param entity
	 *            the new DeckContent
	 */
	public void create(DeckContent entity) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into DECK_CONTENT (" + DeckContent.DbField.ID + "," + DeckContent.DbField.DECK_ID + "," + DeckContent.DbField.SECTION_ID + ","
					+ DeckContent.DbField.NAME + "," + DeckContent.DbField.CARD_ID + "," + DeckContent.DbField.QUANTITY + ") values (?,?,?,?,?,?)";
			Object[] params = new Object[6];

			params[0] = entity.getId();
			params[1] = entity.getDeck().getId();
			params[2] = entity.getSection().getId();
			params[3] = entity.getName();
			params[4] = entity.getCard().getId();
			params[5] = entity.getQuantity();

			execSQL(sql, params);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	public void update(DeckContent entity) {
		String sql = "update DECK_CONTENT set " + DeckContent.DbField.DECK_ID + "=?," + DeckContent.DbField.SECTION_ID + "=?," + DeckContent.DbField.NAME + "=?,"
				+ DeckContent.DbField.CARD_ID + "=?," + DeckContent.DbField.QUANTITY + "=? WHERE " + DeckContent.DbField.ID + "=?";
		Object[] params = new Object[6];
		params[0] = entity.getDeck().getId();
		params[1] = entity.getSection().getId();
		params[2] = entity.getName();
		params[3] = entity.getCard().getId();
		params[4] = entity.getQuantity();
		params[5] = entity.getId();

		execSQL(sql, params);

	}

	public void persist(DeckContent entity) {
		if (entity.getState() == DbState.NEW) {
			create(entity);
		} else if (entity.getState() == DbState.LOADED) {
			update(entity);
		}
	}

}
