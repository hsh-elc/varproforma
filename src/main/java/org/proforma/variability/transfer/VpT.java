package org.proforma.variability.transfer;

import java.lang.reflect.Constructor;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name="vpt-type")
public enum VpT {

	@XmlEnumValue("integer")INTEGER(Integer.class, Integer.class, Vi.class, "integer", true, true),
	@XmlEnumValue("character")CHARACTER(Character.class, Character.class, Vc.class, "character", true, false),
	@XmlEnumValue("double")DOUBLE(Double.class, Double.class, Vd.class, "double", true, true),
	@XmlEnumValue("string")STRING(String.class, String.class, Vs.class, "string", false, false),
	@XmlEnumValue("boolean")BOOLEAN(Boolean.class, Boolean.class, Vb.class, "boolean", false, false),
	@XmlEnumValue("table")TABLE(VarSpecNodeTable.class, CVList.class, Vt.class, "table", false, false);
	
	VpT(Class<?> associatedSpecType, Class<?> associatedValueType, Class<? extends V> representedVSubclass, String displayString, boolean isIntervalScaled, boolean isNumberType) {
        this.associatedSpecType= associatedSpecType;
        this.associatedValueType= associatedValueType;
		this.representedVSubclass= representedVSubclass;
		this.displayString= displayString;
		try {
            this.xmlValue= getDeclaringClass()
                      .getField(name())
                      .getAnnotation(XmlEnumValue.class).value();
        } catch (NoSuchFieldException | SecurityException e) {
            // should never occur
            throw new AssertionError(e);
        }
		this.isIntervalScaled= isIntervalScaled;
		this.isNumberType= isNumberType;
	}
    
    public static VpT fromXmlValue(String xml) {
        for (VpT elem : values()) {
            if (elem.xmlValue().equals(xml)) return elem;
        }
        throw new IllegalArgumentException("Unknown xml representation '"+xml+"' for VpT");
    }
    
    public static VpT fromV(V v) {
        for (VpT elem : values()) {
            if (v.getClass().equals(elem.representedVSubclass)) return elem;
        }
        throw new IllegalArgumentException("Unknown variant type '"+v.getClass()+"' for VpT");
    }
    
    public static VpT fromSpec(Object o) {
        for (VpT elem : values()) {
            if (o.getClass().equals(elem.associatedSpecType)) return elem;
        }
        throw new IllegalArgumentException("Unknown spec type '"+o.getClass()+"' for VpT");
    }

    public static VpT fromValue(Object o) {
        for (VpT elem : values()) {
            if (o.getClass().equals(elem.associatedValueType)) return elem;
        }
        throw new IllegalArgumentException("Unknown value type '"+o.getClass()+"' for VpT");
    }

    @XmlTransient
    private Class<?> associatedSpecType;

    @XmlTransient
    private Class<?> associatedValueType;

	@XmlTransient
	private Class<? extends V>	representedVSubclass;
	
    @XmlTransient
    private String displayString;
    
    @XmlTransient
    private String xmlValue;
    
	@XmlTransient
	private boolean isIntervalScaled;

	@XmlTransient
	private boolean isNumberType;
	
    public Class<?> getAssociatedSpecType() {
        return associatedSpecType;
    }

    public Class<?> getAssociatedValueType() {
        return associatedValueType;
    }

    public Class<? extends V> getRepresentedVSubclass() {
        return representedVSubclass;
    }
    
	public boolean represents(Class<? extends V> clazz) {
		return representedVSubclass.equals(clazz);
	}

    public String displayString() {
        return displayString;
    }
    
    public String xmlValue() {
        return xmlValue;
    }
    
	public boolean isIntervalScaled() {
		return isIntervalScaled;
	}
	
	@Override
	public String toString() {
		return displayString;
	}
	
	public boolean isNumberType() {
	    return isNumberType;
	}
	
	public boolean isTableType() {
	    return this.equals(TABLE);
	}
	
    public Constructor<? extends V> getSpecConstructor() {
        return getConstructor(getAssociatedSpecType(), "spec");
    }
    
    public Constructor<? extends V> getValueConstructor() {
        return getConstructor(getAssociatedValueType(), "spec");
    }
    
    private Constructor<? extends V> getConstructor(Class<?> associatedType, String what) {
        for (Constructor<?> constructor : getRepresentedVSubclass().getDeclaredConstructors()) {
            Class<?>[] paramTypes= constructor.getParameterTypes();
            if (paramTypes.length == 2
                    && paramTypes[0].equals(Vp.class)
                    && associatedType.isAssignableFrom(paramTypes[1])) {
                @SuppressWarnings("unchecked")
                Constructor<? extends V> result= (Constructor<? extends V>) constructor;
                return result;
            }
        }
        throw new AssertionError("Cannot find "+what+" constructor of "+name());
    }
    
}
