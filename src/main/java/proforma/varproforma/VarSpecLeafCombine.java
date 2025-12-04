package proforma.varproforma;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import proforma.varproforma.util.Log;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="combine-type", propOrder= {"value"})
public class VarSpecLeafCombine extends VarSpecLeaf {
	
	public VarSpecLeafCombine() {
		
	}
	public VarSpecLeafCombine(VarSpecLeafCombine other) {
		super(other);
		if (other.inheritedCVp != null) {
			inheritedCVp= other.inheritedCVp.clone();
		}
		if (other.value != null) {
			value= new ArrayList<>();
			for (V s : other.value) {
				value.add(s.clone());
			}
		}
	}
	
    @Override
    public VarSpecLeafCombine clone() {
        return new VarSpecLeafCombine(this);
    }


    @XmlElements({
		@XmlElement(name="integer", type=Vi.class, required = true),
		@XmlElement(name="double", type=Vd.class, required = true),
		@XmlElement(name="string", type=Vs.class, required = true),
		@XmlElement(name="boolean", type=Vb.class, required = true),
		@XmlElement(name="character", type=Vc.class, required = true),
        @XmlElement(name="table", type=Vt.class, required = true)
	})
    private List<V> value;

	@XmlTransient
	private CVp inheritedCVp;

    public List<V> getValue() {
		return value;
	}

	public void setValue(List<V> value) {
		this.value = value;
	}

    /**
     * Can be called only when all children are present.
     */
    @Override
    protected DefRefCollector collectDefRefs() {
        DefRefCollector result= new DefRefCollector();
        for (V c : value) {
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
		for (int i=0; i<dim(); i++) {
		    Vp vp= this.inheritedCVp.get(i);
		    V s= value.get(i);
	        s.pushInheritedCVpToChildren(vp);
		}
	}
	
	@Override
	public long sizeLowerBound() {
		return 1;
	}
	
	@Override
	public int dim() {
		return value.size();
	}
	
	
	@Override
	public CVp getEffectiveCVp() {
		return inheritedCVp;
	}

    
    @Override
    public void setParent(VarSpecNode parent) {
        super.setParent(parent);
        for (V s : value) {
            s.setParent(this);
        }
    }
    
    

	

	static VarSpecLeafCombine create(Object ... specs) {
        ArrayList<V> myVariants= new ArrayList<>();
        for (Object variantSpec : specs) {
            myVariants.add(V.fromSpec(variantSpec));
        }
	    VarSpecLeafCombine result= new VarSpecLeafCombine();
		result.setValue(myVariants);
		return result;
	}


	@Override
	protected void validateNewChild(VarSpecNode child) throws IllegalArgumentException {
		throw new IllegalArgumentException("Cannot use '"+child.getClass()+"' inside '"+this.getClass()+"'");
	}
	
	@Override
	protected void prettyPrint(PrintStream out, String prefix) {
		String keySpec= null;
		if (getEffectiveCVp() != null) {
			List<String> keySpecList= new ArrayList<>();
			for (Vp vp : getEffectiveCVp()) keySpecList.add(vp.toString());
			keySpec= String.join(", ", keySpecList);
		}
		out.format("%scombine([%s] /%s/ -> ", prefix, keySpec, getDebugId());
		ArrayList<String> strings= new ArrayList<>();
		for (V s : value) strings.add(s.toString());
		out.format("(%s))\n", String.join(", ", strings));
	}
	
}
