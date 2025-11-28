package org.proforma.variability.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.proforma.variability.transfer.V;
import org.proforma.variability.transfer.Vis;



/**
 * <p>A set of values of a variation point.</p>
 * 
 * <p>The set is not stored in memory completely, but instead partially generated on request.</p>
 *
 */
public class VOptions implements Iterable<V> {

	
	
	private VOrRangeTree vort;
	private VisOptionsSections optionsSections;
	private List<V> simpleList; // used for non-interval scaled variation points
	
	public VOptions(VOrRangeTree vort) {
		this.vort= vort;
		if (vort.isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		if (vort.getVp().isIntervalScaled()) {
			optionsSections= new VisOptionsSections(vort);
		} else {
			simpleList= new ArrayList<>();
			for (VOrRange v : vort) {
				if (v.isNominalScaled()){
					simpleList.add(v.getSingle());
				} else {
					throw new IllegalArgumentException();
				}
			}
			Collections.sort(simpleList);
		}
	}
	
	public V getVariantAtRank(long rank) {
		if (vort.getVp().isIntervalScaled()) {
			return optionsSections.getVariantAtRank(rank);
		} else {
			return simpleList.get((int)rank);
		}
	}
	
	public long getRankAtVariant(V variant) {
		if (vort.getVp().isIntervalScaled()) {
			return optionsSections.getRankAtVariant((Vis)variant);
		} else {
			int index= Collections.binarySearch(simpleList, variant);
			if (index < 0) return -1;
			return index;
		}
	}

	public V getOptionOrNeighbour(V variant) {
		if (vort.getVp().isIntervalScaled()) {
			return optionsSections.getOptionOrNeighbour((Vis)variant);
		} else {
			int index= Collections.binarySearch(simpleList, variant);
			if (index >= 0) return simpleList.get(index);
			int insertionPoint= -(index+1);
			if (insertionPoint == simpleList.size()) return simpleList.get(insertionPoint-1);
			if (insertionPoint == 0) return simpleList.get(0);
			double diff1= variant.distanceTo(simpleList.get(insertionPoint));
			double diff2= variant.distanceTo(simpleList.get(insertionPoint-1));
			if (diff1 < diff2) return simpleList.get(insertionPoint);
			return simpleList.get(insertionPoint-1);
		}
	}
	
	private static Random random= new Random();
	
	public V getRandomElement() {
		long index= Math.abs(random.nextLong()) % this.getSize();
		return getVariantAtRank(index);
	}

	public long getSize() {
		if (vort.getVp().isIntervalScaled()) {
			return optionsSections.getSize();
		} else {
			return simpleList.size();
		}
	}
	
	public boolean isEmpty() {
		if (vort.getVp().isIntervalScaled()) {
			return optionsSections.isEmpty();
		} else {
			return simpleList.isEmpty();
		}
	}
	
	public boolean containsOption(V query) {
		return getRankAtVariant(query) >= 0;
	}


	@Override
	public Iterator<V> iterator() {
		if (vort.getVp().isIntervalScaled()) {
			Iterator<Vis> iter= optionsSections.iterator();
			return new Iterator<V>() {
                @Override public boolean hasNext() {
                    return iter.hasNext();
                }
                @Override public V next() {
                    return (V)iter.next();
                }
			};
		} else {
			return simpleList.iterator();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((vort == null) ? 0 : vort.hashCode());
		result = prime * result + ((optionsSections == null) ? 0 : optionsSections.hashCode());
		result = prime * result + ((simpleList == null) ? 0 : simpleList.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VOptions))
			return false;
		VOptions other = (VOptions) obj;
		if (vort == null) {
			if (other.vort != null)
				return false;
		} else if (!vort.equals(other.vort))
			return false;
		if (optionsSections == null) {
			if (other.optionsSections != null)
				return false;
		} else if (!optionsSections.equals(other.optionsSections))
			return false;
		if (simpleList == null) {
			if (other.simpleList != null)
				return false;
		} else if (!simpleList.equals(other.simpleList))
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return toString("");
	}
	
	public String toString(String prefix) {
		StringBuilder sb= new StringBuilder(
		         prefix + "SOptions [\n"
			   + prefix + "  size = " + getSize() + "\n"
			   + prefix + "  vort = [\n");
		sb.append(vort.toString("    ")+"\n");
		sb.append(prefix+ "  ]\n");

		sb.append(prefix+ "  optionsSections = [");
		if (optionsSections == null) {
			sb.append(null+"]\n");
		} else {
			sb.append("\n");
			sb.append(optionsSections.toString(prefix+"    "));
			sb.append(prefix + "  ]\n");
		}

		sb.append(prefix+ "  simpleList = [");
		if (simpleList == null) {
			sb.append(null+"]\n");
		} else {
			sb.append("\n");
			for (V s : simpleList) {
				sb.append(s.toString(prefix + "    ")).append("\n");
			}
			sb.append(prefix + "  ]\n");
		}

		sb.append(prefix + "]\n");
		return sb.toString();
	}
}
