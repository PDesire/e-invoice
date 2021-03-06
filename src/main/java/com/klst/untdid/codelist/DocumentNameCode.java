package com.klst.untdid.codelist;

import java.util.HashMap;
import java.util.Map;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CreditNoteTypeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.InvoiceTypeCodeType;
import un.unece.uncefact.data.standard.qualifieddatatype._100.DocumentCodeType;

/* urn:xoev-de:kosit:codeliste:untdid.1001
 * United Nations Trade Data Interchange Directory (UNTDID), http://www.unece.org/fileadmin/DAM/trade/untdid/d16b/tred/tredi2.htm
 * UN/EDIFACT 1001  Document name code

     326   Partial invoice
              Document/message specifying details of an incomplete invoice.
     380   Commercial invoice
              (1334) Document/message claiming payment for goods or services supplied under conditions agreed between seller and buyer.
     384   Corrected invoice
              Commercial invoice that includes revised information differing from an earlier submission of the same invoice.
     389   Self-billed invoice
              An invoice the invoicee is producing instead of the seller.
     381   Credit note
              (1113) Document/message for providing credit information to the relevant party.

 */
public enum DocumentNameCode {
	
	PartialInvoice 			(326),
	CommercialInvoice		(380),
	CorrectedInvoice 		(384),
	SelfbilledInvoice		(389),
	CreditNote 				(381);
	
	/**
	 * @see <A HREF="http://www.unece.org/trade/untdid/d13b/tred/tred1001.htm">UN/EDIFACT 1001</A>
	 */
	public static final String SCHEME_ID  = "UN/EDIFACT 1001";

	DocumentNameCode(int value) {
		this.value = value;
	}
	
	private final int value;
	
	int getValue() {
		return value;
	}

	public String getValueAsString() {
		return ""+value;
	}

    private static Map<Integer, DocumentNameCode> map = new HashMap<Integer, DocumentNameCode>();
    static {
        for (DocumentNameCode documentNameCode : DocumentNameCode.values()) {
            map.put(documentNameCode.value, documentNameCode);
        }
    }
    
    public static DocumentNameCode valueOf(int code) {
        return map.get(code);
    }

    public static DocumentNameCode valueOf(InvoiceTypeCodeType ublCode) {
    	int code = Integer.parseInt(ublCode.getValue());
        return valueOf(code);
    }

    public static DocumentNameCode valueOf(CreditNoteTypeCodeType ublCode) {
    	int code = Integer.parseInt(ublCode.getValue());
        return valueOf(code);
    }

    public static DocumentNameCode valueOf(DocumentCodeType ciiCode) {
    	int code = Integer.parseInt(ciiCode.getValue());
        return valueOf(code);
    }

}
