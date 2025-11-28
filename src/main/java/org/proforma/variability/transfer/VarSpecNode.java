package org.proforma.variability.transfer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlTransient;

import org.proforma.variability.util.Log;





@XmlTransient
public abstract class VarSpecNode implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;


    public VarSpecNode() {
		
	}
	public VarSpecNode(VarSpecNode other) {
	}
	
	public abstract VarSpecNode clone();

	protected String getDebugId() {
	    if (parent == null) return getClass().getName();
	    int i=0;
	    if (parent instanceof VarSpecInnerNode && parent.getChildren() != null) {
    	    for (VarSpecNode sibling : parent.getChildren()) {
    	        if (sibling == this) break;
    	        i++;
    	    }
	    }
	    return parent.getDebugId() + (char)('A'+i);
	}
	
	@XmlTransient
	private VarSpecNode parent;

    
    public abstract List<VarSpecNode> getChildren();
    public abstract void setChildren(List<VarSpecNode> children);
    public abstract CVp getCVp();
    public abstract void setCVp(CVp cvp);

    /**
     * This is stored at the root node
     */
    @XmlTransient
    private List<VarSpecNodeDef> allDefs;

    @XmlTransient
    static class DefRefCollector {
        private List<VarSpecNodeDef> defs;
        private List<VarSpecLeafRef> refs;
        DefRefCollector() {
            defs= new ArrayList<>();
            refs= new ArrayList<>();
        }
        void addDef(VarSpecNodeDef def) {
            defs.add(def);
        }
        void addRef(VarSpecLeafRef ref) {
            refs.add(ref);
        }
        void addAll(DefRefCollector c) {
            defs.addAll(c.defs);
            refs.addAll(c.refs);
        }
        List<VarSpecNodeDef> getDefs() {
            return defs;
        }
        List<VarSpecLeafRef> getRefs() {
            return refs;
        }
    }
    
    /**
     * Can be called only when all children are present.
     */
    protected DefRefCollector collectDefRefs() {
        DefRefCollector result= new DefRefCollector();
        if (this instanceof VarSpecInnerNode && getChildren() != null) {
            for (VarSpecNode c : getChildren()) {
                if (c instanceof VarSpecNodeDef) {
                    result.addDef((VarSpecNodeDef)c);
                } else if (c instanceof VarSpecLeafRef) {
                    result.addRef((VarSpecLeafRef)c);
                } 
                result.addAll(c.collectDefRefs());
            }
        }
        return result;
    }
    

    
    
    protected VarSpecNodeDef findDef(String id) {
        List<VarSpecNodeDef> allDefs= ((VarSpecNode)getRoot()).allDefs;
        if (allDefs == null) return null;
        for (VarSpecNodeDef d : allDefs) {
            if (d.getId().equals(id)) return d;
        }
        return null;
    }
    
    /**
     * Postprocessing sets parent relationships
     */
    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (this instanceof VarSpecInnerNode && getChildren() != null) {
            for (VarSpecNode c : getChildren()) {
                c.setParent(this);
            }
        }
    	if (this instanceof VarSpecRoot) { // root
    	    collectAndValidateAllDefsAndRefs();
    		pushInheritedCVpToChildren(null);
    	}
    }

    /**
     * This method is called once by the root node.
     */
    protected void collectAndValidateAllDefsAndRefs() {
        DefRefCollector collector= collectDefRefs();
        this.allDefs= collector.getDefs();
        List<VarSpecLeafRef> allRefs= collector.getRefs();

//System.out.println("defs: "+allDefs);
//System.out.println("refs: "+allRefs);
        HashSet<String> unusedDefs= new HashSet<>();
        if (allDefs != null) {
            for (VarSpecNodeDef def : allDefs) {
                if (unusedDefs.contains(def.getId())) {
                    throw new IllegalArgumentException("Duplicate define id '"+def.getId()+"'");
                }
                unusedDefs.add(def.getId());
            }
        }
        
        if (allRefs != null) {
            for (VarSpecLeafRef ref : allRefs) {
                boolean found= false;
                if (allDefs != null) {
                    for (VarSpecNodeDef def : allDefs) {
                        if (def.getId().equals(ref.getId())) {
                            found= true;
                            unusedDefs.remove(def.getId());
                            break;
                        }
                    }
                }
                if (!found) {
                    throw new IllegalArgumentException("There is an undefined ref '"+ref.getId()+"'");
                }
            }
        }
        if (!unusedDefs.isEmpty()) {
            Log.warn("There were unreferenced defines "+unusedDefs);
        }
        
    }
    
    protected abstract void pushInheritedCVpToChildren(CVp inheritedCVp);
    
	/**
	 * @return the number of tuples in this set.
	 */
    public abstract long sizeLowerBound();
    
    public abstract int dim();
    
    public abstract CVp getEffectiveCVp();
    
    
	public VarSpecRoot getRoot() {
	    if (parent == null) return (VarSpecRoot)this;
	    return parent.getRoot();
	}
	
    /**
     * @return parent node (null for the root)
     */
	public VarSpecNode getParent() {
		return parent;
	}

	/**
	 * @param parent parent node (or null for the root)
	 */
	public void setParent(VarSpecNode parent) {
		this.parent = parent;
	}
	
	
	public int getEffectiveVpIndex(String key) {
		CVp cvp= getEffectiveCVp();
		int sz= cvp.size();
		for (int i=0; i<sz; i++) {
			Vp vp= cvp.get(i);	
			if (vp.getKey().equals(key)) return i;
		}
		return -1;
	}
	public Vp getEffectiveVp(String key) {
		for (Vp vp : getEffectiveCVp()) {
			if (vp.getKey().equals(key)) return vp;
		}
		return null;
	}
	
	/**
	 * Returns the last child node.
	 */
	public VarSpecNode getLastChild() {
	    if (this instanceof VarSpecLeaf) return null;
	    if (getChildren() == null) return null;
	    return getChildren().get(getChildren().size()-1);
	}
	
	
	public ArrayList<VarSpecNode> getNonDefineChildren() {
		ArrayList<VarSpecNode> result= new ArrayList<>();
		if (this instanceof VarSpecInnerNode && getChildren() != null) {
    		for (VarSpecNode b : getChildren()) {
    			if (! (b instanceof VarSpecNodeDef))
    				result.add(b);
    		}
		}
		return result;
	}
	
	protected int numNonDefineChildren() {
		return getNonDefineChildren().size();
	}
	
    public VarSpecRoot endBuild() {
        if (! (this instanceof VarSpecRoot)) {
            throw new IllegalArgumentException("endBuild operation not allowed inside "+this.getClass());
        }
        return ((VarSpecRoot)this).endBuild();
    }

    public VarSpecNodeTable endBuildTable() {
        if (! (this instanceof VarSpecNodeTable)) {
            throw new IllegalArgumentException("endBuildTable operation not allowed inside "+this.getClass());
        }
        return ((VarSpecNodeTable)this).endBuildTable();
    }


	
	public VarSpecNodeDef define(String id, String ... keys) {
		if (keys.length == 0)
			throw new IllegalArgumentException("define must be used with at least one vp key");
		if (this instanceof VarSpecLeaf)
            throw new IllegalArgumentException("define cannot be applied to a leaf");
		VarSpecNodeDef definer= new VarSpecNodeDef(id, keys);
		definer.setChildren(new ArrayList<>());
		definer.setParent(this);
		this.getChildren().add(definer);
		return definer;
	}
	
	public VarSpecNode endDefine() {
		if (! (this instanceof VarSpecNodeDef)) {
			throw new IllegalArgumentException("endDefine operation not allowed inside "+this.getClass());
		}
		return ((VarSpecNodeDef)this).endDefine();
	}

	
	public VarSpecNodeCollectGroup collectGroup(String ... keys) {
        if (this instanceof VarSpecLeaf)
            throw new IllegalArgumentException("collectGroup cannot be applied to a leaf");
        VarSpecNodeCollectGroup collector= new VarSpecNodeCollectGroup(keys);
        collector.setChildren(new ArrayList<>());
		collector.setParent(this);
		this.getChildren().add(collector);
		return collector;		
	}
	
	public VarSpecNode endCollectGroup() {
		if (! (this instanceof VarSpecNodeCollectGroup)) {
			throw new IllegalArgumentException("endCollectGroup operation not allowed inside "+this.getClass());
		}
		return ((VarSpecNodeCollectGroup)this).endCollectGroup();
	}
    
	public VarSpecNodeCombineGroup combineGroup(String ... keys) {
        if (this instanceof VarSpecLeaf)
            throw new IllegalArgumentException("combineGroup cannot be applied to a leaf");
        VarSpecNodeCombineGroup combiner= new VarSpecNodeCombineGroup(keys);
        combiner.setChildren(new ArrayList<>());
		combiner.setParent(this);
		this.getChildren().add(combiner);
		return combiner;		
	}
	
	public VarSpecNode endCombineGroup() {
		if (! (this instanceof VarSpecNodeCombineGroup)) {
			throw new IllegalArgumentException("endCombineGroup operation not allowed inside "+this.getClass());
		}
		return ((VarSpecNodeCombineGroup)this).endCombineGroup();
	}

	public VarSpecNode combine(Object ... values) {
        if (this instanceof VarSpecLeaf)
            throw new IllegalArgumentException("combine cannot be applied to a leaf");
		VarSpecLeafCombine tuple= VarSpecLeafCombine.create(values);
		tuple.setParent(this);
		this.getChildren().add(tuple);
		validateNewChild(tuple);
		return this;		
	}
	
	public VarSpecNode ref(String id) {
        if (this instanceof VarSpecLeaf)
            throw new IllegalArgumentException("ref cannot be applied to a leaf");
		VarSpecLeafRef ref= new VarSpecLeafRef();
		ref.setId(id);
		ref.setParent(this);
		this.getChildren().add(ref);
		validateNewChild(ref);
		return this;		
	}

	
	
	public VarSpecNode collect(Object ... choices) {
        if (this instanceof VarSpecLeaf)
            throw new IllegalArgumentException("collect cannot be applied to a leaf");
		VarSpecLeafCollect leaf= VarSpecLeafCollect.create(choices);
		leaf.setParent(this);
		this.getChildren().add(leaf);
		validateNewChild(leaf);
		return this;		

	}
	
	public VarSpecNode range(Object first, Object last) {
		return range(first, last, null);
	}

	public VarSpecNode range(Object first, Object last, int steps) {
		return range(first, last, (long)steps);
	}
	
	public VarSpecNode range(Object first, Object last, Long steps) {
        if (this instanceof VarSpecLeaf)
            throw new IllegalArgumentException("range cannot be applied to a leaf");
		VarSpecLeafRange leaf= VarSpecLeafRange.createSpec(first, last, steps);
		leaf.setParent(this);
		this.getChildren().add(leaf);
		validateNewChild(leaf);
		return this;		

	}
	
	
	public VarSpecNode val(Object item) {
        if (this instanceof VarSpecLeaf)
            throw new IllegalArgumentException("val cannot be applied to a leaf");
		VarSpecLeafVal leaf= VarSpecLeafVal.create(item);
		leaf.setParent(this);
		this.getChildren().add(leaf);
		validateNewChild(leaf);
		return this;		

	}
	
	public VarSpecNode deriveVal(String jsSource) {
        if (this instanceof VarSpecLeaf)
            throw new IllegalArgumentException("deriveVal cannot be applied to a leaf");
		VarSpecLeafDerive leaf= new VarSpecLeafDerive(DerivativeAggregateType.VALUE, jsSource);
		leaf.setParent(this);
		this.getChildren().add(leaf);
		validateNewChild(leaf);
		return this;		

	}
	
	
	public VarSpecNode deriveCollect(String jsSource) {
        if (this instanceof VarSpecLeaf)
            throw new IllegalArgumentException("deriveCollect cannot be applied to a leaf");
        VarSpecLeafDerive leaf= new VarSpecLeafDerive(DerivativeAggregateType.COLLECTION, jsSource);
		leaf.setParent(this);
		this.getChildren().add(leaf);
		validateNewChild(leaf);
		return this;		

	}
	

	

	protected abstract void validateNewChild(VarSpecNode child) throws IllegalArgumentException;
	
	
	
	public void prettyPrint() {
    	prettyPrint(System.out);
    }

	public void prettyPrint(PrintStream out) {
		prettyPrint(out, "");
	}
	protected abstract void prettyPrint(PrintStream out, String prefix);

	
	@Override
	public String toString() {
		ByteArrayOutputStream baos= new ByteArrayOutputStream();
		PrintStream ps;
		try {
			ps = new PrintStream(baos, true, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError(e); // should never occur
		}
		prettyPrint(ps);
		String result= new String(baos.toByteArray(), StandardCharsets.UTF_8);
		return result;
	}
}
