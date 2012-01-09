package org.amphiprion.droidvirtualtable.dto;

import java.io.Serializable;
import java.util.List;

public class Criteria implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static enum Operator {
		like, equals, gt, lt, between
	}

	private String type;
	private String name;
	private Operator operator = Operator.equals;
	private String firstValue;
	private String secondValue;
	private List<String> allowedValues;

	public Criteria(String type, String name, List<String> allowedValues) {
		this.type = type;
		this.name = name;
		this.allowedValues = allowedValues;
	}

	public String getType() {
		return type;
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

	public List<String> getAllowedValues() {
		return allowedValues;
	}
}
