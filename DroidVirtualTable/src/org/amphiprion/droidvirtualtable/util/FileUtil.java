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
package org.amphiprion.droidvirtualtable.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.amphiprion.droidvirtualtable.ApplicationConstants;

import android.util.Log;

/**
 * @author Amphiprion
 * 
 */
public class FileUtil {
	public static void unzip(File zip, String root) throws Exception {
		// Open the ZipInputStream
		ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zip));

		// Loop through all the files and folders
		for (ZipEntry entry = inputStream.getNextEntry(); entry != null; entry = inputStream.getNextEntry()) {

			String innerFileName = root + File.separator + entry.getName();
			File innerFile = new File(innerFileName);
			if (innerFile.exists()) {
				innerFile.delete();
			}

			// Check if it is a folder
			if (entry.isDirectory()) {
				// Its a folder, create that folder
				innerFile.mkdirs();
			} else {
				// Create a file output stream
				FileOutputStream outputStream = new FileOutputStream(innerFileName);
				final int BUFFER = 2048;

				// Buffer the ouput to the file
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, BUFFER);

				// Write the contents
				int count = 0;
				byte[] data = new byte[BUFFER];
				while ((count = inputStream.read(data, 0, BUFFER)) != -1) {
					bufferedOutputStream.write(data, 0, count);
				}

				// Flush and close the buffers
				bufferedOutputStream.flush();
				bufferedOutputStream.close();
			}

			// Close the current entry
			inputStream.closeEntry();
		}
		inputStream.close();
	}

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return path.delete();
	}

	public static boolean copy(File input, File output) {
		try {
			return copy(new FileInputStream(input), output);
		} catch (Exception e) {
			Log.d(ApplicationConstants.PACKAGE, "" + output, e);
			return false;
		}
	}

	public static boolean copy(InputStream inputStream, File output) {
		try {
			FileOutputStream outputStream = new FileOutputStream(output);
			final int BUFFER = 2048;

			// Buffer the ouput to the file
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, BUFFER);
			int count = 0;
			byte[] data = new byte[BUFFER];
			while ((count = inputStream.read(data, 0, BUFFER)) != -1) {
				bufferedOutputStream.write(data, 0, count);
			}
			bufferedOutputStream.flush();
			bufferedOutputStream.close();
			inputStream.close();
			return true;
		} catch (Exception e) {
			Log.d(ApplicationConstants.PACKAGE, "" + output, e);
			return false;
		}
	}
}
