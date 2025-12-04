package proforma.varproforma;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import proforma.varproforma.util.Ribbon;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="mat-spec-type", propOrder = {
        "matArtifacts", "matMethods", "materializations", "any"
    })
public class MatSpec {
    @XmlElement(name="mat-artifacts", required=true)
    private MatArtifacts matArtifacts;

    @XmlElement(name="mat-methods", required=true)
    private MatMethods matMethods;

    @XmlElement(name="materializations", required=true)
    private Materializations materializations;

    @XmlAnyElement(lax = true)
    protected List<Object> any;

    public MatSpec() {
        
    }

    public MatSpec(MatSpec other) {
        if (other.matArtifacts != null) {
            this.matArtifacts= new MatArtifacts(other.matArtifacts);
        }
        if (other.matMethods != null) {
            this.matMethods= new MatMethods(other.matMethods);
        }
        if (other.materializations != null) {
            this.materializations= new Materializations(other.materializations);
        }
        if (other.any != null) {
            this.any= new ArrayList<>(other.any);
        }
    }

    
    public MatArtifacts getMatArtifacts() {
        return matArtifacts;
    }

    public void setMatArtifacts(MatArtifacts matArtifacts) {
        this.matArtifacts = matArtifacts;
    }

    public MatMethods getMatMethods() {
        return matMethods;
    }

    public void setMatMethods(MatMethods matMethods) {
        this.matMethods = matMethods;
    }

    public Materializations getMaterializations() {
        return materializations;
    }

    public void setMaterializations(Materializations materializations) {
        this.materializations = materializations;
    }
    
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    public MatSpec addArtifact(MatArtifact o) {
        if (matArtifacts == null) matArtifacts= new MatArtifacts();
        matArtifacts.addArtifact(o);
        return this;
    }

    public MatSpec addMethod(MatMethod m) {
        if (matMethods == null) matMethods= new MatMethods();
        matMethods.addMethod(m);
        return this;
    }
    
    public MatSpec addMaterialization(Materialization r) {
        for (MatMethod method : getMatMethodsOf(r)) {
            for (MatArtifact artifact : getMatArtifactsOf(r)) {
                // Für einige Methods ist es jetzt zwar noch zu früh, weil diese erst nach dem
                // init-Aufruf ihren Typ kennen. Für alle anderen machen wir es aber jetzt schon:
                if (method.getGenericType() != null && artifact.getValueType() != null && !method.getGenericType().equals(artifact.getValueType())) {
                    throw new IllegalArgumentException("MatSpec: materialization has inconsistent generic types (artifact '"+artifact.getId()+"':"+artifact.getValueType()+", method '"+method.getId()+"':"+method.getGenericType()+")");
                
                }
            }
        }
        if (materializations == null) materializations= new Materializations();
        materializations.addMaterialization(r);
        return this;
    }
    
    public MatSpec addMaterialization(MatArtifact artifact, MatMethod ... methods) {
        if (findMatArtifact(artifact.getId()) != null) {
            throw new IllegalArgumentException("Duplicate artifact '"+artifact.getId()+"' in MatSpec");
        }
        addArtifact(artifact);
        return addMaterialization(artifact.getId(), methods);
    }
    
    public MatSpec addMaterialization(MatMethod method, MatArtifact ... artifacts) {
        if (findMatMethod(method.getId()) != null) {
            throw new IllegalArgumentException("Duplicate method '"+method.getId()+"' in MatSpec");
        }
        addMethod(method);
        return addMaterialization(method.getId(), artifacts);
    }
    
    public MatSpec addMaterialization(String methodId, MatArtifact ... artifacts) {
        Materialization materialization= new Materialization().addMethodIds(methodId);
        for (MatArtifact artifact : artifacts) {
            addArtifact(artifact);
            materialization.addArtifactIds(artifact.getId());
        }
        addMaterialization(materialization);
        return this;
    }

    public MatSpec addMaterialization(String artifactId, MatMethod ... methods) {
        Materialization materialization= new Materialization().addArtifactIds(artifactId);
        for (MatMethod method : methods) {
            addMethod(method);
            materialization.addMethodIds(method.getId());
        }
        addMaterialization(materialization);
        return this;
    }
    


    

    public Iterable<MatArtifact> getMatArtifactsOf(Materialization r) {
        return new Iterable<MatArtifact>() {
            @Override public Iterator<MatArtifact> iterator() {
                Iterator<String> iter= r.getArtifactId().iterator();
                return new Iterator<MatArtifact>() {
                    @Override public boolean hasNext() {
                        return iter.hasNext();
                    }
                    @Override public MatArtifact next() {
                        return getMatArtifacts().findMatArtifact(iter.next());
                    }
                };
            }
        };
    }
    
    public Iterable<MatMethod> getMatMethodsOf(Materialization r) {
        return new Iterable<MatMethod>() {
            @Override public Iterator<MatMethod> iterator() {
                Iterator<String> iter= r.getMethodId().iterator();
                return new Iterator<MatMethod>() {
                    @Override public boolean hasNext() {
                        return iter.hasNext();
                    }
                    @Override public MatMethod next() {
                        return getMatMethods().findMatMethod(iter.next());
                    }
                };
            }
        };
    }
    

    /**
     * Executes all Materializations. The order is: 
     * <pre>
     * for each Materialization (in insertion order as specified by {@link #addMaterialization(Materialization)})
     *   for each MatMethod in the materialization (in insertion order as specified by {@link Materialization#addMethodIds(String...)})
     *     for each MatArtifact in the materialization (in insertion order as specified by {@link Materialization#addArtifactIds(String...)})
     *       execute the method on the artifact
     * </pre>
     */
    public void executeMaterializations(boolean assumeFullyEquippedArtifacts) {
        for (Materialization materialization : getMaterializations().getMaterialization()) {
            for (MatMethod method : getMatMethodsOf(materialization)) {
                for (MatArtifact artifact : getMatArtifactsOf(materialization)) {
                    if (method.getGenericType() != null && artifact.getValueType() != null && !method.getGenericType().equals(artifact.getValueType())) {
                        throw new IllegalArgumentException("MatSpec: materialization has inconsistent generic types (artifact '"+artifact.getId()+"':"+artifact.getValueType()+", method '"+method.getId()+"':"+method.getGenericType()+")");
                    }
                    if (assumeFullyEquippedArtifacts ^ artifact.operatesOnTaskXml()) {
                        for (Ribbon<?> item : artifact.getProvider().getItems()) {
                            execute(method, item);
                        }
                    }
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void execute(MatMethod method, Ribbon<?> item) {
        @SuppressWarnings("rawtypes")
        Supplier supplier= (Supplier)item.getSource();
        @SuppressWarnings("rawtypes")
        Consumer consumer= (Consumer)item.getTarget();
        method.getProvider().execute(supplier, consumer, item.getHint());
        
    }
    
    
    public <T> T findAnyByInstanceOfClass(Class<T> clazz) {
        if (getAny() != null) {
            for (Object o : getAny()) {
                if (clazz.isAssignableFrom(o.getClass())) {
                    @SuppressWarnings("unchecked")
                    T result= (T)o;
                    return result;
                }
            }
        }
        return null;
    }

    public MatArtifact findMatArtifact(String id) {
        if (matArtifacts == null) return null;
        return matArtifacts.findMatArtifact(id);
    }
    
    public MatMethod findMatMethod(String id) {
        if (matMethods == null) return null;
        return matMethods.findMatMethod(id);
    }
    
    /**
     * Adds artifacts to the first materialization containing the given methodId.
     * @param methodId
     * @param artifacts
     */
    public void addArtifactToExistingMethodId(String methodId, MatArtifact ... artifacts) {
        for (Materialization materialization : getMaterializations().getMaterialization()) {
            for (MatMethod method : getMatMethodsOf(materialization)) {
                if (method.getId().equals(methodId)) {
                    for (MatArtifact a : artifacts) {
                        materialization.addArtifactIds(a.getId());
                        this.addArtifact(a);
                    }
                    return;
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return "MatSpec [matArtifacts=" + matArtifacts + ", matMethods=" + matMethods + ", materializations=" + materializations
                + ", any=" + any + "]";
    }

}
