package org.proforma.variability.transfer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.proforma.variability.transfer.VarSpecNode.DefRefCollector;

/**
 * <p>A {@link V} object is a variant, i. e. a possible
 * value taken by a variation point.</p>
 * 
 * <p>A variant can be represented as a specification, that has to 
 * be resolved before materializing a task or an artifact, 
 * or as a value.</p>
 * 
 * <p>Variant objects belong to a variation point.
 * Usually a template specification defines several possible values
 * for each variation point. The possible values are defined either by
 * a set of {@link V} objects or by a set of value ranges represented by
 * a first and a last {@link V} object.</p>
 * 
 * <p>The {@link V} objects have a type that is taken from the
 * associated variation point. There are interval scaled and nominal 
 * scaled variants (see {@link Vis} and {@link Vns}).</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
public abstract class V implements Comparable<V>, java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @XmlTransient
    private Vp vp;
    
    /** This is available after push... having been called */
    public Vp getVp() {
        return vp;
    }

    protected void setVp(Vp vp) {
        this.vp= vp;
    }
    
    public abstract V clone();


    public DefRefCollector collectDefRefs() {
        return new DefRefCollector(); // default implementation
    }
    

    final public void setParent(VarSpecNode parent) {
        if (getSpec() instanceof VarSpecNode) {
            ((VarSpecNode)getSpec()).setParent(parent);
        }

    }
    
    public void pushInheritedCVpToChildren(Vp vp) {
        if (vp == null) {
            throw new IllegalArgumentException(this.getClass().getName()+".getSpec received unexpected vp=null");
        }
        if (!VpT.fromV(this).equals(vp.getType())) {
            throw new IllegalArgumentException("Type mismatch! Cannot "+this.getClass().getName()+".pushInheritedCVpToChildren(vp) for vp '"+vp.getKey()+"' of type '"+vp.getType()+"' (expected type '"+VpT.fromV(this)+"')");
        }

        this.vp= vp;

        // by default do nothing more
        // subclasses like St might change this behaviour.
    }
    
    
    public abstract Object getSpec();
    public abstract Class<?> getSpecType();
    public abstract Object getValue();
    public abstract Class<?> getValueType();
    public abstract boolean isSpec();
    public abstract boolean isValue();
    public abstract V switchToSpec();
    public abstract V switchToValue();
    
    protected abstract Character getSpecTypeSymbol();
    public abstract double distanceTo(V choice);
    
    public abstract boolean isIntervalScaled();
    
    @Override
    public abstract int hashCode();
    
    @Override
    public abstract boolean equals(Object o);


    @Override public String toString() {
        return toString("");
    }
    
    public String toString(String prefix) {
        return prefix + String.format("%c:%s", getSpecTypeSymbol(), getSpec());
    }


    public static String nullToString() {
        return "-:null";
    }

    public static V fromString(String s) {
        Character symbol= s.charAt(0);
        s= s.substring(2);
        switch (symbol) {
        case  'I': return new Vi(null, Integer.parseInt(s));
        case  'D': return new Vd(null, Double.parseDouble(s));
        case  'C': return new Vc(null, s.charAt(0));
        case  'S': return new Vs(null, s);
        case  'B': return new Vb(null, Boolean.parseBoolean(s));
        case  'T': throw new UnsupportedOperationException("V.fromString not supported for type "+Vt.class);
        case  '-': return null;
        }
        return null;
    }
    

    
    
    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(V other) {
        Object v1= this.getSpec();
        Object v2= other.getSpec();
        if (v1 == null && v2 == null) return 0;
        if (v1 == null) return -1;
        if (v2 == null) return 1;
        if (v1 instanceof Comparable<?>) return ((Comparable<Object>)v1).compareTo(v2);
        return this.toString().compareTo(other.toString());
    }

    
    public static V fromSpec(Object spec, Vp vp) {
        V s= fromSpec(spec, vp, vp.getType().getSpecConstructor());
        return s;
    }

    public static V fromSpec(Object spec) {
        VpT type;
        if (spec instanceof V) {
            V v= (V)spec;
            if (v.getVp() == null) return v;
            type= v.getVp().getType();
        } else {
            type= VpT.fromSpec(spec);
        }
        V s= fromSpec(spec, null, type.getSpecConstructor());
        return s;
    }

    
    private static V fromSpec(Object spec, Vp vp, Constructor<? extends V> constructor) {
        try {
            if (spec instanceof V) {
                spec= ((V)spec).getSpec();
            }
            return (V) constructor.newInstance(vp, spec);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new IllegalArgumentException("In "+V.class+".fromSpec(spec,"+vp+",constructor): Cannot create instance of "+V.class+" via constructor("+(vp==null?null:vp)+","+(spec==null?null:spec)+")", e);
        }
    }
    

    
    public static V fromValue(Object value, Vp vp) {
        V s= fromValue(value, vp, vp.getType().getValueConstructor());
        return s;
    }

    public static V fromValue(Object value) {
        VpT type;
        if (value instanceof V) {
            V v= (V)value;
            if (v.getVp() == null) return v;
            type= v.getVp().getType();
        } else {
            type= VpT.fromValue(value);
        }
        V s= fromValue(value, null, type.getValueConstructor());
        return s;
    }

    private static V fromValue(Object value, Vp vp, Constructor<? extends V> constructor) {
        try {
            if (value instanceof V) {
                value= ((V)value).getValue();
            }
            return (V) constructor.newInstance(vp, value);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new IllegalArgumentException("In "+V.class+".fromValue(value,"+vp+",constructor): Cannot create instance of "+V.class+" via constructor("+(vp==null?null:vp)+","+(value==null?null:value)+")", e);
        }
    }
    
    public static Object convertToValue(Vp vp, Object spec) {
        return V.fromSpec(spec, vp).getValue();
    }
    
    public static Object convertToSpec(Vp vp, Object value) {
        return V.fromValue(value, vp).getSpec();
    }
    

    

}
