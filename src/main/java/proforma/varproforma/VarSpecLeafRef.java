package proforma.varproforma;

import java.io.PrintStream;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import proforma.varproforma.util.Log;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ref-type", propOrder = {"id"})
public class VarSpecLeafRef extends VarSpecLeaf {
	
	private static final long serialVersionUID = 1L;

    public VarSpecLeafRef() {
	}
	public VarSpecLeafRef(VarSpecLeafRef other) {
		super(other);
		id= other.id;
	}
	
    @Override
    public VarSpecLeafRef clone() {
        return new VarSpecLeafRef(this);
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
	protected void pushInheritedCVpToChildren(CVp inheritedCVp) {
        Log.debug(getClass()+".pushInherited:");
        Log.debug("   effective: "+getEffectiveCVp());

	}
	
	public VarSpecNodeDef defNode() {
//	    return getParent().findPreviousDefineNode(id, this);
	    return findDef(id);
	}
	
	@Override
	public long sizeLowerBound() {
		return defNode().sizeLowerBound();
	}

	@Override
	public int dim() {
		return defNode().dim();
	}

	@Override
	public CVp getEffectiveCVp() {
		CVp result= defNode().getEffectiveCVp();
		return result;
	}

    
    public VarSpecNode getReferencedNode() {
        VarSpecNodeDef def= defNode();
        return def.getDefinedNode();
    }

	
	

	

	@Override
	protected void validateNewChild(VarSpecNode child) throws IllegalArgumentException {
		throw new IllegalArgumentException("Cannot use '"+child.getClass()+"' inside '"+this.getClass()+"'");
	}
	
	@Override
	protected void prettyPrint(PrintStream out, String prefix) {
		out.format("%sref(%s) /%s/\n", prefix, id, getDebugId());
	}

}
