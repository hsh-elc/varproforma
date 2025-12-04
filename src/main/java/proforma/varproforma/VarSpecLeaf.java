package proforma.varproforma;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public abstract class VarSpecLeaf extends VarSpecNode {

    public VarSpecLeaf() {
        
    }
    
    protected VarSpecLeaf(VarSpecLeaf other) {
        super(other);
    }

    @Override
    public CVp getCVp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCVp(CVp cvp) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<VarSpecNode> getChildren() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChildren(List<VarSpecNode> children) {
        throw new UnsupportedOperationException();
    }

}
