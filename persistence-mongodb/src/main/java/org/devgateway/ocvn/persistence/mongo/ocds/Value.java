/**
 * 
 */
package org.devgateway.ocvn.persistence.mongo.ocds;

import java.math.BigDecimal;

/**
 * @author mihai
 * Value OCDS Entity http://standard.open-contracting.org/latest/en/schema/reference/#value
 */
public class Value {
	BigDecimal amount;
	String currency;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
