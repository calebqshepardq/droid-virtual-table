package org.amphiprion.droidvirtualtable.dto;

import java.io.Serializable;

import org.amphiprion.droidvirtualtable.engine3d.mesh.CardMesh;
import org.amphiprion.droidvirtualtable.entity.Card;

public class GameCard implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean frontDisplayed;
	private Card card;

	private CardMesh cardMesh;

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public CardMesh getCardMesh() {
		return cardMesh;
	}

	public void setCardMesh(CardMesh cardMesh) {
		this.cardMesh = cardMesh;
		cardMesh.setFrontDisplayed(frontDisplayed);
	}

	public boolean isFrontDisplayed() {
		return frontDisplayed;
	}

	public void setFrontDisplayed(boolean frontDisplayed) {
		this.frontDisplayed = frontDisplayed;
		if (cardMesh != null) {
			cardMesh.setFrontDisplayed(frontDisplayed);
		}
	}
}
