package proforma.varproforma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="materialization-type", propOrder = {
        "artifactId", "methodId"
    })
public class Materialization {

    @XmlElement(name="artifact-id")
    private List<String> artifactId;
    
    @XmlElement(name="method-id")
    private List<String> methodId;
  
    public Materialization() {
        
    }
    
    public Materialization(Materialization other) {
        if (other.artifactId != null) {
            this.artifactId= new ArrayList<>(other.artifactId);
        }
        if (other.methodId != null) {
            this.methodId= new ArrayList<>(other.methodId);
        }
    }
    
    public Materialization clone() {
        return new Materialization(this);
    }

    public List<String> getArtifactId() {
        if (artifactId == null) {
            artifactId = new ArrayList<>();
        }
        return this.artifactId;
    }
    public List<String> getMethodId() {
        if (methodId == null) {
            methodId = new ArrayList<>();
        }
        return this.methodId;
    }

    public Materialization addArtifactIds(String ... ids) {
        Collections.addAll(getArtifactId(), ids);
        return this;
    }
    public Materialization addMethodIds(String ... ids) {
        Collections.addAll(getMethodId(), ids);
        return this;
    }

    public Materialization addArtifactIds(Iterable<String> ids) {
        for (String id : ids) getArtifactId().add(id);
        return this;
    }
    public Materialization addMethodIds(Iterable<String> ids) {
        for (String id : ids) getMethodId().add(id);
        return this;
    }

    @Override
    public String toString() {
        return "Materialization [artifactId=" + artifactId + ", methodId=" + methodId + "]";
    }
}
