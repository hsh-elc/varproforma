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
import proforma.varproforma.util.VisOrRange;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="range-type", propOrder= {"first", "last", "count"})
public class VarSpecLeafRange extends VarSpecLeaf {
	
	private static final long serialVersionUID = 1L;

    public VarSpecLeafRange() {
		
	}
	public VarSpecLeafRange(VarSpecLeafRange other) {
		super(other);
		if (other.inheritedCVp != null) {
			inheritedCVp= other.inheritedCVp.clone();
		}
        first= other.first.clone();
        last= other.last.clone();
        count= other.count;
	}
	
	   
    @Override
    public VarSpecLeafRange clone() {
        return new VarSpecLeafRange(this);
    }
    
	
    @XmlElements({
        @XmlElement(name="first-integer", type=Vi.class, required = true),
        @XmlElement(name="first-double", type=Vd.class, required = true),
        @XmlElement(name="first-character", type=Vc.class, required = true),
    })
    private Vis first;

    public Vis getFirst() {
        return first;
    }
    
    public void setFirst(Vis first) {
        this.first= first;
    }
    
    @XmlElements({
        @XmlElement(name="last-integer", type=Vi.class, required = true),
        @XmlElement(name="last-double", type=Vd.class, required = true),
        @XmlElement(name="last-character", type=Vc.class, required = true),
    })
    private Vis last;

    public Vis getLast() {
        return last;
    }
    
    public void setLast(Vis last) {
        this.last= last;
    }
    
    @XmlElement(name="count", required=false)
    private Long count;

    public Long getCount() {
        return count;
    }
    
    public void setCount(Long count) {
        this.count= count;
    }
    
	@XmlTransient
	private CVp inheritedCVp;

    /**
     * Can be called only when all children are present.
     */
    @Override
    protected DefRefCollector collectDefRefs() {
        DefRefCollector result= new DefRefCollector();
        for (Vis c : new Vis[] { first, last }) {
            result.addAll(c.collectDefRefs());
        }
        return result;
    }


	

	@Override
	protected void pushInheritedCVpToChildren(CVp inheritedCVp) {
        // subList returns a non-serializable list, so we need to copy it to a new ArrayList:
		this.inheritedCVp= new CVp(new ArrayList<>(inheritedCVp.getVariationPoints().subList(0, dim())));
		Log.debug(getClass()+".pushInherited:");
		Log.debug("   inherited: "+inheritedCVp);
		Log.debug("   effective: "+getEffectiveCVp());
		Vp vp= this.inheritedCVp.get(0);
        first.pushInheritedCVpToChildren(vp);
        last.pushInheritedCVpToChildren(vp);
        VisOrRange r= new VisOrRange(first, last, count);
        r.validate();
        count= r.getCount();
	}
	
	@Override
	public long sizeLowerBound() {
		return count;
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
        for (Vis c : new Vis[] { first, last }) {
            c.setParent(this);
        }
    }

    
	

    static VarSpecLeafRange createSpec(Object first, Object last, Long steps) {
        VarSpecLeafRange result= new VarSpecLeafRange();
        result.setFirst((Vis)V.fromSpec(first));
        result.setLast((Vis)V.fromSpec(last));
        result.setCount(steps);
        return result;
    }
    
    
	@Override
	protected void validateNewChild(VarSpecNode child) throws IllegalArgumentException {
		throw new IllegalArgumentException("Cannot use '"+child.getClass()+"' inside '"+this.getClass()+"'");
	}
	
	@Override
	protected void prettyPrint(PrintStream out, String prefix) {
		out.format("%srange([%s] /%s/ -> [%s, %s, n=%d]\n", prefix, getEffectiveCVp() == null ? null : getEffectiveCVp().get(0),
		        getDebugId(), first, last, count);
	}
	
}
