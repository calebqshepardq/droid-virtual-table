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

import javax.microedition.khronos.opengles.GL10;

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.dto.Camera;
import org.amphiprion.droidvirtualtable.dto.GameTable;
import org.amphiprion.droidvirtualtable.dto.TableLocation;
import org.amphiprion.droidvirtualtable.dto.TableZone;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/**
 * @author Amphiprion
 * 
 */
public class GameTableHandler extends DefaultHandler {
	private GL10 gl;
	private File tableDir;
	private GameTable gameTable;

	private TableLocation loc;
	private String xmlPath;

	public GameTableHandler(GL10 gl, File tableDir, GameTable gameTable) {
		this.gl = gl;
		this.tableDir = tableDir;
		this.gameTable = gameTable;
	}

	@Override
	public void startDocument() throws SAXException {
		xmlPath = "";
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals("table")) {
		} else if (localName.equals("location")) {
			String name = attributes.getValue("name");
			loc = new TableLocation(name);
			gameTable.getTableLocations().add(loc);
		} else if (localName.equals("camera")) {
			Camera cam = new Camera();
			cam.setX(Float.parseFloat(attributes.getValue("x")));
			cam.setY(Float.parseFloat(attributes.getValue("y")));
			cam.setZ(Float.parseFloat(attributes.getValue("z")));
			cam.setAngleX(Integer.parseInt(attributes.getValue("angleX")));
			cam.setAngleZ(Integer.parseInt(attributes.getValue("angleZ")));
			loc.setCamera(cam);
		} else if (localName.equals("zone")) {
			TableZone zone = new TableZone();
			zone.setX(Float.parseFloat(attributes.getValue("x")));
			zone.setY(Float.parseFloat(attributes.getValue("y")));
			zone.setZ(Float.parseFloat(attributes.getValue("z")));
			zone.setLinkedGroupName(attributes.getValue("group"));
			loc.getTableZones().add(zone);
		} else if (localName.equals("object")) {
			File objFile = new File(tableDir, attributes.getValue("file"));
			try {
				gameTable.getMeshes().addAll(ObjLoader.loadObjects(gl, gameTable.getTable().getGame().getId() + "/tables/" + gameTable.getTable().getId(), objFile));
			} catch (SAXException e) {
				throw e;
			} catch (Exception e) {
				Log.e(ApplicationConstants.PACKAGE, "loadObjects", e);
				throw new SAXException(e);
			}
		}
		xmlPath += "/" + localName;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		xmlPath = xmlPath.substring(0, xmlPath.lastIndexOf("/"));
	}

	@Override
	public void endDocument() throws SAXException {
	}

}
