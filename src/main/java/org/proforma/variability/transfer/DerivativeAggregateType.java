package org.proforma.variability.transfer;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name="derivative-aggregate-type-type")
public enum DerivativeAggregateType {
    @XmlEnumValue("value")VALUE,
    @XmlEnumValue("collection")COLLECTION,
    @XmlEnumValue("range")RANGE;
    
}