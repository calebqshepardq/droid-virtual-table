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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.dto.GameSession;
import org.amphiprion.droidvirtualtable.dto.GameTable;
import org.amphiprion.droidvirtualtable.engine3d.util.GameTableLoader;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

public class GameSessionRenderer implements GLSurfaceView.Renderer {
	private Context context;
	private GameSession gameSession;

	public GameSessionRenderer(Context context, GameSession gameSession) {
		this.context = context;
		this.gameSession = gameSession;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// load meshes
		GameTable gameTable = gameSession.getGameTable();
		try {
			GameTableLoader.load(gl, gameTable);
		} catch (Exception e) {
			Log.e(ApplicationConstants.PACKAGE, "onSurfaceCreated", e);
		}
	}
}
