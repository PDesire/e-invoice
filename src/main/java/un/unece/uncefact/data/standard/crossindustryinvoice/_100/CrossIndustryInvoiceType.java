//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2019.05.13 um 09:58:17 AM CEST 
//


package un.unece.uncefact.data.standard.crossindustryinvoice._100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.ExchangedDocumentContextType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.ExchangedDocumentType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.SupplyChainTradeTransactionType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100.ValuationBreakdownStatementType;


/**
 * <p>Java-Klasse f�r CrossIndustryInvoiceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CrossIndustryInvoiceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ExchangedDocumentContext" type="{urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100}ExchangedDocumentContextType"/>
 *         &lt;element name="ExchangedDocument" type="{urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100}ExchangedDocumentType"/>
 *         &lt;element name="SupplyChainTradeTransaction" type="{urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100}SupplyChainTradeTransactionType"/>
 *         &lt;element name="ValuationBreakdownStatement" type="{urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100}ValuationBreakdownStatementType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CrossIndustryInvoiceType", propOrder = {
    "exchangedDocumentContext",
    "exchangedDocument",
    "supplyChainTradeTransaction",
    "valuationBreakdownStatement"
})
public class CrossIndustryInvoiceType {

    @XmlElement(name = "ExchangedDocumentContext", required = true)
    protected ExchangedDocumentContextType exchangedDocumentContext;
    @XmlElement(name = "ExchangedDocument", required = true)
    protected ExchangedDocumentType exchangedDocument;
    @XmlElement(name = "SupplyChainTradeTransaction", required = true)
    protected SupplyChainTradeTransactionType supplyChainTradeTransaction;
    @XmlElement(name = "ValuationBreakdownStatement")
    protected ValuationBreakdownStatementType valuationBreakdownStatement;

    /**
     * Ruft den Wert der exchangedDocumentContext-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExchangedDocumentContextType }
     *     
     */
    public ExchangedDocumentContextType getExchangedDocumentContext() {
        return exchangedDocumentContext;
    }

    /**
     * Legt den Wert der exchangedDocumentContext-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExchangedDocumentContextType }
     *     
     */
    public void setExchangedDocumentContext(ExchangedDocumentContextType value) {
        this.exchangedDocumentContext = value;
    }

    /**
     * Ruft den Wert der exchangedDocument-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExchangedDocumentType }
     *     
     */
    public ExchangedDocumentType getExchangedDocument() {
        return exchangedDocument;
    }

    /**
     * Legt den Wert der exchangedDocument-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExchangedDocumentType }
     *     
     */
    public void setExchangedDocument(ExchangedDocumentType value) {
        this.exchangedDocument = value;
    }

    /**
     * Ruft den Wert der supplyChainTradeTransaction-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SupplyChainTradeTransactionType }
     *     
     */
    public SupplyChainTradeTransactionType getSupplyChainTradeTransaction() {
        return supplyChainTradeTransaction;
    }

    /**
     * Legt den Wert der supplyChainTradeTransaction-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SupplyChainTradeTransactionType }
     *     
     */
    public void setSupplyChainTradeTransaction(SupplyChainTradeTransactionType value) {
        this.supplyChainTradeTransaction = value;
    }

    /**
     * Ruft den Wert der valuationBreakdownStatement-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ValuationBreakdownStatementType }
     *     
     */
    public ValuationBreakdownStatementType getValuationBreakdownStatement() {
        return valuationBreakdownStatement;
    }

    /**
     * Legt den Wert der valuationBreakdownStatement-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ValuationBreakdownStatementType }
     *     
     */
    public void setValuationBreakdownStatement(ValuationBreakdownStatementType value) {
        this.valuationBreakdownStatement = value;
    }

}
