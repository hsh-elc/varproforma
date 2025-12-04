package proforma.varproforma;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import proforma.varproforma.util.MatArtifactProvider;
import proforma.varproforma.util.Ribbon;
import proforma.varproforma.util.TaskUnzipped;

/**
 * <p>A MatArtifact is part of a ProFormA-Task which is subject to materialization.</p>
 * 
 * <p>
 * The artifact references parts like a file, a weight, or the whole task.
 * A single MatArtifact instance may reference many similar parts at once, e. g. 
 * all files of a task. The materialization of an artifact is performed
 * by a {@link MatMethod}. For this, the MatMethod relies on the artifact to
 * supply data as a template and to consume data as the materialization result (the instance).
 * </p>
 * 
 * <p>Examples:</p>
 * <ul>
 * <li>Given a MatArtifact of type {@link MatArtifactType#ATTACHED_TXT_FILE_CONTENTS}
 * that supplies and consumes the content of an attached file.
 * The supplied content contains placeholders for various variation points.
 * The MatMethod might be a search and replace routine provided by
 * {@code MatMethodMustache} that scans the whole
 * string supplied by the artifact, replaces all placeholder occurrences by
 * actual variation point resolution values, and sends the result to the artifact's
 * consumption method. </li>
 * 
 * <li>A MatArtifact of type {@link MatArtifactType#GRADING_HINTS_WEIGHTS}
 * supplies and consumes weights of several TestRef references 
 * from the grading-hints. We apply a MatMethod to the artifact that
 * should multiply all these weights by a factor during
 * materialization, where the factor is resolved from a specific variation point.
 * The multiplying MatMethod receives the weight value from the artifact's supplier.
 * The MatMethod resolves the variation point and multiplies the resolved
 * value to the weight. Usually there would be a single variation point
 * multiplied to the weight, but there could be a set of variation points, that 
 * will get resolved one by one and multiplied to the weight. The resulting product
 * is passed to the artifacts consumption method, which will overwrite the original
 * weight value by the new one.</li>
 * </ul>
 * 
 * <p>In summary, an artifact defines a data supplier and a data consumer. These two
 * components are used by  a MatMethod 
 * to perform materialization.</p>
 * 
 * <p>There are some artifacts defined as standard artifacts. A grader may define
 * further artifacts by using the type {@link MatArtifactType#OTHER} and
 * by implementing a {@link MatArtifactProvider} subclass that implements
 * the grader-specific supplying and consuming methods. A grader-specific
 * artifact must include a {@link #dataType} value and a {@link #operatesOnTaskXml}
 * value and it
 * may store additional data in the {@link #any} field.</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="mat-artifact-type")
public class MatArtifact {

    @XmlAttribute(required=true)
    private String id;
    
    @XmlAttribute(required=true, name="artifact-type")
    private MatArtifactType artifactType;
    
    @XmlAttribute(required=false, name="data-type")
    private VpT dataType;

    @XmlAttribute(required=false, name="operates-on-task-xml")
    private Boolean operatesOnTaskXml;

    @XmlElement(required=false, name="file-id")
    private List<String> fileId;

    @XmlElement(required=false)
    private List<String> path;
    
    @XmlElement(required=false)
    private List<ChildRefRef> ref;

    @XmlAnyElement(lax = true)
    protected List<Object> any;

    
    @XmlTransient
    private MatArtifactProvider<?> provider;

    public static MatArtifact other(String id, VpT dataType, boolean operatesOnTaskXml) {
        return new MatArtifact(id, MatArtifactType.OTHER, dataType, operatesOnTaskXml);
    }
    public static MatArtifact taskXml(String id) {
        return new MatArtifact(id, MatArtifactType.TASK_XML);
    }
    public static MatArtifact fileNames(String id) {
        return new MatArtifact(id, MatArtifactType.FILE_NAMES);
    }
    public static MatArtifact attachedTxtFileContents(String id) {
        return new MatArtifact(id, MatArtifactType.ATTACHED_TXT_FILE_CONTENTS);
    }
    public static MatArtifact fileExistences(String id) {
        return new MatArtifact(id, MatArtifactType.FILE_EXISTENCES);
    }
    public static MatArtifact gradingNodeExistences(String id) {
        return new MatArtifact(id, MatArtifactType.GRADING_NODE_EXISTENCES);
    }
    public static MatArtifact gradingHintsWeights(String id) {
        return new MatArtifact(id, MatArtifactType.GRADING_HINTS_WEIGHTS);
    }
    
    public MatArtifact() {
        
    }
    public MatArtifact(String id, MatArtifactType artifactType) {
        this(id, artifactType, null, null);
    }
    public MatArtifact(String id, MatArtifactType artifactType, VpT dataType, Boolean operatesOnTaskXml) {
        this.id= id;
        this.artifactType= artifactType;
        this.dataType= dataType;
        this.operatesOnTaskXml= operatesOnTaskXml;
        validate();
    }
    
    public MatArtifact(MatArtifact other) {
        this(other.id, other.artifactType, other.dataType, other.operatesOnTaskXml);
        if (other.fileId != null) {
            this.fileId= new ArrayList<>(other.fileId);
        }
        if (other.path != null) {
            this.path= new ArrayList<>(other.path);
        }
        if (other.ref != null) {
            this.ref= new ArrayList<>(other.ref);
        }
        if (other.any != null) {
            this.any= new ArrayList<>(other.any);
        }
    }

    public MatArtifact clone() {
        return new MatArtifact(this);
    }

    private void validate() {
        if (MatArtifactType.OTHER.equals(this.artifactType)) {
            if (this.dataType == null) {
                throw new IllegalArgumentException("A matArtifact of type 'other' must include a dataType attribute. Found: none.");
            }
            if (this.operatesOnTaskXml == null) {
                throw new IllegalArgumentException("A matArtifact of type 'other' must include an operatesOnTaskXml attribute. Found: none.");
            }
        }
    }
    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        validate();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    
    public MatArtifactType getArtifactType() {
        return artifactType;
    }
    
    public VpT getDataType() {
        if (this.dataType != null) return dataType;
        return artifactType.getDataType();
    }

    public Class<?> getValueType() {
        return getDataType().getAssociatedValueType();
    }

    public boolean operatesOnTaskXml() {
        if (this.operatesOnTaskXml != null) return operatesOnTaskXml;
        return artifactType.operatesOnTaskXml();
    }


    public List<String> getFileId() {
        return fileId;
    }
    public List<String> getPath() {
        return path;
    }
    public List<ChildRefRef> getRef() {
        return ref;
    }
    public List<Object> getAny() {
        return any;
    }

    public MatArtifact includeFileIds(String ... fileIds) {
        if (this.fileId == null) {
            this.fileId= new ArrayList<>();
        }
        for (String fileId : fileIds) {
            if (fileId == null) {
                throw new IllegalArgumentException("matArtifact does not accept null as fileId");
            }
            if (!this.fileId.contains(fileId))
                this.fileId.add(fileId);
        }
        return this;
    }
    
    public MatArtifact includePaths(String ... paths) {
        if (this.path == null) {
            this.path= new ArrayList<>();
        }
        for (String path : paths) {
            if (path == null) {
                throw new IllegalArgumentException("matArtifact does not accept null as path");
            }
            if (!this.path.contains(path))
                this.path.add(path);
        }
        return this;
    }
    
    public MatArtifact includeChildRefs(ChildRefRef ... childRefs) {
        if (this.ref == null) {
            this.ref= new ArrayList<ChildRefRef>();
        }
        for (ChildRefRef childRef : childRefs) {
            if (childRef == null) {
                throw new IllegalArgumentException("matArtifact does not accept null as childRef");
            }
            if (!this.ref.contains(childRef))
                this.ref.add(childRef);
        }
        return this;
    }
    
    public MatArtifact includeAny(Object ... anys) {
        if (this.any == null) {
            this.any= new ArrayList<Object>();
        }
        for (Object any : anys) {
            if (any == null) {
                throw new IllegalArgumentException("matArtifact does not accept null as any");
            }
            if (!this.any.contains(any))
                this.any.add(any);
        }
        return this;
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


    @Override
    public String toString() {
        return "MatArtifact [id=" + id + "]";
    }
    
    public MatArtifactProvider<?> getProvider() {
        return provider;
    }
    public void setProvider(MatArtifactProvider<?> provider) {
        this.provider= provider;
    }
    
    public void init(Ribbon<TaskUnzipped> rtu) {
        if (this.provider == null) {
            Class<? extends MatArtifactProvider<?>> providerClass= artifactType.getProviderClass();
            if (providerClass == null) {
                throw new IllegalArgumentException("Cannot init mat artifact. provider missing");
            }
            try {
                this.provider= providerClass.getDeclaredConstructor().newInstance();
            } catch (IllegalAccessException | IllegalArgumentException | SecurityException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                throw new IllegalArgumentException("Cannot instantiate mat artifact provider", e);
            }
        }
        this.provider.init(this, rtu);
    }
    
}
