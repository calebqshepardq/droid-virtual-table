package org.amphiprion.droidvirtualtable.dto;

import java.io.Serializable;

public class Criteria implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static enum Operator {
		like, equals, gt, lt, between
	}

	private String name;
	private Operator operator = Operator.equals;
	private String firstValue;
	private String secondValue;

	public Criteria(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public Operator getOperator() {
		return operator;
	}

	public String getFirstValue() {
		return firstValue;
	}

	public void setFirstValue(String firstValue) {
		this.firstValue = firstValue;
	}

	public String getSecondValue() {
		return secondValue;
	}

	public void setSecondValue(String secondValue) {
		this.secondValue = secondValue;
	}
}
