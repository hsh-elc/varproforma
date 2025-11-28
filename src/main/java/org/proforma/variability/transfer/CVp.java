package org.proforma.variability.transfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;




@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cvp-type")
public class CVp implements Iterable<Vp>, java.io.Serializable {

	private static final long serialVersionUID = 1L;

    public CVp() {
		
	}
	
	public CVp(List<Vp> variationPoints) {
		this.variationPoints= variationPoints;
	}
	
	public CVp(CVp other) {
		if (other.variationPoints != null) {
			variationPoints= new ArrayList<>();
			for (Vp vp : other.variationPoints) {
				variationPoints.add(vp.clone());
			}
		}
	}
	
	public static CVp of(Vp ... variationPoints) {
	    if (variationPoints.length == 0) return new CVp(new ArrayList<>());
		return new CVp(new ArrayList<>(Arrays.asList(variationPoints)));
	}
	
	public CVp clone() {
		return new CVp(this);
	}
	
	@XmlElement(name="vp")
    private List<Vp> variationPoints;

	public List<Vp> getVariationPoints() {
		return variationPoints;
	}

	public void setVariationPoints(List<Vp> variationPoints) {
		this.variationPoints = variationPoints;
	}
	
	
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((variationPoints == null) ? 0 : variationPoints.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CVp))
			return false;
		CVp other = (CVp) obj;
		if (variationPoints == null) {
			if (other.variationPoints != null)
				return false;
		} else if (!variationPoints.equals(other.variationPoints))
			return false;
		return true;
	}

	public String toString(String delim) {
	    if (variationPoints == null) return "(null)";
		ArrayList<String> strings= new ArrayList<>();
		variationPoints.forEach(i -> strings.add(i==null ? "(null)" : i.toString()));
		return "("+String.join(delim, strings)+")";
	}

	@Override public String toString() {
		return toString(",");
	}

	@Override
	public Iterator<Vp> iterator() {
		return variationPoints.iterator();
	}
	
	public int size() {
		return variationPoints.size();
	}

	public Vp get(int index) {
	    if (index < 0 || index >= variationPoints.size()) {
	        throw new IndexOutOfBoundsException(CVp.class+".get("+index+") for variationPoints="+variationPoints);
	    }
		return variationPoints.get(index);
	}
	
	public int indexOf(String vpKey) {
	    for (int i=0; i<variationPoints.size(); i++) {
	        Vp vp= variationPoints.get(i); 
	        if (vp.getKey().equals(vpKey)) return i;
	    }
	    return -1;
	}
	
	public LinkedHashSet<String> keySet() {
	    LinkedHashSet<String> result= new LinkedHashSet<>();
	    if (variationPoints != null) {
    	    for (Vp vp : variationPoints) {
    	        result.add(vp.getKey());
    	    }
	    }
	    return result;
	}
	
    public boolean isAtLeastOneVpMatching(CVp otherCVp) {
        for (Vp a : this)
            for (Vp b : otherCVp) 
                if (a.equals(b)) return true;
        return false;
    }


}
