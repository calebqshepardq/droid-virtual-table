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
 * This entity represente a Playmat Action.
 * 
 * @author amphiprion
 * 
 */
public class Action extends Entity {
	public enum Type {
		GROUP, CARD
	}

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	/** The linked group. */
	private Group group;

	/** The counter name. */
	private String name;
	/** The type. */
	private Type type;
	/** The visibility value for PLAYERS. */
	private String command;
	private boolean defaultAction;

	public enum DbField {
		ID, GROUP_ID, NAME, TYPE, COMMAND, IS_DEFAULT
	}

	public Action() {
		super();
	}

	/**
	 * Default constructor.
	 * 
	 * @param id
	 *            the identifier
	 */
	public Action(String id) {
		super(id);
	}

	/**
	 * @return the action name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the new action name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public boolean isDefaultAction() {
		return defaultAction;
	}

	public void setDefaultAction(boolean defaultAction) {
		this.defaultAction = defaultAction;
	}
}
