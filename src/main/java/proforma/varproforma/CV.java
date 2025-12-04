package proforma.varproforma;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;




/**
 * A composite variant specification is a tuple of variation specifications.
 * Every value itself can be a simple value like an integer or a string
 * or a complex table-specification. 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cv-type")
public class CV implements Iterable<V> , java.io.Serializable {

	private static final long serialVersionUID = 1L;

    public CV() {
		
	}
	
	public CV(V ... variants) {
        this.variants= new ArrayList<>();
        for (V s : variants) {
            this.variants.add(s);
        }
	}
	
	public CV(List<V> variants) {
		this.variants= variants;
	}
	
	public CV(CV other) {
		if (other.variants != null) {
			variants= new ArrayList<>();
			for (V s : other.variants) {
				variants.add(s.clone());
			}
		}
	}
	
	public CV clone() {
	    return new CV(this);
	}
	
    public static CV fromSpecs(Object ... specData) {
        CV result= new CV();
        ArrayList<V> myVariants= new ArrayList<>();
        for (Object item : specData) {
            myVariants.add(V.fromSpec(item));
        }
        result.setVariants(myVariants);
        return result;
    }
    
    public static CV fromValues(Object ... valueData) {
        CV result= new CV();
        ArrayList<V> myVariants= new ArrayList<>();
        for (Object item : valueData) {
            myVariants.add(V.fromValue(item));
        }
        result.setVariants(myVariants);
        return result;
    }
    
	void pushCVp(CVp cvp) {
        if (cvp != null && variants != null) {
            for (int i=0; i<cvp.size(); i++) {
                variants.get(i).pushInheritedCVpToChildren(cvp.get(i));
            }
        }
	}
	
	@XmlElements({
		@XmlElement(name="integer", type=Vi.class),
		@XmlElement(name="double", type=Vd.class),
		@XmlElement(name="string", type=Vs.class),
		@XmlElement(name="boolean", type=Vb.class),
        @XmlElement(name="character", type=Vc.class),
        @XmlElement(name="table", type=Vt.class)
	})
    private List<V> variants;

	public List<V> getVariants() {
		return variants;
	}

	public void setVariants(List<V> variants) {
		this.variants = variants;
	}
	
    public CV switchToValue() {
        if (variants != null) {
            for (V v : variants) {
                v.switchToValue();
            }
        }
        return this;
    }

    public CV switchToSpecs() {
        if (variants != null) {
            for (V v : variants) {
                v.switchToSpec();
            }
        }
        return this;
    }

//	/**
//	 * 
//	 * @param specData these objects are passed to {@link S#fromSpec(Object)}
//	 * @return
//	 */
//	public static CS createSpec(Object ... specData) {
//        CS result= new CS();
//        ArrayList<S> myVariants= new ArrayList<>();
//        for (Object item : specData) {
//            myVariants.add(S.fromSpec(item));
//        }
//        result.setVariants(myVariants);
//        return result;
//	}
//	
	
	
	public static CV compose(CV a, CV b) {
		CV result= new CV();
		ArrayList<V> variants= new ArrayList<>();
		for (CV src : new CV[]{ a, b }){
			for (V variant : src.getVariants()) {
				variants.add(variant);
			}
		}
		result.setVariants(variants);
		return result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((variants == null) ? 0 : variants.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CV))
			return false;
		CV other = (CV) obj;
		if (variants == null) {
			if (other.variants != null)
				return false;
		} else if (!variants.equals(other.variants))
			return false;
		return true;
	}
	

	public String toString(String delim) {
		ArrayList<String> strings= new ArrayList<>();
		variants.forEach(i -> strings.add(i==null ? V.nullToString() : i.toString()));
		return "("+String.join(delim, strings)+")";
	}

	@Override public String toString() {
		return toString(",");
	}
	
	public static CV fromString(String delim, String string) {
        if (string == null) return null;
        if (string.length()<2) return null;
        if (string.charAt(0) != '(') return null;
        if (string.charAt(string.length()-1) != ')') return null;
        string= string.substring(1, string.length()-1);
        
        String[] arr= string.split(Pattern.quote(delim));
        List<V> variants= new ArrayList<>();
        for (String s: arr) {
            try {
                variants.add(V.fromString(s));
            } catch (Throwable t) {
                return null;
            }
        }
        CV result= new CV();
        result.setVariants(variants);
        return result;
    }
    
    public static CV fromString(String string) {
        return fromString(",", string);
    }
    

	
	
	public boolean hasPrefix(List<V> prefix) {
		int n= prefix.size();
		for (int i= 0; i < n; i++) {
			if (!variants.get(i).equals(prefix.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Iterator<V> iterator() {
		return variants.iterator();
	}
	
	public int size() {
		return variants.size();
	}
	
	public V get(int index) {
		return variants.get(index);
	}
	
	public CVp getCVp() {
	    List<Vp> list= new ArrayList<Vp>();
	    for (V v : getVariants()) {
	        Vp vp= v.getVp();
	        if (vp == null) {
	            throw new IllegalStateException("Cannot getCVp");
	        }
	        list.add(vp);
	    }
	    return new CVp(list);
	}
	
}
