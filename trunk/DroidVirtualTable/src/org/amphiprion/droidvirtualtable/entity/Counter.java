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
public class Counter extends Entity {
	public enum Shape {
		RECTANGLE, OVAL
	}

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
	/** The counter Shape. */
	private Shape shape;
	/** The width in millimeter. */
	private int width;
	/** The height in millimeter. */
	private int height;

	public enum DbField {
		ID, GAME_ID, NAME, IMAGE, SHAPE, WIDTH, HEIGHT
	}

	/**
	 * Default constructor.
	 * 
	 * @param id
	 *            the identifier
	 */
	public Counter(String id) {
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

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}
}
