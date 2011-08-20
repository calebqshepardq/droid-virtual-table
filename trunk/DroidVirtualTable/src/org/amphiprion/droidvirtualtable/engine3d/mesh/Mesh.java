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

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.engine3d.util.Texture;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Mesh {
	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 8 * FLOAT_SIZE_BYTES;
	private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
	private static final int TRIANGLE_VERTICES_DATA_NOR_OFFSET = 3;
	private static final int TRIANGLE_VERTICES_DATA_TEX_OFFSET = 6;

	public float x;
	public float y;
	public float z;

	private String name;
	private Texture texture;

	private FloatBuffer verticePropertyBuffer;
	private ShortBuffer indiceBuffer;
	private int indiceCount;

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

	public int getIndiceCount() {
		return indiceCount;
	}

	public void setIndiceCount(int indiceCount) {
		this.indiceCount = indiceCount;
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

	public void draw(int _program, float[] mMMatrix, float[] mVMatrix, float[] mMVPMatrix, float[] mProjMatrix, float[] normalMatrix, float[] mLightPosInEyeSpace,
			float[] lightColor, float[] matAmbient, float[] matDiffuse, float[] matSpecular, float matShininess, float[] eyePos) {
		Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0, x, y, z);

		Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

		// send to the shader
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(_program, "uMVPMatrix"), 1, false, mMVPMatrix, 0);

		// Create the normal modelview matrix
		// Invert + transpose of mvpmatrix
		Matrix.invertM(normalMatrix, 0, mMVPMatrix, 0);
		Matrix.transposeM(normalMatrix, 0, normalMatrix, 0);

		// send to the shader
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(_program, "normalMatrix"), 1, false, mMVPMatrix, 0);

		// lighting variables // send to shaders
		GLES20.glUniform4fv(GLES20.glGetUniformLocation(_program, "lightPos"), 1, mLightPosInEyeSpace, 0);
		GLES20.glUniform4fv(GLES20.glGetUniformLocation(_program, "lightColor"), 1, lightColor, 0);

		// material
		GLES20.glUniform4fv(GLES20.glGetUniformLocation(_program, "matAmbient"), 1, matAmbient, 0);
		GLES20.glUniform4fv(GLES20.glGetUniformLocation(_program, "matDiffuse"), 1, matDiffuse, 0);
		GLES20.glUniform4fv(GLES20.glGetUniformLocation(_program, "matSpecular"), 1, matSpecular, 0);
		GLES20.glUniform1f(GLES20.glGetUniformLocation(_program, "matShininess"), matShininess);

		// eye position
		GLES20.glUniform3fv(GLES20.glGetUniformLocation(_program, "eyePos"), 1, eyePos, 0);

		// mesh = cardMesh;
		// Log.d(ApplicationConstants.PACKAGE, "MESH:" + mesh.getName());
		FloatBuffer _vb = getVerticePropertyBuffer();
		ShortBuffer _ib = getIndiceBuffer();

		// Vertex buffer

		// the vertex coordinates
		_vb.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(_program, "aPosition"), 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "aPosition"));

		// the normal info
		_vb.position(TRIANGLE_VERTICES_DATA_NOR_OFFSET);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(_program, "aNormal"), 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "aNormal"));

		// Texture info

		// bind textures
		if (getTexture() != null) {// && enableTexture) {
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, getTexture().textureId);
			GLES20.glUniform1i(GLES20.glGetUniformLocation(_program, "texture1"), 0);
		}

		// enable texturing? [fix - sending float is waste]
		GLES20.glUniform1f(GLES20.glGetUniformLocation(_program, "hasTexture")/*
																			 * shader
																			 * .
																			 * hasTextureHandle
																			 */, 2.0f);

		// texture coordinates
		_vb.position(TRIANGLE_VERTICES_DATA_TEX_OFFSET);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(_program, "textureCoord")/*
																						 * shader
																						 * .
																						 * maTextureHandle
																						 */, 2, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(_program, "textureCoord"));// GLES20.glEnableVertexAttribArray(shader.maTextureHandle);

		// Draw with indices
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, getIndiceCount(), GLES20.GL_UNSIGNED_SHORT, _ib);
		checkGlError("glDrawElements");
	}

	private void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(ApplicationConstants.PACKAGE, op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}
}
