package org.amphiprion.droidvirtualtable.engine3d.mesh;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.engine3d.util.Texture;
import org.amphiprion.droidvirtualtable.engine3d.util.TextureUtil;

import android.util.Log;

public class CardMesh extends Mesh {
	public static float CARD_HEIGHT = 0.005f;

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int SHORT_SIZE_BYTES = 2;

	private boolean frontDisplayed;
	private Texture backTexture;
	private Texture frontTexture;

	public CardMesh(GL10 gl, String name, String uriBack, String uriFront) {
		super(name);

		float[] normals = { 0, 0, 1 };
		short[] indices = { 0, 1, 2, 0, 2, 3, 0, 4, 5, 0, 5, 1, 1, 5, 6, 1, 6, 2, 2, 6, 7, 2, 7, 3, 3, 7, 4, 3, 4, 0 };
		float[] vertices = new float[] { -1 / 2.0f, 1 / 2.0f, CARD_HEIGHT / 2.0f, // 0,
				// Top
				// Left
				-1 / 2.0f, -1 / 2.0f, CARD_HEIGHT / 2.0f, // 1, Bottom
															// Left
				1 / 2.0f, -1 / 2.0f, CARD_HEIGHT / 2.0f, // 2, Bottom
															// Right
				1 / 2.0f, 1 / 2.0f, CARD_HEIGHT / 2.0f, // 3, Top Right

				-1 / 2.0f, 1 / 2.0f, -CARD_HEIGHT / 2.0f, // 0,
				// Top
				// Left
				-1 / 2.0f, -1 / 2.0f, -CARD_HEIGHT / 2.0f, // 1, Bottom
				// Left
				1 / 2.0f, -1 / 2.0f, -CARD_HEIGHT / 2.0f, // 2, Bottom
				// Right
				1 / 2.0f, 1 / 2.0f, -CARD_HEIGHT / 2.0f, // 3, Top Right

		};
		try {
			backTexture = TextureUtil.loadTexture(uriBack, gl);
			frontTexture = TextureUtil.loadTexture(uriFront, gl);

		} catch (Exception e) {
			Log.e(ApplicationConstants.PACKAGE, "loadTexture", e);
		}
		setFrontDisplayed(false);

		float u = (float) backTexture.originalWidth / backTexture.width;
		float v = (float) backTexture.originalHeight / backTexture.height;
		float[] textureCoordinates = new float[] { 0.0f, 0.0f, //
				0.0f, v, //
				u, v, //
				u, 0.0f, //
				0.0f, 0.0f, //
				0.0f, v, //
				u, v, //
				u, 0.0f, };

		ArrayList<Float> vertexPropertiesBuffer = new ArrayList<Float>(600);
		ArrayList<Short> indicesBuffer = new ArrayList<Short>(200);
		short index = 0;
		for (int i = 0; i < indices.length; i++) {
			int vert = indices[i];
			int texc = indices[i];
			int vertN = 0;

			// Add to the index buffer
			indicesBuffer.add(index++);

			// Add all the vertex info
			vertexPropertiesBuffer.add(vertices[vert * 3]); // x
			vertexPropertiesBuffer.add(vertices[vert * 3 + 1]);// y
			vertexPropertiesBuffer.add(vertices[vert * 3 + 2]);// z

			// add the normal info
			vertexPropertiesBuffer.add(normals[vertN * 3]); // x
			vertexPropertiesBuffer.add(normals[vertN * 3 + 1]); // y
			vertexPropertiesBuffer.add(normals[vertN * 3 + 2]); // z

			// add the tex coord info
			vertexPropertiesBuffer.add(textureCoordinates[texc * 2]); // u
			vertexPropertiesBuffer.add(textureCoordinates[texc * 2 + 1]); // v
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
		setVerticePropertyBuffer(fb);

		// create short buffer
		indicesBuffer.trimToSize();
		indices = new short[indicesBuffer.size()];
		for (int i = 0; i < indicesBuffer.size(); i++) {
			indices[i] = indicesBuffer.get(i);
		}
		ShortBuffer sb = ByteBuffer.allocateDirect(indices.length * SHORT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asShortBuffer();
		sb.put(indices);
		sb.position(0);
		setIndiceBuffer(sb);
		setIndiceCount(indices.length);

		// release to enhance GC
		vertexProperties = null;
		indices = null;
		normals = null;
		textureCoordinates = null;

		vertexPropertiesBuffer.clear();
		vertexPropertiesBuffer = null;
		indicesBuffer.clear();
		indicesBuffer = null;

	}

	public boolean isFrontDisplayed() {
		return frontDisplayed;
	}

	public void setFrontDisplayed(boolean frontDisplayed) {
		this.frontDisplayed = frontDisplayed;
		if (frontDisplayed) {
			setTexture(frontTexture);
		} else {
			setTexture(backTexture);
		}
	}

}
