//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2019.05.13 um 09:58:17 AM CEST 
//


package un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import un.unece.uncefact.data.standard.qualifieddatatype._100.CommunicationChannelCodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._100.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._100.TextType;


/**
 * <p>Java-Klasse f�r UniversalCommunicationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="UniversalCommunicationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="URIID" type="{urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100}IDType" minOccurs="0"/>
 *         &lt;element name="ChannelCode" type="{urn:un:unece:uncefact:data:standard:QualifiedDataType:100}CommunicationChannelCodeType" minOccurs="0"/>
 *         &lt;element name="CompleteNumber" type="{urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100}TextType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UniversalCommunicationType", propOrder = {
    "uriid",
    "channelCode",
    "completeNumber"
})
public class UniversalCommunicationType {

    @XmlElement(name = "URIID")
    protected IDType uriid;
    @XmlElement(name = "ChannelCode")
    protected CommunicationChannelCodeType channelCode;
    @XmlElement(name = "CompleteNumber")
    protected TextType completeNumber;

    /**
     * Ruft den Wert der uriid-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IDType }
     *     
     */
    public IDType getURIID() {
        return uriid;
    }

    /**
     * Legt den Wert der uriid-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IDType }
     *     
     */
    public void setURIID(IDType value) {
        this.uriid = value;
    }

    /**
     * Ruft den Wert der channelCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CommunicationChannelCodeType }
     *     
     */
    public CommunicationChannelCodeType getChannelCode() {
        return channelCode;
    }

    /**
     * Legt den Wert der channelCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CommunicationChannelCodeType }
     *     
     */
    public void setChannelCode(CommunicationChannelCodeType value) {
        this.channelCode = value;
    }

    /**
     * Ruft den Wert der completeNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TextType }
     *     
     */
    public TextType getCompleteNumber() {
        return completeNumber;
    }

    /**
     * Legt den Wert der completeNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TextType }
     *     
     */
    public void setCompleteNumber(TextType value) {
        this.completeNumber = value;
    }

}
