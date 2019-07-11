package com.klst.xrechnung.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.klst.cius.IContact;
import com.klst.marshaller.UblCreditNoteTransformer;
import com.klst.marshaller.UblInvoiceTransformer;
import com.klst.ubl.CommercialInvoice;
import com.klst.ubl.Contact;
import com.klst.ubl.CreditNote;
import com.klst.ubl.CreditNoteLine;
import com.klst.ubl.FinancialAccount;
import com.klst.ubl.Invoice;
import com.klst.ubl.InvoiceLine;
import com.klst.ubl.Party;
import com.klst.ubl.PaymentMeans;
import com.klst.ubl.PaymentTerms;
import com.klst.ubl.VatCategory;
import com.klst.un.unece.uncefact.Amount;
import com.klst.un.unece.uncefact.IBANId;
import com.klst.untdid.codelist.DocumentNameCode;
import com.klst.untdid.codelist.PaymentMeansCode;
import com.klst.untdid.codelist.TaxCategoryCode;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CompanyIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CompanyLegalFormType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.RegistrationNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxExemptionReasonCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxExemptionReasonType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxableAmountType;
import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

public class CreateUblXXXInvoice extends InvoiceFactory {

	private static final Logger LOG = Logger.getLogger(CreateUblXXXInvoice.class.getName());
	
	private static final String TESTDIR = "src/test/resources/"; // mit Daten aus xrechnung-1.2.0-testsuite-2018-12-14.zip\instances\
	
	private File testFile;
	private Invoice testDoc;
	private CreditNote testCreditNote;
	
	// ctor
	public CreateUblXXXInvoice() {
		super(UblInvoiceTransformer.getInstance());
	}
	CreateUblXXXInvoice(String ublXml) {
		super(UblInvoiceTransformer.getInstance());
		testFile = getTestFile(TESTDIR+ublXml);
		if(transformer.isValid(testFile)) {
			testDoc = toModel(testFile);
		} else {
			LOG.warning(ublXml + "ist nicht UblInvoice!!!!!!!!!!!!!!!");
			this.transformer = UblCreditNoteTransformer.getInstance();
			if(transformer.isValid(testFile)) {
				testDoc = null;
				testCreditNote = toModelCreditNote(testFile);
			}
		}
	}

	public Invoice getTestDoc() {
		return testDoc;
	}
		
	Object makeCreditNote() {
		CreditNote ublInvoice = new  CreditNote(testCreditNote.getCustomization(), testCreditNote.getProfile(), testCreditNote.getTypeCode());
		ublInvoice.setId(testCreditNote.getId());
		ublInvoice.setIssueDate(testCreditNote.getIssueDateAsTimestamp());
		ublInvoice.setDocumentCurrency(testCreditNote.getDocumentCurrency());
		ublInvoice.setTaxCurrency(testCreditNote.getTaxCurrency());
		ublInvoice.setBuyerReference(testCreditNote.getBuyerReferenceValue());
		
		LOG.info("makeOptionals...");	
			ublInvoice.setOrderReferenceID(testCreditNote.getOrderReferenceID());
			List<String> notes = testCreditNote.getNotes();
			notes.forEach(note -> {
				ublInvoice.setNote(note);
			});
		LOG.info("finished makeOptionals.");
		
		ublInvoice.setSellerParty(testCreditNote.getSellerParty());
		ublInvoice.setBuyerParty(testCreditNote.getBuyerParty());

//		void makePaymentGroup(Invoice ublInvoice) {
			PaymentMeans paymentInstruction = testCreditNote.getPaymentInstructions().get(0);
			PaymentMeansCode paymentMeansCode = paymentInstruction.getPaymentMeans();
			if(paymentMeansCode==PaymentMeansCode.CreditTransfer) {
				IBANId iban = new IBANId(paymentInstruction.getFinancialAccount().getID().getValue());
				List<String> remittanceInformations = paymentInstruction.getRemittanceInformation();
				ublInvoice.addPaymentInstructions(paymentMeansCode, iban, remittanceInformations.isEmpty() ? null : remittanceInformations.get(0));
			} else {
				LOG.warning("TODO:"+paymentMeansCode);
			}
			ublInvoice.setPaymentTermsAndDate(testCreditNote.getPaymentTerm(), testCreditNote.getDueDateAsTimestamp());
		LOG.info("finished PaymentGroup.");

//		void makesDocumentTotalsGroup(Invoice ublInvoice) {
			ublInvoice.setDocumentTotals(testCreditNote.getInvoiceLineNetTotal(), 
					testCreditNote.getInvoiceTotalTaxExclusive(), 
					testCreditNote.getInvoiceTotalTaxInclusive(), testCreditNote.getDuePayable());
			ublInvoice.setInvoiceTax(testCreditNote.getInvoiceTax());
		LOG.info("finished DocumentTotalsGroup.");
		
		makeVatBreakDownGroup2(ublInvoice);
		
//		 LineGroup 
		List<CreditNoteLine> invoiceLines = testCreditNote.getLines();
		LOG.info("LineGroup starts for "+invoiceLines.size() + " lines.");
		invoiceLines.forEach(testLine -> {
			List<String> itemDescriptions = testLine.getItemDescriptions();
			CreditNoteLine invoiceLine = new CreditNoteLine(testLine.getId(), testLine.getQuantity(),
					testLine.getLineNetAmount(), testLine.getItemNetPrice(), 
					testLine.getItemName(), testLine.getVatCategory());
			itemDescriptions.forEach(description -> {
				invoiceLine.addItemDescription(description);
			});
			ublInvoice.addLine(invoiceLine);
		});
		LOG.info("LineGroup finished. ");

		return ublInvoice;
	}
	void makeVatBreakDownGroup2(CreditNote ublInvoice) {
		List<Map<Object, Object>> vatBreakDowns = testCreditNote.getVATBreakDown();
		vatBreakDowns.forEach(vatBreakDown -> {
			VatCategory vc = (VatCategory) vatBreakDown.get(VatCategory.class);
//			LOG.info("vc =============================" +vc + " ID.value:"+vc.getID().getValue() +
//					"TaxCategoryCode:"+vc.getTaxCategoryCode()
//					);
			VatCategory vatCategory = null;
			if(vc.getTaxCategoryCode().equals(TaxCategoryCode.StandardRate.getValue())) {
				vatCategory = new VatCategory(TaxCategoryCode.StandardRate, vc.getPercent().getValue());
			} else if(vc.getTaxCategoryCode().equals(TaxCategoryCode.ServicesOutsideScope.getValue())) {
				vatCategory = new VatCategory(TaxCategoryCode.ServicesOutsideScope, vc.getPercent().getValue());
			} else {
				vatCategory = new VatCategory(TaxCategoryCode.ExemptFromTax, vc.getPercent().getValue());
				LOG.warning("vatCategory =============================" +vatCategory);
			}
			
			// die optionalen "VAT exemption reason text" und "VAT exemption reason code"
			List<String> taxExemptionReasonList = (List<String>) vatBreakDown.get(TaxExemptionReasonType.class);
			if(taxExemptionReasonList==null) {
				LOG.info("taxExemptionReasonList =============================" +taxExemptionReasonList);
			} else for(int l=0; l<taxExemptionReasonList.size(); l++){
				
				LOG.warning("TaxExemptionReason TODO ============================= #" +l);
				vatCategory.addTaxExemptionReason(taxExemptionReasonList.get(l));
			}
			String reasonCode = (String) vatBreakDown.get(TaxExemptionReasonCodeType.class);
			if(reasonCode!=null) {
				vatCategory.setTaxExemptionReasonCode(reasonCode);
			}
			
			LOG.info("vatCategory =============================" +vatCategory);
			ublInvoice.addVATBreakDown( (Amount) vatBreakDown.get(TaxableAmountType.class), 
					(Amount) vatBreakDown.get(TaxAmountType.class), 
					vatCategory);
			
		});
		LOG.info("finished. "+vatBreakDowns.size() + " vatBreakDowns.");
	}

	@Override
	Object makeInvoice() {
		if(testDoc==null) {
			return makeCreditNote();
		}
		Invoice ublInvoice = new CommercialInvoice(testDoc.getCustomization(), testDoc.getProfile());
		ublInvoice.setId(testDoc.getId());
		ublInvoice.setIssueDate(testDoc.getIssueDateAsTimestamp());
		ublInvoice.setDocumentCurrency(testDoc.getDocumentCurrency());
		ublInvoice.setTaxCurrency(testDoc.getTaxCurrency());
		ublInvoice.setBuyerReference(testDoc.getBuyerReferenceValue());
		
		makeOptionals(ublInvoice);
		
		makeSellerGroup(ublInvoice);
		makeBuyerGroup(ublInvoice);
		makePaymentGroup(ublInvoice);
		makesDocumentTotalsGroup(ublInvoice);
		makeVatBreakDownGroup(ublInvoice);
		makeLineGroup(ublInvoice);
		LOG.info("finished.");
		return ublInvoice;
	}

	void makeOptionals(Invoice ublInvoice) {	
		ublInvoice.setOrderReferenceID(testDoc.getOrderReferenceID());
		List<String> notes = testDoc.getNotes();
		notes.forEach(note -> {
			ublInvoice.setNote(note);
		});
		LOG.info("finished.");
	}
	
	void makeSellerGroup(Invoice ublInvoice) {
		ublInvoice.setSellerParty(testDoc.getSellerParty());
		LOG.info("finished.");
	}
	
	void makeBuyerGroup(Invoice ublInvoice) {
		ublInvoice.setBuyerParty(testDoc.getBuyerParty());
		LOG.info("finished.");
	}
	
	void makePaymentGroup(Invoice ublInvoice) {
		PaymentMeans paymentInstruction = testDoc.getPaymentInstructions().get(0);
		PaymentMeansCode paymentMeansCode = paymentInstruction.getPaymentMeans();
		if(paymentMeansCode==PaymentMeansCode.CreditTransfer) {
			IBANId iban = new IBANId(paymentInstruction.getFinancialAccount().getID().getValue());
			List<String> remittanceInformations = paymentInstruction.getRemittanceInformation();
			ublInvoice.addPaymentInstructions(paymentMeansCode, iban, remittanceInformations.isEmpty() ? null : remittanceInformations.get(0));
		} else {
			LOG.warning("TODO:"+paymentMeansCode);
		}

		ublInvoice.setPaymentTermsAndDate(testDoc.getPaymentTerm(), testDoc.getDueDateAsTimestamp());
		LOG.info("finished.");
	}
	
	void makesDocumentTotalsGroup(Invoice ublInvoice) {
		ublInvoice.setDocumentTotals(testDoc.getInvoiceLineNetTotal(), 
				testDoc.getInvoiceTotalTaxExclusive(), 
				testDoc.getInvoiceTotalTaxInclusive(), testDoc.getDuePayable());
		ublInvoice.setInvoiceTax(testDoc.getInvoiceTax());
		LOG.info("finished.");
	}
	
	void makeVatBreakDownGroup(Invoice ublInvoice) {
		List<Map<Object, Object>> vatBreakDowns = testDoc.getVATBreakDown();
		vatBreakDowns.forEach(vatBreakDown -> {
			VatCategory vc = (VatCategory) vatBreakDown.get(VatCategory.class);
//			LOG.info("vc =============================" +vc + " ID.value:"+vc.getID().getValue() +
//					"TaxCategoryCode:"+vc.getTaxCategoryCode()
//					);
			VatCategory vatCategory = null;
			if(vc.getTaxCategoryCode().equals(TaxCategoryCode.StandardRate.getValue())) {
				vatCategory = new VatCategory(TaxCategoryCode.StandardRate, vc.getPercent().getValue());
			} else if(vc.getTaxCategoryCode().equals(TaxCategoryCode.ServicesOutsideScope.getValue())) {
				vatCategory = new VatCategory(TaxCategoryCode.ServicesOutsideScope, vc.getPercent().getValue());
			} else {
				vatCategory = new VatCategory(TaxCategoryCode.ExemptFromTax, vc.getPercent().getValue());
				LOG.warning("vatCategory =============================" +vatCategory);
			}
			
			// die optionalen "VAT exemption reason text" und "VAT exemption reason code"
			List<String> taxExemptionReasonList = (List<String>) vatBreakDown.get(TaxExemptionReasonType.class);
			if(taxExemptionReasonList==null) {
				LOG.info("taxExemptionReasonList =============================" +taxExemptionReasonList);
			} else for(int l=0; l<taxExemptionReasonList.size(); l++){
				
				LOG.warning("TaxExemptionReason TODO ============================= #" +l);
				vatCategory.addTaxExemptionReason(taxExemptionReasonList.get(l));
			}
			String reasonCode = (String) vatBreakDown.get(TaxExemptionReasonCodeType.class);
			if(reasonCode!=null) {
				vatCategory.setTaxExemptionReasonCode(reasonCode);
			}
			
			LOG.info("vatCategory =============================" +vatCategory);
			ublInvoice.addVATBreakDown( (Amount) vatBreakDown.get(TaxableAmountType.class), 
					(Amount) vatBreakDown.get(TaxAmountType.class), 
					vatCategory);
			
		});
		LOG.info("finished. "+vatBreakDowns.size() + " vatBreakDowns.");
	}
	
	void makeLineGroup(Invoice ublInvoice) {
		List<InvoiceLine> invoiceLines = testDoc.getLines();
		invoiceLines.forEach(testLine -> {
			List<String> itemDescriptions = testLine.getItemDescriptions();
			InvoiceLine invoiceLine = new InvoiceLine(testLine.getId(), testLine.getQuantity(),
					testLine.getLineNetAmount(), testLine.getItemNetPrice(), 
					testLine.getItemName(), testLine.getVatCategory());
			itemDescriptions.forEach(description -> {
				invoiceLine.addItemDescription(description);
			});
			ublInvoice.addLine(invoiceLine);
		});
		LOG.info("finished. "+invoiceLines.size() + " lines.");
	}

	private File getTestFile(String uri) {
		File file = new File(uri);
		LOG.info("test file "+file.getAbsolutePath() + " canRead:"+file.canRead());
		return file;
	}
	
	private CreditNote toModelCreditNote(File xmlfile) {
		CreditNoteType invoice;
		CreditNote creditNote = null;
		try {
			InputStream is = new FileInputStream(xmlfile);
			invoice = transformer.toModel(is);
			creditNote = new CreditNote(invoice);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.severe(ex.getMessage());			
		}
		return creditNote;
	}
	private Invoice toModel(File xmlfile) {
		InvoiceType invoice;
		CommercialInvoice cmInvoice = null;
		try {
			InputStream is = new FileInputStream(xmlfile);
			invoice = transformer.toModel(is);
			DocumentNameCode documentNameCode = DocumentNameCode.valueOf(invoice.getInvoiceTypeCode()); // ein enum
			LOG.info("enum InvoiceTypeCode: " + documentNameCode);
			if(documentNameCode==DocumentNameCode.CommercialInvoice) {
				cmInvoice = new CommercialInvoice(invoice);
				
				String sellerRegistrationName = cmInvoice.getSellerParty().getName();
				String sellerCompanyID = cmInvoice.getSellerParty().getCompanyID();
				String sellerCompanyLegalForm = cmInvoice.getSellerParty().getCompanyLegalForm();
				
				String buyerRegistrationName = cmInvoice.getBuyerParty().getName();
				String buyerCompanyID = cmInvoice.getBuyerParty().getCompanyID();
				String buyerCompanyLegalForm = cmInvoice.getBuyerParty().getCompanyLegalForm();
				
				List<Map<Object,String>> sellerTaxSchemes = cmInvoice.getSellerTaxSchemes();
				Map<Object, String> sellerTaxScheme = null;
				if(sellerTaxSchemes.isEmpty()) {
					LOG.warning("sellerTaxSchemes is empty");
				} else {
					LOG.info("------------------------------ sellerTaxSchemes #="+sellerTaxSchemes.size());
					sellerTaxScheme = sellerTaxSchemes.get(0); // first
				}
				List<Map<Object,String>> buyerTaxSchemes = cmInvoice.getBuyerTaxSchemes();
				Map<Object, String> buyerTaxScheme = null;
				if(buyerTaxSchemes.isEmpty()) {
					LOG.warning("buyerTaxSchemes is empty");
					buyerTaxScheme = new HashMap<Object,String>();
				} else {
					buyerTaxScheme = buyerTaxSchemes.get(0); // first
				}
				
				IContact buyerContact = cmInvoice.getBuyerContact();
				if(buyerContact==null) {
					LOG.warning("buyerContact is null");
					buyerContact = new Contact("nix", "0", "nix@nix.nix");
				}
				
				List<PaymentMeans> paymentInstructions = cmInvoice.getPaymentInstructions();
				PaymentMeans paymentInstruction = null;
				if(paymentInstructions.isEmpty()) {
					LOG.warning("paymentInstructions is empty");
					paymentInstruction = new PaymentMeans(PaymentMeansCode.InCash, new FinancialAccount(new IBANId("nix")));
				} else {
					paymentInstruction = paymentInstructions.get(0); // first
				}
				
				String paymentTerm = cmInvoice.getPaymentTerm();
				
				List<Map<Object,Object>> vatBreakDownList = cmInvoice.getVATBreakDown();
				Map<Object, Object> vatBreakDown = null;
				if(vatBreakDownList.isEmpty()) {
					LOG.warning("vatBreakDownList is empty");
					vatBreakDown = new HashMap<Object,Object>();
				} else {
					vatBreakDown = vatBreakDownList.get(0); // first
				} 

				List<InvoiceLine> invoiceLines = cmInvoice.getLines();
				LOG.info("\ninvoice ID="+			        cmInvoice.getId() +
						" \ninvoice IssueDate="+			cmInvoice.getIssueDateAsTimestamp() + 
						" \ninvoice CustomizationID="+		cmInvoice.getCustomization() + 
						" \ninvoice ProfileID="+			cmInvoice.getProfile() + 
						" \ninvoice tDocumentNameCode="+	cmInvoice.getTypeCode() + 
						" \ninvoice Notes#:"+				cmInvoice.getNotes().size() +
						" \ninvoice TaxCurrency="+			cmInvoice.getTaxCurrency() + 
						" \ninvoice DocumentCurrency="+		cmInvoice.getDocumentCurrency() + 
						" \ninvoice BuyerReferenceValue="+	cmInvoice.getBuyerReferenceValue() + 
						" \ninvoice OrderReferenceID="+		cmInvoice.getOrderReferenceID() + 
						" \ninvoice SellerRegistrationName="+	sellerRegistrationName +					
						" \ninvoice SellerCompanyID="+			sellerCompanyID +					
						" \ninvoice SellerCompanyLegalForm="+	sellerCompanyLegalForm +					
						" \ninvoice BuyerRegistrationName="+	buyerRegistrationName +					
						" \ninvoice BuyerCompanyID="+			buyerCompanyID +					
						" \ninvoice BuyerCompanyLegalForm="+	buyerCompanyLegalForm +					
						" \ninvoice SellerTaxScheme="+		sellerTaxScheme.get(TaxSchemeType.class) +					
						" \ninvoice SellerCompanyID="+		sellerTaxScheme.get(CompanyIDType.class) +					
						" \ninvoice SellerCountryCode="+	cmInvoice.getSellerPostalAddress().getCountryCode() +					
						" \ninvoice SellerRegion="+			cmInvoice.getSellerPostalAddress().getCountrySubdivision() +					
						" \ninvoice SellerPostCode City="+	cmInvoice.getSellerPostalAddress().getPostCode()+" "+cmInvoice.getSellerPostalAddress().getCity() + 					
						" \ninvoice SellerStreet Building="+cmInvoice.getSellerPostalAddress().getStreet()+" "+cmInvoice.getSellerPostalAddress().getBuilding() + 					
						" \ninvoice SellerContactPoint="+		cmInvoice.getSellerContact().getContactPoint() +					
						" \ninvoice SellerContactTelephone="+	cmInvoice.getSellerContact().getContactTelephone() +					
						" \ninvoice SellerContactEmail="+		cmInvoice.getSellerContact().getContactEmail() +					
						" \ninvoice BuyerCountryCode="+		cmInvoice.getBuyerPostalAddress().getCountryCode() +					
						" \ninvoice BuyerRegion="+			cmInvoice.getBuyerPostalAddress().getCountrySubdivision() +					
						" \ninvoice BuyerPostCode City="+	cmInvoice.getBuyerPostalAddress().getPostCode()+" "+cmInvoice.getBuyerPostalAddress().getCity() + 					
						" \ninvoice BuyerStreet Building="+	cmInvoice.getBuyerPostalAddress().getStreet()+" "+cmInvoice.getBuyerPostalAddress().getBuilding() + 					
						" \ninvoice BuyerContactPoint="+		buyerContact.getContactPoint() +					
						" \ninvoice BuyerContactTelephone="+	buyerContact.getContactTelephone() +					
						" \ninvoice BuyerContactEmail="+		buyerContact.getContactEmail() +					
						" \ninvoice buyerTaxSchemes#:"+		cmInvoice.getBuyerTaxSchemes().size() +
						" \ninvoice BuyerTaxScheme="+		buyerTaxScheme.get(TaxSchemeType.class) +					
						" \ninvoice BuyerCompanyID="+		buyerTaxScheme.get(CompanyIDType.class) +					
						" \ninvoice InvoiceLineNetTotal="+		cmInvoice.getInvoiceLineNetTotal() +					
						" \ninvoice InvoiceTotalTaxExclusive="+	cmInvoice.getInvoiceTotalTaxExclusive() +					
						" \ninvoice InvoiceTotalTaxInclusive="+	cmInvoice.getInvoiceTotalTaxInclusive() +					
						" \ninvoice DuePayable="+				cmInvoice.getDuePayable() +					
						" \ninvoice InvoiceTax="+				cmInvoice.getInvoiceTax() +					
						" \ninvoice paymentInstructions#:"+		cmInvoice.getPaymentInstructions().size() +					
						" \ninvoice paymentInstruction.PaymentMeans="+	paymentInstruction.getPaymentMeans()+" "+paymentInstruction.getFinancialAccount().getID().getValue() +				
						" \ninvoice paymentTerms.1st Note="+	paymentTerm +				
						" \ninvoice VATBreakDown.TaxableAmount="+	vatBreakDown.get(TaxableAmountType.class) +					
						" \ninvoice VATBreakDown.TaxAmount="+		vatBreakDown.get(TaxAmountType.class) +					
						" \ninvoice VATBreakDown.VatCategory="+		vatBreakDown.get(VatCategory.class) +					
						" \ninvoice invoiceLines#:"+		invoiceLines.size() 
						);
				invoiceLines.forEach(invoiceLine -> {
					LOG.info("\n invoiceLine ID="+			        invoiceLine.getId() +
							" \n invoiceLine ItemName="+			invoiceLine.getItemName() +
							" \n invoiceLine Quantity="+			invoiceLine.getQuantity() +
							" \n invoiceLine ItemNetPrice="+		invoiceLine.getItemNetPrice() +
							" \n invoiceLine LineNetAmount="+		invoiceLine.getLineNetAmount() +
							" \n invoiceLine TaxCategory="+			invoiceLine.getVatCategory()
							);
				});

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.severe(ex.getMessage());
		}
		return cmInvoice;
	}

}