package org.proforma.variability.transfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="vp-type-type")
public class VpType {

    private static HashMap<VpT, VpType> cache= new HashMap<>();
    
	@XmlAttribute(required=true)
	private VpT type;

	@XmlElement(name="cvp", required=false)
	private CVp cvp;

    public VpType() {
    }

    public VpType(VpType other) {
        if (other == null) return; // may be the case when creating preliminary Vp instances.
        this.type= other.type;
        if (other.cvp != null) this.cvp= new CVp(other.cvp);
    }
    
    public VpType clone() {
        return new VpType(this);
    }

    public VpType(VpT type, Vp ... vps) {
        this.type= type;
        if (vps.length > 0) {
            this.cvp= new CVp(new ArrayList<>(Arrays.asList(vps)));
        }
    }

    public VpT getType() {
        return type;
    }

    public void setType(VpT type) {
        this.type = type;
    }

    public CVp getCVp() {
        return cvp;
    }
    
    public void setCVp(CVp cvp) {
        this.cvp= cvp;
    }

    public Vp getVp(String key) {
        if (cvp == null) return null;
        int index= cvp.indexOf(key);
        if (index < 0) return null;
        return cvp.get(index);
    }
    
    public Vp[] getVps() {
        if (cvp == null) return null;
        return cvp.getVariationPoints().toArray(new Vp[0]);
    }
    
    public boolean isIntervalScaled() {
        return type.isIntervalScaled();
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, cvp);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass())  return false;
        VpType other = (VpType) obj;
        return Objects.equals(this.type, other.type)
                && Objects.equals(this.cvp, other.cvp);
    }

    @Override
    public String toString() {
        String s= type==null ? "-" : type.displayString();
        if (cvp != null) {
            s= "(" + s + ", cvp=(" + cvp + "))";
        }
        return s;
    }

    public Class<? extends V> getRepresentedVSubclass() {
        return type.getRepresentedVSubclass();
    }
    public boolean represents(Class<? extends V> clazz) {
        return type.represents(clazz);
    }

    public String displayString() {
        return type.displayString();
    }

    public Class<?> getAssociatedValueType() {
        return type.getAssociatedValueType();
    }

    
    public static VpType i() {
        if (!cache.containsKey(VpT.INTEGER)) cache.put(VpT.INTEGER, new VpType(VpT.INTEGER));
        return cache.get(VpT.INTEGER);
    }
    public static VpType c() {
        if (!cache.containsKey(VpT.CHARACTER)) cache.put(VpT.CHARACTER, new VpType(VpT.CHARACTER));
        return cache.get(VpT.CHARACTER);
    }
    public static VpType s() {
        if (!cache.containsKey(VpT.STRING)) cache.put(VpT.STRING, new VpType(VpT.STRING));
        VpType result= cache.get(VpT.STRING);
        return result;
    }
    public static VpType d() {
        if (!cache.containsKey(VpT.DOUBLE)) cache.put(VpT.DOUBLE, new VpType(VpT.DOUBLE));
        return cache.get(VpT.DOUBLE);
    }
    public static VpType b() {
        if (!cache.containsKey(VpT.BOOLEAN)) cache.put(VpT.BOOLEAN, new VpType(VpT.BOOLEAN));
        return cache.get(VpT.BOOLEAN);
    }
    public static VpType t(Vp ...vps ) {
        return new VpType(VpT.TABLE, vps);
    }

    public boolean isNumberType() {
        return type.isNumberType();
    }
    
    public boolean isTableType() {
        return type.isTableType();
    }

    
}
