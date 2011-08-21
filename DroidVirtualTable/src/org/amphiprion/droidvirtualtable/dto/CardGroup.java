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
package org.amphiprion.droidvirtualtable.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.amphiprion.droidvirtualtable.entity.Group;

/**
 * @author Amphiprion
 * 
 */
public class CardGroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Group group;
	private List<GameCard> cards;

	public CardGroup(Group group) {
		this.group = group;
		cards = new ArrayList<GameCard>();
	}

	public Group getGroup() {
		return group;
	}

	public int count() {
		return cards.size();
	}

	public List<GameCard> getCards() {
		return cards;
	}

	public void add(GameCard card) {
		synchronized (cards) {
			// TODO gerer me, et la liste de user
			if (group.getVisibility() == Group.Visibility.none) {
				card.setFrontDisplayed(false);
			} else if (group.getVisibility() == Group.Visibility.all) {
				card.setFrontDisplayed(true);
			}
			cards.add(card);
		}
	}

	public GameCard takeTopCard() {
		GameCard c = null;
		synchronized (cards) {
			c = cards.get(cards.size() - 1);
			cards.remove(cards.size() - 1);
		}
		return c;
	}
}