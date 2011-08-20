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
 * along with DroidVirtualTable. If not, see <http://www.gnu.org/licenses/>.
 */
package org.amphiprion.droidvirtualtable.entity;

/**
 * This entity represente a Game.
 * 
 * @author amphiprion
 * 
 */
public class Game extends Entity {
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	/** The game name. */
	private String name;
	/** The image name. */
	private String imageName;
	/** The player summary. */
	private String playerSummary;
	private int gameSetCount;
	private int cardCount;
	private int deckCount;
	private int tableCount;

	public enum DbField {
		ID, NAME, IMAGE_NAME, PLAYER_SUMMARY
	}

	/**
	 * Default constructor.
	 * 
	 * @param id
	 *            the identifier
	 */
	public Game(String id) {
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

	/**
	 * @return the image name
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * @param imageName
	 *            the new image name
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public void setPlayerSummary(String playerSummary) {
		this.playerSummary = playerSummary;
	}

	public String getPlayerSummary() {
		return playerSummary;
	}

	public int getCardCount() {
		return cardCount;
	}

	public void setCardCount(int cardCount) {
		this.cardCount = cardCount;
	}

	public int getGameSetCount() {
		return gameSetCount;
	}

	public void setGameSetCount(int gameSetCount) {
		this.gameSetCount = gameSetCount;
	}

	public int getDeckCount() {
		return deckCount;
	}

	public void setDeckCount(int deckCount) {
		this.deckCount = deckCount;
	}

	public int getTableCount() {
		return tableCount;
	}

	public void setTableCount(int tableCount) {
		this.tableCount = tableCount;
	}
}
