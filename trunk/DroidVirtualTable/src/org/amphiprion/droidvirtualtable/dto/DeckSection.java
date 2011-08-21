package org.amphiprion.droidvirtualtable.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeckSection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String startupGroupName;
	private List<GameCard> cards;

	public DeckSection() {
		cards = new ArrayList<GameCard>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<GameCard> getCards() {
		return cards;
	}

	public String getStartupGroupName() {
		return startupGroupName;
	}

	public void setStartupGroupName(String startupGroupName) {
		this.startupGroupName = startupGroupName;
	}
}
