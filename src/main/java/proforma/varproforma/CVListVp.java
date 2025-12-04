package proforma.varproforma;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;






@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cv-list-vp-type")
@XmlRootElement(name = "cv-list-vp")
public class CVListVp implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

    public CVListVp() {
		
	}
	
    public static CVListVp createEmptyList(CVp cvp) {
        return new CVListVp(cvp, new ArrayList<CV>());
    }
    
    public static CVListVp createFromCSList(CVList list) {
        if (list == null || list.getElements().isEmpty()) return null;
        
        List<Vp> vps= new ArrayList<>();
        for (V s : list.getElements().get(0).getVariants()) {
            Vp vp= s.getVp();
            if (vp == null) {
                throw new IllegalArgumentException("Unexpected vp=null");
            }
            vps.add(vp);
        }

        return new CVListVp(new CVp(vps), list.getElements());
    }
    
	
	public CVListVp(CVp cvp, List<CV> list) {
	    this.cvp= cvp;
	    this.list= list;
	}

	public CVListVp(CVListVp other) {
		if (other.cvp != null) {
			cvp= other.cvp.clone();
		}
		if (other.list != null) {
		    list= new ArrayList<>();
			for (CV cv : other.list) {
			    list.add(new CV(cv));
			}
		}

	}
    @XmlElement(name = "cvp")
    private CVp cvp;

    @XmlElement(name = "cv")
    private List<CV> list;

	public CVp getCVp() {
		return cvp;
	}

	public void setCVp(CVp cvp) {
		this.cvp = cvp;
	}

	public List<CV> getList() {
		return list;
	}

	public void setList(List<CV> list) {
		this.list = list;
	}


	static CVListVp createEmptySet(CVp cvp) {
		CVListVp result= new CVListVp();
		result.setCVp(cvp);
		result.setList(new ArrayList<CV>());
		return result;
	}
	
	public CVListVp duplicateWithReorderedCVp(CVp reorderedCVp) {
	    return new CVListVp(reorderedCVp, new CVList(list).duplicateWithReorderedCVp(getCVp(), reorderedCVp).getElements());
	}
	
	
	
	/**
     * Calculates the union set of all given sets.
     * @param sets
     * @return
     */
	static CVListVp collect(CVListVp ... sets){
		CVp cvp= null;
		CVListVp result= new CVListVp();
		result.setList(new ArrayList<>());
    	for (CVListVp set : sets){
    		CVp d= set.getCVp();
    		if (cvp == null) {
    			cvp= d;
    		} else {
    			if (!cvp.equals(d)) {
    				throw new IllegalArgumentException("Cannot collect two sets with different vp "+d+" and "+cvp);
    			}
    		}
	    	for (CV cv : set.getList()) {
				result.getList().add(cv);
	    	}
    	}
		result.setCVp(cvp);
    	return result;
    }

	/**
	 * Calculates the cartesian product, which consists of all combinations of
	 * data elements in the given sets.
	 * @param sets
	 * @return
	 */
    static CVListVp combine(CVListVp ... sets) {
    	if (sets.length == 1) return sets[0];
		CVListVp[] sets2ToN= new CVListVp[sets.length-1];
    	System.arraycopy(sets, 1, sets2ToN, 0, sets2ToN.length);
    	CVListVp result= new CVListVp();
    	result.setList(new ArrayList<>());
    	CVListVp tail= combine(sets2ToN);
    	for (CV a : sets[0].getList()) {
    		for (CV b : tail.getList()) {
    			result.getList().add(CV.compose(a, b));
    		}
    	}
    	ArrayList<Vp> vps= new ArrayList<>();
    	vps.addAll(sets[0].getCVp().getVariationPoints());
    	vps.addAll(tail.getCVp().getVariationPoints());
    	result.setCVp(new CVp(vps));
    	return result;
    }
    
    
	public void sort() {
	    list.sort(new Comparator<CV>() {
			@Override
			public int compare(CV vt1, CV vt2) {
				for (int i=0; i<vt1.getVariants().size(); i++) {
					int cmp= vt1.getVariants().get(i).toString().compareTo(vt2.getVariants().get(i).toString());
					if (cmp != 0) return cmp;
				}
				return 0;
			}
		});
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
            wid[i]= cvp.get(i).getKey().toString().length();
        }
        for (CV cv : list) {
        	for (int i=0; i<cvp.size(); i++) {
                int len= cv.getVariants().get(i).toString().length();
                if (len > wid[i])
                    wid[i]= len;
            }
        }
        out.format("%s", prefix);
        for (int i=0; i<cvp.size(); i++) {
            out.format(" %-"+wid[i]+"s ", cvp.get(i).getKey());
            if (i < cvp.size()-1) out.print("|");
        }
        out.print("\n");
        out.format("%s", prefix);
        for (int i=0; i<cvp.size(); i++) {
        	for (int c=0; c<wid[i]+2; c++) out.print('-');
            if (i < cvp.size()-1) out.print("+");
        }
        out.print("\n");
        for (CV cv : list) {
            out.format("%s", prefix);
        	for (int i=0; i<cvp.size(); i++) {
                out.format(" %-"+wid[i]+"s ", cv.getVariants().get(i).toString());
                if (i < cvp.size()-1) out.print("|");
            }
            out.print("\n");
        }
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cvp == null) ? 0 : cvp.hashCode());
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CVListVp))
			return false;
		CVListVp other = (CVListVp) obj;
		if (cvp == null) {
			if (other.cvp != null)
				return false;
		} else if (!cvp.equals(other.cvp))
			return false;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		return true;
	}
    
    @Override
    public String toString() {
    	PrintStream ps= null;
		try {
	    	ByteArrayOutputStream baos= new ByteArrayOutputStream();
			ps = new PrintStream(baos, true, StandardCharsets.UTF_8.name());
	    	prettyPrint(ps);
	    	String result= new String(baos.toByteArray(), StandardCharsets.UTF_8);
	    	return result;
		} catch (UnsupportedEncodingException e) {
			// should never occur
			return e.getMessage();
		} finally {
			if (ps != null) ps.close();
		}
    }
    
    
    

    
    public CVVp getInst(int index) {
    	CV cv= getList().get(index);
    	CVVp result= new CVVp();
    	result.setCVp(cvp);
    	result.setCV(cv);
    	return result;
    }
    
    
	public List<V> getDistinctValuesForVp(int index) {
		List<V> result= new ArrayList<>();
		for (CV cv : list) {
			V v= cv.getVariants().get(index);
			if (!result.contains(v)) result.add(v);
		}
		return result;
	}
	
	public List<V> calculateSelectableItemsForGivenPrefix(List<V> prefix) {
		int rowIndex= prefix.size();
		List<V> choicesToEnable= new ArrayList<>();
		if (list != null) {
			for (CV cv : list) {
				if ( cv.hasPrefix(prefix) ) {
	    			V v= cv.getVariants().get(rowIndex);
	    			if (!choicesToEnable.contains(v)) choicesToEnable.add(v);
				}
			}
		}
		return choicesToEnable;
	}

	
	public int size() {
		return list.size();
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public boolean contains(CV cv) {
		return list.contains(cv);
	}
	
	@XmlTransient
	private Random random= new Random();
	
	public CV getRandomElement() {
		return list.get(random.nextInt(list.size()));
	}
}
