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
 * This entity represente a Playmat Group.
 * 
 * @author amphiprion
 * 
 */
public class Group extends Entity {
	public enum Type {
		HAND, TABLE, PILE
	}

	public enum Visibility {
		none, all, me, PLAYERS, undefined
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
	/** The type. */
	private Type type;
	/** The visibility. */
	private Visibility visibility;
	/** The visibility value for PLAYERS. */
	private String visibilityValue;
	/** The width in millimeter. */
	private int width;
	/** The height in millimeter. */
	private int height;

	public enum DbField {
		ID, GAME_ID, NAME, IMAGE, TYPE, VISIBILITY, VISIBILITY_VALUE, WIDTH, HEIGHT
	}

	public Group() {
		super();
	}

	/**
	 * Default constructor.
	 * 
	 * @param id
	 *            the identifier
	 */
	public Group(String id) {
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	public String getVisibilityValue() {
		return visibilityValue;
	}

	public void setVisibilityValue(String visibilityValue) {
		this.visibilityValue = visibilityValue;
	}
}
