package com.klst.einvoice.unece.uncefact;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.klst.einvoice.BG13_DeliveryInformation;
import com.klst.einvoice.BG24_AdditionalSupportingDocs;
import com.klst.einvoice.BG4_Seller;
import com.klst.einvoice.BG7_Buyer;
import com.klst.einvoice.BusinessParty;
import com.klst.einvoice.CoreInvoice;
import com.klst.einvoice.CoreInvoiceLine;
import com.klst.einvoice.CoreInvoiceVatBreakdown;
import com.klst.einvoice.CreditTransfer;
import com.klst.einvoice.DirectDebit;
import com.klst.einvoice.IContact;
import com.klst.einvoice.PaymentCard;
import com.klst.einvoice.PaymentInstructions;
import com.klst.einvoice.PostalAddress;
import com.klst.untdid.codelist.DateTimeFormats;
import com.klst.untdid.codelist.DocumentNameCode;
import com.klst.untdid.codelist.PaymentMeansEnum;
import com.klst.untdid.codelist.TaxCategoryCode;

import un.unece.uncefact.data.standard.crossindustryinvoice._100.CrossIndustryInvoiceType;
import un.unece.uncefact.data.standard.qualifieddatatype._100.DocumentCodeType;
import un.unece.uncefact.data.standard.qualifieddatatype._100.FormattedDateTimeType;
import un.unece.uncefact.data.standard.qualifieddatatype._100.TimeReferenceCodeType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.DocumentContextParameterType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.ExchangedDocumentContextType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.ExchangedDocumentType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.HeaderTradeAgreementType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.HeaderTradeDeliveryType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.NoteType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.ProcuringProjectType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.ReferencedDocumentType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.SpecifiedPeriodType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.SupplyChainTradeLineItemType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.SupplyChainTradeTransactionType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.TradePartyType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.TradePaymentTermsType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.TradeSettlementHeaderMonetarySummationType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.TradeTaxType;
import un.unece.uncefact.data.standard.unqualifieddatatype._100.AmountType;
import un.unece.uncefact.data.standard.unqualifieddatatype._100.BinaryObjectType;
import un.unece.uncefact.data.standard.unqualifieddatatype._100.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._100.DateType;
import un.unece.uncefact.data.standard.unqualifieddatatype._100.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._100.TextType;

// @see https://www.unece.org/fileadmin/DAM/cefact/rsm/RSM_CrossIndustryInvoice_v2.0.pdf
//      https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=1&cad=rja&uact=8&ved=2ahUKEwijlav-gPjhAhUGU1AKHSv2CIoQFjAAegQIARAC&url=https%3A%2F%2Fwww.unece.org%2Ffileadmin%2FDAM%2Fcefact%2Frsm%2FRSM_CrossIndustryInvoice_v2.0.pdf&usg=AOvVaw0yPVFpbRqJ50xaDMUaYm62
// ZUGFeRD 2.0 Spezifikation - Technischer Anhang : ZUGFeRD-2.0-Spezifikation-TA.pdf
public class CrossIndustryInvoice extends CrossIndustryInvoiceType implements CoreInvoice {

	private static final Logger LOG = Logger.getLogger(CrossIndustryInvoice.class.getName());
	
	private static final String NOT_IMPEMENTED = "NOT IMPEMENTED";
	
/*
1 .. 1 ExchangedDocumentContext Prozesssteuerung BG-2 xs:sequence 
0 .. 1 TestIndicator Testkennzeichen                  xs:choice 
1 .. 1 Indicator Testkennzeichen, Wert
0 .. 1 BusinessProcessSpecifiedDocumentContextParameter Gruppierung der Geschäftsprozessinformationen xs:sequence 
0 .. 1 ID Geschäftsprozesstyp                    BT-23 
1 .. 1 GuidelineSpecifiedDocumentContextParameter Gruppierung der Anwendungsempfehlungsinformationen xs:sequence 
1 .. 1 ID Spezifikationskennung                  BT-24

1 .. 1 ExchangedDocument Gruppierung der Eigenschaften, die das gesamte Dokument betreffen. xs:sequence 
1 .. 1 ID Rechnungsnummer                        BT-1 
0 .. 1 Name Dokumentenart (Freitext) 
1 .. 1 TypeCode Code für den Rechnungstyp        BT-3 
1 .. 1 IssueDateTime Rechnungsdatum xs:choice 
1 .. 1 DateTimeString Rechnungsdatum             BT-2 required format Datum, Format BT-2-0 
0 .. 1 CopyIndicator xs:choice 
1 .. 1 Indicator 
0 .. 1 LanguageID Sprachkennzeichen 
0 .. n IncludedNote Freitext zur Rechnung        BG-1 xs:sequence
0 .. 1 ContentCode Freitext auf Dokumentenebene 
1 .. 1 Content Freitext zur Rechnung             BT-22 
0 .. 1 SubjectCode Code zur Qualifizierung des Freitextes zur Rechnung BT-21 
0 .. 1 EffectiveSpecifiedPeriod Vertragliches Fälligkeitsdatum der Rechnung xs:sequence 
1 .. 1 CompleteDateTime Vertragliches Fälligkeitsdatum der Rechnung xs:choice 
1 .. 1 DateTimeString Vertragliches Fälligkeitsdatum der Rechnung, Wert required format Datum, Format
 */
	CrossIndustryInvoice() {
		super();
	}

	ApplicableHeaderTradeSettlement applicableHeaderTradeSettlement;
	HeaderTradeDeliveryType applicableHeaderTradeDelivery;
	ExchangedDocumentType exchangedDocument;
	
	public CrossIndustryInvoice(String customization, DocumentNameCode documentNameCode) {
		this(customization, null, documentNameCode);
	}
	
	public CrossIndustryInvoice(String customization, String profile, DocumentNameCode documentNameCode) {
		this();
		setProcessControl(customization, profile);
		supplyChainTradeTransaction = new SupplyChainTradeTransactionType();
		
		applicableHeaderTradeDelivery = new ApplicableHeaderTradeDelivery();
		supplyChainTradeTransaction.setApplicableHeaderTradeDelivery(applicableHeaderTradeDelivery);
		
		applicableHeaderTradeSettlement = new ApplicableHeaderTradeSettlement();
		supplyChainTradeTransaction.setApplicableHeaderTradeSettlement(applicableHeaderTradeSettlement.get());
		super.setSupplyChainTradeTransaction(supplyChainTradeTransaction);
		
		exchangedDocument = new ExchangedDocumentType();
		DocumentCodeType documentCode = new DocumentCodeType();
		documentCode.setValue(documentNameCode.getValueAsString());
		exchangedDocument.setTypeCode(documentCode);
		super.setExchangedDocument(exchangedDocument);
	}

	// copy-ctor
	public CrossIndustryInvoice(CrossIndustryInvoiceType doc) {
		this(getCustomization(doc), getProfile(doc), getTypeCode(doc));
		LOG.info("Customization:"+getCustomization() + ", Profile:"+getProfile() + ", TypeCode:"+getTypeCode());
		
		Object ahtd = doc.getSupplyChainTradeTransaction().getApplicableHeaderTradeDelivery();
		LOG.info("ApplicableHeaderTradeDelivery ahtd:"+ahtd);
		if(ahtd==null) {
			LOG.info("ahtd==null");
		} else {
			HeaderTradeDeliveryType htd = (HeaderTradeDeliveryType)ahtd;
			applicableHeaderTradeDelivery = new ApplicableHeaderTradeDelivery(htd);
			supplyChainTradeTransaction.setApplicableHeaderTradeDelivery(applicableHeaderTradeDelivery);
		}

		ApplicableHeaderTradeSettlement ahts = ApplicableHeaderTradeSettlement.getApplicableHeaderTradeSettlement(doc.getSupplyChainTradeTransaction());
		LOG.info("\n PayeeParty:");
		setPayee(ahts.getPayeeTradeParty());
		
		LOG.info("PaymentMeans "+ahts.getPaymentMeansEnum() + " PaymentMeansText:"+ahts.getPaymentMeansText() + " RemittanceInformation:"+ahts.getRemittanceInformation());
		List<CreditTransfer> creditTransferList = ahts.getCreditTransfer();
		setPaymentInstructions(ahts.getPaymentMeansEnum(), ahts.getPaymentMeansText(), ahts.getRemittanceInformation()
				, creditTransferList, null, ahts.getDirectDebit());
		
		setStartDate(getStartDateAsTimestamp(ahts));
		setEndDate(getEndDateAsTimestamp(ahts));
		
		setPaymentTermsAndDate(getPaymentTerm(ahts), getDueDateAsTimestamp(ahts)); // optional
		
		List<TradeTaxType> attList = ahts.getApplicableTradeTax();
		List<VatBreakdown> vatBreakdownList = new ArrayList<VatBreakdown>(attList.size()); // VatBreakdown extends TradeTaxType
		attList.forEach(vbd -> {
			VatBreakdown vatBreakdown = new VatBreakdown(vbd);
			LOG.info("vatBreakdown "+vatBreakdown);
			vatBreakdownList.add(vatBreakdown);
		});
		addVATBreakDown(vatBreakdownList);

		setId(getId(doc));
		setIssueDate(getIssueDateAsTimestamp(doc));
		
		String documentCurrency = ahts.getDocumentCurrency();
		setDocumentCurrency(documentCurrency);

		String taxCurrency = ahts.getTaxCurrency();
		setTaxCurrency(taxCurrency); // optional
		
		setTaxPointDate(getTaxPointDateAsTimestamp(ahts)); // optional
		
		setBuyerReference(getBuyerReferenceValue(doc)); // optional
		setOrderReference(getOrderReferenceID(doc)); // optional

		addNotes(doc);	
		LOG.info("\n SellerParty:");;
		setSeller(getSellerParty(doc));
		LOG.info("\n BuyerParty:");;
		setBuyer(getBuyerParty(doc));
		LOG.info("\n SellerTaxRepresentativeParty:");;
		setTaxRepresentative(getTaxRepresentativeParty(doc)); // optional
		LOG.info("\n ...");
		
		TradeSettlementHeaderMonetarySummationType stshms = ahts.getTradeSettlementHeaderMonetarySummation(); // BG-22 1..1
		setDocumentTotals(new Amount(stshms.getLineTotalAmount().get(0).getValue())     // getInvoiceLineNetTotal(doc)
						, new Amount(stshms.getTaxBasisTotalAmount().get(0).getValue()) // getInvoiceTotalTaxExclusive(doc)
						, new Amount(stshms.getGrandTotalAmount().get(0).getValue())    // getInvoiceTotalTaxInclusive(doc)
						, new Amount(stshms.getDuePayableAmount().get(0).getValue())    // getDuePayable(doc)
				);

		List<AmountType> list = stshms.getTaxTotalAmount(); // BT-110, BT-111 1..1
		if(list.isEmpty()) {
			// sollte nicht vorkommen
		} else {
			for(int i=0; i<list.size(); i++) {
				LOG.info("i="+i + " taxCurrency:"+taxCurrency + " documentCurrency:"+documentCurrency);
				if(taxCurrency==null || documentCurrency.equals(taxCurrency)) {
					Amount invoiceTaxAmount = list.get(i).getCurrencyID()==null ? new Amount(list.get(i).getValue()) : new Amount(list.get(i).getCurrencyID(), list.get(i).getValue());
					LOG.info("invoiceTaxAmount "+invoiceTaxAmount);
					setInvoiceTax(invoiceTaxAmount);
				}
			}
		}
		
		addLines(doc);
	}


	/* Invoice number                              BT-1  Identifier            1 (mandatory) 
	 * Eine eindeutige Kennung der Rechnung, die diese im System des Verkäufers identifiziert.
	 * Anmerkung: Es ist kein „identification scheme“ zu verwenden.

ID Spezifikationskennung . 
Datentyp: udt:IDType . 
Beschreibung: Eine Kennung der Spezifikation, die das gesamte Regelwerk zum semantischen Inhalt, zu den Kardinalitäten und den Geschäftsregeln enthält und zu denen die im Instanzdokument enthaltenen Daten conformant sind . 
Hinweis: In diesem wird die Compliance oder Conformance der Instanz mit diesem Dokument angegeben. Rechnungen, die compliant sind, geben folgendes an: urn:cen.eu:en16931:2017. Rechnungen, die compliant mit einer Benutzerspezifikation sind, dürfen diese Benutzerspezifikation an dieser Stelle angeben. Es ist kein Identifikationsschema zu verwenden. . 
Synonym: Anwendungsempfehlung . 
Kardinalität: 1 .. 1 . 
EN16931-ID: BT-24 . 
Anwendung: Profil EXTENDED: urn:cen.eu:EN16931:2017#conformant#urn:zugferd.de:2p0:extended 
           Profil EN 16931 (COMFORT): urn:cen.eu:EN16931:2017 
           Profil BASIC: urn:cen.eu:EN16931:2017#compliant#urn:zugferd.de:2p0:basic 
           Profil BASIC WL: urn:zugferd.de:2p0:basicwl 
           Profil MINIMUM: urn:zugferd.de:2p0:minimum . 
Geschäftsregel: BR-1 Prozesssteuerung Eine Rechnung muss eine Spezifikationskennung (BT-24) haben.

	 */
	@Override
	public void setId(String id) {
		exchangedDocument.setID(newIDType(id, null)); // null : No identification scheme is to be used.
	}
	
	@Override
	public String getId() {
		return getId(this);
	}
	static String getId(CrossIndustryInvoiceType doc) {
		return doc.getExchangedDocument().getID().getValue();
	}

	/* Invoice issue date                          BT-2  Date                  1 (mandatory) 
	 * Das Datum, an dem die Rechnung ausgestellt wurde.
	 */
	@Override
	public void setIssueDate(String ymd) {	
		setIssueDate(DateTimeFormats.ymdToTs(ymd));
	}
	
	@Override
	public void setIssueDate(Timestamp ts) {
		DateTimeType dateTime = newDateTime(ts);
		this.getExchangedDocument().setIssueDateTime(dateTime);
	}

	@Override
	public Timestamp getIssueDateAsTimestamp() {
		return getIssueDateAsTimestamp(this);
	}
	static Timestamp getIssueDateAsTimestamp(CrossIndustryInvoiceType doc) {
		DateTimeType dateTime = doc.getExchangedDocument().getIssueDateTime();
		return DateTimeFormats.ymdToTs(dateTime.getDateTimeString().getValue());
	}

	@Override
	public void setTypeCode(DocumentNameCode code) {
		DocumentCodeType documentCode = new DocumentCodeType();
		documentCode.setValue(code.getValueAsString());
		exchangedDocument.setTypeCode(documentCode);
	}

	@Override
	public DocumentNameCode getTypeCode() {
		return getTypeCode(this);
	}
	static DocumentNameCode getTypeCode(CrossIndustryInvoiceType doc) {
		return DocumentNameCode.valueOf(doc.getExchangedDocument().getTypeCode());
	}

	// 1 .. 1 InvoiceCurrencyCode Code für die Rechnungswährung BT-5
	@Override
	public void setDocumentCurrency(String isoCurrencyCode) {
		applicableHeaderTradeSettlement.setDocumentCurrency(isoCurrencyCode);
	}

	@Override
	public String getDocumentCurrency() {
		return applicableHeaderTradeSettlement.getDocumentCurrency();
	}

	// 0..1 (optional) BT-6
	@Override
	public void setTaxCurrency(String isoCurrencyCode) {
		applicableHeaderTradeSettlement.setTaxCurrency(isoCurrencyCode);
	}

	@Override
	public String getTaxCurrency() {
		return applicableHeaderTradeSettlement.getTaxCurrency();
	}

	/*
1 .. 1 ApplicableHeaderTradeSettlement Gruppierung von Angaben zur Zahlung und Rechnungsausgleich
0 .. n ApplicableTradeTax Umsatzsteueraufschlüsselung          BG-23

0 .. 1 TaxPointDate Datum der Steuerfälligkeit xs:choice 
1 .. 1 DateString Datum der Steuerfälligkeit, Wert             BT-7 
required format Datum, Format                                  BT-7-0
0 .. 1 DueDateTypeCode Code für das Datum der Steuerfälligkeit BT-8

Anwendung: In Deutschland wird dieses nicht verwendet. 
Statt dessen ist das Liefer- und Leistungsdatum anzugeben.
	 */
	@Override
	public void setTaxPointDate(String ymd) {
		setTaxPointDate(DateTimeFormats.ymdToTs(ymd));
	}

	// 0..1 (optional) BT-7 BT-7-0
	@Override
	public void setTaxPointDate(Timestamp ts) {
		applicableHeaderTradeSettlement.setTaxPointDate(ts);
	}

	DateType newDateType(Timestamp ts) {
		if(ts==null) return null;
		
		DateType dateTime = new DateType();
		DateType.DateString dts = new DateType.DateString(); // DateString ist inner class in DateType
		dts.setFormat(DateTimeFormats.CCYYMMDD_QUALIFIER);
		dts.setValue(DateTimeFormats.tsToCCYYMMDD(ts));
		dateTime.setDateString(dts);
		return dateTime;
	}

	@Override
	public Timestamp getTaxPointDateAsTimestamp() {
		return getTaxPointDateAsTimestamp(applicableHeaderTradeSettlement);
	}
	static Timestamp getTaxPointDateAsTimestamp(ApplicableHeaderTradeSettlement ahts) {
		List<TradeTaxType> tradeTaxList = ahts.getApplicableTradeTax();
		if(tradeTaxList.isEmpty()) return null;
		
		List<Timestamp> results = new ArrayList<Timestamp>(tradeTaxList.size());
		tradeTaxList.forEach(tradeTax -> {
			DateType date = tradeTax.getTaxPointDate();
			if(date==null) {
				LOG.warning("getTaxPointDateAsTimestamp(doc) TaxPointDate is null");
			} else if(DateTimeFormats.CCYYMMDD_QUALIFIER.equals(date.getDateString().getFormat())) {
				results.add(DateTimeFormats.ymdToTs(date.getDateString().getValue()));
			} else {
				LOG.warning("not CCYYMMDD-Format:"+date.getDateString().getFormat() + " value:"+date.getDateString().getValue());
			}		
		});
		if(results.isEmpty()) return null;
		if(results.size()>1) {
			LOG.warning("results.size()>1:"+results.size());
		}
		return results.get(0);
	}
	
	// BT-8 + 0..1 Value added tax point date code
	@Override
	public void setTaxPointDateCode(String code) {
		if(code==null) return;  // optional
		TimeReferenceCodeType timeReferenceCode = new TimeReferenceCodeType();
		timeReferenceCode.setValue(code);
		TradeTaxType tradeTax = new TradeTaxType();
		tradeTax.setDueDateTypeCode(timeReferenceCode);
		applicableHeaderTradeSettlement.getApplicableTradeTax().add(tradeTax);
	}

	@Override
	public String getTaxPointDateCode() {
		LOG.warning(NOT_IMPEMENTED); // TODO
		return null;
	}

	// BT-9 0..1 DueDateDateTime Fälligkeitsdatum
	@Override
	public void setDueDate(String ymd) {
		setDueDate(DateTimeFormats.ymdToTs(ymd));
	}

	@Override
	public void setDueDate(Timestamp ts) {
		setPaymentTermsAndDate(null, ts);
	}

	@Override
	public Timestamp getDueDateAsTimestamp() {
		return getDueDateAsTimestamp(applicableHeaderTradeSettlement);
	}
	static Timestamp getDueDateAsTimestamp(ApplicableHeaderTradeSettlement ahts) {
		if(ahts==null) return null;
		List<TradePaymentTermsType> tradePaymentTermsList = ahts.get().getSpecifiedTradePaymentTerms(); // 0 .. n
		// tradePaymentTermsList / Detailinformationen zu Zahlungsbedingungen
		if(tradePaymentTermsList.isEmpty()) return null;
		DateTimeType dateTime = tradePaymentTermsList.get(0).getDueDateDateTime();
		if(dateTime==null) return null;
		return DateTimeFormats.ymdToTs(dateTime.getDateTimeString().getValue(), dateTime.getDateTimeString().getFormat());
	}

	@Override
	public void setPaymentTermsAndDate(String description, String ymd) {
		setPaymentTermsAndDate(description, DateTimeFormats.ymdToTs(ymd));
	}

	// BT-9 & BT-20 : Payment terms & Payment due date
	@Override
	public void setPaymentTermsAndDate(String description, Timestamp ts) {
		LOG.info("Payment terms description:"+description + " & Payment due date Timestamp:"+ts);
		TradePaymentTermsType tradePaymentTerms = new TradePaymentTermsType();
		if(description==null) {
//			LOG.warning("text==null");
		} else {
			tradePaymentTerms.getDescription().add(newTextType(description)); // returns List<TextType>
		}
		if(ts==null) {
//			LOG.warning("Timestamp ts==null");
		} else {
			tradePaymentTerms.setDueDateDateTime(newDateTime(ts));
		}
		
		applicableHeaderTradeSettlement.addPaymentTerms(tradePaymentTerms);
	}
	
	static DateTimeType newDateTime(Timestamp ts) {
		if(ts==null) return null;
		
		DateTimeType dateTime = new DateTimeType();
		DateTimeType.DateTimeString dts = new DateTimeType.DateTimeString(); // DateTimeString ist inner class in DateTimeType
		dts.setFormat(DateTimeFormats.CCYYMMDD_QUALIFIER);
		dts.setValue(DateTimeFormats.tsToCCYYMMDD(ts));
		dateTime.setDateTimeString(dts);
		return dateTime;
	}

	@Override
	public String getPaymentTerm() {
		return getPaymentTerm(applicableHeaderTradeSettlement);
	}
	static String getPaymentTerm(ApplicableHeaderTradeSettlement ahts) {
		List<TradePaymentTermsType> tradePaymentTermsList = ahts.get().getSpecifiedTradePaymentTerms();
		if(tradePaymentTermsList.isEmpty()) return null;
		
		TradePaymentTermsType tradePaymentTerms = tradePaymentTermsList.get(0); // da Cardinality 0..1 kann es nur einen geben
		List<TextType> textList = tradePaymentTerms.getDescription();
		return textList.isEmpty() ? null : textList.get(0).getValue();
	}

	/* EN16931-ID: 	BT-10
	 * (non-Javadoc)
	 * @see com.klst.cius.CoreInvoice#setBuyerReference(java.lang.String)
	 */
	@Override
	public void setBuyerReference(String reference) {
		HeaderTradeAgreementType headerTradeAgreement = getApplicableHeaderTradeAgreement();
		headerTradeAgreement.setBuyerReference(newTextType(reference));
		
		SupplyChainTradeTransactionType supplyChainTradeTransaction = this.getSupplyChainTradeTransaction();
		supplyChainTradeTransaction.setApplicableHeaderTradeAgreement(headerTradeAgreement);
		super.setSupplyChainTradeTransaction(supplyChainTradeTransaction);		
	}

	@Override
	public String getBuyerReferenceValue() {
		return getBuyerReferenceValue(this);
	}
	static String getBuyerReferenceValue(CrossIndustryInvoiceType doc) {
		HeaderTradeAgreementType headerTradeAgreement = doc.getSupplyChainTradeTransaction().getApplicableHeaderTradeAgreement();
		TextType text = headerTradeAgreement.getBuyerReference();
		return text==null ? null : text.getValue();
	}
	
	// BT-11 + 0..1 Project reference
	@Override
	public void setProjectReference(String docRefId) {
		setProjectReference(docRefId, null);
	}
	@Override
	public void setProjectReference(String docRefId, String name) {
		if(docRefId==null) return; // optional
		ProcuringProjectType procuringProject = new ProcuringProjectType();
		procuringProject.setID(CrossIndustryInvoice.newIDType(docRefId, null));
		procuringProject.setName(CrossIndustryInvoice.newTextType(name));
		
		HeaderTradeAgreementType headerTradeAgreement = getApplicableHeaderTradeAgreement();
		headerTradeAgreement.setSpecifiedProcuringProject(procuringProject);
		
		SupplyChainTradeTransactionType supplyChainTradeTransaction = this.getSupplyChainTradeTransaction();
		supplyChainTradeTransaction.setApplicableHeaderTradeAgreement(headerTradeAgreement);
		super.setSupplyChainTradeTransaction(supplyChainTradeTransaction);		
	}

	@Override
	public String getProjectReference() {
		LOG.warning(NOT_IMPEMENTED); // TODO
		return null;
	}

	// BT-12 + 0..1 Contract reference
	@Override
	public void setContractReference(String id) {
		// TODO test daten in 03 06 08 15
		
	}

	@Override
	public String getContractReference() {
		// TODO Auto-generated method stub
		return null;
	}

	// BT-13 + 0..1 Purchase order reference
	@Override
	public void setPurchaseOrderReference(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPurchaseOrderReference() {
		// TODO Auto-generated method stub
		return null;
	}

	// BT-14 + 0..1 Sales order reference
/* IssuerAssignedID Verkaufsauftragsreferenz
	1 .. 1 ApplicableHeaderTradeAgreement Gruppierung der Vertragsangaben
	0 .. 1 SellerOrderReferencedDocument Detailangaben zur zugehörigen Auftragsbestätigung xs:sequence 
	1 .. 1 IssuerAssignedID Verkaufsauftragsreferenz                                                     BT-14 
	0 .. 1 FormattedIssueDateTime Details zum Auftragsbestätigungsdatum xs:sequence 
	1 .. 1 DateTimeString Auftragsbestätigungsdatum, Wert required format Datum, Format
 */
	@Override
	public void setOrderReference(String docRefId) {
		if(docRefId==null) return; // optional
		ReferencedDocumentType referencedDocument = new ReferencedDocumentType();
		referencedDocument.setIssuerAssignedID(newIDType(docRefId, null)); // null : No identification scheme
		
		HeaderTradeAgreementType headerTradeAgreement = getApplicableHeaderTradeAgreement();
		headerTradeAgreement.setSellerOrderReferencedDocument(referencedDocument);

		SupplyChainTradeTransactionType supplyChainTradeTransaction = this.getSupplyChainTradeTransaction();
		supplyChainTradeTransaction.setApplicableHeaderTradeAgreement(headerTradeAgreement);
		super.setSupplyChainTradeTransaction(supplyChainTradeTransaction);		
	}
	@Override
	public String getOrderReference() {
		return getOrderReferenceID(this);
	}
	static String getOrderReferenceID(CrossIndustryInvoiceType doc) {
		HeaderTradeAgreementType headerTradeAgreement = doc.getSupplyChainTradeTransaction().getApplicableHeaderTradeAgreement();
		ReferencedDocumentType referencedDocument = headerTradeAgreement.getSellerOrderReferencedDocument();
		return referencedDocument==null ? null : referencedDocument.getIssuerAssignedID().getValue();
		
	}

	/* INVOICE NOTE                                BG-1                        0..*
	 * Eine Gruppe von Informationselementen für rechnungsrelevante Erläuterungen mit Hinweisen auf den Rechnungsbetreff.
	 * 
	 */
	@Override
	public List<Object> getNotes() {
		return getNotes(this);
	}
	static List<Object> getNotes(CrossIndustryInvoiceType doc) {
		ExchangedDocumentType ed = doc.getExchangedDocument();
		List<NoteType> noteList = ed.getIncludedNote();
		List<Object> res = new ArrayList<Object>(noteList.size());
		noteList.forEach(note -> {
			res.add(note);
		});
		return res;
	}

	@Override
	public void setNote(String subjectCode, String content) {
		addNote(subjectCode, content);
	}
	@Override
	public void setNote(String content) {
		setNote(null, content);
	}
	public void addNote(String subjectCode, String noteContent) {
		NoteType note = new NoteType();
		TextType content = new TextType();
		content.setValue(noteContent);
		note.getContent().add(content);
		if(subjectCode!=null) {
			TextType subject = new TextType();
			subject.setValue(subjectCode);
			note.setSubject(subject); //  z.B ADU
		}
		addNote(note);
	}
	public void addNote(Object note) {
		exchangedDocument.getIncludedNote().add((NoteType)note);
		super.setExchangedDocument(exchangedDocument);
	}
	void addNotes(CrossIndustryInvoiceType doc) {
		List<Object> noteList = getNotes(doc);
		noteList.forEach(note -> {
			exchangedDocument.getIncludedNote().add((NoteType)note);
		});
	}

	/* PROCESS CONTROL                             BG-2                        1 (mandatory) 
	 * Eine Gruppe von Informationselementen, 
	 * die Informationen über den Geschäftsprozess und für die Rechnung geltende Regeln liefern.
	 */
	/**
	 * mandatory group PROCESS CONTROL BG-2
	 * contains ProfileID and CustomizationID. 
	 * 
	 * ProfileID identifies what business process a given message is part of, 
	 * and CustomizationID identifies the kind of message and the rules applied.
	 * 
	 * @param customization, not null
	 * @param profile (optional)
	 */
	void setProcessControl(String customization, String profile) {
		ExchangedDocumentContextType exchangedDocumentContext = new ExchangedDocumentContextType();
		DocumentContextParameterType documentContextParameter = new DocumentContextParameterType();
		documentContextParameter.setID(newIDType(customization, null)); // null : No identification scheme

		exchangedDocumentContext.getGuidelineSpecifiedDocumentContextParameter().add(documentContextParameter);
		if(profile==null) {
			// profileIDType ist optional
		} else { 
			DocumentContextParameterType dcp = new DocumentContextParameterType();
			dcp.setID(newIDType(profile, null)); // null : No identification scheme
			exchangedDocumentContext.getBusinessProcessSpecifiedDocumentContextParameter().add(dcp);
		}
		this.setExchangedDocumentContext(exchangedDocumentContext);
	}

	@Override
	public String getCustomization() {
		return getCustomization(this);
	}
	static String getCustomization(CrossIndustryInvoiceType doc) {
		List<DocumentContextParameterType> documentContextParameterList = doc.getExchangedDocumentContext().getGuidelineSpecifiedDocumentContextParameter();
//		LOG.info("documentContextParameterList.size()="+documentContextParameterList.size());
		List<String> res = new ArrayList<String>(documentContextParameterList.size());
		documentContextParameterList.forEach(documentContextParameter -> {
			res.add(documentContextParameter.getID().getValue());
		});
		return res.isEmpty() ? null : res.get(0);
	}
	public String getProfile() {
		return getProfile(this);
	}
	static String getProfile(CrossIndustryInvoiceType doc) {
		List<DocumentContextParameterType> documentContextParameterList = doc.getExchangedDocumentContext().getBusinessProcessSpecifiedDocumentContextParameter();
		List<String> res = new ArrayList<String>(documentContextParameterList.size());
		documentContextParameterList.forEach(documentContextParameter -> {
			res.add(documentContextParameter.getID().getValue());
		});
		return res.isEmpty() ? null : res.get(0);
	}

	/* PRECEDING INVOICE REFERENCE                 BG-3                        0..* (optional)
	 * Eine Gruppe von Informationselementen, die Informationen über eine vorausgegangene Rechnung liefern, 
	 * die berichtigt oder gutgeschrieben werden soll.
	 * 
	 * Anmerkung: Das Informationselement ist zu verwenden, wenn eine vorangegangene Rechnung korrigiert wird, 
	 * eine Abschlussrechnung auf vorangegangene Teilrechnungen Bezug nimmt 
	 * oder eine Abschlussrechnung auf vorangegangene Vorauszahlungsrechnungen Bezug nimmt.

1 .. 1 ApplicableHeaderTradeSettlement Gruppierung von Angaben zur Zahlung und Rechnungsausgleich
0 .. 1 InvoiceReferencedDocument Referenz auf die vorausgegangene Rechnungen BG-3 xs:sequence      <============
1 .. 1 IssuerAssignedID Nummer der vorausgegangenen Rechnung BT-25 
0 .. 1 FormattedIssueDateTime Rechnungsdatum xs:sequence 
1 .. 1 DateTimeString Rechnungsdatum der vorausgegangenen Rechnung BT-26 
       required format Datum, Format BT-26-0
 */
	@Override
	public void setPrecedingInvoiceReference(String docRefId, String ymd) {
		setPrecedingInvoiceReference(docRefId, DateTimeFormats.ymdToTs(ymd));
	}
	@Override
	public void setPrecedingInvoiceReference(String docRefId) {
		setPrecedingInvoiceReference(docRefId, (Timestamp)null);
	}
	// 0..n (optional) BG-3 , BT-25,BT-26,BT-26-0
	@Override
	public void setPrecedingInvoiceReference(String docRefId, Timestamp ts) {
		ReferencedDocumentType referencedDocument = new ReferencedDocumentType();
		referencedDocument.setIssuerAssignedID(newIDType(docRefId, null)); // null : No identification scheme
		FormattedDateTimeType dateTime = newFormattedDateTimeType(ts);
		if(dateTime!=null) referencedDocument.setFormattedIssueDateTime(dateTime);

		applicableHeaderTradeSettlement.get().setInvoiceReferencedDocument(referencedDocument);
	}

	@Override
	public String getPrecedingInvoiceReference() {
		ReferencedDocumentType referencedDocument = applicableHeaderTradeSettlement.get().getInvoiceReferencedDocument();
		return referencedDocument==null ? null : referencedDocument.getLineID().getValue();
	}
	static String getPrecedingInvoiceReference(CrossIndustryInvoiceType doc) {
		ReferencedDocumentType referencedDocument =  doc.getSupplyChainTradeTransaction().getApplicableHeaderTradeSettlement().getInvoiceReferencedDocument();
		return referencedDocument==null ? null : referencedDocument.getLineID().getValue();
	}

	FormattedDateTimeType newFormattedDateTimeType(Timestamp ts) {
		if(ts==null) return null;
		
		FormattedDateTimeType dateTime = new FormattedDateTimeType();
		FormattedDateTimeType.DateTimeString dts = new FormattedDateTimeType.DateTimeString(); // DateTimeString ist inner class in FormattedDateTimeType
		dts.setFormat(DateTimeFormats.CCYYMMDD_QUALIFIER);
		dts.setValue(DateTimeFormats.tsToCCYYMMDD(ts));
		dateTime.setDateTimeString(dts);
		return dateTime;
	}

	/* SELLER                                      BG-4                        1 (mandatory) 
	 * Eine Gruppe von Informationselementen, die Informationen über den Verkäufer liefern.
	 */
	/**
	 * Seller (AccountingSupplierParty)
	 * Seller is mandatory information and provided in cii element SellerTradeParty
	 * 
	 * @param name mandatory BT-27 : The full formal name by which the Seller is registered 
	 *        in the national registry of legal entities or as a Taxable person or otherwise trades as a person or persons.
	 * @param postalAddress mandatory group BG-5/R53 : A group of business terms providing information about the address of the Seller.
              Sufficient components of the address are to be filled to comply with legal requirements.
	 * @param contact mandatory group BG-6/R57 : A group of business terms providing contact information about the Seller.
	 * @param companyId optional / Seller legal registration identifier, BT-30/R52
	 * @param companyLegalForm optional / Seller additional legal information, BT-33/R47
	 */
	public void setSeller(String name, PostalAddress address, IContact contact, String companyId, String companyLegalForm) {
		                               // BT-27 , BG-5   , BG-6          , BT-30    , BT-33
		TradeParty party = new TradeParty(name, address, contact); //, companyId, companyLegalForm);
		party.setCompanyId(companyId);
		party.setCompanyLegalForm(companyLegalForm);
		setSeller(party);
	}
	
	public void setSeller(BusinessParty party) {
		HeaderTradeAgreementType headerTradeAgreement = getApplicableHeaderTradeAgreement();
		headerTradeAgreement.setSellerTradeParty((TradePartyType)party);
		
		SupplyChainTradeTransactionType supplyChainTradeTransaction = this.getSupplyChainTradeTransaction();
		supplyChainTradeTransaction.setApplicableHeaderTradeAgreement(headerTradeAgreement);
		super.setSupplyChainTradeTransaction(supplyChainTradeTransaction);		
	}

	public BG4_Seller getSeller() {
		return getSellerParty(this);
	}
	static TradeParty getSellerParty(CrossIndustryInvoiceType doc) {
		HeaderTradeAgreementType headerTradeAgreement = doc.getSupplyChainTradeTransaction().getApplicableHeaderTradeAgreement();
		TradePartyType sellerParty = headerTradeAgreement.getSellerTradeParty();
		return sellerParty==null ? null : new TradeParty(sellerParty);
	}

	/* BUYER                                       BG-7                        1 (mandatory) 
	 * Eine Gruppe von Informationselementen, die Informationen über den Erwerber liefern.
	 */
	public void setBuyer(String name, PostalAddress address, IContact contact) {
		TradeParty party = new TradeParty(name, address, contact); // BT-44, BG-8, BG-9
		setBuyer(party);
	}
	public void setBuyer(BusinessParty party) {
		HeaderTradeAgreementType headerTradeAgreement = getApplicableHeaderTradeAgreement();
		headerTradeAgreement.setBuyerTradeParty((TradePartyType) party);
		
		SupplyChainTradeTransactionType supplyChainTradeTransaction = this.getSupplyChainTradeTransaction();
		supplyChainTradeTransaction.setApplicableHeaderTradeAgreement(headerTradeAgreement);
		super.setSupplyChainTradeTransaction(supplyChainTradeTransaction);		
	}

	public BG7_Buyer getBuyer() {
		return getBuyerParty(this);
	}
	static TradeParty getBuyerParty(CrossIndustryInvoiceType doc) {
		HeaderTradeAgreementType headerTradeAgreement = doc.getSupplyChainTradeTransaction().getApplicableHeaderTradeAgreement();
		TradePartyType buyerParty = headerTradeAgreement.getBuyerTradeParty();
		return buyerParty==null ? null : new TradeParty(buyerParty);
	}

	/* PAYEE                                       BG-10                       0..1
	 * Eine Gruppe von Informationselementen, die Informationen über den Zahlungsempfänger liefern. 
	 * Die Gruppe wird genutzt, wenn der Zahlungsempfänger nicht mit dem Verkäufer identisch ist.
	 *
   01.02a-INVOICE_uncefact.xml :
        <ram:ApplicableHeaderTradeSettlement>
            <ram:TaxCurrencyCode>EUR</ram:TaxCurrencyCode>
            <ram:InvoiceCurrencyCode>EUR</ram:InvoiceCurrencyCode>
            <ram:PayeeTradeParty>
                <ram:Name>VSB - Verlagsservice Braunschweig GmbH</ram:Name>
            </ram:PayeeTradeParty>
            
   01.14a-INVOICE_uncefact.xml :
           <ram:ApplicableHeaderTradeSettlement>
            <ram:PaymentReference>Deb. 12345 / Fact. 9876543</ram:PaymentReference>
            <ram:TaxCurrencyCode>EUR</ram:TaxCurrencyCode>
            <ram:InvoiceCurrencyCode>EUR</ram:InvoiceCurrencyCode>
            <ram:PayeeTradeParty>
                <ram:ID>74</ram:ID>
                <ram:Name>[Payee name]</ram:Name>
            </ram:PayeeTradeParty>
 */
	public void setPayee(String name, String id, String companyLegalForm) {
		TradeParty party = new TradeParty(name, null, null);
		party.setId(id);
		party.setCompanyLegalForm(companyLegalForm);
		setPayee(party);
	}
	public void setPayee(BusinessParty party) {
		LOG.info("Payee BusinessParty party "+party);
		applicableHeaderTradeSettlement.get().setPayeeTradeParty((TradePartyType) party);
	}

	public BusinessParty getPayee() {
		TradePartyType payeeParty = applicableHeaderTradeSettlement.get().getPayeeTradeParty();
		return payeeParty==null ? null : new TradeParty(payeeParty);
	}

	/* SELLER TAX REPRESENTATIVE PARTY             BG-11                       0..1
	 * Eine Gruppe von Informationselementen, die Informationen über den Steuervertreter des Verkäufers liefern.
	 */
	// BT-62 ++ 1..1 Seller tax representative name
	
	// BT-63 ++ 1..1 Seller tax representative VAT identifier
	public void setTaxRepresentative(String name, PostalAddress address, String taxRegistrationName, String taxRegistrationSchemaID) {
		TradeParty party = new TradeParty(name, address, null);
		party.setTaxRegistrationId(taxRegistrationName, taxRegistrationSchemaID);
		setTaxRepresentative(party);
	}
	public void setTaxRepresentative(BusinessParty party) {
		HeaderTradeAgreementType headerTradeAgreement = getApplicableHeaderTradeAgreement();
		headerTradeAgreement.setSellerTaxRepresentativeTradeParty((TradePartyType) party);
		
		SupplyChainTradeTransactionType supplyChainTradeTransaction = this.getSupplyChainTradeTransaction();
		supplyChainTradeTransaction.setApplicableHeaderTradeAgreement(headerTradeAgreement);
		super.setSupplyChainTradeTransaction(supplyChainTradeTransaction);		
	}

	public BusinessParty getTaxRepresentative() {
		return getTaxRepresentativeParty(this);
	}
	static TradeParty getTaxRepresentativeParty(CrossIndustryInvoiceType doc) {
		HeaderTradeAgreementType headerTradeAgreement = doc.getSupplyChainTradeTransaction().getApplicableHeaderTradeAgreement();
		TradePartyType party = headerTradeAgreement.getSellerTaxRepresentativeTradeParty();
		return party==null ? null : new TradeParty(party);
	}

	/* DELIVERY INFORMATION                        BG-13                       0..1
	 * Eine Gruppe von Informationselementen, die Informationen darüber liefern, 
	 * wo und wann die in Rechnung gestellten Waren und Dienstleistungen geliefert bzw. erbracht werden.
	 */
	/**
	 * DELIVERY INFORMATION
	 * <p>
	 * A group of business terms providing information about where and when the goods and services invoiced are delivered.
	 * <p>
	 * Cardinality: 0..1 (optional)
	 * <br>ID: BG-13
	 * <br>Req.ID: R31, R32, R57
	 */
	@Override
	public void setDelivery(String name, Timestamp ts, PostalAddress address, String locationId, String schemeId) {
		applicableHeaderTradeDelivery = new ApplicableHeaderTradeDelivery(name, ts, address, locationId, schemeId);
		supplyChainTradeTransaction.setApplicableHeaderTradeDelivery(applicableHeaderTradeDelivery);
	}
	
	@Override
	public void setDelivery(BG13_DeliveryInformation delivery) {
		supplyChainTradeTransaction.setApplicableHeaderTradeDelivery((HeaderTradeDeliveryType) delivery);
	}
	
	@Override
	public BG13_DeliveryInformation getDelivery() {
		HeaderTradeDeliveryType headerTradeDelivery = supplyChainTradeTransaction.getApplicableHeaderTradeDelivery();
		if(headerTradeDelivery==null) return null;
		return new ApplicableHeaderTradeDelivery(headerTradeDelivery);
	}

	// BG-14.BT-73 +++ 0..1 Invoicing period start date
	@Override
	public void setStartDate(String ymd) {
		setStartDate(DateTimeFormats.ymdToTs(ymd));
	}

	@Override
	public void setStartDate(Timestamp ts) {
		if(ts==null) return;
		DateTimeType dateTime = newDateTime(ts);
		applicableHeaderTradeSettlement.getBillingSpecifiedPeriod().setStartDateTime(dateTime);
	}

	@Override
	public Timestamp getStartDateAsTimestamp() {
		return getStartDateAsTimestamp(applicableHeaderTradeSettlement);
	}
	static Timestamp getStartDateAsTimestamp(ApplicableHeaderTradeSettlement ahts) {
		SpecifiedPeriodType specifiedPeriod = ahts.getBillingSpecifiedPeriod();
		if(specifiedPeriod==null) return null;
		DateTimeType dateTime = specifiedPeriod.getStartDateTime();
		return dateTime==null ? null : DateTimeFormats.ymdToTs(dateTime.getDateTimeString().getValue());		
	}
	
	// BG-14.BT-74 +++ 0..1 Invoicing period end date
	@Override
	public void setEndDate(String ymd) {
		setEndDate(DateTimeFormats.ymdToTs(ymd));		
	}

	@Override
	public void setEndDate(Timestamp ts) {
		if(ts==null) return;
		DateTimeType dateTime = newDateTime(ts);
		applicableHeaderTradeSettlement.getBillingSpecifiedPeriod().setEndDateTime(dateTime);
	}

	@Override
	public Timestamp getEndDateAsTimestamp() {
		return getEndDateAsTimestamp(applicableHeaderTradeSettlement);
	}
	static Timestamp getEndDateAsTimestamp(ApplicableHeaderTradeSettlement ahts) {
		SpecifiedPeriodType specifiedPeriod = ahts.getBillingSpecifiedPeriod();
		if(specifiedPeriod==null) return null;
		DateTimeType dateTime = specifiedPeriod.getEndDateTime();
		return dateTime==null ? null : DateTimeFormats.ymdToTs(dateTime.getDateTimeString().getValue());
	}

/*

EN16931 sagt: BG-16 0..1 PAYMENT INSTRUCTIONS
	die Kardinalität ist 0..1, wg. BR-DE-1 (1..1 this cius rule makes it mandatory)
	ich implemetierte setXXX so, dass PaymentInstructions null sein darf

 */
	@Override // wg. interface PaymentInstructionsFactory
	public PaymentInstructions createPaymentInstructions(PaymentMeansEnum code, String paymentMeansText) {
		return null; // TODO
//		return ((PaymentMeans)applicableHeaderTradeSettlement).createPaymentInstructions(code, paymentMeansText);
	}

	@Override
	public void setPaymentInstructions(PaymentMeansEnum code, String paymentMeansText, String remittanceInformation
			, CreditTransfer creditTransfer, PaymentCard paymentCard, DirectDebit directDebit) {
		List<CreditTransfer> ctList = new ArrayList<CreditTransfer>();
		if(creditTransfer!=null) ctList.add(creditTransfer);
		setPaymentInstructions(code, paymentMeansText, remittanceInformation, ctList, paymentCard, directDebit);		
	}
	@Override
	public void setPaymentInstructions(PaymentMeansEnum code, String paymentMeansText, String remittanceInformation
			, List<CreditTransfer> creditTransfer, PaymentCard paymentCard, DirectDebit directDebit) {
		// TODO dieser ctor kann BG-14 informationen überschreiben !!!!
		applicableHeaderTradeSettlement = new ApplicableHeaderTradeSettlement(code, paymentMeansText, remittanceInformation, creditTransfer, paymentCard, directDebit);
		setPaymentInstructions(applicableHeaderTradeSettlement);
	}

	public void setPaymentInstructions(PaymentInstructions paymentInstructions) {
		// TODO ?brauche ich es? im ctor: applicableHeaderTradeSettlement = new ...
		// und : supplyChainTradeTransaction.setApplicableHeaderTradeSettlement(applicableHeaderTradeSettlement.get());
	}
	public PaymentInstructions getPaymentInstructions() {
		return applicableHeaderTradeSettlement; // das implementiert PaymentInstructions!
	}

	// BG-22.BT-106 - 1..1/1..1
	@Override
	public Amount getInvoiceLineNetTotal() {
		return new Amount(applicableHeaderTradeSettlement.getTradeSettlementHeaderMonetarySummation().getLineTotalAmount().get(0).getValue());
	}
	// BG-22.BT-109 - 1..1/1..1
	@Override
	public Amount getInvoiceTotalTaxExclusive() {
		return new Amount(applicableHeaderTradeSettlement.getTradeSettlementHeaderMonetarySummation().getTaxBasisTotalAmount().get(0).getValue());
	}
	// BG-22.BT-112 - 1..1/1..1
	@Override
	public Amount getInvoiceTotalTaxInclusive() {
		return new Amount(applicableHeaderTradeSettlement.getTradeSettlementHeaderMonetarySummation().getGrandTotalAmount().get(0).getValue());
	}
	// BG-22.BT-115 - 1..1/1..1
	@Override
	public Amount getDuePayable() {
		return new Amount(applicableHeaderTradeSettlement.getTradeSettlementHeaderMonetarySummation().getDuePayableAmount().get(0).getValue());
	}
	
	/**
	 * mandatory total amounts of the invoice
	 * 
	 * @param lineExtension : Sum of Invoice line net amount
	 * @param taxExclusive : Invoice total amount without VAT
	 * @param taxInclusive : Invoice total amount with VAT
	 * @param payable : Amount due for payment 
	 */
	@Override
	public void setDocumentTotals(Amount lineExtension, Amount taxExclusive, Amount taxInclusive, Amount payable) {
		LOG.info("lineExtension:"+lineExtension + " taxExclusive:"+taxExclusive + " taxInclusive:"+taxInclusive + " payable:"+payable);
		applicableHeaderTradeSettlement.setDocumentTotals(lineExtension, taxExclusive, taxInclusive, payable);
		// zurückschreinem:
		supplyChainTradeTransaction.setApplicableHeaderTradeSettlement(applicableHeaderTradeSettlement.get());
		super.setSupplyChainTradeTransaction(supplyChainTradeTransaction);
	}

	private Amount getInvoiceTax(boolean sameCurrency) {
		List<AmountType> list = applicableHeaderTradeSettlement.getTradeSettlementHeaderMonetarySummation().getTaxTotalAmount();
		if(list.isEmpty()) return null;
		for(int i=0; i<list.size(); i++) {
			if(sameCurrency && (this.getTaxCurrency()==null || this.getDocumentCurrency().equals(this.getTaxCurrency()))) {
//				return new Amount(list.get(i).getValue());
				return list.get(i).getCurrencyID()==null ? new Amount(list.get(i).getValue()) : new Amount(list.get(i).getCurrencyID(), list.get(i).getValue());
			} else if(!sameCurrency && !(this.getTaxCurrency()==null || this.getDocumentCurrency().equals(this.getTaxCurrency()))) {
				return new Amount(list.get(i).getCurrencyID(), list.get(i).getValue());
			}
		}
		return null;
	}
	// BG-22.BT-110/BT-111 - 1..1/0..2
	/* zu BT-111: getTaxCurrency() BT-6 getDocumentCurrency() BT-5
	 * Zu verwenden, wenn der Code für die Währung der Umsatzsteuerbuchung (BT-6) nach Artikel 230 der Richtlinie 2006/112/EG über Umsatzsteuer 
	 * vom Code für die Rechnungswährung (BT-5) abweicht.
	 */
	@Override
	public Amount getInvoiceTax() {
		return getInvoiceTax(true);
	}
	@Override
	public Amount getInvoiceTaxInAccountingCurrency() {
		return getInvoiceTax(false);
	}

	/**
	 * optional total VAT amount of the invoice
	 * 
	 * @param taxTotalAmount : Invoice total VAT amount
	 */
	@Override
	public void setInvoiceTax(Amount taxTotalAmount) {
//		LOG.info("taxTotalAmount "+taxTotalAmount);
		applicableHeaderTradeSettlement.setInvoiceTax(taxTotalAmount);
	}
	
	@Override
	public void setInvoiceTaxInAccountingCurrency(Amount amount) {
		// TODO assert:
		if(this.getDocumentCurrency().equals(this.getTaxCurrency())) {
			LOG.warning("Document currency is "+getDocumentCurrency() + " equals to Tax Currency!");
		}
		setInvoiceTax(amount);	
	}

	@Override
	public void setAllowancesTotal(Amount amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setChargesTotal(Amount amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPaid(Amount amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRounding(Amount amount) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * VAT BREAKDOWN

1 .. 1 SupplyChainTradeTransaction Gruppierung der Informationen zum Geschäftsvorfall
1 .. 1 ApplicableHeaderTradeSettlement Gruppierung von Angaben zur Zahlung und Rechnungsausgleich
0 .. n ApplicableTradeTax Umsatzsteueraufschlüsselung                                                 BG-23 xs:sequence 

       VatBreakdown extends ApplicableTradeTax

	 */
	/**
	 * Adds a mandatory VAT BREAKDOWN element
	 * 
	 * @param vatBreakdown
	 */
	@Override
	public void addVATBreakDown(CoreInvoiceVatBreakdown vatBreakdown) {
		List<TradeTaxType> tradeTaxes = applicableHeaderTradeSettlement.getApplicableTradeTax();
		tradeTaxes.add((VatBreakdown)vatBreakdown);
	}
	public void addVATBreakDown(List<VatBreakdown> vatBreakdowns) {
		List<TradeTaxType> tradeTaxes = applicableHeaderTradeSettlement.getApplicableTradeTax();
		vatBreakdowns.forEach(vbd -> {
			tradeTaxes.add(vbd);
		});	
	}
	public void addVATBreakDown(Amount taxableAmount, Amount tax, TaxCategoryCode taxCategoryCode, BigDecimal taxRate) {
		VatBreakdown vatBreakdown = new VatBreakdown(taxableAmount, tax, taxCategoryCode, taxRate);
		addVATBreakDown(vatBreakdown);
	}
	public List<VatBreakdown> getVATBreakDowns() {
		if(applicableHeaderTradeSettlement==null) return null;
		List<TradeTaxType> list = applicableHeaderTradeSettlement.getApplicableTradeTax();
		List<VatBreakdown> result = new ArrayList<VatBreakdown>(list.size()); // VatBreakdown extends TradeTaxType
		list.forEach(vbd -> {
			result.add(new VatBreakdown(vbd));
		});
		return result;
	}
	
	// BG-24 + 0..n ADDITIONAL SUPPORTING DOCUMENTS
	@Override
	public void addSupportigDocument(String docRefId, String description, byte[] content, String mimeCode, String filename) {
		ReferencedDocumentType referencedDocument = new ReferencedDocumentType();
		referencedDocument.setIssuerAssignedID(CrossIndustryInvoice.newIDType(docRefId, null));
		if(description!=null) {
			referencedDocument.getName().add(CrossIndustryInvoice.newTextType(description));
		}
		
		BinaryObjectType binaryObject = new BinaryObjectType();
		binaryObject.setValue(content);
		binaryObject.setMimeCode(mimeCode);
		binaryObject.setFilename(filename);
		referencedDocument.getAttachmentBinaryObject().add(binaryObject);
				
		HeaderTradeAgreementType applicableHeaderTradeAgreement = getApplicableHeaderTradeAgreement();
		applicableHeaderTradeAgreement.getAdditionalReferencedDocument().add(referencedDocument);
	}

	@Override
	public void addSupportigDocument(String docRefId, String description, String url) {
		ReferencedDocumentType referencedDocument = new ReferencedDocumentType();
		referencedDocument.setIssuerAssignedID(CrossIndustryInvoice.newIDType(docRefId, null));
		if(description!=null) {
			referencedDocument.getName().add(CrossIndustryInvoice.newTextType(description));
		}
		if(url!=null) {
			referencedDocument.setURIID(CrossIndustryInvoice.newIDType(url, null));
		}
				
		HeaderTradeAgreementType applicableHeaderTradeAgreement = getApplicableHeaderTradeAgreement();
		applicableHeaderTradeAgreement.getAdditionalReferencedDocument().add(referencedDocument);
	}

	@Override
	public List<BG24_AdditionalSupportingDocs> getAdditionalSupportingDocuments() {
		LOG.warning(NOT_IMPEMENTED); // TODO
		return null;
	}

	/* INVOICE LINE                                BG-25                       1..* (mandatory)
	 * Eine Gruppe von Informationselementen, die Informationen über einzelne Rechnungspositionen liefern.
	 * 
	 */
	
	/**
	 * Adds a mandatory invoice line element
	 * 
	 * @param line
	 */
	@Override
	public void addLine(CoreInvoiceLine line) {
//		LOG.info("CoreInvoiceLine line:"+line + " - Class:"+line.getClass() + " - TaxCategory:"+line.getTaxCategory());
		supplyChainTradeTransaction.getIncludedSupplyChainTradeLineItem().add((TradeLineItem)line);
	}
	
	public void addLine(SupplyChainTradeLineItemType line) {
		supplyChainTradeTransaction.getIncludedSupplyChainTradeLineItem().add(line);
		super.setSupplyChainTradeTransaction(supplyChainTradeTransaction);
	}

	public void addLines(CrossIndustryInvoiceType doc) {
		List<TradeLineItem> tradeLineItemList = getLines(doc);
		tradeLineItemList.forEach(line -> {
			CoreInvoiceLine invoiceLine = new TradeLineItem(line); // TradeLineItem implements CoreInvoiceLine
			addLine(invoiceLine);
		});
	}

	public List<TradeLineItem> getLines() {
		List<SupplyChainTradeLineItemType> lines = supplyChainTradeTransaction.getIncludedSupplyChainTradeLineItem();
		List<TradeLineItem> resultLines = new ArrayList<TradeLineItem>(lines.size());
		lines.forEach(line -> {
			resultLines.add(new TradeLineItem(line));
		});
		return resultLines;
	}
	static List<TradeLineItem> getLines(CrossIndustryInvoiceType doc) {
		List<SupplyChainTradeLineItemType> lines = doc.getSupplyChainTradeTransaction().getIncludedSupplyChainTradeLineItem();
		List<TradeLineItem> resultLines = new ArrayList<TradeLineItem>(lines.size());
		lines.forEach(line -> {
			resultLines.add(new TradeLineItem(line));
		});
		return resultLines;
	}

// -----------------------------------------------------------
	
	private HeaderTradeAgreementType getApplicableHeaderTradeAgreement() {
		return getApplicableHeaderTradeAgreement(this.getSupplyChainTradeTransaction());
	}
	private HeaderTradeAgreementType getApplicableHeaderTradeAgreement(SupplyChainTradeTransactionType supplyChainTradeTransaction) {
		HeaderTradeAgreementType headerTradeAgreement = supplyChainTradeTransaction.getApplicableHeaderTradeAgreement();
		if(headerTradeAgreement==null) {
			headerTradeAgreement = new HeaderTradeAgreementType();
			LOG.info("new HeaderTradeAgreementType:"+headerTradeAgreement);
		}
		return headerTradeAgreement;
	}	

	// ----------------- gemeinsam mit TradeParty
	static IDType newIDType(IBANId iban) {
		return newIDType(iban.getValue(), iban.getSchemeID());
	}
	
	static IDType newIDType(BICId bic) {
		return newIDType(bic.getValue(), bic.getSchemeID());
	}
	
	static IDType newIDType(String value, String schemeID) {
		IDType ID = new IDType();
		ID.setValue(value);
		ID.setSchemeID(schemeID);
		return ID;
	}

	static TextType newTextType(String value) {
		TextType text = new TextType();
		text.setValue(value);
		return text;
	}

	// ----------------- 
	@Override
	public BusinessParty createParty(String name, PostalAddress address, IContact contact) {
		return new TradeParty(name, address, contact); 
	}

	@Override
	public BusinessParty createParty(BusinessParty party) {
		return new TradeParty((TradePartyType)party); 
	}

	@Override
	public PostalAddress createAddress(String countryCode, String postalCode, String city) {
		TradeParty party = new TradeParty();
		return party.createAddress(countryCode, postalCode, city);
	}

	@Override
	public PostalAddress createAddress(PostalAddress address) {
		TradeParty party = new TradeParty();
		return party.createAddress(address);
	}

	@Override
	public IContact createContact(String contactName, String contactTel, String contactMail) {
		TradeParty party = new TradeParty();
		return party.createContact(contactName, contactTel, contactMail);
	}

	@Override
	public IContact createContact(IContact contact) {
		TradeParty party = new TradeParty();
		return party.createContact(contact);
	}

}
