package proforma.varproforma;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import proforma.varproforma.util.MatMethodProvider;

/**
 * <p>A MatMethod applies some operation to data supplied by a MatArtifact; the result of the operation
 * is passed to the MatArtifact's consumption feature.</p>
 * 
 * <p>A MatMethod gets its data from two sources. First, the artifact supplies a value
 * to the method. Second the variation point resolution supplies some variation point
 * values. The MatMethod may decide, to restrict its attention to a specified subset of
 * all variation points. By default all variation points are passed to the method.</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="mat-method-type")
public class MatMethod {
    
    @XmlAttribute(required=true)
    private String id;
    
    @XmlAttribute(required=true, name="method-type")
    private MatMethodType methodType;
    

    @XmlElement(required=false, name="restrict-vp")
    private List<String> restrictVp;

    @XmlAttribute(required=false)
    private String prefix;
    
    @XmlAttribute(required=false)
    private String suffix;

    @XmlAttribute(required=false)
    private MatMethodOperator operator;

//    @XmlElement(required=false)
//    private List<MatMethodMapping> mappings;
//    
//    @XmlElements({
//        @XmlElement(name="default-value-integer", type=Vi.class),
//        @XmlElement(name="default-value-double", type=Vd.class),
//        @XmlElement(name="default-value-string", type=Vs.class),
//        @XmlElement(name="default-value-boolean", type=Vb.class),
//        @XmlElement(name="default-value-character", type=Vc.class),
//        @XmlElement(name="default-value-table", type=Vt.class)
//    })
//    private V defaultValue;
//    
//
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    @XmlTransient
    private MatMethodProvider<?> provider;

    
    public static MatMethod mustache(String id, List<String> restrictVp, String prefix, String suffix) {
        return new MatMethod(id, MatMethodType.MUSTACHE, restrictVp).setPrefix(prefix).setSuffix(suffix);
    }

    public static MatMethod setVpValue(String id, List<String> restrictVp) {
        return new MatMethod(id, MatMethodType.SET_VP_VALUE, restrictVp);
    }

//    public static MatMethod mapVpValue(String id, List<String> restrictVp, V defaultValue) {
//        return new MatMethod(id, MatMethodType.MAP_VP_VALUE, restrictVp).setDefaultValue(defaultValue);
//    }
//    
    public static MatMethod arithmeticOperation(String id, List<String> restrictVp, MatMethodOperator operator) {
        return new MatMethod(id, MatMethodType.ARITHMETIC_OPERATION, restrictVp).setOperator(operator);
    }

    public static MatMethod other(String id, List<String> restrictVp) {
        return new MatMethod(id, MatMethodType.OTHER, restrictVp);
    }

    public MatMethod() {
        
    }
    public MatMethod(String id, MatMethodType methodType, List<String> restrictVp) {
        this.id= id;
        this.methodType= methodType;
        this.restrictVp= restrictVp;
    }
    
    public MatMethod(MatMethod other) {
        this(other.id, other.methodType, other.restrictVp == null ? null : new ArrayList<>(other.restrictVp));
        this.prefix= other.prefix;
        this.suffix= other.suffix;
        this.operator= other.operator;
//        this.defaultValue= other.defaultValue;
//        if (other.mappings != null) {
//            this.mappings= new ArrayList<>(other.mappings);
//        }
        if (other.any != null) {
            this.any= new ArrayList<>(other.any);
        }
    }

    public MatMethod clone() {
        return new MatMethod(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
    public MatMethodType getMethodType() {
        return methodType;
    }

    public List<String> getRestrictVp() {
        if (restrictVp == null)
            restrictVp= new ArrayList<>();
        return restrictVp;
    }
    
    
    
    public String getPrefix() {
        return prefix;
    }

    public MatMethod setPrefix(String prefix) {
        if (prefix == null || prefix.length() == 2) {
            this.prefix = prefix;
        } else {
            throw new IllegalArgumentException("mustache expects prefix of 2 characters length");
        }
        return this;
    }

    public String getSuffix() {
        return suffix;
    }

    public MatMethod setSuffix(String suffix) {
        if (suffix == null || suffix.length() == 2) {
            this.suffix = suffix;
        } else {
            throw new IllegalArgumentException("mustache expects suffix of 2 characters length");
        }
        return this;
    }
    

    
    public MatMethodOperator getOperator() {
        return operator;
    }


    public MatMethod setOperator(MatMethodOperator operator) {
        this.operator = operator;
        return this;
    }

//    public List<MatMethodMapping> getMappings() {
//        return this.mappings;
//    }
//    
//    public MatMethod map(V from, V to) {
//        if (this.mappings == null) {
//            this.mappings= new ArrayList<>();
//        }
//        this.mappings.add(new MatMethodMapping(from, to));
//        return this;
//    }
//    
//    
//    
//    public V getDefaultValue() {
//        return defaultValue;
//    }
//
//    public MatMethod setDefaultValue(V defaultValue) {
//        this.defaultValue = defaultValue;
//        return this;
//    }

    public List<Object> getAny() {
        return any;
    }

    public MatMethod includeAny(Object ... anys) {
        if (this.any == null) {
            this.any= new ArrayList<Object>();
        }
        for (Object any : anys) {
            if (any == null) {
                throw new IllegalArgumentException("matMethod does not accept null as any");
            }
            if (!this.any.contains(any))
                this.any.add(any);
        }
        return this;
    }


    public <T> T findAnyByInstanceOfClass(Class<T> clazz) {
        if (any != null) {
            for (Object o : any) {
                if (clazz.isAssignableFrom(o.getClass())) {
                    @SuppressWarnings("unchecked")
                    T result= (T)o;
                    return result;
                }
            }
        }
        return null;
    }

    public Class<?> getGenericType() {
        if (provider == null) return null;
        return provider.getValueType();
    }

    public MatMethodProvider<?> getProvider() {
        return provider;
    }
    public void setProvider(MatMethodProvider<?> provider) {
        this.provider= provider;
    }

    @Override
    public String toString() {
        return "MatMethod [id=" + id + "]";
    }
    
    public void init(CV cv) {
        if (this.provider == null) {
            Class<? extends MatMethodProvider<?>> providerClass= methodType.getProviderClass();
            if (providerClass == null) {
                throw new IllegalArgumentException("Cannot init mat method. provider missing");
            }
            try {
                this.provider= providerClass.getDeclaredConstructor().newInstance();
            } catch (IllegalAccessException | IllegalArgumentException | SecurityException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                throw new IllegalArgumentException("Cannot instantiate mat method provider", e);
            }
        }
        this.provider.init(this, cv);
    }
    
    
}
