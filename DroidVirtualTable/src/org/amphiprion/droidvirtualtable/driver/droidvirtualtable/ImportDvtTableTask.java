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
package org.amphiprion.droidvirtualtable.driver.droidvirtualtable;

import java.io.File;

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.R;
import org.amphiprion.droidvirtualtable.dao.TableDao;
import org.amphiprion.droidvirtualtable.driver.ImportTableListener;
import org.amphiprion.droidvirtualtable.entity.Game;
import org.amphiprion.droidvirtualtable.entity.Table;
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
public class ImportDvtTableTask extends AsyncTask<File, Integer, Table> {
	private Game game;
	private ProgressDialog progress;
	private ImportTableListener caller;
	private File file;
	private String title;

	/**
	 * Default constructor.
	 */
	public ImportDvtTableTask(ImportTableListener listener, Game game) {
		caller = listener;
		this.game = game;
	}

	@Override
	protected Table doInBackground(File... files) {
		file = files[0];
		title = file.getName();
		File root = null;
		try {
			TableDao.getInstance(caller.getContext()).getDatabase().beginTransaction();
			root = new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY_IMPORT_TABLES + "/current");
			root.mkdirs();

			// Unzip o8s
			publishProgress(R.string.import_table_step_unzip);
			FileUtil.unzip(file, root.getAbsolutePath());

			// read table info
			File gameFile = new File(root, "table.xml");
			DvtTableHandler tableHandler = new DvtTableHandler(caller.getContext(), game, this);
			Table table = tableHandler.parse(new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY_GAMES + "/" + game.getId() + "/tables"),
					root, gameFile);

			// delete current directory (unzipped data)
			TableDao.getInstance(caller.getContext()).getDatabase().setTransactionSuccessful();
			return table;
		} catch (Exception e) {
			Log.e(ApplicationConstants.PACKAGE, "" + file, e);
			return null;
		} finally {
			TableDao.getInstance(caller.getContext()).getDatabase().endTransaction();
			if (root != null) {
				FileUtil.deleteDirectory(root);
			}
		}
	}

	public void publishProgress(int txtId) {
		publishProgress(R.string.import_table_step, txtId);
	}

	public void publishProgress(int txtId, int number) {
		publishProgress(R.string.import_table_step, txtId, number);
	}

	@Override
	protected void onPreExecute() {
		progress = ProgressDialog.show(caller.getContext(), "...", caller.getContext().getString(R.string.import_table), true, true, new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				caller.importEnded(false, null);
			}
		});
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		progress.setTitle(title);
		if (values.length >= 3) {
			progress.setMessage(caller.getContext().getResources().getString(values[0], caller.getContext().getString(values[1], values[2])));
		} else if (values.length == 2) {
			progress.setMessage(caller.getContext().getResources().getString(values[0], caller.getContext().getString(values[1])));
		} else {
			progress.setMessage(caller.getContext().getResources().getString(values[0]));
		}
	}

	@Override
	protected void onPostExecute(Table table) {
		if (progress != null) {
			progress.dismiss();
		}
		Log.d(ApplicationConstants.PACKAGE, "onPostExecute");
		if (!isCancelled()) {
			caller.importEnded(table != null, table);
		}
	}
}
