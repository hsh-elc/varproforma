package org.proforma.variability.transfer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.proforma.variability.util.Log;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="collect-type", propOrder= {"choices"})
public class VarSpecLeafCollect extends VarSpecLeaf {
	
	public VarSpecLeafCollect() {
		
	}
	public VarSpecLeafCollect(VarSpecLeafCollect other) {
		super(other);
		if (other.inheritedCVp != null) {
			inheritedCVp= other.inheritedCVp.clone();
		}
		if (other.choices != null) {
			choices= new ArrayList<>();
			for (V s : other.choices) {
				choices.add(s.clone());
			}
		}
	}
	
	@Override
	public VarSpecLeafCollect clone() {
	    return new VarSpecLeafCollect(this);
	}
	
    @XmlElements({
		@XmlElement(name="integer", type=Vi.class, required = true),
		@XmlElement(name="double", type=Vd.class, required = true),
		@XmlElement(name="string", type=Vs.class, required = true),
		@XmlElement(name="boolean", type=Vb.class, required = true),
		@XmlElement(name="character", type=Vc.class, required = true),
        @XmlElement(name="table", type=Vt.class, required = true)
	})
    private List<V> choices;

	@XmlTransient
	private CVp inheritedCVp;

    public List<V> getChoices() {
		return choices;
	}

	public void setChoices(List<V> choices) {
		this.choices = choices;
	}

	
    /**
     * Can be called only when all children are present.
     */
    @Override
    protected DefRefCollector collectDefRefs() {
        DefRefCollector result= new DefRefCollector();
        for (V c : choices) {
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
		
		for (V c : choices) {
		    c.pushInheritedCVpToChildren(this.inheritedCVp.get(0));
		}
	}
	
	@Override
	public long sizeLowerBound() {
		return choices.size();
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
	    for (V c : choices) {
	        c.setParent(this);
	    }
	}
	
	
	static VarSpecLeafCollect create(Object ... specs) {
		VarSpecLeafCollect result= new VarSpecLeafCollect();
		ArrayList<V> myChoices= new ArrayList<>();
		for (Object choice : specs)
			myChoices.add(V.fromSpec(choice));
		result.setChoices(myChoices);
		return result;
	}


	@Override
	protected void validateNewChild(VarSpecNode child) throws IllegalArgumentException {
		throw new IllegalArgumentException("Cannot use '"+child.getClass()+"' inside '"+this.getClass()+"'");
	}
	
	@Override
	protected void prettyPrint(PrintStream out, String prefix) {
		out.format("%scollect([%s]  /%s/ -> ", prefix, getEffectiveCVp() == null ? null : getEffectiveCVp().get(0), getDebugId());
		ArrayList<String> strings= new ArrayList<>();
		for (V c : choices) strings.add(c.toString());
		out.format("{%s})\n", String.join(", ", strings));
	}
	
}
