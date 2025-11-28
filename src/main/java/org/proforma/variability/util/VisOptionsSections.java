package org.proforma.variability.util;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.proforma.variability.transfer.V;
import org.proforma.variability.transfer.Vis;



/**
 * <p>A set of values of a variation point.</p>
 * 
 * <p>The set is not stored in memory completely, but instead partially generated on request.</p>
 *
 */
class VisOptionsSections implements Iterable<Vis> {

	private VOrRangeTree vort;
	private TreeMap<Long, VisOptionsSection> sectionsByRank;
	private TreeMap<Double, VisOptionsSection> sectionsByVp;
	private Vis left, right;
	private long size;
	
	public VisOptionsSections(VOrRangeTree vort) {
		this.vort= vort;
		if (vort.isEmpty()) {
			throw new IllegalArgumentException();
		}

		// search min/max:
		left= null;
		right= null;
		for (VOrRange vor : vort) {
			V v= vor.getHullMin();
			if (left == null || v.compareTo(left) < 0) left= (Vis)v;
			v= vor.getHullMax();
			if (right == null || v.compareTo(right) > 0) right= (Vis)v;
		}
		
		VisOptionsSection root= new VisOptionsSection(left.toDouble(), right.toDouble(), true, vort.getVp());
		vort.forEach( vor -> root.add(vor.getSisOrRange()) );
		
		sectionsByRank= new TreeMap<>();
		sectionsByVp= new TreeMap<>();
		this.size= insertIntoTree(root, 0L);
	}
	
	public Vis getVariantAtRank(long rank) {
		Map.Entry<Long, VisOptionsSection> entry= sectionsByRank.ceilingEntry(rank);
		if (entry == null) throw new IndexOutOfBoundsException();
		VisOptionsSection section= entry.getValue();
		if (section == null) throw new IndexOutOfBoundsException();
//		long key= entry.getKey();
//		NavigableMap<Long, VpOptionsSection> predecessors= sectionsByRank.headMap(key, false);
//		
//		long szPrev= 0L;
//		Map.Entry<Long, VpOptionsSection> prevEntry= predecessors.lastEntry();
//		if (prevEntry != null) szPrev= prevEntry.getKey() + 1;
//		
//		
//		long queryIndex= rank- szPrev;
//		if ((int)queryIndex != queryIndex) {
//			throw new AssertionError(getClass()+ ".getVpAtRank: Unexpected int overflow");
//		}
//		return section.getVpAtRank((int)queryIndex); 

		return section.getVariantAtRank(rank);
	}
	
	public long getRankAtVariant(Vis variant) {
	    Vis accuracy= Vis.fromDouble(vort.getVp().getAccuracyOrDefault(), vort.getVp());
		Map.Entry<Double, VisOptionsSection> entry= sectionsByVp.ceilingEntry(variant.minus(accuracy).toDouble());
		if (entry == null) return -1;
		VisOptionsSection section= entry.getValue();
		if (section == null) return -1;
		return section.getRankAtVariant(variant); 
	}

	public Vis getOptionOrNeighbour(Vis variant) {
        Vis accuracy= Vis.fromDouble(vort.getVp().getAccuracyOrDefault(), vort.getVp());
		Map.Entry<Double, VisOptionsSection> entry= sectionsByVp.ceilingEntry(variant.minus(accuracy).toDouble());
		if (entry == null) {
			if (variant.compareTo(left) < 0) return left;
			return right;
		}
		VisOptionsSection section= entry.getValue();
		return section.getOptionOrNeighbour(variant);
	}
	
	public long getSize() {
		return size;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
//	public boolean containsOption(Vp query) {
//		double d= query.toDouble();
//		Map.Entry<Double, VpOptionsSection> entry= sectionsByVp.ceilingEntry(d);
//		if (entry == null) return false;
//		VpOptionsSection section= entry.getValue();
//		if (section == null) return false;
//		return section.containsOption(query);
//	}
//	
	private long insertIntoTree(VisOptionsSection section, long prevSize) {
		long sz= section.getSize();
		
		if (sz < 20) {
			long key= prevSize + sz - 1L;
			this.sectionsByRank.put(key, section);
			this.sectionsByVp.put(section.right, section);
			section.setRankInterval(prevSize, key);
		} else {
			VisOptionsSection[] children= splitSection(section);
			long sz0= insertIntoTree(children[0], prevSize);
			insertIntoTree(children[1], prevSize + sz0);
		}
		
		return sz;
	}
	
	private static VisOptionsSection[] splitSection(VisOptionsSection section) {
		class WeightedDouble {
			double val;
			double weight;
			public WeightedDouble(VisOrRange vor) {
				if (vor.isSingle()) {
					this.weight= 1L;
					this.val= vor.getSingle().toDouble();
				} else {
					// identify the relevant part of r inside the current section:
					double rf= section.maxLeft(vor.getFirst().toDouble());
					double rl= section.minRight(vor.getLast().toDouble());
					// calculate the center:
					this.val= (rf+rl) / 2.0; 
					// the weight is the number of steps in the relevant range part:
					this.weight= (rl-rf) / vor.stepWidth().toDouble(); // 
				}
			}
		}
		double sumVal= 0.0;
		double sumWeight= 0.0;
		for (VisOrRange vor : section.vorList) {
			WeightedDouble wd= new WeightedDouble(vor);
			sumVal += wd.weight * wd.val;
			sumWeight += wd.weight;
		}
		double middle= sumVal / sumWeight;
		
		VisOptionsSection[] newSections= { 
				new VisOptionsSection(section.getLeft(), middle, section.leftInclusive, section.vp), 
				new VisOptionsSection(middle, section.getRight(), false, section.vp) 
		};
		
		for (VisOptionsSection ns : newSections) {
			for (VisOrRange vor : section.vorList) {
				if (vor.isSingle()) {
					if (ns.isInInterval(vor.getSingle().toDouble())) {
						ns.add(vor);
					}
				} else {
					if (ns.intersectsInterval(vor.getFirst().toDouble(), vor.getLast().toDouble())) {
						ns.add(vor);
					}
				}
			}
		}
		
		return newSections;
	}

	@Override
	public Iterator<Vis> iterator() {
		long sz= getSize();
		return new Iterator<Vis>() {
			private int i;
			@Override public boolean hasNext() {
				return i < sz;
			}
			@Override public Vis next() {
				return getVariantAtRank(i++);
			}
		};
	}

	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		result = prime * result + ((sectionsByRank == null) ? 0 : sectionsByRank.hashCode());
		result = prime * result + ((sectionsByVp == null) ? 0 : sectionsByVp.hashCode());
		result = prime * result + (int) (size ^ (size >>> 32));
		result = prime * result + ((vort == null) ? 0 : vort.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VisOptionsSections))
			return false;
		VisOptionsSections other = (VisOptionsSections) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		if (sectionsByRank == null) {
			if (other.sectionsByRank != null)
				return false;
		} else if (!sectionsByRank.equals(other.sectionsByRank))
			return false;
		if (sectionsByVp == null) {
			if (other.sectionsByVp != null)
				return false;
		} else if (!sectionsByVp.equals(other.sectionsByVp))
			return false;
		if (size != other.size)
			return false;
		if (vort == null) {
			if (other.vort != null)
				return false;
		} else if (!vort.equals(other.vort))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return toString("");
	}
	
	public String toString(String prefix) {
		StringBuilder sb= new StringBuilder(
		         prefix + "VpOptionsSections [\n"
			   + prefix + "  size = " + size + "\n"
			   + prefix + "  vort = [\n");
		sb.append(vort.toString(prefix+"    ")+"\n");
		sb.append(prefix + "  ]\n");
		sb.append(prefix+ "  sections = [\n");
		for (Long key : sectionsByRank.keySet()) {
			sb.append(prefix + "    key = " + key.toString() + " -> value = [\n");
			sb.append(sectionsByRank.get(key).toString(prefix+"      "));
			sb.append(prefix + "    ]\n");
		}
		sb.append(prefix + "  ]\n");
		sb.append(prefix + "]\n");
		return sb.toString();
	}
}
