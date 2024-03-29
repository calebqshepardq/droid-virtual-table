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
 * This entity represente a GameSet.
 * 
 * @author amphiprion
 * 
 */
public class GameSet extends Entity {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	/** The linked game. */
	private Game game;

	/** The counter name. */
	private String name;
	/** The image name. */
	private String ImageName;

	private int cardCount;

	public enum DbField {
		ID, GAME_ID, NAME, IMAGE
	}

	public GameSet() {
		super();
	}

	/**
	 * Default constructor.
	 * 
	 * @param id
	 *            the identifier
	 */
	public GameSet(String id) {
		super(id);
	}

	/**
	 * @return the group name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the new group name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getImageName() {
		return ImageName;
	}

	public void setImageName(String imageName) {
		ImageName = imageName;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public int getCardCount() {
		return cardCount;
	}

	public void setCardCount(int cardCount) {
		this.cardCount = cardCount;
	}
}
