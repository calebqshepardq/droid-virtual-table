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

import org.amphiprion.droidvirtualtable.dao.GameDao;
import org.amphiprion.droidvirtualtable.entity.Game;

import android.content.Context;
import android.os.AsyncTask;

/**
 * @author amphiprion
 * 
 */
public class LoadGamesTask extends AsyncTask<Void, Integer, List<Game>> {
	private LoadGameListener caller;
	private int pageIndex;
	private int pageSize;

	/**
	 * Default constructor.
	 */
	public LoadGamesTask(LoadGameListener caller, int pageIndex, int pageSize) {
		this.caller = caller;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;

	}

	@Override
	protected List<Game> doInBackground(Void... v) {
		try {
			List<Game> games = GameDao.getInstance(caller.getContext()).getGames(pageIndex, pageSize);
			return games;

		} catch (Exception e) {
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
	protected void onPostExecute(List<Game> games) {
		caller.importEnded(!isCancelled() && games != null, games);
	}

	public interface LoadGameListener {
		void importEnded(boolean succeed, List<Game> games);

		Context getContext();
	}
}
