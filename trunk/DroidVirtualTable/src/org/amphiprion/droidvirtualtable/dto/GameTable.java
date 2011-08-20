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

import org.amphiprion.droidvirtualtable.engine3d.mesh.Mesh;
import org.amphiprion.droidvirtualtable.entity.Table;

public class GameTable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Table table;
	private List<Mesh> meshes;
	private List<TableLocation> tableLocations;

	public GameTable(Table table) {
		this.table = table;
		meshes = new ArrayList<Mesh>();
		tableLocations = new ArrayList<TableLocation>();
	}

	public Table getTable() {
		return table;
	}

	public List<Mesh> getMeshes() {
		return meshes;
	}

	public List<TableLocation> getTableLocations() {
		return tableLocations;
	}
}
