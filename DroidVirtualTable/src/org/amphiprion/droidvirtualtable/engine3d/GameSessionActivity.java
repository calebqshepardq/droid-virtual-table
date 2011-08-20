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
package org.amphiprion.droidvirtualtable.engine3d;

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.dto.GameSession;
import org.amphiprion.droidvirtualtable.engine3d.util.TextureUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author Amphiprion
 * 
 */
public class GameSessionActivity extends Activity {
	private GLSurfaceView mGLSurfaceView;

	// The Renderer
	private GameSessionRenderer renderer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE); // (NEW)
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // (NEW)

		Intent i = getIntent();
		GameSession gameSession = (GameSession) i.getSerializableExtra("GAME_SESSION");
		// Create a new GLSurfaceView - this holds the GL Renderer
		mGLSurfaceView = new GLSurfaceView(this);

		// detect if OpenGL ES 2.0 support exists - if it doesn't, exit.
		if (detectOpenGLES20()) {
			// Tell the surface view we want to create an OpenGL ES
			// 2.0-compatible
			// context, and set an OpenGL ES 2.0-compatible renderer.
			mGLSurfaceView.setEGLContextClientVersion(2);

			renderer = new GameSessionRenderer(this, gameSession);
			mGLSurfaceView.setRenderer(renderer);
		} else { // quit if no support - get a better phone! :P
			finish();
		}

		// set the content view
		setContentView(mGLSurfaceView);
	}

	/**
	 * Detects if OpenGL ES 2.0 exists
	 * 
	 * @return true if it does
	 */
	private boolean detectOpenGLES20() {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		Log.d(ApplicationConstants.PACKAGE, "OpenGL Ver:" + info.getGlEsVersion());
		return info.reqGlEsVersion >= 0x20000;
	}

	@Override
	protected void onDestroy() {
		TextureUtil.unloadAll();
		super.onDestroy();
	}
}
