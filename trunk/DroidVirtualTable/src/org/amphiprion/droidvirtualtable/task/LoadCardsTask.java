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

import java.util.HashMap;
import java.util.List;

import org.amphiprion.droidvirtualtable.dao.CardDao;
import org.amphiprion.droidvirtualtable.dto.Criteria;
import org.amphiprion.droidvirtualtable.entity.Card;
import org.amphiprion.droidvirtualtable.entity.Game;

import android.content.Context;
import android.os.AsyncTask;

/**
 * @author amphiprion
 * 
 */
public class LoadCardsTask extends AsyncTask<Void, Integer, List<Card>> {
	private LoadCardListener caller;
	private Game game;
	private int pageIndex;
	private int pageSize;
	private HashMap<String, List<Criteria>> criterias;

	/**
	 * Default constructor.
	 */
	public LoadCardsTask(LoadCardListener caller, Game game, int pageIndex, int pageSize, HashMap<String, List<Criteria>> criterias) {
		this.caller = caller;
		this.game = game;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.criterias = criterias;
	}

	@Override
	protected List<Card> doInBackground(Void... v) {
		try {
			List<Card> cards = CardDao.getInstance(caller.getContext()).getCards(game, pageIndex, pageSize, criterias);
			return cards;

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
	protected void onPostExecute(List<Card> cards) {
		caller.importEnded(!isCancelled() && cards != null, cards);
	}

	public interface LoadCardListener {
		void importEnded(boolean succeed, List<Card> cards);

		Context getContext();
	}
}
