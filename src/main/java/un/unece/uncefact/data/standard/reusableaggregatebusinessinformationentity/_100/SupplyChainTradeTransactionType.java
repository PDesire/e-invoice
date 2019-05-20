//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2019.05.13 um 09:58:17 AM CEST 
//


package un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r SupplyChainTradeTransactionType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="SupplyChainTradeTransactionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IncludedSupplyChainTradeLineItem" type="{urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100}SupplyChainTradeLineItemType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ApplicableHeaderTradeAgreement" type="{urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100}HeaderTradeAgreementType"/>
 *         &lt;element name="ApplicableHeaderTradeDelivery" type="{urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100}HeaderTradeDeliveryType"/>
 *         &lt;element name="ApplicableHeaderTradeSettlement" type="{urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100}HeaderTradeSettlementType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupplyChainTradeTransactionType", propOrder = {
    "includedSupplyChainTradeLineItem",
    "applicableHeaderTradeAgreement",
    "applicableHeaderTradeDelivery",
    "applicableHeaderTradeSettlement"
})
public class SupplyChainTradeTransactionType {

    @XmlElement(name = "IncludedSupplyChainTradeLineItem")
    protected List<SupplyChainTradeLineItemType> includedSupplyChainTradeLineItem;
    @XmlElement(name = "ApplicableHeaderTradeAgreement", required = true)
    protected HeaderTradeAgreementType applicableHeaderTradeAgreement;
    @XmlElement(name = "ApplicableHeaderTradeDelivery", required = true)
    protected HeaderTradeDeliveryType applicableHeaderTradeDelivery;
    @XmlElement(name = "ApplicableHeaderTradeSettlement", required = true)
    protected HeaderTradeSettlementType applicableHeaderTradeSettlement;

    /**
     * Gets the value of the includedSupplyChainTradeLineItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the includedSupplyChainTradeLineItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncludedSupplyChainTradeLineItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupplyChainTradeLineItemType }
     * 
     * 
     */
    public List<SupplyChainTradeLineItemType> getIncludedSupplyChainTradeLineItem() {
        if (includedSupplyChainTradeLineItem == null) {
            includedSupplyChainTradeLineItem = new ArrayList<SupplyChainTradeLineItemType>();
        }
        return this.includedSupplyChainTradeLineItem;
    }

    /**
     * Ruft den Wert der applicableHeaderTradeAgreement-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link HeaderTradeAgreementType }
     *     
     */
    public HeaderTradeAgreementType getApplicableHeaderTradeAgreement() {
        return applicableHeaderTradeAgreement;
    }

    /**
     * Legt den Wert der applicableHeaderTradeAgreement-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link HeaderTradeAgreementType }
     *     
     */
    public void setApplicableHeaderTradeAgreement(HeaderTradeAgreementType value) {
        this.applicableHeaderTradeAgreement = value;
    }

    /**
     * Ruft den Wert der applicableHeaderTradeDelivery-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link HeaderTradeDeliveryType }
     *     
     */
    public HeaderTradeDeliveryType getApplicableHeaderTradeDelivery() {
        return applicableHeaderTradeDelivery;
    }

    /**
     * Legt den Wert der applicableHeaderTradeDelivery-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link HeaderTradeDeliveryType }
     *     
     */
    public void setApplicableHeaderTradeDelivery(HeaderTradeDeliveryType value) {
        this.applicableHeaderTradeDelivery = value;
    }

    /**
     * Ruft den Wert der applicableHeaderTradeSettlement-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link HeaderTradeSettlementType }
     *     
     */
    public HeaderTradeSettlementType getApplicableHeaderTradeSettlement() {
        return applicableHeaderTradeSettlement;
    }

    /**
     * Legt den Wert der applicableHeaderTradeSettlement-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link HeaderTradeSettlementType }
     *     
     */
    public void setApplicableHeaderTradeSettlement(HeaderTradeSettlementType value) {
        this.applicableHeaderTradeSettlement = value;
    }

}