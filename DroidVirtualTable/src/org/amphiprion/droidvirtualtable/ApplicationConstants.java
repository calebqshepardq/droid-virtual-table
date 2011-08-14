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
package org.amphiprion.droidvirtualtable;

public interface ApplicationConstants {

	/** Package name. */
	public static final String PACKAGE = "org.amphiprion.droivirtualtable";
	public static final String DIRECTORY = "DroidVirtualTable";
	public static final String DIRECTORY_GAMES = DIRECTORY + "/games";
	public static final String DIRECTORY_TABLES = DIRECTORY + "/tables";
	public static final String DIRECTORY_IMPORT_GAMES = DIRECTORY + "/import/games";
	public static final String DIRECTORY_IMPORT_SETS = DIRECTORY + "/import/sets";
	public static final String DIRECTORY_IMPORT_DECKS = DIRECTORY + "/import/decks";

	public static final int MENU_ID_IMPORT_GAME = 1;
	public static final int MENU_ID_MANAGE_SET = 2;
	public static final int MENU_ID_IMPORT_SET = 3;

	public static final int ACTIVITY_RETURN_MANAGE_SET = 1;

}
