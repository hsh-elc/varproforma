package org.proforma.variability.transfer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.proforma.variability.util.Util;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="vs-type")
public class Vs extends Vns {

    @XmlValue
    private String data;

    public Vs() {
        
    }
    
    public Vs(Vp vp, String spec) {
        super.setVp(vp);
        this.data= spec;
    }
    
    public Vs(Vs other) {
        this(other.getVp(), other.data);
    }

    @Override
    public Vs clone() {
        return new Vs(this);
    }

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Vs))
            return false;
        Vs other = (Vs) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        return true;
    }

    @Override
    protected Character getSpecTypeSymbol() {
        return 'S';
    }

    
    @Override
    public double distanceTo(V choice) {
        if (choice == null || !choice.getClass().equals(getClass())) {
            throw new IllegalArgumentException(getClass()+".distanceTo: unexpected argument type "+(choice == null ? null : choice.getClass()));
        }
        if (choice.equals(this)) return 0.0;
        String s= ((Vs)choice).data;
        return Util.levenshteinDistance(s, data);
    }

    

    @Override
    public String getSpec() {
        return data;
    }

    @Override
    public Class<?> getSpecType() {
        return String.class;
    }

    @Override
    public String getValue() {
        return data;
    }

    @Override
    public Class<?> getValueType() {
        return String.class;
    }


    @Override
    public boolean isSpec() {
        return true;
    }

    @Override
    public boolean isValue() {
        return true;
    }

    @Override
    public Vs switchToSpec() {
        return this;
    }

    @Override
    public Vs switchToValue() {
        return this;
    }

}
