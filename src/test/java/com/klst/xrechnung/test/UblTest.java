package com.klst.xrechnung.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.klst.einvoice.BG13_DeliveryInformation;
import com.klst.einvoice.BG24_AdditionalSupportingDocs;
import com.klst.einvoice.ubl.GenericInvoice;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UblTest {

	private static final Logger LOG = Logger.getLogger(UblTest.class.getName());
	
	private static final String[] UBL_XML = {
			"ubl001.xml" ,
//			"ubl002.xml" , // error tr=val-sch.1.1BR-06error[BR-06]-An Invoice shall contain the Seller name (BT-27).
			"ubl004.xml" ,
			"ubl007.xml" ,
//			"ubl008.xml" , //TODO CreditNote ist nicht valide: 11 Fehler zu Schematron rules for EN16931 (UBL) (val-sch.1)
			"01.01a-INVOICE_ubl.xml" ,
			"01.02a-INVOICE_ubl.xml" ,
			"01.03a-INVOICE_ubl.xml" ,
			"01.04a-INVOICE_ubl.xml" ,
			"01.05a-INVOICE_ubl.xml" ,
			"01.06a-INVOICE_ubl.xml" ,
			"01.07a-INVOICE_ubl.xml" ,
			"01.08a-INVOICE_ubl.xml" ,
			"01.09a-INVOICE_ubl.xml" ,
			"01.10a-INVOICE_ubl.xml" ,
			"01.11a-INVOICE_ubl.xml" ,
			"01.12a-INVOICE_ubl.xml" ,
			"01.13a-INVOICE_ubl.xml" ,
			"01.14a-INVOICE_ubl.xml" ,
			"01.15a-INVOICE_ubl.xml" };
	
	static private KositValidation validation;
	
    @BeforeClass
    public static void staticSetup() {
		validation = new KositValidation();
    }

	@Test
    public void ubl0() {
    	InvoiceFactory factory = new CreateUblXXXInvoice(UBL_XML[1]);
    	byte[] bytes = factory.toUbl(); // the xml
    	String xml = new String(bytes);
    	LOG.info("xml=\n"+xml);
    	assertTrue(validation.check(bytes));
   }
    
//	@Test 
    public void ublCreditNote() {
    	InvoiceFactory factory = new CreateUblXXXInvoice("ubl008.xml");  // Quelle ist nicht valide
    	byte[] bytes = factory.toUbl(); // the xml
    	String xml = new String(bytes);
    	LOG.info("xml=\n"+xml);
    	assertTrue(validation.check(bytes));
   }
    
	@Test
    public void ublPEPPOL() {
//    	InvoiceFactory factory = new CreateUblXXXInvoice("example-peppol-ubl.xml"); // Quelle ist nicht valide
    	InvoiceFactory factory = new CreateUblXXXInvoice("example-peppol-ubl-creditnote.xml"); // Quelle valide gemacht
//    	InvoiceFactory factory = new CreateUblXXXInvoice("01.01a-INVOICE_ubl.xml");
    	byte[] bytes = factory.toUbl(); // the xml
    	String xml = new String(bytes);
    	LOG.info("xml=\n"+xml);
    	assertTrue(validation.check(bytes));
   }
    
    @Test
    public void ublAll() {
    	for(int i=0; i<UBL_XML.length; i++) {
    		String fileName = UBL_XML[i];
        	InvoiceFactory factory = new CreateUblXXXInvoice(fileName);
        	byte[] bytes = factory.toUbl(); // the xml
        	LOG.info("\n-------------------------------- "+fileName);
        	assertTrue(validation.check(bytes));
    	}
   }

	@Test
    public void ubl14_Delivery() {
    	InvoiceFactory factory = new CreateUblXXXInvoice("01.14a-INVOICE_ubl.xml");
    	Object o = factory.makeInvoice();
    	GenericInvoice<InvoiceType> ublInvoice = new GenericInvoice<InvoiceType>((InvoiceType)o);
    	BG13_DeliveryInformation delivery = ublInvoice.getDelivery();
    	assertEquals(Timestamp.valueOf("2018-04-13 01:00:00"), delivery.getActualDate());    // 2018-04-13+01:00 "yyyy-[m]m-[d]d hh:mm:ss[.f...]"
    	assertEquals("68", delivery.getId()); 
    	assertEquals("[DE (Bayern), 98765, [Deliver to city]]", delivery.getAddress().toString());
    	assertEquals("[Deliver to party name]", delivery.getBusinessName()); 
    	
    	assertEquals(Timestamp.valueOf("2018-04-13 01:00:00"), ublInvoice.getStartDateAsTimestamp()); 
    	assertEquals(Timestamp.valueOf("2018-04-13 01:00:00"), ublInvoice.getEndDateAsTimestamp()); 
	}

	@Test
    public void ubl15_AdditionalDocs() {
    	InvoiceFactory factory = new CreateUblXXXInvoice("01.15a-INVOICE_ubl.xml");
    	Object o = factory.makeInvoice();
    	GenericInvoice<InvoiceType> ublInvoice = new GenericInvoice<InvoiceType>((InvoiceType)o);
    	List<BG24_AdditionalSupportingDocs> asdList = ublInvoice.getAdditionalSupportingDocuments();
    	assertEquals(1, asdList.size());
    	BG24_AdditionalSupportingDocs asDoc = asdList.get(0);
    	assertEquals("01_15_Anhang_01.pdf", asDoc.getSupportingDocumentReference());
    	assertEquals("Aufschlüsselung der einzelnen Leistungspositionen", asDoc.getSupportingDocumentDescription());
    	assertEquals("application/pdf", asDoc.getAttachedDocumentMimeCode());
    	assertEquals("01_15_Anhang_01.pdf", asDoc.getAttachedDocumentFilename());
    	assertNull(asDoc.getExternalDocumentLocation());
	}

	@Test
    public void ublZZZ() {
    	InvoiceFactory factory = new CreateUblXXXInvoice("01.13a-INVOICE_ubl.xml");
    	byte[] bytes = factory.toUbl(); // the xml
    	String xml = new String(bytes);
    	LOG.info("xml=\n"+xml);
    	assertTrue(validation.check(bytes));
   }

}
