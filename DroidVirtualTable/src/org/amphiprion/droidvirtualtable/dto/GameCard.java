package org.amphiprion.droidvirtualtable.dto;

import java.io.Serializable;

import org.amphiprion.droidvirtualtable.engine3d.for2d.Image2D;
import org.amphiprion.droidvirtualtable.engine3d.mesh.CardMesh;
import org.amphiprion.droidvirtualtable.entity.Card;
import org.amphiprion.droidvirtualtable.entity.Group.Type;

public class GameCard implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean frontDisplayed;
	private Card card;
	private CardGroup container;
	private CardMesh cardMesh;
	private Image2D image2D;

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public CardMesh getCardMesh() {
		return cardMesh;
	}

	public Image2D getImage2D() {
		return image2D;
	}

	public void setImage2D(Image2D image2d) {
		image2D = image2d;
	}

	public void setCardMesh(CardMesh cardMesh) {
		this.cardMesh = cardMesh;
		cardMesh.setFrontDisplayed(frontDisplayed);
		updateScale();
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

	public CardGroup getContainer() {
		return container;
	}

	public void setContainer(CardGroup container) {
		this.container = container;
		updateScale();
	}

	private void updateScale() {
		if (cardMesh != null) {
			if (container == null || container.getGroup().getType() != Type.PILE) {
				// TODO mettre la taille du type de carte
				cardMesh.scaleX = 0.63f;
				cardMesh.scaleY = 0.88f;
			} else {
				cardMesh.scaleX = container.getGroup().getWidth() / 100.0f;
				cardMesh.scaleY = container.getGroup().getHeight() / 100.0f;
			}
		}
	}
}
