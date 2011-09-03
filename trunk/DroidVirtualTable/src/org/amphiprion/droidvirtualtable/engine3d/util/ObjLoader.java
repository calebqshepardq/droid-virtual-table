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
package org.amphiprion.droidvirtualtable.engine3d.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.amphiprion.droidvirtualtable.engine3d.mesh.AbstractMesh;
import org.amphiprion.droidvirtualtable.engine3d.mesh.SimpleMesh;

/**
 * @author Amphiprion
 * 
 */
public class ObjLoader {
	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int SHORT_SIZE_BYTES = 2;

	public static List<AbstractMesh> loadObjects(GL10 gl, String textureUriPrefix, File objFile) throws Exception {

		HashMap<String, String> materialTextures = null;

		ArrayList<AbstractMesh> meshes = new ArrayList<AbstractMesh>();
		SimpleMesh currentMesh = null;

		ArrayList<Float> vs = new ArrayList<Float>(100); // vertices x/y/z
		ArrayList<Float> tc = new ArrayList<Float>(100); // texture coords u/v
		ArrayList<Float> ns = new ArrayList<Float>(100); // normals x/y/z

		BufferedReader objReader = new BufferedReader(new InputStreamReader(new FileInputStream(objFile)));
		String str = objReader.readLine();
		while (str != null) {
			String[] elements = str.split(" ");
			String type = elements[0];
			if ("mtllib".equals(type)) {
				materialTextures = parseMaterialFile(new File(objFile.getParentFile(), elements[1]));
			} else if ("o".equals(type)) {
				currentMesh = new SimpleMesh(elements[1]);
				str = updateMesh(gl, textureUriPrefix, currentMesh, objReader, materialTextures, vs, tc, ns);
				meshes.add(currentMesh);
				continue;
			}

			str = objReader.readLine();
		}
		objReader.close();

		vs.clear();
		vs = null;
		tc.clear();
		tc = null;
		ns.clear();
		ns = null;

		return meshes;
	}

	private static String updateMesh(GL10 gl, String textureUriPrefix, SimpleMesh mesh, BufferedReader reader, HashMap<String, String> materialTextures, ArrayList<Float> vs,
			ArrayList<Float> tc, ArrayList<Float> ns) throws Exception {

		ArrayList<Float> vertexPropertiesBuffer = new ArrayList<Float>(600);
		ArrayList<Short> indicesBuffer = new ArrayList<Short>(200);

		String[] elements = null;
		String type = null;
		String sFace = null;
		String[] faceElements = null;

		String str = reader.readLine();
		short index = 0;
		float minX = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		float minY = Float.MAX_VALUE;
		float maxY = Float.MIN_VALUE;
		float minZ = Float.MAX_VALUE;
		float maxZ = Float.MIN_VALUE;
		while (str != null) {
			elements = str.split(" ");
			type = elements[0];
			if ("o".equals(type)) {
				break;
			} else if ("v".equals(type)) {
				vs.add(Float.parseFloat(elements[1]));
				vs.add(Float.parseFloat(elements[2]));
				vs.add(Float.parseFloat(elements[3]));
			} else if ("vt".equals(type)) {
				tc.add(Float.parseFloat(elements[1]));
				tc.add(1 - Float.parseFloat(elements[2]));
			} else if ("vn".equals(type)) {
				ns.add(Float.parseFloat(elements[1]));
				ns.add(Float.parseFloat(elements[2]));
				ns.add(Float.parseFloat(elements[3]));
			} else if ("usemtl".equals(type)) {
				if (mesh.getTexture() == null) {
					// only one texture per object
					mesh.setTexture(TextureUtil.loadTexture(textureUriPrefix + "/" + materialTextures.get(elements[1]), gl));
				}
			} else if ("f".equals(type)) {
				for (int j = 0; j < 3; j++) {
					sFace = elements[j + 1];
					// another tokenizer - based on /
					faceElements = sFace.split("/");

					int vert = Integer.parseInt(faceElements[0]) - 1;
					int texc = Integer.parseInt(faceElements[1]) - 1;
					int vertN = Integer.parseInt(faceElements[2]) - 1;

					// Add to the index buffer
					indicesBuffer.add(index++);

					// Add all the vertex info
					float x = vs.get(vert * 3);
					float y = vs.get(vert * 3 + 1);
					float z = vs.get(vert * 3 + 2);
					if (x > maxX) {
						maxX = x;
					}
					if (x < minX) {
						minX = x;
					}
					if (y > maxY) {
						maxY = y;
					}
					if (y < minY) {
						minY = y;
					}
					if (z > maxZ) {
						maxZ = z;
					}
					if (z < minZ) {
						minZ = z;
					}
					vertexPropertiesBuffer.add(x); // x
					vertexPropertiesBuffer.add(y);// y
					vertexPropertiesBuffer.add(z);// z

					// add the normal info
					vertexPropertiesBuffer.add(ns.get(vertN * 3)); // x
					vertexPropertiesBuffer.add(ns.get(vertN * 3 + 1)); // y
					vertexPropertiesBuffer.add(ns.get(vertN * 3 + 2)); // z

					// add the tex coord info
					vertexPropertiesBuffer.add(tc.get(texc * 2)); // u
					vertexPropertiesBuffer.add(tc.get(texc * 2 + 1)); // v

				}
			}
			str = reader.readLine();
		}

		// Create Float buffers
		vertexPropertiesBuffer.trimToSize();
		float[] vertexProperties = new float[vertexPropertiesBuffer.size()];
		for (int i = 0; i < vertexPropertiesBuffer.size(); i++) {
			vertexProperties[i] = vertexPropertiesBuffer.get(i);
		}
		FloatBuffer fb = ByteBuffer.allocateDirect(vertexProperties.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
		fb.put(vertexProperties);
		fb.position(0);
		mesh.setVerticePropertyBuffer(fb);

		// create short buffer
		indicesBuffer.trimToSize();
		short[] indices = new short[indicesBuffer.size()];
		for (int i = 0; i < indicesBuffer.size(); i++) {
			indices[i] = indicesBuffer.get(i);
		}
		ShortBuffer sb = ByteBuffer.allocateDirect(indices.length * SHORT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asShortBuffer();
		sb.put(indices);
		sb.position(0);
		mesh.setIndiceBuffer(sb);
		mesh.setIndiceCount(indices.length);
		mesh.setBounds(minX, maxX, minY, maxY, minZ, maxZ);
		// release to enhance GC
		vertexProperties = null;
		indices = null;

		vertexPropertiesBuffer.clear();
		vertexPropertiesBuffer = null;
		indicesBuffer.clear();
		indicesBuffer = null;

		elements = null;
		type = null;
		sFace = null;
		faceElements = null;

		// return last read line
		return str;
	}

	/**
	 * Parse a Material Texture Library file.
	 * 
	 * @param mtlFile
	 * @return key=material name, value=texture filename
	 */
	private static HashMap<String, String> parseMaterialFile(File mtlFile) throws Exception {
		HashMap<String, String> textures = new HashMap<String, String>();

		BufferedReader mtlReader = new BufferedReader(new InputStreamReader(new FileInputStream(mtlFile)));
		String str = mtlReader.readLine();
		String key = null;
		while (str != null) {
			String[] elements = str.split(" ");
			String type = elements[0];
			if ("newmtl".equals(type)) {
				key = elements[1];
			} else if ("map_Kd".equals(type)) {
				String texture = elements[1];
				textures.put(key, texture);
			}

			str = mtlReader.readLine();
		}
		mtlReader.close();

		return textures;
	}
}
