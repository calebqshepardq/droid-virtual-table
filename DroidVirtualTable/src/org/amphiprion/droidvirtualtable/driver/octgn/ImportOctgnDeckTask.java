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

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.R;
import org.amphiprion.droidvirtualtable.dao.DeckDao;
import org.amphiprion.droidvirtualtable.driver.ImportDeckListener;
import org.amphiprion.droidvirtualtable.entity.Deck;
import org.amphiprion.droidvirtualtable.entity.Game;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author amphiprion
 * 
 */
public class ImportOctgnDeckTask extends AsyncTask<File, Integer, Deck> {
	private Game game;
	private ProgressDialog progress;
	private ImportDeckListener caller;
	private File file;
	private String title;
	private Exception exception;

	/**
	 * Default constructor.
	 */
	public ImportOctgnDeckTask(ImportDeckListener listener, Game game) {
		caller = listener;
		this.game = game;
	}

	@Override
	protected Deck doInBackground(File... files) {
		file = files[0];
		title = file.getName();
		try {
			DeckDao.getInstance(caller.getContext()).getDatabase().beginTransaction();

			// read deck info
			OctgnDeckHandler deckHandler = new OctgnDeckHandler(caller.getContext(), game, this);
			Deck deck = deckHandler.parse(file);

			DeckDao.getInstance(caller.getContext()).getDatabase().setTransactionSuccessful();
			return deck;
		} catch (Exception e) {
			exception = e;
			Log.e(ApplicationConstants.PACKAGE, "" + file, e);
			return null;
		} finally {
			DeckDao.getInstance(caller.getContext()).getDatabase().endTransaction();
		}
	}

	public void publishProgress(int txtId) {
		publishProgress(R.string.import_deck_step, txtId);
	}

	public void publishProgress(int txtId, int number) {
		publishProgress(R.string.import_deck_step, txtId, number);
	}

	@Override
	protected void onPreExecute() {
		progress = ProgressDialog.show(caller.getContext(), "...", caller.getContext().getString(R.string.import_deck), true, true, new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				caller.importEnded(false, null, exception);
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
	protected void onPostExecute(Deck deck) {
		if (progress != null) {
			progress.dismiss();
		}
		Log.d(ApplicationConstants.PACKAGE, "onPostExecute");
		if (!isCancelled()) {
			caller.importEnded(deck != null, deck, exception);
		}
	}
}
