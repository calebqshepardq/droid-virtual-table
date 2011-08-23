/*
 * @copyright 2010 Gerald Jacobson
 * @license GNU General Public License
 * 
 * This file is part of MyTricTrac.
 *
 * MyTricTrac is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyTricTrac is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with My Accounts.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.amphiprion.droidvirtualtable.driver.octgn;

import java.io.File;
import java.util.HashMap;

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.R;
import org.amphiprion.droidvirtualtable.dao.GameDao;
import org.amphiprion.droidvirtualtable.driver.ImportGameListener;
import org.amphiprion.droidvirtualtable.entity.Game;
import org.amphiprion.droidvirtualtable.util.FileUtil;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

/**
 * @author amphiprion
 * 
 */
public class ImportOctgnGameTask extends AsyncTask<File, Integer, Game> {
	private ProgressDialog progress;
	private ImportGameListener caller;
	private File file;
	private String title;
	private Exception exception;

	/**
	 * Default constructor.
	 */
	public ImportOctgnGameTask(ImportGameListener listener) {
		caller = listener;
	}

	@Override
	protected Game doInBackground(File... files) {
		file = files[0];
		title = file.getName();
		File root = null;
		try {
			GameDao.getInstance(caller.getContext()).getDatabase().beginTransaction();
			root = new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY_IMPORT_GAMES + "/current");
			root.mkdirs();

			// Unzip o8g
			publishProgress(R.string.import_game_step_unzip);
			FileUtil.unzip(file, root.getAbsolutePath());

			// read _rels/.rels
			publishProgress(R.string.import_game_step_ref);
			OctgnRelationShipHandler handler = new OctgnRelationShipHandler();
			HashMap<String, String> relationships = handler.parse(new File(root, "_rels/.rels"));
			String mainFile = relationships.get("def");
			relationships = handler.parse(new File(root, "_rels" + mainFile + ".rels"));

			// read game info
			File gameFile = new File(root, mainFile);
			OctgnGameHandler gameHandler = new OctgnGameHandler(caller.getContext(), this, relationships);
			Game game = gameHandler.parse(new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY_GAMES), root, gameFile);

			// delete current directory (unzipped data)
			GameDao.getInstance(caller.getContext()).getDatabase().setTransactionSuccessful();
			return game;
		} catch (Exception e) {
			exception = e;
			Log.e(ApplicationConstants.PACKAGE, "" + file, e);
			return null;
		} finally {
			GameDao.getInstance(caller.getContext()).getDatabase().endTransaction();
			if (root != null) {
				FileUtil.deleteDirectory(root);
			}
		}
	}

	public void publishProgress(int gameNumber) {
		publishProgress(R.string.import_game_step, gameNumber);
	}

	@Override
	protected void onPreExecute() {
		progress = ProgressDialog.show(caller.getContext(), "...", caller.getContext().getString(R.string.import_game), true, true, new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				caller.importEnded(false, null, exception);
			}
		});
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		progress.setTitle(title);
		if (values.length > 1) {
			progress.setMessage(caller.getContext().getResources().getString(values[0], caller.getContext().getString(values[1])));
		} else {
			progress.setMessage(caller.getContext().getResources().getString(values[0]));
		}
	}

	@Override
	protected void onPostExecute(Game game) {
		if (progress != null) {
			progress.dismiss();
		}
		Log.d(ApplicationConstants.PACKAGE, "onPostExecute");
		if (!isCancelled()) {
			caller.importEnded(game != null, game, exception);
		}
	}
}
