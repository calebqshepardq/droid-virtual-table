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

import org.amphiprion.droidvirtualtable.engine3d.for2d.Image2D;
import org.amphiprion.droidvirtualtable.entity.Group;
import org.amphiprion.droidvirtualtable.entity.Group.Type;

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

	public void move(int sourceIndex, int insertAt) {
		GameCard card = cards.remove(sourceIndex);
		cards.add(insertAt, card);
	}

	public void add(GameCard card) {
		synchronized (cards) {

			cards.add(card);
			if (card.getContainer() != null) {
				card.getContainer().remove(card);
			}
			if (getGroup().getType() == Type.HAND) {
				card.setImage2D(new Image2D(card.getCardMesh().getTexture(true), card.getCardMesh().getName()));
			}
			card.setContainer(this);

		}
	}

	public boolean isFrontDisplayed(GameCard card, String myName, String playerName) {
		if (group.getVisibility() == Group.Visibility.none) {
			return false;
		} else if (group.getVisibility() == Group.Visibility.all) {
			return true;
		} else if (group.getVisibility() == Group.Visibility.me) {
			return myName.equals(playerName);
		} else if (group.getVisibility() == Group.Visibility.undefined) {
			return card.isFrontDisplayed();
		} else if (group.getVisibility() == Group.Visibility.PLAYERS) {
			return group.getVisibilityValue().indexOf("|" + playerName + "|") != -1;
		}
		return false;
	}

	public void remove(GameCard card) {
		synchronized (cards) {
			cards.remove(card);
			card.setContainer(null);
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
