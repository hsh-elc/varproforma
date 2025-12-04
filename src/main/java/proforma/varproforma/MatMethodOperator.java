package proforma.varproforma;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "mat-method-operator-type")
@XmlEnum
public enum MatMethodOperator {

    @XmlEnumValue("add")
    ADD("add"),
    @XmlEnumValue("sub")
    SUB("sub"),
    @XmlEnumValue("mul")
    MUL("mul"),
    @XmlEnumValue("div")
    DIV("div");
    
    
    private final String value;
    
    MatMethodOperator(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MatMethodOperator fromValue(String v) {
        for (MatMethodOperator c: MatMethodOperator.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    
}
