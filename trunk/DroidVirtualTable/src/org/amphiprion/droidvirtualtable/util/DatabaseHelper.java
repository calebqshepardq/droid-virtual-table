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
package org.amphiprion.droidvirtualtable.util;

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.entity.Action;
import org.amphiprion.droidvirtualtable.entity.CardDefinition;
import org.amphiprion.droidvirtualtable.entity.CardProperty;
import org.amphiprion.droidvirtualtable.entity.CardType;
import org.amphiprion.droidvirtualtable.entity.Counter;
import org.amphiprion.droidvirtualtable.entity.Game;
import org.amphiprion.droidvirtualtable.entity.Group;
import org.amphiprion.droidvirtualtable.entity.Script;
import org.amphiprion.droidvirtualtable.entity.Section;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "amphiprion_droidvirtualtable";
	private static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL("create table GAME (" + Game.DbField.ID + " text primary key, " + Game.DbField.NAME + " text not null, " + Game.DbField.IMAGE_NAME + " text" + ","
					+ Game.DbField.PLAYER_SUMMARY + " text" + ") ");

			db.execSQL("create table CARD_DEFINITION (" + CardDefinition.DbField.ID + " text primary key, " + CardDefinition.DbField.GAME_ID + " text not null,"
					+ CardDefinition.DbField.IS_DEFAULT + " integer(1), " + CardDefinition.DbField.NAME + " text not null," + CardDefinition.DbField.BACK_IMAGE
					+ " text not null, " + CardDefinition.DbField.FRONT_IMAGE + " text not null, " + CardDefinition.DbField.WIDTH + " integer, " + CardDefinition.DbField.HEIGHT
					+ " integer)");

			db.execSQL("create table CARD_PROPERTY (" + CardProperty.DbField.ID + " text primary key, " + CardProperty.DbField.CARD_DEF_ID + " text not null,"
					+ CardProperty.DbField.NAME + " text not null," + CardProperty.DbField.TYPE + " text not null)");

			db.execSQL("create table CARD_TYPE (" + CardType.DbField.ID + " text primary key, " + CardType.DbField.CARD_DEF_ID + " text not null," + CardType.DbField.NAME
					+ " text not null)");

			db.execSQL("create table COUNTER (" + Counter.DbField.ID + " text primary key, " + Counter.DbField.GAME_ID + " text not null," + Counter.DbField.NAME
					+ " text not null," + Counter.DbField.IMAGE + " text not null, " + Counter.DbField.WIDTH + " integer, " + Counter.DbField.HEIGHT + " integer)");

			db.execSQL("create table ZONE (" + Group.DbField.ID + " text primary key, " + Group.DbField.GAME_ID + " text not null," + Group.DbField.NAME + " text not null,"
					+ Group.DbField.IMAGE + " text not null," + Group.DbField.TYPE + " text not null," + Group.DbField.VISIBILITY + " text not null,"
					+ Group.DbField.VISIBILITY_VALUE + " text, " + Group.DbField.WIDTH + " integer, " + Group.DbField.HEIGHT + " integer)");

			db.execSQL("create table ACTION (" + Action.DbField.ID + " text primary key, " + Action.DbField.GROUP_ID + " text not null," + Action.DbField.NAME + " text not null,"
					+ Action.DbField.TYPE + " text not null," + Action.DbField.COMMAND + " text not null, " + Action.DbField.IS_DEFAULT + " integer(1) not null)");

			db.execSQL("create table SECTION (" + Section.DbField.ID + " text primary key, " + Section.DbField.GAME_ID + " text not null," + Section.DbField.NAME
					+ " text not null, " + Section.DbField.GROUP_ID + " text not null)");

			db.execSQL("create table SCRIPT (" + Script.DbField.ID + " text primary key, " + Script.DbField.GAME_ID + " text not null," + Script.DbField.FILENAME
					+ " text not null)");

			onUpgrade(db, 1, DATABASE_VERSION);
		} catch (Throwable e) {
			Log.e(ApplicationConstants.PACKAGE, "", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// if (oldVersion == 1) {
		// db.execSQL("create table SEARCH (" + Search.DbField.ID +
		// " text not null, " + Search.DbField.NAME + " text not null," +
		// Search.DbField.MIN_PLAYER + " integer,"
		// + Search.DbField.MAX_PLAYER + " integer)");
		// oldVersion++;
		// }
		// if (oldVersion == 2) {
		// db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MIN_DIFFICULTY
		// + " integer default 0");
		// db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MAX_DIFFICULTY
		// + " integer default 0");
		// db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MIN_LUCK +
		// " integer default 0");
		// db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MAX_LUCK +
		// " integer default 0");
		// db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MIN_STRATEGY +
		// " integer default 0");
		// db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MAX_STRATEGY +
		// " integer default 0");
		// db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MIN_DIPLOMACY +
		// " integer default 0");
		// db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MAX_DIPLOMACY +
		// " integer default 0");
		// oldVersion++;
		// }
	}

}
