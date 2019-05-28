package com.klst.un.unece.uncefact;

import java.math.BigDecimal;
import java.math.RoundingMode;

import un.unece.uncefact.data.specification.corecomponenttypeschemamodule._2.AmountType;

/* Mit diesem Datentyp wird ein Betrag in numerischer Form abgebildet. 
 * 
 * Er basiert auf dem Typ „Amount. Type“, wie in ISO 15000-5:2014 Anhang B definiert.
 * 
 * Hinweis: Dem Geldwert wird über ein eigenständiges Informationselement eine Währung zugeordnet.
 * 
 */
/**
 * Amount
 * 
 * @see European standard EN 16931-1:2017 : 6.5.2 Amount. Type
 * 
 * An amount states a numerical monetary value. 
 * The currency of the amount is defined as a separate business term. 
 * 
 * This EN 16931_ Amount. Type4 is based on the Amount. Type as defined in ISO 15000-5:2014, Annex B. EN 16931_ Amount. 
 * Type is floating up to two fraction digits.
 *
 */
public class Amount extends AmountType {

	Amount() {
		super();
	}

	public Amount(String currencyID, BigDecimal amount) {
		this();
		this.setCurrencyID(currencyID);
		this.setValue(amount);
	}

	public Amount(oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_2.AmountType amount) {
		this(amount.getCurrencyID(), amount.getValue());
	}

	public Amount(un.unece.uncefact.data.standard.unqualifieddatatype._100.AmountType amount) {
		this(amount.getCurrencyID(), amount.getValue());
	}

	void copyTo(un.unece.uncefact.data.standard.unqualifieddatatype._100.AmountType amount) {
//		amount.setCurrencyID(this.getCurrencyID()); // wg. [CII-DT-031] - currencyID should not be present
		amount.setValue(this.getValue(RoundingMode.HALF_UP));
	}
	
	public void copyTo(oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_2.AmountType amount) {
		amount.setCurrencyID(this.getCurrencyID());
		amount.setValue(this.getValue(RoundingMode.HALF_UP));
	}
	
	// Der Betrag wird mit zwei Nachkommastellen angegeben. ==> setScale(2, RoundingMode.HALF_UP)
	private static final int SCALE = 2;
	public BigDecimal getValue(RoundingMode roundingMode) {
		return getValue().setScale(SCALE, roundingMode);
	}
	
	@Override
	public String toString() {
		return this.getCurrencyID() + this.getValue(RoundingMode.HALF_UP);
	}
}
