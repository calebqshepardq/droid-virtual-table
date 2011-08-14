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
 * This entity represente a Game.
 * 
 * @author amphiprion
 * 
 */
public class CardDefinition extends Entity {
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	private Game game;

	private boolean defaultDefinition;
	/** The game name. */
	private String name;
	/** The image name. */
	private String backImageName;
	/** The image name. */
	private String frontImageName;
	/** The width in millimeter. */
	private int width;
	/** The height in millimeter. */
	private int height;

	public enum DbField {
		ID, GAME_ID, IS_DEFAULT, NAME, BACK_IMAGE, FRONT_IMAGE, WIDTH, HEIGHT
	}

	/**
	 * Default constructor.
	 * 
	 * @param id
	 *            the identifier
	 */
	public CardDefinition(String id) {
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

	public String getBackImageName() {
		return backImageName;
	}

	public void setBackImageName(String backImageName) {
		this.backImageName = backImageName;
	}

	public String getFrontImageName() {
		return frontImageName;
	}

	public void setFrontImageName(String frontImageName) {
		this.frontImageName = frontImageName;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public boolean isDefaultDefinition() {
		return defaultDefinition;
	}

	public void setDefaultDefinition(boolean defaultDefinition) {
		this.defaultDefinition = defaultDefinition;
	}
}
