package com.klst.ubl;

import com.klst.un.unece.uncefact.BICId;
import com.klst.un.unece.uncefact.IBANId;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.BranchType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.FinancialAccountType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.FinancialInstitutionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;

// Gruppe CREDIT TRANSFER
/*

Name                                        ID    Semantischer Datentyp Anz. 
Payment account identifier                  BT-84 Identifier            1 
Die Kennung des Kontos, auf das die Zahlung erfolgen soll: 
IBAN für Zahlungen im SEPA-Raum, 
Kontonummer oder IBAN im Falle von Auslandszahlungen.
Payment account name                        BT-85 Text                  0..1
Name des Kontos bei einem Zahlungsdienstleister, auf das die Zahlung erfolgen soll. (z. B. Kontoinhaber)
Payment service provider identifier         BT-86 Identifier            0..1
Die Kennung des Konto führenden Kreditinstitutes, ergibt sich bei Zahlungen im SEPA-Raum im Regelfall aus IBAN;
für Auslandszahlungen, bei denen um BIC ergänzt werden muss:

• für alle Zahlungen an Bankverbindungen des SEPA-Raumes als SCT (Code 31 des „Payment means type code“
(BT-81)) oder SDD (Code 49 des „Payment means type code“ (BT-81)) mit Ausnahme der nachstehenden Ausnahmen,
reicht die Angabe der IBAN, die in der Länge variiert; BIC nicht erforderlich. Ausnahmen: z. B. San
Marino, Monaco, Schweiz, Saint Pierre und Miquelon, wobei die Länge der IBAN variiert, aber immer 1. und 2.
Stelle = Ländercode nach ISO, 3. und 4. Stelle = Prüfziffer; Angabe des BIC ist neben der Angabe der IBAN
zwingend erforderlich
• für alle Zahlungen an Bankverbindungen außerhalb des SEPA-Raumes (Code 42 des „Payment means type
code“ (BT-81)) sind, abhängig vom empfangenden Institut, IBAN bzw. Kontonummer und BIC nötig
*/
public class CreditTransfer extends FinancialAccountType {

	CreditTransfer() {
		super();
	}
	
	public CreditTransfer(IBANId iban) {
		this();
		IDType ibanID = new IDType();
		ibanID.setValue(iban.getValue());
		ibanID.setSchemeID(iban.getSchemeID()); // so nicht in XRechnung-v1-2-0.pdf dokumentiert
		super.setID(ibanID);
	}

	//TODO besser Klasse Iban als Subklasse von IDType, dto Bic
	public CreditTransfer(String account, String accountName, BICId bic) {
		this();
		IDType accountID = new IDType();
		accountID.setValue(account);	
		super.setID(accountID);
		
		if(accountName!=null) {
			NameType name = new NameType();
			name.setValue(accountName);
			super.setName(name);
		}
		
		BranchType branch = new BranchType();
		FinancialInstitutionType financialInstitution = new FinancialInstitutionType();
		IDType bicID = new IDType();
		bicID.setValue(bic.getValue());
		bicID.setSchemeID(bic.getSchemeID());
		financialInstitution.setID(bicID);
		branch.setFinancialInstitution(financialInstitution);
		super.setFinancialInstitutionBranch(branch);		
	}

	public String getAccountID() {
		return super.getID().getValue();
	}
}
