package proforma.varproforma;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="child-ref-ref-type", propOrder = {"type", "ref", "subRef"})
public class ChildRefRef {
    
    @XmlAttribute(required=true)
    private String type;
    
    @XmlAttribute(required=true)
    private String ref;
    
    @XmlAttribute(name="sub-ref", required=false)
    private String subRef;

    
    public ChildRefRef() {
        
    }
    
    public static ChildRefRef combine(String ref) {
        return new ChildRefRef(false, ref, null);
    }
    
    public static ChildRefRef test(String ref, String subRef) {
        return new ChildRefRef(true, ref, subRef);
    }
    public static ChildRefRef test(String ref) {
        return new ChildRefRef(true, ref, null);
    }
    
    public ChildRefRef(boolean isTestRef, String ref, String subRef) {
        this.type= isTestRef ? "test" : "combine";
        this.ref = ref;
        this.subRef = subRef;
    }
    
    
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public boolean isTestRef() {
        return type.equals("test");
    }
    public String getRef() {
        return ref;
    }
    public void setRef(String ref) {
        this.ref = ref;
    }
    public String getSubRef() {
        return subRef;
    }
    public void setSubRef(String subRef) {
        this.subRef = subRef;
    }
    @Override
    public int hashCode() {
        return Objects.hash(type, ref, subRef);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChildRefRef other = (ChildRefRef) obj;
        return Objects.equals(type, other.type) && Objects.equals(ref, other.ref) && Objects.equals(subRef, other.subRef);
    }
    @Override
    public String toString() {
        if (subRef == null) return type+"("+ref+")";
        return type+"("+ref + "," + subRef + ")";
    }
    
    
    
}