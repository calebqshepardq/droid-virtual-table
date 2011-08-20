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
package org.amphiprion.droidvirtualtable.engine3d.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import org.amphiprion.droidvirtualtable.ApplicationConstants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLUtils;
import android.os.Environment;

public class TextureUtil {
	private static Map<String, Texture> textures = new HashMap<String, Texture>();
	private static GL10 gl;

	public static Texture loadTexture(String uri, GL10 gl) throws Exception {
		if (TextureUtil.gl != null && TextureUtil.gl != gl) {
			unloadAll();
		}
		TextureUtil.gl = gl;

		Texture texture = textures.get(uri);
		if (texture == null) {
			File file = new File(Environment.getExternalStorageDirectory(), ApplicationConstants.DIRECTORY_GAMES + "/" + uri);
			InputStream is = new FileInputStream(file);
			// Log.d("OPENGL", "load texture:" + uri);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			int contentWidth = bitmap.getWidth();
			int contentHeight = bitmap.getHeight();
			int mStrikeWidth = 1;
			while (mStrikeWidth < contentWidth) {
				mStrikeWidth <<= 1;
			}

			int mStrikeHeight = 1;
			while (mStrikeHeight < contentHeight) {
				mStrikeHeight <<= 1;
			}
			if (contentWidth != mStrikeWidth || contentHeight != mStrikeHeight) {
				Bitmap.Config config = Bitmap.Config.ARGB_4444;
				Bitmap mBitmap = Bitmap.createBitmap(mStrikeWidth, mStrikeHeight, config);
				Canvas mCanvas = new Canvas(mBitmap);
				mCanvas.drawBitmap(bitmap, 0, 0, new Paint());
				mCanvas = null;
				texture = loadGLTexture(mBitmap, gl);
				mBitmap.recycle();
				mBitmap = null;
			} else {
				texture = loadGLTexture(bitmap, gl);
			}
			texture.height = mStrikeHeight;
			texture.width = mStrikeWidth;
			texture.originalHeight = contentHeight;
			texture.originalWidth = contentWidth;
			bitmap.recycle();
			bitmap = null;
			textures.put(uri, texture);
		}
		return texture;
	}

	private static Texture loadGLTexture(Bitmap bitmap, GL10 gl) { // New
																	// function
		Texture texture = new Texture();
		// Generate one texture pointer...
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		texture.textureId = textures[0];
		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.textureId);

		// Create Nearest Filtered Texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		// Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		// Use the Android GLUtils to specify a two-dimensional texture image
		// from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		return texture;
	}

	public static void unloadAll() {
		if (TextureUtil.gl != null) {
			int[] indexs = new int[1];
			for (Texture t : textures.values()) {
				indexs[0] = t.textureId;
				gl.glDeleteTextures(1, indexs, 0);
			}
		}
		textures.clear();
	}
}
