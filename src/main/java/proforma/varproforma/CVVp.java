package proforma.varproforma;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import proforma.varproforma.util.Derive;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cvvp-type")
@XmlRootElement(name = "cvvp")
public class CVVp  implements java.io.Serializable {
	
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "cvp")
    private CVp cvp;

    @XmlElement(name = "cv")
    private CV cv;

    @XmlTransient
    private HashMap<String,Integer> mapKeyToRank;



	public CVVp() {
		
	}
	
	public CVVp(CVp cvp, CV cv) {
	    this.cvp= cvp;
	    this.cv= cv;
	}
	
	public static CVVp create(CV cv) {
        Vp[] vps= new Vp[cv.size()];
        for (int i=0; i<vps.length; i++) {
            V v= cv.get(i);
            vps[i]= v.getVp();
        }
        CVp cvp= CVp.of(vps);
        CVVp result= new CVVp();
        result.setCVp(cvp);
        result.setCV(cv);
        return result;
	    
	}

	public static CVVp create(V ... items) {
        return create(new CV(items));
    }

	public static CVVp create(Collection<V> items) {
        return create(new CV(items.toArray(new V[items.size()])));
    }
	
	public CVVp(CVVp other) {
		if (other.cvp != null) {
			cvp= other.cvp.clone();
		}
		if (other.cv != null) {
			cv= new CV(other.cv);
		}
	}
	
	public CVp getCVp() {
		return cvp;
	}

	public void setCVp(CVp cvp) {
		this.cvp = cvp;
		recreateInternalMap();
	}

	public CV getCV() {
		return cv;
	}

	public void setCV(CV cs) {
		this.cv = cs;
	}

	private void recreateInternalMap() {
		mapKeyToRank= null;
		createInternalMap();
	}
	private void createInternalMap() {
		if (mapKeyToRank == null) {
			mapKeyToRank= new HashMap<>();
			if (cvp != null) {
				for (int i=0; i<cvp.size(); i++) {
					mapKeyToRank.put(cvp.get(i).getKey(), i);
				}
			}
		}
	}
	
	public Vp getVp(String key) {
		for (Vp vp : cvp) {
			if (vp.getKey().equals(key)) return vp;
		}
		return null;
	}
	
	public V get(String key) {
		createInternalMap();
		int index= mapKeyToRank.get(key);
		List<V> variants= cv.getVariants();
		if (index < 0 || index >= variants.size()) {
		    throw new IndexOutOfBoundsException(CVVp.class+".get("+key+") for cvp="+cvp+", cs="+cv+": index="+index+", variants.size()="+variants.size());
		}
		return variants.get(mapKeyToRank.get(key));
	}
	public Character getChar(String key) {
		return ((Vc)get(key)).getValue();//.charValue();
	}
	public Integer getInt(String key) {
		return ((Vi)get(key)).getValue();//.intValue();
	}
	public Double getDouble(String key) {
		return ((Vd)get(key)).getValue();//.doubleValue();
	}
	public String getString(String key) {
		return ((Vs)get(key)).getValue();
	}
	public Boolean getBoolean(String key) {
		return ((Vb)get(key)).getValue();// .booleanValue();  <- since a Vp can be null (if not applicable because of other Vp values), we cannot call booleanValue!
	}
    public CVList getTableValue(String key) {
        return ((Vt)get(key)).getValue();
    }
    public VarSpecNodeTable getTableSpec(String key) {
        return ((Vt)get(key)).getSpec();
    }

    public CVVp switchToValue() {
        if (cv != null) {
            cv.switchToValue();
        }
        return this;
    }
    
    public CVVp switchToSpecs() {
        if (cv != null) {
            cv.switchToSpecs();
        }
        return this;
    }
    
	public void set(String key, V v) {
		createInternalMap();
		cv.getVariants().set(mapKeyToRank.get(key), v);
	}
	
	
    /**
     * Postprocessing 
     */
    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        cv.pushCVp(cvp);
    	recreateInternalMap();
    }
    
    
    
    public void prettyPrint() {
    	prettyPrint(System.out);
    }
    
    public void prettyPrint(PrintStream out) {
    	prettyPrint(out, "");
    }
    void prettyPrint(PrintStream out, String prefix) {
        int[] wid= new int[cvp.size()];
        for (int i=0; i<cvp.size(); i++) {
            wid[i]= cvp.get(i).toString().length();
        }
    	for (int i=0; i<cvp.size(); i++) {
            int len= cv.getVariants().get(i).toString().length();
            if (len > wid[i])
                wid[i]= len;
        }
        out.format("%s", prefix);
        for (int i=0; i<cvp.size(); i++) {
            out.format(" %-"+wid[i]+"s ", cvp.get(i));
            if (i < cvp.size()-1) out.print("|");
        }
        out.print("\n");
        out.format("%s", prefix);
        for (int i=0; i<cvp.size(); i++) {
        	for (int c=0; c<wid[i]+2; c++) out.print('-');
            if (i < cvp.size()-1) out.print("+");
        }
        out.print("\n");
        out.format("%s", prefix);
    	for (int i=0; i<cvp.size(); i++) {
            out.format(" %-"+wid[i]+"s ", cv.getVariants().get(i).toString());
            if (i < cvp.size()-1) out.print("|");
        }
        out.print("\n");
    }
    
    

    

    
    public CVVp projectToCVpLeavingNonMatchingVpsNull(CVp cvp) {
        List<V> tuple= new ArrayList<>();
        for (Vp vp : cvp) {
            V s= null;
            if (getVp(vp.getKey()) != null) {
                s= get(vp.getKey());
            }
            tuple.add(s);
        }
        return new CVVp(cvp, new CV(tuple));
    }
    
    
    public CVVp projectToCVp(String ... vpKeys) {
        List<V> tuple= new ArrayList<>();
        List<Vp> vps= new ArrayList<>();
        for (String vpKey : vpKeys) {
            Vp myVp= getVp(vpKey);
            if (myVp != null) {
                tuple.add(get(vpKey));
                vps.add(myVp);
            }
        }
        return CVVp.create(tuple);
    }
    
    
    public CVp createIntersectionWithCVp(CVp otherCVp) {
		List<Vp> result= new ArrayList<>();
		for (Vp a : this.cvp)
			for (Vp b : otherCVp) 
				if (a.equals(b)) result.add(a);
		return new CVp(result);
	}
	
    public boolean isAtLeastOneVpMatching(CVp otherCVp) {
		for (Vp a : this.cvp)
			for (Vp b : otherCVp) 
				if (a.equals(b)) return true;
		return false;
	}

    /**
     * @return a mapping from vp keys to values. The value type is determined by the 
     * Vp type. E.g. for a {@link Vi} the returned value is Integer. 
     * For a {@link Vt} the returned value is a nested <code>List&lt;Map&lt;String,Object&gt;&gt;</code>. 
     */
    public LinkedHashMap<String, Object> getAsKeyOrderedMap() {
    	return getAsKeyOrderedMap(null);
    }

    /**
     * @return a mapping from vp keys to values. The value type is determined by the 
     * Vp type. E.g. for a {@link Vi} the returned value is Integer.  
     * For a {@link Vt} the returned value is a nested <code>List&lt;Map&lt;String,Object&gt;&gt;</code>. 
     */
    public LinkedHashMap<String, Object> getAsKeyOrderedMap(List<String> restrictToKeys) {
        return Derive.getAsKeyOrderedMap(this.cvp, this.cv, restrictToKeys);
    }


	@Override
	public String toString() {
		return "CVr [cvp=" + cvp + ", cv=" + cv + "]";
	}
    
    
    

}
