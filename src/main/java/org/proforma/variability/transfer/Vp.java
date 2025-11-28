package org.proforma.variability.transfer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Variation point.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vp-type", propOrder = {"key", "accuracy"})
public class Vp extends VpType  implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

    @XmlAttribute(required = true)
	private String key;
	
	@XmlAttribute(required = false)
	private Double accuracy;

	public Vp() {
	}
	
	public Vp(Vp other) {
	    super(other);
		this.key= other.key;
		this.accuracy= other.accuracy;
	}
	
	public Vp clone() {
		return new Vp(this);
	}
	
	public Vp(String key, VpType type, Double accuracy) {
	    super(type);
		this.key = key;
		this.accuracy = accuracy;
	}

	public static Vp s(String key) {
		return new Vp(key, VpType.s(), null);
	}
	
	public static Vp b(String key) {
		return new Vp(key, VpType.b(), null);
	}
	
	public static Vp c(String key) {
		return new Vp(key, VpType.c(), null);
	}
	
	public static Vp i(String key) {
		return new Vp(key, VpType.i(), null);
	}
	
	public static Vp d(String key, double accuracy) {
		return new Vp(key, VpType.d(), accuracy);
	}
	
    public static Vp t(String key, Vp ... vps) {
        return new Vp(key, VpType.t(vps), null);
    }
    
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public double getAccuracyOrDefault() {
		return accuracy == null ? 0.0 : accuracy;
	}

	public Double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Double accuracy) {
		this.accuracy = accuracy;
	}

	@Override
    public String toString() {
	    String s= key+":"+super.toString();
	    if (accuracy != null) s += ", acc="+accuracy;
	    return s;
    }

	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((accuracy == null) ? 0 : accuracy.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Vp other = (Vp) obj;
        if (accuracy == null) {
            if (other.accuracy != null) {
                return false;
            }
        } else if (!accuracy.equals(other.accuracy)) {
            return false;
        }
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }


}