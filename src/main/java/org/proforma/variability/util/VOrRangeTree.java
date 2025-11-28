package org.proforma.variability.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.proforma.variability.transfer.Vis;
import org.proforma.variability.transfer.Vns;
import org.proforma.variability.transfer.Vp;
import org.proforma.variability.util.algs4.Interval1D;
import org.proforma.variability.util.algs4.IntervalST;

/**
 * <p>
 * This tree stores {@link VOrRange} instances such that duplicates are eliminated 
 * as good as possible. E. g. the following two ranges would be concatenated 
 * to a single range: { 2, 4, 6, 8, 10 } and { 6, 8, 10, 12, 14, 16 } will be joined to 
 * { 2, 4, 6, 8, 10, 12, 14, 16 }. Another example: the range { 4, 7, 10, 13 } and the
 * value 1 will be joined to { 1, 4, 7, 10, 13 }.</p>
 * 
 * <p>
 * Elements of the tree can be queried by specifying a {@link Vis} query interval.
 * </p>
 */
public class VOrRangeTree implements Iterable<VOrRange> {

	private IntervalST<List<VisOrRange>> tree;  // used for interval scaled types
	private List<Vns> simpleList; // used for non interval scaled types
	private Vp vp;
	
	/**
	 * @param vp if null, then nothing can be added to this tree.
	 */
	public VOrRangeTree(Vp vp) {
		if (vp != null) {
			if (vp.isIntervalScaled()) {
				tree= new IntervalST<>();
			} else {
				simpleList= new ArrayList<>();
			}
			this.vp= vp;
		}
	}
	
	public Vp getVp() {
		return vp;
	}
	
	private List<VisOrRange> getElementsIntersecting(VisOrRange query) {
		List<Interval1D> intervals;
		if (query.isSingle()) {
			double d= query.getSingle().toDouble();
			intervals= tree.searchAll(new Interval1D(d,d));
		} else {
			intervals= tree.searchAll(new Interval1D(query.getFirst().toDouble(), query.getLast().toDouble()));
		}
		List<VisOrRange> result= new ArrayList<>();
		for (Interval1D i : intervals) {
			List<VisOrRange> list= tree.get(i);
			result.addAll(list);
		}
		return result;
	}
	

	

	
	
	private String prefix= "";
	
	public void addElement(VOrRange subject) {
		if (!this.vp.represents(subject.getHullMin().getClass())) {
			throw new IllegalArgumentException("Unexpected V subclass '"+subject.getHullMin().getClass()+"'. Expected: '"+this.vp+"'");
		}
		
		if (contains(subject)) return;
	
		String oldPrefix= prefix;
		prefix= prefix+"  ";
		boolean logDebug= !this.isEmpty();
		
		if (logDebug) {
			Log.debug(prefix+"-------------------------------------------");
			Log.debug(prefix+getClass()+".addElement("+subject+")");
			Log.debug(prefix+"Content before:");
			Log.debug(this.toString(prefix+"  "));
		}

		boolean shouldAddSubjectAtTheEnd= true; 
		// ^ this controls adding the given object at the end, when
		//   we haven't found any joining possibility.

		if (this.vp.isIntervalScaled()) {
		    
		    if (!subject.isIntervalScaled()) {
		        throw new IllegalArgumentException("subject should be interval scaled");
		    }
			
			//
			// Search for join candidates ...
			//
	
			List<VisOrRange> joinCandidates= getElementsIntersecting(subject.getSisOrRange());
			if (logDebug) {
				Log.debug(prefix+"joinCandidates:\n");
				for (VisOrRange x : joinCandidates) Log.debug(x.toString(prefix+"  "));
			}
			
			for (VisOrRange joinCandidate : joinCandidates) {
	
				if (joinCandidate.contains(subject.getSisOrRange())) {
					// completely contained -> nothing to add.
					shouldAddSubjectAtTheEnd= false;
					break; // exit joinCandidates for loop
				}
	
				// check, if we can in fact join "subject" and the join candidate:
				List<? extends VisOrRange> newElements= joinCandidate.calculateNewVpOrRangesIncludingThisAndGivenVpOrRange(subject.getSisOrRange());
				if (logDebug) {
					Log.debug(prefix+"newElements:\n");
					for (VisOrRange x : newElements) Log.debug(x.toString(prefix+"  "));
				}
	
				// analyze the new elements, whether the join candidate and/or the subject is affected.
				boolean addSubjectAsOriginal= false;
				boolean keepJoinCandidateAsOriginal= false;
				List<VisOrRange> otherNewElementsToAdd= new ArrayList<>();
				for (VisOrRange ne : newElements) {
					if (ne == joinCandidate) {
						keepJoinCandidateAsOriginal= true;
					} else if (ne == subject.getSisOrRange()) {
						addSubjectAsOriginal= true;
					} else {
						otherNewElementsToAdd.add(ne);
					}
				}
				
				boolean foundMatchingJoinCandidate= !keepJoinCandidateAsOriginal || !otherNewElementsToAdd.isEmpty() || !addSubjectAsOriginal;
				if (foundMatchingJoinCandidate) {
					
					if (logDebug) {
						Log.debug(prefix+"keepJoinCandidateAsOriginal: "+keepJoinCandidateAsOriginal+"\n");
						Log.debug(prefix+"addSubjectAsOriginal: "+addSubjectAsOriginal+"\n");
						Log.debug(prefix+"otherNewElementsToAdd:\n");
						for (VisOrRange x : otherNewElementsToAdd) Log.debug(x.toString(prefix+"  "));
					}
					if (!keepJoinCandidateAsOriginal) {
						if (logDebug) Log.debug(prefix+"removing joinCandidate:\n"+joinCandidate.toString(prefix+"  "));					
						removeElement(joinCandidate);
						Log.debug(prefix+"Content after removing:");
						Log.debug(this.toString(prefix+"  "));
					}
					if (addSubjectAsOriginal) {
						if (logDebug) Log.debug(prefix+"adding subject non recursively:\n"+subject.toString(prefix+"  "));					
						// no recursion, if subject can be added unchanged
						addElementImpl(subject);
					}
					for (VisOrRange ne : otherNewElementsToAdd) {
						if (logDebug) Log.debug(prefix+"adding recursively:\n"+ne.toString(prefix+"  "));					
						addElement(new VOrRange(ne));  // recur because a new value might concatenate two ranges
					}
	
					shouldAddSubjectAtTheEnd= false; // done here already. So don't add at the end.
					break; // exit joinCandidates for loop
				}
				
				// else try next join candidate
			}
			
		}
		
		if (shouldAddSubjectAtTheEnd) {
			addElementImpl(subject);
		}

		if (logDebug) {
			Log.debug(prefix+"Content after:");
			Log.debug(this.toString("  "+prefix));
			Log.debug(prefix+"-------------------------------------------");
		}
		prefix= oldPrefix;
	}
	
	private Interval1D mkInterval(VisOrRange e) {
		Interval1D interval;
		if (e.isSingle()) {
			Vis s= e.getSingle();
			// add a interval ranging from the predecessor to the successor
			// in order to facilitate joining of several values to a range:
			interval= new Interval1D(s.pred().toDouble(), s.succ().toDouble());
		} else {
			interval= new Interval1D(e.getFirst().minus(e.stepWidth()).toDouble(), e.getLast().plus(e.stepWidth()).toDouble());
		}
		return interval;
	}	
	
	private void addElementImpl(VOrRange e) {
		if (vp.isIntervalScaled()) {
            if (!e.isIntervalScaled()) {
                throw new IllegalArgumentException("e should be interval scaled");
            }
			Interval1D i= mkInterval(e.getSisOrRange());
			Log.debug(prefix+getClass()+".addElementImpl("+e+"): i="+i);
			List<VisOrRange> list= tree.get(i);
			Log.debug(prefix+getClass()+".addElementImpl("+e+"): list="+list);
			if (list == null) {
				list= new ArrayList<>();
				tree.put(i, list);
			}
			list.add(e.getSisOrRange());
		} else {
			if (! simpleList.contains(e.getSns())) {
				simpleList.add(e.getSns());
			}
		}
	}
	
	
	private void removeElement(VisOrRange toRemove) {
		Interval1D i= mkInterval(toRemove);
		List<VisOrRange> list= tree.get(i);
		if (list == null || list.isEmpty()) throw new IllegalArgumentException();

		list.remove(toRemove);
		if (list.isEmpty()) {
			tree.remove(i);
		}
	}

	public Iterator<VOrRange> iterator() {
		if (vp == null) {
			return new ArrayList<VOrRange>().iterator();
		}
		if (vp.isIntervalScaled()) {
			List<Interval1D> list= tree.searchAll();
			Iterator<Interval1D> intervalIter= list.iterator();
			return new Iterator<VOrRange>() {
				private Iterator<VisOrRange> elemIter;
				private VisOrRange next= null;
				
				@Override
				public boolean hasNext() {
					while (next == null) {
						while (elemIter == null || !elemIter.hasNext()) {
							if (intervalIter.hasNext()) {
								Interval1D i= intervalIter.next();
								List<VisOrRange> elements= tree.get(i);
								elemIter= elements.iterator();
							} else {
								return false;
							}
						}
						next= elemIter.next();
					}
					return true;
				}
	
				@Override
				public VOrRange next() {
					if (next == null) hasNext();
					if (next == null) throw new NoSuchElementException();
					VOrRange result= new VOrRange(next);
					next= null;
					return result;
				}
			};
		} else {
            Iterator<Vns> listIter= simpleList.iterator();
		    return new Iterator<VOrRange>() {
                @Override
                public boolean hasNext() {
                    return listIter.hasNext();
                }
                @Override
                public VOrRange next() {
                    return new VOrRange(listIter.next());
                }
		    };
		}
	}
	
	public boolean contains(VOrRange query) {
		if (vp == null) return false;
		List<VOrRange> list= new ArrayList<>();
		if (vp.isIntervalScaled()) {
		    if (!query.isIntervalScaled()) {
		        throw new IllegalArgumentException("Query should be interval scaled");
		    }
		    List<VisOrRange> theList= tree.get(mkInterval(query.getSisOrRange()));
		    if (theList != null) {
    		    for (VisOrRange e : theList) {
    		        list.add(new VOrRange(e));
    		    }
		    }
		} else {
			simpleList.forEach( e -> list.add(new VOrRange(e)) );;
		}
		return list.contains(query);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((vp == null) ? 0 : vp.hashCode());
		result = prime * result + ((tree == null) ? 0 : tree.hashCode());
		result = prime * result + ((simpleList == null) ? 0 : simpleList.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VOrRangeTree))
			return false;
		VOrRangeTree other = (VOrRangeTree) obj;
		if (vp == null) {
			if (other.vp != null)
				return false;
		} else if (!vp.equals(other.vp))
			return false;
		if (tree == null) {
			if (other.tree != null)
				return false;
		} else if (!tree.equals(other.tree))
			return false;
		if (simpleList == null) {
			if (other.simpleList != null)
				return false;
		} else if (!simpleList.equals(other.simpleList))
			return false;
		return true;
	}
	
	public boolean isEmpty() {
		if (vp == null) return true;
		if (vp.isIntervalScaled()) {
			return tree.size() == 0;
		} else {
			return simpleList.isEmpty();
		}
	}

	@Override
	public String toString() {
		return toString("");
	}
	public String toString(String prefix) {
		StringBuilder sb= new StringBuilder(
				  prefix + "SusOrRangeTree [\n"
				+ prefix + "  vp = "+(vp == null ? null : vp.toString())+"\n"
				+ prefix + "  tree = [");
		if (tree == null) {
			sb.append(null+"]\n");
		} else {
			sb.append("\n");
			sb.append(prefix + "    size = "+tree.size()+"\n"
					+ prefix + "    rootInterval = "+tree.getRootInterval()+"\n"
					+ prefix + "    tree = [\n");
			for (Interval1D i : tree.searchAll()){
				sb.append(prefix + "      " + i + ": [");
				List<VisOrRange> list= tree.get(i);
				if (list == null) {
					sb.append(null+"]\n");
				} else if (list.isEmpty()) {
					sb.append("  ]\n");
				} else {
					sb.append("\n");
					for (VisOrRange vor : tree.get(i)) {
						sb.append(vor.toString(prefix+"      ")).append("\n");
					}
					sb.append(prefix + "    ]\n");
				}
			}
			sb.append(prefix + "    ]\n");
			sb.append(prefix + "  ]\n");
		}
		sb.append(prefix + "  simpleList = [");
		if (simpleList == null) {
			sb.append(null+"]\n");
		} else if (simpleList.isEmpty()) {
			sb.append("  ]\n");
		} else {
			sb.append("\n");
			for (Vns vor : simpleList) {
				sb.append(vor.toString(prefix+"    ")).append("\n");
			}
			sb.append(prefix + "  ]\n");
		}
		sb.append(prefix + "]\n");
		return sb.toString();
	}
	
}
