package proforma.varproforma;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({
    VarSpecReorderingNode.class,
    VarSpecRoot.class})
@XmlType(name="inner-node-type", propOrder = {"children"})
public abstract class VarSpecInnerNode extends VarSpecNode {

    private static final long serialVersionUID = 1L;

    @XmlElements({
        @XmlElement(name="val", type=VarSpecLeafVal.class, required = true),
        @XmlElement(name="collect-group", type=VarSpecNodeCollectGroup.class, required = true),
        @XmlElement(name="combine-group", type=VarSpecNodeCombineGroup.class, required = true),
        @XmlElement(name="combine", type=VarSpecLeafCombine.class, required = true),
        @XmlElement(name="collect", type=VarSpecLeafCollect.class, required = true),
        @XmlElement(name="range", type=VarSpecLeafRange.class, required = true),
        @XmlElement(name="def", type=VarSpecNodeDef.class, required = true),
        @XmlElement(name="ref", type=VarSpecLeafRef.class, required = true),
        @XmlElement(name="derive", type=VarSpecLeafDerive.class, required = true)
    })
    private List<VarSpecNode> children;

    @XmlTransient
    private CVp cvp;


    public VarSpecInnerNode() {
        
    }
    
    public VarSpecInnerNode(VarSpecInnerNode other) {

        if (other.cvp != null) {
            this.cvp= other.cvp.clone();
        }

        if (other.children != null) {
            this.children= new ArrayList<VarSpecNode>();
            for (VarSpecNode otherChild : other.children) {
                Constructor<? extends VarSpecNode> constr;
                VarSpecNode child= null;
                try {
                    constr = otherChild.getClass().getDeclaredConstructor(otherChild.getClass());
                    child= constr.newInstance(otherChild);
                } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new Error("Cannot copy construct "+this.getClass(), e);
                }
                this.children.add(child);
                child.setParent(this);
            }
        }        
    }
    
    
    /**
     * @return list of child nodes
     * @exception UnsupportedOperationException if this is a {@link VarSpecLeafRef} or
     * {@link VarSpecLeafCombine}.
     */
    @Override
    public List<VarSpecNode> getChildren() {
        return children;
    }

    /**
     * @param children list of child nodes
     * @exception UnsupportedOperationException if this is a {@link VarSpecLeafRef} or
     * {@link VarSpecLeafCombine}.
     */
    public void setChildren(List<VarSpecNode> children) {
        this.children = children;
    }
    
    
    /**
     * @return list of variation points.
     * @exception UnsupportedOperationException if this is a {@link VarSpecLeafRef} or
     * {@link VarSpecLeafCombine}.
     */
    public CVp getCVp() {
        return cvp;
    }

    /**
     * @param cvp list of variation points
     * @exception UnsupportedOperationException if this is a {@link VarSpecLeafRef} or
     * {@link VarSpecLeafCombine}.
     */
    public void setCVp(CVp cvp) {
        this.cvp = cvp;
    }
    
    /**
     * @return the first child that is not a {@link VarSpecNodeDef} node. Or null, if it does not exist.
     */
    public VarSpecNode getFirstNonDefineChild() {
        if (getChildren() != null) {
            for (VarSpecNode b : getChildren()) {
                if (! (b instanceof VarSpecNodeDef))
                    return b;
            }
        }
        return null;
    }
    


    
}
