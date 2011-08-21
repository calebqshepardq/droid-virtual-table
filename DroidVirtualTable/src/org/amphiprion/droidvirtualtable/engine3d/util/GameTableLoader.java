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

import java.io.File;
import java.io.FileInputStream;

import javax.microedition.khronos.opengles.GL10;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.dto.GameSession;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.os.Environment;

/**
 * @author Amphiprion
 * 
 */
public class GameTableLoader {
	public static void load(GL10 gl, GameSession gameSession) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		XMLReader xr = sp.getXMLReader();
		File tableDir = new File(Environment.getExternalStorageDirectory(), ApplicationConstants.DIRECTORY_GAMES + "/" + gameSession.getGameTable().getTable().getGame().getId()
				+ "/tables/" + gameSession.getGameTable().getTable().getId());
		GameTableHandler myXMLHandler = new GameTableHandler(gl, tableDir, gameSession);
		xr.setContentHandler(myXMLHandler);
		File file = new File(tableDir, "table.xml");
		FileInputStream fis = new FileInputStream(file);
		xr.parse(new InputSource(fis));

		fis.close();
	}

}
