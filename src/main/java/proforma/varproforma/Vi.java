package proforma.varproforma;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="vi-type")
public class Vi extends Vis {
    
    private static final long serialVersionUID = 1L;
    @XmlValue
    private Integer data;
    
    
    public Vi() {
        
    }
    
    public Vi(Vp vp, Integer spec) {
        super.setVp(vp);
        this.data= spec;
    }
    
    public Vi(Vi other) {
        this(other.getVp(), other.data);
    }

    @Override
    public Vi clone() {
        return new Vi(this);
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
        if (!(obj instanceof Vi))
            return false;
        Vi other = (Vi) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        return true;
    }
    
    

    @Override
    protected Character getSpecTypeSymbol() {
        return 'I';
    }

    @Override
    public boolean isZero() {
        double accuracy= getVp().getAccuracyOrDefault();
        return Math.abs(getSpec().intValue()-0) <= accuracy;
    }

    @Override
    public Vi abs() {
        return new Vi(getVp(), Math.abs(data));

    }
    @Override
    public Vi plus(Vis other) {
        if (! (other instanceof Vi)) throw new IllegalArgumentException(getClass()+".plus: wrong argument type "+other.getClass());
        return new Vi(getVp(), data + ((Vi)other).data);
    }

    @Override
    public Vi minus(Vis other) {
        if (! (other instanceof Vi)) throw new IllegalArgumentException(getClass()+".minus: wrong argument type "+other.getClass());
        return new Vi(getVp(), data - ((Vi)other).data);
    }

    @Override
    public Vi pred() {
        return new Vi(getVp(), data - 1);
    }
    @Override
    public Vi succ() {
        return new Vi(getVp(), data + 1);
    }

    @Override
    public Vi times(long i) {
        return new Vi(getVp(), (int)(data * i));
    }

    @Override
    public Vi divideBy(long i) {
        return new Vi(getVp(), (int)(data / i));
    }

    @Override
    public long flooredDivideBy(Vis divisor) {
        double accuracy= getVp().getAccuracyOrDefault();
        if (! (divisor instanceof Vi)) throw new IllegalArgumentException(getClass()+".flooredDivideBy: wrong argument type "+divisor.getClass());
        
        double d= (double)data.intValue() / ((Vi)divisor).data.intValue();
        if (Math.abs(Math.ceil(d) - d)<= accuracy) return (int)Math.ceil(d);
        return (long)Math.floor(d);
    }

    @Override
    public Vi modulo(long i) {
        return new Vi(getVp(), (int)(data % i));
    }


    @Override
    public double toDouble() {
        return data.doubleValue();
    }
    
    @Override
    public Long countStepsFromThisTo(Vis other) {
        if (! (other instanceof Vi)) throw new IllegalArgumentException();
        Vi i= (Vi) other;
        return (long)(i.getSpec().intValue() - getSpec().intValue() + 1);
    }
    


    @Override
    public Integer getSpec() {
        return data;
    }

    @Override
    public Class<?> getSpecType() {
        return Integer.class;
    }

    @Override
    public Integer getValue() {
        return data;
    }

    @Override
    public Class<?> getValueType() {
        return Integer.class;
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
    public Vi switchToSpec() {
        return this;
    }

    @Override
    public Vi switchToValue() {
        return this;
    }

    public static Vi fromDouble(double d, Vp vp) {
        int i= (int)Math.round(d);
if (Math.abs(d - Math.round(d)) > 1E-5) System.out.println("WARNING! Rounding error in Si.fromDouble: "+d+" -> "+i);     
        if (Math.abs(i - d) > vp.getAccuracyOrDefault()) throw new IllegalArgumentException(Vi.class+": fromDouble. Parameter '"+d+"' cannot be converted");
        return new Vi(vp, i);
    }
    
    
}
