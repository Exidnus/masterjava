
package ru.javaops.masterjava.xml.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="id_participant" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Group_member", namespace = "http://javaops.ru")
public class GroupMember {

    @XmlAttribute(name = "id_participant", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object idParticipant;

    /**
     * Gets the value of the idParticipant property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getIdParticipant() {
        return idParticipant;
    }

    /**
     * Sets the value of the idParticipant property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setIdParticipant(Object value) {
        this.idParticipant = value;
    }

}
