package com.klst.einvoice.ubl;

import com.klst.einvoice.IContact;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ContactType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ElectronicMailType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TelephoneType;

/* BUYER CONTACT                               BG-9                        0..1
 * Eine Gruppe von Informationselementen, die Angaben zum Ansprechpartner oder der Kontaktstelle beim Erwerber liefern.
 
Name                                        ID    Semantischer Datentyp Anz. 
Buyer contact point                         BT-56 Text                  0..1
Ansprechpartner oder Kontaktstelle beim Erwerber (wie z. B. Name einer Person, Abteilungs- oder Bürobezeichnung).
Buyer contact telephone number              BT-57 Text                  0..1
Eine Telefonnummer der Kontaktstelle.
Buyer contact email address                 BT-58 Text                  0..1
Eine E-Mail-Adresse der Kontaktstelle. 
 
 */
/* SELLER CONTACT                              BG-6                        1
 * Eine Gruppe von Informationselementen, die Kontaktinformationen des Verkäufers enthalten.

Name                                        ID    Semantischer Datentyp Anz. 
Seller contact point                        BT-41 Text                  1
Seller contact telephone number             BT-42 Text                  1
Seller contact email address                BT-43 Text                  1

*/
public class Contact extends ContactType implements IContact {

	Contact() {
		super();
	}
	
	// copy ctor
	Contact(ContactType contact) {
		this();
		super.setName(contact.getName());
		super.setTelephone(contact.getTelephone());
		super.setElectronicMail(contact.getElectronicMail());
	}
	
	/**
	 * ctor for Seller Contact which is mandatory group
	 * 
	 * @param contactName mandatory item
	 * @param contactTel mandatory item
	 * @param contactMail mandatory item
	 */
	Contact(String contactName, String contactTel, String contactMail) {
		// Seller contact point                        BT-41 Text                  1 (mandatory)
		// Buyer contact point                         BT-56 Text                  0..1
		//  Angaben zu Ansprechpartner oder Kontaktstelle (wie z. B. Name einer Person, Abteilungs- oder Bürobezeichnung):
		setContactPoint(contactName);

		// Seller contact telephone number             BT-42 Text                  1 (mandatory)
		// Buyer contact telephone number              BT-57 Text                  0..1
		//  Telefonnummer des Ansprechpartners oder der Kontaktstelle:
		setContactTelephone(contactTel);

		// Seller contact email address                BT-43 Text                  1 (mandatory)
		// Buyer contact email address                 BT-58 Text                  0..1
		//  Eine E-Mail-Adresse des Ansprechpartners oder der Kontaktstelle:
		setContactEmail(contactMail);
	}

	Contact(IContact contact) {
		this(contact.getContactPoint(), contact.getContactTelephone(), contact.getContactEmail());
	}

	@Override
	public void setContactPoint(String name) {
		NameType cName = new NameType();
		cName.setValue(name);	
		this.setName(cName);
	}

	@Override
	public void setContactTelephone(String contactTel) {
		TelephoneType telephone = new TelephoneType();
		telephone.setValue(contactTel);
		this.setTelephone(telephone);
	}

	@Override
	public void setContactEmail(String contactMail) {
		ElectronicMailType electronicMail = new ElectronicMailType();
		electronicMail.setValue(contactMail);
		this.setElectronicMail(electronicMail);
	}
	
	@Override
	public String getContactPoint() {
		NameType name = super.getName();
		return name==null ? null : name.getValue();
	}
	
	@Override
	public String getContactTelephone() {
		TelephoneType telephone = super.getTelephone();
		return telephone==null ? null : telephone.getValue();
	}
	
	@Override
	public String getContactEmail() {
		ElectronicMailType electronicMail = super.getElectronicMail();
		return electronicMail==null ? null : electronicMail.getValue();
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");
		stringBuilder.append(getContactPoint()==null ? "null" : getContactPoint());
		stringBuilder.append(", ");
		stringBuilder.append(getContactTelephone()==null ? "null" : getContactTelephone());
		stringBuilder.append(", ");
		stringBuilder.append(getContactEmail()==null ? "null" : getContactEmail());
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

}
