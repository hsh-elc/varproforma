package proforma.varproforma;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="vc-type")
public class Vc extends Vis {
    
    @XmlValue
    private Character data;
    
    
    public Vc() {
        
    }
    
    public Vc(Vp vp, Character spec) {
        super.setVp(vp);
        this.data= spec;
    }
    
    public Vc(Vc other) {
        this(other.getVp(), other.data);
    }

    @Override
    public Vc clone() {
        return new Vc(this);
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
        if (!(obj instanceof Vc))
            return false;
        Vc other = (Vc) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        return true;
    }
    
    

    @Override
    protected Character getSpecTypeSymbol() {
        return 'C';
    }

    @Override
    public boolean isZero() {
        double accuracy= getVp().getAccuracyOrDefault();
        return Math.abs(data.charValue()) <= accuracy;
    }

    @Override
    public Vc abs() {
        return new Vc(getVp(), (char)Math.abs((int)data));

    }
    @Override
    public Vc plus(Vis other) {
        if (! (other instanceof Vc)) throw new IllegalArgumentException(getClass()+".plus: wrong argument type "+other.getClass());
        return new Vc(getVp(), (char)(data + ((Vc)other).data));
    }

    @Override
    public Vc minus(Vis other) {
        if (! (other instanceof Vc)) throw new IllegalArgumentException(getClass()+".minus: wrong argument type "+other.getClass());
        return new Vc(getVp(), (char)(data - ((Vc)other).data));
    }

    @Override
    public Vc pred() {
        return new Vc(getVp(), (char)(data - 1));
    }
    @Override
    public Vc succ() {
        return new Vc(getVp(), (char)(data + 1));
    }

    @Override
    public Vc times(long i) {
        return new Vc(getVp(), (char)(data * i));
    }

    @Override
    public Vc divideBy(long i) {
        return new Vc(getVp(), (char)(data / i));
    }

    @Override
    public long flooredDivideBy(Vis divisor) {
        double accuracy= getVp().getAccuracyOrDefault();
        if (! (divisor instanceof Vc)) throw new IllegalArgumentException(getClass()+".flooredDivideBy: wrong argument type "+divisor.getClass());

        double d= (double)data.charValue() / (double)((Vc)divisor).data.charValue();
        if (Math.abs(Math.ceil(d) - d)<= accuracy) return (long)Math.ceil(d);
        return (long)Math.floor(d);
    }

    @Override
    public Vc modulo(long i) {
        return new Vc(getVp(), (char)(data % i));
    }

    @Override
    public double toDouble() {
        return data.charValue();
    }
    
    @Override
    public Long countStepsFromThisTo(Vis other) {
        if (! (other instanceof Vc)) throw new IllegalArgumentException();
        Vc c= (Vc) other;
        return (long)(c.getSpec().charValue() - getSpec().charValue() + 1);
    }


    @Override
    public Character getSpec() {
        return data;
    }

    @Override
    public Class<?> getSpecType() {
        return Character.class;
    }

    @Override
    public Character getValue() {
        return data;
    }

    @Override
    public Class<?> getValueType() {
        return Character.class;
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
    public Vc switchToSpec() {
        return this;
    }

    @Override
    public Vc switchToValue() {
        return this;
    }

    public static Vc fromDouble(double d, Vp vp) {
        char c= (char)Math.round(d);
if (Math.abs(d - Math.round(d)) > 1E-5) System.out.println("WARNING! Rounding error in Sc.fromDouble: "+d+" -> "+c);     
        double diff= Math.abs(c-d);
        if (diff > vp.getAccuracyOrDefault()) throw new IllegalArgumentException(Vc.class+": fromDouble. Parameter '"+d+"' cannot be converted");
        return new Vc(vp, c);
    }
    
    
}
