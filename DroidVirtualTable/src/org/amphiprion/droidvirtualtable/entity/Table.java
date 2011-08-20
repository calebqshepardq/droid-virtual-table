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
package org.amphiprion.droidvirtualtable.entity;

/**
 * This entity represente a Deck.
 * 
 * @author amphiprion
 * 
 */
public class Table extends Entity {
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	private Game game;

	/** The game name. */
	private String name;
	private int maxLocation;

	public enum DbField {
		ID, GAME_ID, NAME, LOCATION_COUNT
	}

	public Table() {
		super();
	}

	/**
	 * Default constructor.
	 * 
	 * @param id
	 *            the identifier
	 */
	public Table(String id) {
		super(id);
	}

	/**
	 * @return the game name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the new game name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public int getMaxLocation() {
		return maxLocation;
	}

	public void setMaxLocation(int maxLocation) {
		this.maxLocation = maxLocation;
	}
}
