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
package org.amphiprion.droidvirtualtable.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Amphiprion
 * 
 */
public class GameSession implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GameTable table;
	private List<Player> players;

	public GameSession() {
		players = new ArrayList<Player>();
	}

	public GameTable getGameTable() {
		return table;
	}

	public void setGameTable(GameTable table) {
		this.table = table;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Player getPlayer(String locationName) {
		for (Player p : players) {
			if (locationName.equals(p.getLocationName())) {
				return p;
			}
		}
		return null;
	}
}
