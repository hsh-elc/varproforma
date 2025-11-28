package org.proforma.variability.transfer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.proforma.variability.transfer.VarSpecNode.DefRefCollector;
import org.proforma.variability.util.SpecValueConverter;


/**
 * Represents a "table", i. e. a list of records, specified by a {@link VarSpecNodeTable} object and
 * expanded to a {@link CVList} object.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="vt-type")
public class Vt extends Vns {

    @XmlElements({
        @XmlElement(name="spec", type=VarSpecNodeTable.class, required = true),
        @XmlElement(name="value", type=CVList.class, required = true)
    })
    private Object data;

    @XmlTransient
    private VarSpecNodeTable specCached;
    
    @XmlTransient
    private CVList valueCached;
    
    
    public Vt() {
		
	}
	
    public Vt(Vp vp, VarSpecNodeTable spec) {
        setVp(vp);
        this.data= spec;
    }
    
    public Vt(Vp vp, CVList value) {
        setVp(vp);
        this.data= value;
    }

    public Vt(Vt other) {
        setVp(other.getVp());
        this.data= other.data;
        this.specCached= other.specCached;
        this.valueCached= other.valueCached;
    }

    @Override
    public Vt clone() {
        return new Vt(this);
    }
    

    @Override
    public boolean isSpec() {
        return data instanceof VarSpecNodeTable;
    }
    
    @Override
    public boolean isValue() {
        return data instanceof CVList;
    }
    
    @Override
    public Vt switchToValue() {
        if (isSpec()) {
            specCached= getSpec();
            if (valueCached != null) {
                data= valueCached;
            } else {
                data= getValue();
            }
            valueCached= null;
        }
        return this;
    }
    
    @Override
    public Vt switchToSpec() {
        if (isValue()) {
            valueCached= getValue();
            if (specCached != null) {
                data= specCached;
            } else {
                data= getSpec();
            }
            specCached= null;
        }
        return this;
    }
    
    
    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Vt))
            return false;
        Vt other = (Vt) obj;
        return Objects.equals(this.data, other.data);
    }
    
    @Override
    public DefRefCollector collectDefRefs() {
        if (isSpec()) {
            return getSpec().collectDefRefs();
        }
        return super.collectDefRefs(); // default implementation
    }

    @Override
    public void pushInheritedCVpToChildren(Vp vp) {
        if (vp != null && vp.equals(super.getVp())) return; // nothing to do for e. g. cached data
        
        super.pushInheritedCVpToChildren(vp);
        CVp cvp= vp.getCVp();
        if (cvp == null || cvp.size() == 0) {
            throw new IllegalArgumentException("Cannot St.pushInheritedCVpToChildren(vp) for vp with null or empty sub-cvp");
        }
        if (isSpec()) {
            ((VarSpecNodeTable)data).pushInheritedCVpToChildren(cvp);
        } else {
            for (CV cv : ((CVList)data).getElements()) {
                cv.pushCVp(cvp);
            }
        }
    }

    
    @Override
    protected Character getSpecTypeSymbol() {
        return 'T';
    }

    
    @Override
    public double distanceTo(V choice) {
        if (choice == null || !choice.getClass().equals(getClass())) {
            throw new IllegalArgumentException(getClass()+".distanceTo: unexpected argument type "+(choice == null ? null : choice.getClass()));
        }
        return Objects.equals(data, ((Vt)choice).data) ? 0 : 1;
    }

    

	@Override
	public VarSpecNodeTable getSpec() {
	    if (data instanceof VarSpecNodeTable) return (VarSpecNodeTable)data;
	    if (specCached == null) {
	        specCached= SpecValueConverter.toTableSpec(CVListVp.createFromCSList(getValue()));
	    }
	    return specCached;	
	}
    
	@Override
	public Class<?> getSpecType() {
		return VarSpecNodeTable.class;
	}

	
    @Override
    public CVList getValue() {
        if (data instanceof CVList) return (CVList)data;
        if (valueCached == null) {
            valueCached= new CVList(SpecValueConverter.expandNode(getSpec()).getList());
        }
        return valueCached;
    }

    @Override
    public Class<?> getValueType() {
        return CVList.class;
    }


    @Override
    public String toString(String prefix) {
        if (isValue()) return super.toString(prefix);
        ByteArrayOutputStream baos= new ByteArrayOutputStream();
        try (PrintStream ps= new PrintStream(baos, true, "UTF-8")) {
            getSpec().prettyPrint(ps, prefix+"    ");
            return new String(baos.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return e.getMessage();
        }
    }
    


}
