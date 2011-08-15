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
package org.amphiprion.droidvirtualtable.task;

import java.util.List;

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.dao.DeckDao;
import org.amphiprion.droidvirtualtable.entity.Deck;
import org.amphiprion.droidvirtualtable.entity.Game;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author amphiprion
 * 
 */
public class LoadDecksTask extends AsyncTask<Void, Integer, List<Deck>> {
	private LoadDeckListener caller;
	private Game game;
	private int pageIndex;
	private int pageSize;

	/**
	 * Default constructor.
	 */
	public LoadDecksTask(LoadDeckListener caller, Game game, int pageIndex, int pageSize) {
		this.caller = caller;
		this.game = game;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;

	}

	@Override
	protected List<Deck> doInBackground(Void... v) {
		try {
			List<Deck> decks = DeckDao.getInstance(caller.getContext()).getDecks(game, pageIndex, pageSize);
			return decks;

		} catch (Exception e) {
			Log.d(ApplicationConstants.PACKAGE, "", e);
			return null;
		}
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
	}

	@Override
	protected void onPostExecute(List<Deck> decks) {
		caller.importEnded(!isCancelled() && decks != null, decks);
	}

	public interface LoadDeckListener {
		void importEnded(boolean succeed, List<Deck> decks);

		Context getContext();
	}
}
