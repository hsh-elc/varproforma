package proforma.varproforma;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import proforma.varproforma.util.Log;





@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="combine-group-type")
public class VarSpecNodeCombineGroup extends VarSpecReorderingNode {

	public VarSpecNodeCombineGroup() {
		
	}
	
	public VarSpecNodeCombineGroup(String ... keys) {
	    super(keys);
	}
	    

	public VarSpecNodeCombineGroup(VarSpecNodeCombineGroup other) {
		super(other);
		if (other.inheritedCVp != null) {
			inheritedCVp= other.inheritedCVp.clone();
		}
	}
	
    @Override
    public VarSpecNodeCombineGroup clone() {
        return new VarSpecNodeCombineGroup(this);
    }


	@XmlTransient
	private CVp inheritedCVp;
	
	@Override
	protected void pushInheritedCVpToChildren(CVp inheritedCVp) {
        completeOwnVpsFromParent(inheritedCVp);
		this.inheritedCVp= inheritedCVp;
		Log.debug(getClass()+".pushInherited:");
		Log.debug("   inherited: "+inheritedCVp);
		Log.debug("   effective: "+getEffectiveCVp());
		CVp effectiveDimensions= (getCVp() == null ? inheritedCVp : getCVp());
		int d= 0;
		if (getChildren() != null) {
			for (VarSpecNode c : getChildren()) {
				if (c instanceof VarSpecNodeDef) {
					c.pushInheritedCVpToChildren(null);
				} else {
				    int sz= effectiveDimensions.size();
				    List<Vp> list= effectiveDimensions.getVariationPoints();
					CVp g= new CVp(list.subList(d, sz));
					c.pushInheritedCVpToChildren(g);
					int dc= c.dim();
					d += dc;
				}
			}
		}
		// truncate dimensions to maximum dimension
		this.inheritedCVp= new CVp(new ArrayList<>(this.inheritedCVp.getVariationPoints().subList(0, dim())));
        // subList returns a non-serializable list, so we need to copy it to a new ArrayList:
	}
	
	@Override
	public long sizeLowerBound() {
		long prod= 1;
		for (int i=0; i<getChildren().size(); i++) {
			VarSpecNode c= getChildren().get(i);
			if (! (c instanceof VarSpecNodeDef)) {
				prod *= c.sizeLowerBound();
			}
		}
		return prod;
	}

	@Override
	public int dim() {
		int result= 0;
		for (int i=0; i<getChildren().size(); i++) {
			VarSpecNode c= getChildren().get(i);
			if (! (c instanceof VarSpecNodeDef)) {
				result += c.dim();
			}
		}
		return result;
	}

	@Override
	public CVp getEffectiveCVp() {
		if (getCVp() != null) return getCVp();
		return inheritedCVp;
	}
	
	
	
	@Override
	public VarSpecNode endCombineGroup() {
		getParent().validateNewChild(this);
		return getParent();
	}
	
	
	@Override
	protected void validateNewChild(VarSpecNode child) throws IllegalArgumentException {
		if (child instanceof VarSpecRoot) {
			throw new IllegalArgumentException("Cannot use '"+child.getClass()+"' inside '"+this.getClass()+"'");
		}

		
	}

	@Override
	protected void prettyPrint(PrintStream out, String prefix) {
		String keySpec= null;
		if (getEffectiveCVp() != null) {
			List<String> keySpecList= new ArrayList<>();
			for (Vp vp : getEffectiveCVp()) keySpecList.add(vp.toString());
			keySpec= String.join(", ", keySpecList);
		}
		if (getCVp() == null) keySpec= "["+keySpec+"]";
		out.format("%scombineGroup(%s) /%s/\n", prefix, keySpec, getDebugId());
		for (VarSpecNode c : getChildren()) {
			c.prettyPrint(out, prefix+"    ");
		}
		out.format("%sendCombineGroup\n", prefix);
	}

	
	
	

	
}
