package org.amphiprion.droidvirtualtable.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class Player implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean available = false;
	private String name;
	private String locationName;
	private TableLocation tableLocation;
	private GameDeck deck;

	private HashMap<String, CardGroup> cardGroups;

	public Player() {
		cardGroups = new HashMap<String, CardGroup>();
	}

	public void addCardGroup(CardGroup group) {
		cardGroups.put(group.getGroup().getName(), group);
	}

	public void addCard(String groupName, GameCard card) {
		CardGroup group = cardGroups.get(groupName);
		group.add(card);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TableLocation getTableLocation() {
		return tableLocation;
	}

	public void setTableLocation(TableLocation tableLocation) {
		this.tableLocation = tableLocation;
	}

	public GameDeck getDeck() {
		return deck;
	}

	public void setDeck(GameDeck deck) {
		this.deck = deck;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Collection<CardGroup> getCardGroups() {
		return cardGroups.values();
	}
}
