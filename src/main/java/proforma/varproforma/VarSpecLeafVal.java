package proforma.varproforma;

import java.io.PrintStream;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import proforma.varproforma.util.Log;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="val-type", propOrder= {"value"})
public class VarSpecLeafVal extends VarSpecLeaf {
	
	private static final long serialVersionUID = 1L;

    public VarSpecLeafVal() {
		
	}
	public VarSpecLeafVal(VarSpecLeafVal other) {
		super(other);
		if (other.inheritedCVp != null) {
			inheritedCVp= other.inheritedCVp.clone();
		}
		if (other.value != null) {
			value= other.value.clone();
		}
	}
	
    @Override
    public VarSpecLeafVal clone() {
        return new VarSpecLeafVal(this);
    }
	
	
    @XmlElements({
		@XmlElement(name="integer", type=Vi.class, required = true),
		@XmlElement(name="double", type=Vd.class, required = true),
		@XmlElement(name="string", type=Vs.class, required = true),
		@XmlElement(name="boolean", type=Vb.class, required = true),
        @XmlElement(name="character", type=Vc.class, required = true),
        @XmlElement(name="table", type=Vt.class, required = true)
	})
    private V value;

	@XmlTransient
	private CVp inheritedCVp;

    public V getValue() {
		return value;
	}

	public void setValue(V item) {
		this.value = item;
	}

	/**
     * Can be called only when all children are present.
     */
    @Override
    protected DefRefCollector collectDefRefs() {
        return value.collectDefRefs();
    }

    
	
	@Override
	protected void pushInheritedCVpToChildren(CVp inheritedCvp) {
        // subList returns a non-serializable list, so we need to copy it to a new ArrayList:
		this.inheritedCVp= new CVp(new ArrayList<>(inheritedCvp.getVariationPoints().subList(0, dim())));
		Log.debug(getClass()+".pushInherited:");
		Log.debug("   inherited: "+inheritedCVp);
		Log.debug("   effective: "+getEffectiveCVp());
		this.value.pushInheritedCVpToChildren(this.inheritedCVp.get(0));
	}
	
	@Override
	public long sizeLowerBound() {
		return 1;
	}
	
	@Override
	public int dim() {
		return 1;
	}
	
	
	@Override
	public CVp getEffectiveCVp() {
		return inheritedCVp;
	}

    @Override
    public void setParent(VarSpecNode parent) {
        super.setParent(parent);
        value.setParent(this);
    }
    
	
	static VarSpecLeafVal create(Object spec) {
		VarSpecLeafVal result= new VarSpecLeafVal();
		result.setValue(V.fromSpec(spec));
		return result;
	}

	@Override
	protected void validateNewChild(VarSpecNode child) throws IllegalArgumentException {
		throw new IllegalArgumentException("Cannot use '"+child.getClass()+"' inside '"+this.getClass()+"'");
	}
	
	@Override
	protected void prettyPrint(PrintStream out, String prefix) {
		out.format("%sval([%s] /%s/ -> %s)\n", prefix, getEffectiveCVp() == null ? null : getEffectiveCVp().get(0), getDebugId(), value.toString());
	}
	
}
