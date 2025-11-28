package org.proforma.variability.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.proforma.variability.transfer.V;
import org.proforma.variability.transfer.Vis;
import org.proforma.variability.transfer.Vp;

class VisOptionsSection {
		private double left;
		boolean leftInclusive;
		double right;
		Vp vp;
		List<VisOrRange> vorList;
		private long sizeCached= -1;
		private long minRank= -1;
		private long maxRank= -1;
		private List<Vis> elementsCached= null;
		
		VisOptionsSection(double left, double right, boolean leftInclusive, Vp vp) {
			this.left= left;
			this.leftInclusive= leftInclusive;
			this.right= right;
			this.vp= vp;
			this.vorList= new ArrayList<>();
		}

		public void setRankInterval(long minRank, long maxRank) {
			this.minRank= minRank;
			this.maxRank= maxRank;
		}

		double maxLeft(double vp) {
			return Math.max(vp, left);
		}
		
		double minRight(double vp) {
			return Math.min(vp, right);
		}
		boolean isInInterval(double query) {
			return query <= right && (left < query || leftInclusive && left == query);
		}
		
		boolean containsOption(V query) {
			calcCachedElements();
			return elementsCached.contains(query);
		}
		boolean intersectsInterval(double queryLeft, double queryRight) {
			if (queryLeft > queryRight) throw new IllegalArgumentException();
			return queryLeft <= this.right && (this.left < queryRight || leftInclusive && this.left == queryRight);
		}
		
		public double getLeft() {
			return left;
		}
		public double getRight() {
			return right;
		}
		public void add(VisOrRange vor) {
			vorList.add(vor);
		}
		
		public long getSize() {
			if (sizeCached >= 0) return sizeCached;
			long sum= 0;
			for (VisOrRange vor : this.vorList) {
				if (vor.isSingle()) {
					sum += 1;
				} else {
					double rf= vor.getFirst().toDouble();
					double rl= vor.getLast().toDouble();
					double sw= vor.stepWidth().toDouble();
					double ml= this.maxLeft(rf);
					long sz= (long)Math.floor((this.minRight(rl) - rf) / sw) - (long)Math.floor((ml - rf) / sw);
					
					if (leftInclusive) {
						sz++;
					} else {
						// Does left boundary fall exactly on "left"?
						// Then we do not count it.
						if (rf > this.left) {
							sz++; // no. count it
						} else {
							double tmp= Math.floor((this.left - rf) / sw);
							if ( tmp != (long)tmp ) {
								sz++; // no. count it.
							}
						}
					}
					sum += sz;
				}
			}
			this.sizeCached= sum;
			return sum;
		}
		
		private void calcCachedElements() {
			if (elementsCached != null) return;
			Log.debug("Calculating elementsCached");				
			elementsCached= new ArrayList<>();
			Vis accuracy= vp.isIntervalScaled() ? Vis.fromDouble(vp.getAccuracyOrDefault(), vp) : null;
			for (VisOrRange vor : this.vorList) {
				if (vor.isSingle()) {
					elementsCached.add(vor.getSingle());
				} else {
					Vis rf= vor.getFirstCeil(left, true, vp);
					if (rf.toDouble() == this.left && !leftInclusive) {
						rf= rf.plus(vor.stepWidth());
					}
					Vis rl= vor.getLastFloor(right, true, vp);
					Vis sw= vor.stepWidth();
					for (Vis vp= rf; vp.plus(accuracy).compareTo(rl) < 0; vp= vp.plus(sw) ) {
						elementsCached.add(vp);
					}
					if (!elementsCached.contains(rl)) elementsCached.add(rl);
				}
			}
			Collections.sort(elementsCached);
		}
		public Vis getVariantAtRank(long rank) {
			if (rank < minRank || rank > maxRank) {
				throw new IndexOutOfBoundsException("rank="+rank+" is not in ["+minRank+","+maxRank+"]");
			}
			calcCachedElements();
			long queryIndex= rank- minRank;
			if ((int)queryIndex != queryIndex) {
				throw new AssertionError(getClass()+ ".getVariantAtRank: Unexpected int overflow");
			}
			return elementsCached.get((int)queryIndex);
		}
		public long getRankAtVariant(Vis variant) {
            if (!vp.isIntervalScaled()) {
                throw new UnsupportedOperationException("Cannot getOptionOrNeighbour for non interval scaled vp");
            }
			double d= variant.toDouble();
			double accuracy= vp.getAccuracyOrDefault();
			if (left-d > accuracy || d-right > accuracy) return -1;
			if (!leftInclusive && left-d >= accuracy) return -1;
			calcCachedElements();
			
			if (vp.isIntervalScaled()) {
				// handle rounding errors by searching a neighbour
				Vis found= getOptionOrNeighbour(variant); 
				if (Math.abs(found.minus(variant).toDouble()) <= accuracy) {
					// the nearest neighbour is close enough. We return the neighbour's rank.
					variant= found;
				}
			}
			int index= elementsCached.indexOf(variant);
			if (index < 0) return index;
			long result= minRank + index;
			if (result > maxRank){
				throw new IllegalStateException("elementsCached.indexOf returned too large value '"+index+"'. Max expected: '"+(maxRank-minRank)+"'");
			}
			return result;
		}
		
		public Vis getOptionOrNeighbour(Vis variant) {
		    if (!vp.isIntervalScaled()) {
		        throw new UnsupportedOperationException("Cannot getOptionOrNeighbour for non interval scaled vp");
		    }
			//double d= vp.toDouble();
			calcCachedElements();
			int index= Collections.binarySearch(elementsCached, variant);
//System.out.println("searching neighbour of '"+vp+"': found index="+index+" with elementsCached="+elementsCached);
			if (index >= 0) return (Vis)elementsCached.get(index);
			int insertionPoint= -(index+1);
			if (insertionPoint == elementsCached.size()) return (Vis)elementsCached.get(insertionPoint-1);
			if (insertionPoint == 0) return (Vis)elementsCached.get(0);
			double diff1= variant.distanceTo(elementsCached.get(insertionPoint));
			double diff2= variant.distanceTo(elementsCached.get(insertionPoint-1));
			if (diff1 < diff2) return (Vis)elementsCached.get(insertionPoint);
			return (Vis)elementsCached.get(insertionPoint-1);
		}
		
		@Override
		public String toString() {
			return toString("");
		}
		
		public String toString(String prefix) {
			StringBuilder sb= new StringBuilder(
			         prefix + "VpOptionsSection [\n"
				   + prefix + "  sizeCached = " + sizeCached + "\n"
				   + prefix + "  left = " + left + "\n"
				   + prefix + "  leftInclusive = " + leftInclusive + "\n"
				   + prefix + "  right = " + right + "\n"
				   + prefix + "  vorList = [\n");
			for (VisOrRange vor : vorList) {
				sb.append(vor.toString(prefix+"    ") + "\n");
			}
			sb.append(prefix + "  ]\n");
			sb.append(prefix + "  elementsCached = ");
//calcCachedElements();
			if (elementsCached == null) {
				sb.append(null+"\n");
			} else {
				sb.append("[\n");
				for (V vp : elementsCached) {
					sb.append(vp.toString(prefix+"    ") + "\n");
				}
				sb.append(prefix + "  ]\n");
			}
			sb.append(prefix + "]\n");
			return sb.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((vp == null) ? 0 : vp.hashCode());
			long temp;
			temp = Double.doubleToLongBits(left);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime * result + (leftInclusive ? 1231 : 1237);
			temp = Double.doubleToLongBits(right);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime * result + ((vorList == null) ? 0 : vorList.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof VisOptionsSection))
				return false;
			VisOptionsSection other = (VisOptionsSection) obj;
			if (vp == null) {
				if (other.vp != null)
					return false;
			} else if (!vp.equals(other.vp))
				return false;
			if (Double.doubleToLongBits(left) != Double.doubleToLongBits(other.left))
				return false;
			if (leftInclusive != other.leftInclusive)
				return false;
			if (Double.doubleToLongBits(right) != Double.doubleToLongBits(other.right))
				return false;
			if (vorList == null) {
				if (other.vorList != null)
					return false;
			} else if (!vorList.equals(other.vorList))
				return false;
			return true;
		}
		
		

	}