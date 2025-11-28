package org.proforma.variability.transfer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.proforma.variability.util.Log;





@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="def-type", propOrder = {"id"})
public class VarSpecNodeDef extends VarSpecReorderingNode {
	
	public VarSpecNodeDef() {
		this(null, new String[0]);
	}
	
	public VarSpecNodeDef(String id, String ... keys) {
	    super(keys);
	    setId(id);
	}

	public VarSpecNodeDef(VarSpecNodeDef other) {
		super(other);
		id= other.id;
	}
	
    
    @Override
    public VarSpecNodeDef clone() {
        return new VarSpecNodeDef(this);
    }
  
	@XmlAttribute
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	@Override
	public void setChildren(List<VarSpecNode> children) {
		if (children != null) {
			int cnt= 0;
			for (VarSpecNode c : children) {
				if (! (c instanceof VarSpecNodeDef)) {
					cnt++;
					if (cnt > 1) {
						throw new IllegalArgumentException(this.getClass()+" must have exactly one non-define child");
					}
				}
			}
		}
		super.setChildren(children);
	}

	
	@Override
	protected void pushInheritedCVpToChildren(CVp dummy) {
		completeOwnVpsFromParent(dummy);
		Log.debug(getClass()+".pushInherited:");
		Log.debug("   effective: "+getEffectiveCVp());
		for (VarSpecNode c : getChildren()) {
			if (c instanceof VarSpecNodeDef) {
				c.pushInheritedCVpToChildren(null);
			} else {
				c.pushInheritedCVpToChildren(getCVp());
			}
		}
	}

	@Override
	public long sizeLowerBound() {
        VarSpecNode node= getDefinedNode();
        if (node != null) 
            return node.sizeLowerBound();
		return 0;
	}

	@Override
	public int dim() {
        VarSpecNode node= getDefinedNode();
        if (node != null) 
            return node.dim();
		return 0;
	}
	
	@Override
	public CVp getEffectiveCVp() {
		return getCVp();
	}


	
    public VarSpecNode getDefinedNode() {
        if (getChildren() != null) {
            for (VarSpecNode c : getChildren()) {
                if (! (c instanceof VarSpecNodeDef)) {
                    return c;
                }
            }
        }
        return null;
    }
	
	@Override
	public VarSpecNode endDefine() {
		getParent().validateNewChild(this);
		return getParent();
	}

	@Override
	protected void validateNewChild(VarSpecNode child) throws IllegalArgumentException {
		if (child instanceof VarSpecRoot || child instanceof VarSpecLeafRef) {
			throw new IllegalArgumentException("Cannot use '"+child.getClass()+"' inside '"+this.getClass()+"'");
		} else if (numNonDefineChildren() > 1) {
			throw new IllegalArgumentException("Cannot use more than one nested non-define-construct inside '"+this.getClass()+"("+id+")'");
		}

		
	}
	
	
	@Override
	protected void prettyPrint(PrintStream out, String prefix) {
		String keySpec= null;
		if (getCVp() != null) {
			List<String> keySpecList= new ArrayList<>();
			for (Vp vd : getCVp()) keySpecList.add(vd.toString());
			keySpec= String.join(", ", keySpecList);
		}
		out.format("%sdefine(%s, %s) /%s/\n", prefix, id, keySpec, getDebugId());

		for (VarSpecNode c : getChildren()) {
			c.prettyPrint(out, prefix+"    ");
		}
		out.format("%sendDefine\n", prefix);
	}

}
