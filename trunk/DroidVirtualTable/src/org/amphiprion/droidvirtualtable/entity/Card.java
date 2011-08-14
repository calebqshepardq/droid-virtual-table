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
 * This entity represente a Card.
 * 
 * @author amphiprion
 * 
 */
public class Card extends Entity {
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	private GameSet gameSet;

	/** The game name. */
	private String name;
	/** The image name. */
	private String imageName;

	private CardDefinition definition;

	public enum DbField {
		ID, GAME_SET_ID, NAME, IMAGE, CARD_DEF_ID
	}

	/**
	 * Default constructor.
	 * 
	 * @param id
	 *            the identifier
	 */
	public Card(String id) {
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

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public GameSet getGameSet() {
		return gameSet;
	}

	public void setGameSet(GameSet gameSet) {
		this.gameSet = gameSet;
	}

	public CardDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(CardDefinition definition) {
		this.definition = definition;
	}
}
