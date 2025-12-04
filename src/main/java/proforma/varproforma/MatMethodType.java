package proforma.varproforma;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import proforma.varproforma.util.MatMethodProvider;
import proforma.varproforma.util.MatMethodProviderArithmeticOperation;
import proforma.varproforma.util.MatMethodProviderMustache;
import proforma.varproforma.util.MatMethodProviderSetVpValue;

@XmlType(name = "mat-method-type-type")
@XmlEnum
public enum MatMethodType {

    
    @XmlEnumValue("set-vp-value")
    SET_VP_VALUE("set-vp-value", null, MatMethodProviderSetVpValue.class),
//    @XmlEnumValue("map-vp-value")
//    MAP_VP_VALUE("map-vp-value", null, MatMethodProviderMapVpValue.class),
    @XmlEnumValue("arithmetic-operation")
    ARITHMETIC_OPERATION("arithmetic-operation", VpT.DOUBLE, MatMethodProviderArithmeticOperation.class),
    @XmlEnumValue("mustache")
    MUSTACHE("mustache", VpT.STRING, MatMethodProviderMustache.class),
    @XmlEnumValue("other")
    OTHER("other", null, null);
    
    
    private final String value;
    private final VpT dataType;
    private Class<? extends MatMethodProvider<?>> providerClass;
    
    MatMethodType(String v, VpT dataType, Class<? extends MatMethodProvider<?>> providerClass) {
        value = v;
        this.dataType= dataType;
        this.providerClass= providerClass;
    }

    public String value() {
        return value;
    }

    public static MatMethodType fromValue(String v) {
        for (MatMethodType c: MatMethodType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public VpT getDataType() {
        return dataType;
    }
    
    public Class<? extends MatMethodProvider<?>> getProviderClass() {
        return providerClass;
    }
}
