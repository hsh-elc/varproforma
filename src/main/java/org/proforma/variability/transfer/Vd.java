package org.proforma.variability.transfer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="vd-type")
public class Vd extends Vis {
    
    @XmlValue
    private Double data;
    
    
    public Vd() {
        
    }
    
    public Vd(Vp vp, Double spec) {
        super.setVp(vp);
        this.data= spec;
    }
    
    
    public Vd(Vd other) {
        this(other.getVp(), other.data);
    }

    @Override
    public Vd clone() {
        return new Vd(this);
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
        if (!(obj instanceof Vd))
            return false;
        Vd other = (Vd) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        return true;
    }
    
    

    @Override
    protected Character getSpecTypeSymbol() {
        return 'D';
    }

    @Override
    public boolean isZero() {
        double accuracy= getVp().getAccuracyOrDefault();
        return Math.abs(getSpec().doubleValue()) <= accuracy;
    }

    @Override
    public Vd abs() {
        return new Vd(getVp(), Math.abs(data));

    }
    @Override
    public Vd plus(Vis other) {
        if (! (other instanceof Vd)) throw new IllegalArgumentException(getClass()+".plus: wrong argument type "+other.getClass());
        return new Vd(getVp(), data + ((Vd)other).data);
    }

    @Override
    public Vd minus(Vis other) {
        if (! (other instanceof Vd)) throw new IllegalArgumentException(getClass()+".minus: wrong argument type "+other.getClass());
        return new Vd(getVp(), data - ((Vd)other).data);
    }

    @Override
    public Vd pred() {
        return new Vd(getVp(), data - 1);
    }
    @Override
    public Vd succ() {
        return new Vd(getVp(), data + 1);
    }

    @Override
    public Vd times(long i) {
        return new Vd(getVp(), data * i);
    }

    @Override
    public Vd divideBy(long i) {
        return new Vd(getVp(), data / i);
    }

    @Override
    public long flooredDivideBy(Vis divisor) {
        double accuracy= getVp().getAccuracyOrDefault();
        if (! (divisor instanceof Vd)) throw new IllegalArgumentException(getClass()+".flooredDivideBy: wrong argument type "+divisor.getClass());
        
        double d= data.doubleValue() / ((Vd)divisor).data.doubleValue();
        if (Math.abs(Math.ceil(d) - d)<= accuracy) return (long)Math.ceil(d);
        return (long)Math.floor(d);
    }

    @Override
    public Vd modulo(long i) {
        return new Vd(getVp(), data % i);
    }

    @Override
    public double toDouble() {
        return data.doubleValue();
    }
    
    @Override
    public Long countStepsFromThisTo(Vis other) {
        if (! (other instanceof Vd)) throw new IllegalArgumentException();
        return 2L;
    }


    @Override
    public Double getSpec() {
        return data;
    }

    @Override
    public Class<?> getSpecType() {
        return Double.class;
    }

    @Override
    public Double getValue() {
        return data;
    }

    @Override
    public Class<?> getValueType() {
        return Double.class;
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
    public Vd switchToSpec() {
        return this;
    }

    @Override
    public Vd switchToValue() {
        return this;
    }

    public static Vd fromDouble(double d, Vp vp) {
        return new Vd(vp, d);
    }
    
    
}
