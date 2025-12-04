package proforma.varproforma;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="vb-type")
public class Vb extends Vns {

    private static final long serialVersionUID = 1L;
    @XmlValue
    private Boolean data;

    public Vb() {
        
    }
    
    public Vb(Vp vp, Boolean spec) {
        super.setVp(vp);
        this.data= spec;
    }
    
    public Vb(Vb other) {
        this(other.getVp(), other.data);
    }

    @Override
    public Vb clone() {
        return new Vb(this);
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
        if (!(obj instanceof Vb))
            return false;
        Vb other = (Vb) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        return true;
    }

    @Override
    protected Character getSpecTypeSymbol() {
        return 'B';
    }

    
    @Override
    public double distanceTo(V choice) {
        if (choice == null || !choice.getClass().equals(getClass())) {
            throw new IllegalArgumentException(getClass()+".distanceTo: unexpected argument type "+(choice == null ? null : choice.getClass()));
        }
        return Objects.equals(data, ((Vb)choice).data) ? 0 : 1;
    }

    

    @Override
    public Boolean getSpec() {
        return data;
    }

    @Override
    public Class<?> getSpecType() {
        return Boolean.class;
    }

    @Override
    public Boolean getValue() {
        return data;
    }

    @Override
    public Class<?> getValueType() {
        return Boolean.class;
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
    public Vb switchToSpec() {
        return this;
    }

    @Override
    public Vb switchToValue() {
        return this;
    }

}
