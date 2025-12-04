package proforma.varproforma;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="mat-artifacts-type")
public class MatArtifacts {

    @XmlElement(name="mat-artifact")
    private List<MatArtifact> matArtifact;

    
    public MatArtifacts() {
        
    }

    public MatArtifacts(MatArtifacts other) {
        if (other.matArtifact != null) {
            this.matArtifact= new ArrayList<>();
            for (MatArtifact o : other.getMatArtifact()) {
                this.matArtifact.add(o.clone());
            }
        }
    }

    public List<MatArtifact> getMatArtifact() {
        if (matArtifact == null) {
            matArtifact = new ArrayList<>();
        }
        return this.matArtifact;
    }

    MatArtifact findMatArtifact(String id) {
        for (MatArtifact m : getMatArtifact()) {
            if (id.equals(m.getId())) return m;
        }
        return null;
        
    }
//    private boolean containsMatArtifact(String id) {
//        return findMatArtifact(id) != null;
//    }
    
    public MatArtifacts addArtifact(MatArtifact o) {
        getMatArtifact().add(o);
        return this;
    }

    @Override
    public String toString() {
        return "MatArtifacts [matArtifact=" + matArtifact + "]";
    }
    


}
