package com.klst.einvoice.unece.uncefact;

import java.util.List;
import java.util.logging.Logger;

import com.klst.einvoice.CreditTransfer;
import com.klst.einvoice.DirectDebit;
import com.klst.einvoice.PaymentInstructions;
import com.klst.untdid.codelist.PaymentMeansEnum;

import un.unece.uncefact.data.standard.qualifieddatatype._100.PaymentMeansCodeType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.CreditorFinancialAccountType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.CreditorFinancialInstitutionType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.TradeSettlementPaymentMeansType;
import un.unece.uncefact.data.standard.unqualifieddatatype._100.TextType;

/* wg BT-83 ist PaymentInstructions in ApplicableHeaderTradeSettlement implementiert

Bsp: 01.15a:
        <ram:ApplicableHeaderTradeSettlement>
            <ram:PaymentReference>0000123456</ram:PaymentReference>
            ...
            <ram:SpecifiedTradeSettlementPaymentMeans>
                <ram:TypeCode>30</ram:TypeCode>                                        <!-- BT-81
                <ram:PayeePartyCreditorFinancialAccount>                               <!-- BG-17 0..n CREDIT TRANSFER
                    <ram:IBANID>DE12123000001234567890</ram:IBANID>
                    <ram:AccountName>[Payment account name]</ram:AccountName>
                </ram:PayeePartyCreditorFinancialAccount>
                <ram:PayeeSpecifiedCreditorFinancialInstitution>
                    <ram:BICID>[BIC]</ram:BICID>
                </ram:PayeeSpecifiedCreditorFinancialInstitution>                                   bis hierhin -->
            </ram:SpecifiedTradeSettlementPaymentMeans>

SpecifiedTradeSettlementPaymentMeans Zahlungsanweisungen umbenannt in TradeSettlementPaymentMeans
Datentyp: ram:TradeSettlementPaymentMeansType

1 .. 1 ApplicableHeaderTradeSettlement Gruppierung von Angaben zur Zahlung und Rechnungsausgleich
0 .. 1 PaymentReference Verwendungszweck                                        BT-83 

0 .. n SpecifiedTradeSettlementPaymentMeans Zahlungsanweisungen                 BG-16 xs:sequence 
  1..1 TypeCode Code für die Zahlungsart                                        BT-81 
  0..1 Information Text zur Zahlungsart                                         BT-82 

  0..1 ApplicableTradeSettlementFinancialCard Informationen zur Zahlungskarte   BG-18 xs:sequence 
    1..1 ID Zahlungskartennummer                                                  BT-87 
    0..1 CardholderName Name des Zahlungskarteninhabers                           BT-88 

  0..1 PayerPartyDebtorFinancialAccount Bankinstitut des Käufers xs:sequence 
    1..1 IBANID Lastschriftverfahren: Kennung des zu belastenden Kontos           BG-19/ BT-91 

  0..1 PayeePartyCreditorFinancialAccount Überweisung                           BG-17 xs:sequence 
    0..1 IBANID Kennung des Zahlungskontos                                        BT-84 
    0..1 AccountName Name des Zahlungskontos                                      BT-85 
    0..1 ProprietaryID Nationale Kontonummer (nicht für SEPA)                     BT-84-0 

  0..1 PayeeSpecifiedCreditorFinancialInstitution Bankinstitut des Verkäufers xs:sequence 
    1..1 BICID Kennung des Zahlungsdienstleisters                                 BT-86


 */   
@ Deprecated
public class TradeSettlementPaymentMeans extends TradeSettlementPaymentMeansType implements PaymentInstructions, CreditTransfer
// PaymentInstructions IF wird ohne BT-83 implementiert
{
	private static final Logger LOG = Logger.getLogger(TradeSettlementPaymentMeans.class.getName());

	TradeSettlementPaymentMeans() {
		super();
	}

	// copy ctor
	public TradeSettlementPaymentMeans(TradeSettlementPaymentMeansType tspm) {
		this();
		PaymentMeansEnum pmc = tspm.getTypeCode()==null ? null : PaymentMeansEnum.valueOf(tspm.getTypeCode());
		String paymentMeansText = tspm.getInformation().isEmpty() ? null : tspm.getInformation().get(0).getValue();
//		LOG.info("pmc:"+pmc + ", paymentMeansText:"+paymentMeansText);
		init(pmc, paymentMeansText);
		
		CreditorFinancialAccountType payeePartyCreditorFinancialAccount = tspm.getPayeePartyCreditorFinancialAccount();
		IBANId iban = null;
		String accountId = null;
		String accountName = null;
		if(payeePartyCreditorFinancialAccount==null) {
			// beide nicht gesetzt
		} else {
			iban = payeePartyCreditorFinancialAccount.getIBANID()==null ? null : new IBANId(payeePartyCreditorFinancialAccount.getIBANID().getValue());
			accountId = payeePartyCreditorFinancialAccount.getProprietaryID()==null ? null : payeePartyCreditorFinancialAccount.getProprietaryID().getValue();
			accountName = payeePartyCreditorFinancialAccount.getAccountName()==null ? null : payeePartyCreditorFinancialAccount.getAccountName().getValue();
		}
		
		CreditorFinancialInstitutionType creditorFinancialInstitution = tspm.getPayeeSpecifiedCreditorFinancialInstitution();
		BICId bic = null;
		if(creditorFinancialInstitution==null) {
			// optional
		} else {
			bic = creditorFinancialInstitution.getBICID()==null ? null : new BICId(creditorFinancialInstitution.getBICID().getValue());			
		}
		
		LOG.info("iban:"+iban + ", AccountName:"+accountName + ", bic:"+bic + ", accountId:"+accountId);
		this.setPaymentAccountID(iban);
		this.setPaymentAccountName(accountName);
		this.setPaymentServiceProviderID(bic);
	}
	
	public TradeSettlementPaymentMeans(PaymentMeansEnum code, String paymentMeansText) {
		this();
		init(code, paymentMeansText);
	}
	
	void init(PaymentMeansEnum code, String paymentMeansText) {
		LOG.info("code:"+code + ", paymentMeansText:"+paymentMeansText);
		if(code==null) return;
		PaymentMeansCodeType pmc = new PaymentMeansCodeType(); // BT-81
		pmc.setValue(code.getValueAsString());
		super.setTypeCode(pmc);
		
		if(paymentMeansText!=null) {
			super.getInformation().add(CrossIndustryInvoice.newTextType(paymentMeansText));
		}
	}
	
//	// SEPA ctor
//	public TradeSettlementPaymentMeans(IBANId iban) {
//		this();
//		setPaymentAccountID(iban);
//	}
//	// non SEPA ctor
//	public TradeSettlementPaymentMeans(String accountId, String accountName, BICId bic) {
//		this();
//		setPaymentAccountID(accountId);
//		setPaymentAccountName(accountName);
//		setPaymentServiceProviderID(bic);
//	}
	
	CreditorFinancialAccountType getCreditorFinancialAccountType() {
		return super.getPayeePartyCreditorFinancialAccount()==null ? new CreditorFinancialAccountType() : super.getPayeePartyCreditorFinancialAccount();
	}
	
	CreditorFinancialInstitutionType getCreditorFinancialInstitutionType() {
		return super.getPayeeSpecifiedCreditorFinancialInstitution()==null ? new CreditorFinancialInstitutionType() : super.getPayeeSpecifiedCreditorFinancialInstitution();
	}
	
	@Override // wg. implements interface CreditTransfer
	public String getPaymentAccountID() { // IBAN (in case of a SEPA payment) or a national account number.
		CreditorFinancialAccountType payeePartyCreditorFinancialAccount = super.getPayeePartyCreditorFinancialAccount();
		if(payeePartyCreditorFinancialAccount==null) return null;
		return payeePartyCreditorFinancialAccount.getIBANID()==null ? 
			( payeePartyCreditorFinancialAccount.getProprietaryID()==null ? null : payeePartyCreditorFinancialAccount.getProprietaryID().getValue())
			: payeePartyCreditorFinancialAccount.getIBANID().getValue();
	}

	@Override // wg. implements interface CreditTransfer
	public void setPaymentAccountID(IBANId iban) { // use ctor
		CreditorFinancialAccountType payeePartyCreditorFinancialAccount = getCreditorFinancialAccountType();
		payeePartyCreditorFinancialAccount.setIBANID(CrossIndustryInvoice.newIDType(iban));		
		super.setPayeePartyCreditorFinancialAccount(payeePartyCreditorFinancialAccount);
	}
	@Override // wg. implements interface CreditTransfer
	public void setPaymentAccountID(String accountId) {
		CreditorFinancialAccountType payeePartyCreditorFinancialAccount = getCreditorFinancialAccountType();
		payeePartyCreditorFinancialAccount.setProprietaryID(CrossIndustryInvoice.newIDType(accountId, null));		
		super.setPayeePartyCreditorFinancialAccount(payeePartyCreditorFinancialAccount);
	}

	@Override // wg. implements interface CreditTransfer
	public String getPaymentAccountName() {
		CreditorFinancialAccountType payeePartyCreditorFinancialAccount = super.getPayeePartyCreditorFinancialAccount();
		if(payeePartyCreditorFinancialAccount==null) return null;
		return payeePartyCreditorFinancialAccount.getAccountName()==null ? null : payeePartyCreditorFinancialAccount.getAccountName().getValue();
	}

	@Override // wg. implements interface CreditTransfer
	public void setPaymentAccountName(String name) {
		LOG.info(", AccountName:"+name);
		CreditorFinancialAccountType payeePartyCreditorFinancialAccount = getCreditorFinancialAccountType();
		payeePartyCreditorFinancialAccount.setAccountName(CrossIndustryInvoice.newTextType(name));		
		super.setPayeePartyCreditorFinancialAccount(payeePartyCreditorFinancialAccount);
	}

	@Override // wg. implements interface CreditTransfer
	public String getPaymentServiceProviderID() {
		CreditorFinancialInstitutionType payeeSpecifiedCreditorFinancialInstitution = super.getPayeeSpecifiedCreditorFinancialInstitution();
		if(payeeSpecifiedCreditorFinancialInstitution==null) return null;
		return payeeSpecifiedCreditorFinancialInstitution.getBICID()==null ? null : payeeSpecifiedCreditorFinancialInstitution.getBICID().getValue();
	}

	@Override // wg. implements interface CreditTransfer
	public void setPaymentServiceProviderID(BICId bic) {
		LOG.info(", bic:"+bic);
		if(bic==null) return;
		CreditorFinancialInstitutionType payeeSpecifiedCreditorFinancialInstitution = getCreditorFinancialInstitutionType();
		payeeSpecifiedCreditorFinancialInstitution.setBICID(CrossIndustryInvoice.newIDType(bic));
		super.setPayeeSpecifiedCreditorFinancialInstitution(payeeSpecifiedCreditorFinancialInstitution);
	}
	@Override  // wg. implements interface CreditTransfer
	public void setPaymentServiceProviderID(String id) {
		if(id==null) return;
		CreditorFinancialInstitutionType payeeSpecifiedCreditorFinancialInstitution = getCreditorFinancialInstitutionType();
		payeeSpecifiedCreditorFinancialInstitution.setBICID(CrossIndustryInvoice.newIDType(id, null));	
	}

//	wg. implements PaymentInstructions : (ABER an RemittanceInformation komme ich nicht dran!) ---------------------------- :
	@Override
	public PaymentMeansEnum getPaymentMeansEnum() {
		return PaymentMeansEnum.valueOf(super.getTypeCode());
	}

	@Override
	public void setPaymentMeansEnum(PaymentMeansEnum code) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPaymentMeansText() {
		List<TextType> list = super.getInformation();
		return list.isEmpty() ? null : list.get(0).getValue();
	}

	@Override
	public void setPaymentMeans(PaymentMeansEnum code, String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRemittanceInformation(String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getRemittanceInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO , PAYMENT CARD INFORMATION, DIRECT DEBIT

	@Override
	public void addCreditTransfer(CreditTransfer creditTransfer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<CreditTransfer> getCreditTransfer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDirectDebit(DirectDebit directDebit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DirectDebit getDirectDebit() {
		// TODO Auto-generated method stub
		return null;
	}

}
