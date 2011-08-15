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

import org.amphiprion.droidvirtualtable.entity.Deck;
import org.amphiprion.droidvirtualtable.entity.DeckContent;
import org.amphiprion.droidvirtualtable.entity.Entity.DbState;
import org.amphiprion.droidvirtualtable.entity.Game;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database deck access.
 * 
 * @author amphiprion
 * 
 */
public class DeckDao extends AbstractDao {
	/** The singleton. */
	private static DeckDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private DeckDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static DeckDao getInstance(Context context) {
		if (instance == null) {
			instance = new DeckDao(context);
		}
		return instance;
	}

	/**
	 * Return all existing deck of a given game.
	 * 
	 * @param game
	 *            the game
	 * @return the deck list
	 */
	public List<Deck> getDecks(Game game, int pageIndex, int pageSize) {
		String sql = "SELECT " + Deck.DbField.ID + "," + Deck.DbField.GAME_ID + "," + Deck.DbField.NAME;
		sql += ", (select sum(" + DeckContent.DbField.QUANTITY + ") FROM DECK_CONTENT c where c." + DeckContent.DbField.DECK_ID + "=d." + Deck.DbField.ID + ")";
		sql += " FROM DECK d where d." + Deck.DbField.GAME_ID + "=?";

		sql += " order by " + Deck.DbField.NAME + " asc limit " + (pageSize + 1) + " offset " + pageIndex * pageSize;

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { game.getId() });
		ArrayList<Deck> result = new ArrayList<Deck>();
		if (cursor.moveToFirst()) {
			do {
				Deck a = new Deck(cursor.getString(0));
				a.setGame(new Game(cursor.getString(1)));
				a.setName(cursor.getString(2));
				a.setCardCount(cursor.getInt(3));
				result.add(a);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	/**
	 * Return true if the given Deck exists in android database.
	 * 
	 * @param gameId
	 *            the linked game id
	 * @param name
	 *            the name
	 * @return the Deck or null if not exists
	 */
	public Deck getDeck(String gameId, String name) {

		String sql = "SELECT " + Deck.DbField.ID + "," + Deck.DbField.GAME_ID + "," + Deck.DbField.NAME + " from DECK where " + Deck.DbField.GAME_ID + "=? and "
				+ Deck.DbField.NAME + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { gameId, name });
		Deck result = null;
		if (cursor.moveToFirst()) {
			Deck a = new Deck(cursor.getString(0));
			a.setGame(new Game(cursor.getString(1)));
			a.setName(cursor.getString(2));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Return the given Deck or null if not exists.
	 * 
	 * @param id
	 *            the id
	 * @return the Deck
	 */
	public Deck getDeck(String id) {
		String sql = "SELECT " + Deck.DbField.ID + "," + Deck.DbField.GAME_ID + "," + Deck.DbField.NAME + " from DECK where " + Deck.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		Deck result = null;
		if (cursor.moveToFirst()) {
			Deck a = new Deck(cursor.getString(0));
			a.setGame(new Game(cursor.getString(1)));
			a.setName(cursor.getString(2));
			result = a;
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new Deck.
	 * 
	 * @param entity
	 *            the new Deck
	 */
	public void create(Deck entity) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into DECK (" + Deck.DbField.ID + "," + Deck.DbField.GAME_ID + "," + Deck.DbField.NAME + ") values (?,?,?)";
			Object[] params = new Object[3];

			params[0] = entity.getId();
			params[1] = entity.getGame().getId();
			params[2] = entity.getName();

			execSQL(sql, params);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	public void update(Deck entity) {
		String sql = "update DECK set " + Deck.DbField.GAME_ID + "=?," + Deck.DbField.NAME + "=? WHERE " + Deck.DbField.ID + "=?";
		Object[] params = new Object[3];
		params[0] = entity.getGame().getId();
		params[1] = entity.getName();
		params[2] = entity.getId();

		execSQL(sql, params);

	}

	public void persist(Deck entity) {
		if (entity.getState() == DbState.NEW) {
			create(entity);
		} else if (entity.getState() == DbState.LOADED) {
			update(entity);
		}
	}

}
