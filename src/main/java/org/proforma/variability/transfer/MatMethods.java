package org.proforma.variability.transfer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="mat-methods-type")
public class MatMethods {
    
    @XmlElement(name="mat-method")
    private List<MatMethod> matMethod;

    public MatMethods() {
        
    }

    public MatMethods(MatMethods other) {
        if (other.matMethod != null) {
            this.matMethod= new ArrayList<>();
            for (MatMethod o : other.getMatMethod()) {
                this.matMethod.add(o.clone());
            }
        }
    }

    public List<MatMethod> getMatMethod() {
        if (matMethod == null) {
            matMethod = new ArrayList<>();
        }
        return this.matMethod;
    }

    
    MatMethod findMatMethod(String id) {
        for (MatMethod m : getMatMethod()) {
            if (id.equals(m.getId())) return m;
        }
        return null;
    }
    
//    private boolean containsMatMethod(String id) {
//        return findMatMethod(id) != null;
//    }
    
    
    public MatMethods addMethod(MatMethod m) {
        getMatMethod().add(m);
        return this;
    }

    @Override
    public String toString() {
        return "MatMethods [matMethod=" + matMethod + ", toString()=" + super.toString() + "]";
    }
    

}
