/*
 * @copyright 2010 Gerald Jacobson
 * @license GNU General Public License
 * 
 * This file is part of My Accounts.
 *
 * My Accounts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * My Accounts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with My Accounts.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.amphiprion.droidvirtualtable.engine3d.mesh;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.amphiprion.droidvirtualtable.engine3d.util.Texture;

public class Mesh {
	private String name;
	private Texture texture;

	private FloatBuffer verticePropertyBuffer;
	private ShortBuffer indiceBuffer;

	public Mesh(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FloatBuffer getVerticePropertyBuffer() {
		return verticePropertyBuffer;
	}

	public void setVerticePropertyBuffer(FloatBuffer verticePropertyBuffer) {
		this.verticePropertyBuffer = verticePropertyBuffer;
	}

	public ShortBuffer getIndiceBuffer() {
		return indiceBuffer;
	}

	public void setIndiceBuffer(ShortBuffer indiceBuffer) {
		this.indiceBuffer = indiceBuffer;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
}
