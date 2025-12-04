package proforma.varproforma.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import proforma.varproforma.Vd;
import proforma.varproforma.Vis;
import proforma.varproforma.Vp;

/**
 * This class holds either a single specification value of an interval scaled type,
 * or it holds a range specification of an interval scaled type.
 */
public class VisOrRange {

    private Vis single;
    private Vis first, last;
    private Long count;
    
    public VisOrRange(Vis single) {
        this.single= single;
    }

    public VisOrRange(Vis first, Vis last, Long count) {
        this.first = first;
        this.last = last;
        this.count = count;
    }



    public boolean isRange() {
        return first != null || last != null;
    }
    public boolean isSingle() {
        return single != null;
    }

    public Vis getSingle() {
        return single;
    }
    
    public Vis getFirst() {
        return first;
    }
    
    public Vis getLast() {
        return last;
    }
    
    public Long getCount() {
        return count;
    }
    

    public Vis getHullMin() {
        if (isSingle()) {
            return single;
        } else {
            return first;
        }
    }

    public Vis getHullMax() {
        if (isSingle()) {
            return single;
        } else {
            return last;
        }
    }


    public boolean contains(VisOrRange other) {
        
        if (other.isSingle()) {
            if (this.isSingle()) {
                return singleContainsSingle(single, other.single);
            } else {
                return rangeContainsSingle(first, last, count, other.single);
            }
        } else {
            if (this.isSingle()) {
                return singleContainsRange(single, other.first, other.last, other.count);
            } else {
                return rangeContainsRange(this.first, this.last, this.count, other.first, other.last, other.count);
            }
        }
    }
    
    private static boolean singleContainsSingle(Vis single1, Vis single2) {
        return single1.equals(single2);
    }
    
    private static boolean rangeContainsSingle(Vis first1, Vis last1, Long count1, Vis single2) {
        if (!single2.getClass().equals(first1.getClass())) return false;
        if (first1.compareTo(single2) > 0) return false;
        if (last1.compareTo(single2) < 0) return false;
        Vis wid= last1.minus(first1).divideBy(count1-1);
        if (!single2.minus(first1).hasDivisor(wid)) return false;
        return true;
    }
    
    private static boolean singleContainsRange(Vis single1, Vis first2, Vis last2, Long count2) {
        return single1.equals(first2) && single1.equals(last2);
    }
    
    private static boolean rangeContainsRange(Vis first1, Vis last1, Long count1, Vis first2, Vis last2, Long count2) {
        Vis tf= first1;
        Vis of= first2;
        if (!first1.getClass().equals(first2.getClass())) return false;
        if (tf.compareTo(of) > 0) return false;
        Vis tl= last1;
        Vis ol= last2;
        if (tl.compareTo(ol) < 0) return false;
        Vis tw= tl.minus(tf).divideBy(count1-1);
        Vis ow= ol.minus(of).divideBy(count2-1);
        Vis ofmtf= of.minus(tf);
        if (! ofmtf.hasDivisor(tw) ) return false;
        if (!ow.hasDivisor(ow)) return false;
        return true;
        
    }
    
    public List<VisOrRange> calculateNewVpOrRangesIncludingThisAndGivenVpOrRange(VisOrRange given) {
        if (this.isSingle()) {
            if (contains(given)) return Arrays.asList(this);
            if (given.contains(this)) return Arrays.asList(given);
            
            if (given.isSingle()) {
                return calculateNewSisOrRangesIncludingSingleAndSingle(this.single, given.single);
            } else {
                return calculateNewSisOrRangesIncludingSingleAndRange(this.single, given.first, given.last, given.count);
            }
        } else {
            if (given.isSingle()) {
                return calculateNewSisOrRangesIncludingRangeAndSingle(this.first, this.last, this.count, given.single);
            } else {
                return calculateNewSisOrRangesIncludingRangeAndRange(this.first, this.last, this.count, given.first, given.last, given.count);
            }
        }
    }
    
    private static List<VisOrRange> calculateNewSisOrRangesIncludingSingleAndSingle(Vis single1, Vis single2) {
        if (!single1.getClass().equals(single2.getClass())) {
            return Arrays.asList(
                    new VisOrRange(single1), new VisOrRange(single2));
        }

        Vis before= single1.pred();
        if (single2.equals(before)) {
            return Arrays.asList(new VisOrRange(before, single1, 2L));
        } else {
            Vis after= single1.succ();
            if (single2.equals(after)) {
                return Arrays.asList(new VisOrRange(single1, after, 2L));
            }
        }
        return Arrays.asList(new VisOrRange(single1), new VisOrRange(single2));
    }
    
    private static List<VisOrRange> calculateNewSisOrRangesIncludingSingleAndRange(Vis single1, Vis first2, Vis last2, Long count2) {
        return calculateNewSisOrRangesIncludingRangeAndSingle(first2, last2, count2, single1);
    }
    
    private static List<VisOrRange> calculateNewSisOrRangesIncludingRangeAndSingle(Vis first1, Vis last1, Long count1, Vis single2) {
        if (!single2.getClass().equals(first1.getClass())) {
            return Arrays.asList(
                    new VisOrRange(first1, last1, count1), new VisOrRange(single2));
        }
        
        if (rangeContainsSingle(first1, last1, count1, single2)) {
            return Arrays.asList(new VisOrRange(first1, last1, count1));
        }

        Vis tf= first1;
        Vis tl= last1;
        Vis tw= tl.minus(tf).divideBy(count1-1);
        Vis before= tf.minus(tw);
        if (single2.equals(before)) {
            return Arrays.asList(new VisOrRange(before, last1, count1+1));
        } else {
            Vis after= tl.plus(tw);
            if (single2.equals(after)) {
                return Arrays.asList(new VisOrRange(first1, after, count1+1));
            }
        }
        return Arrays.asList(new VisOrRange(first1, last1, count1), new VisOrRange(single2));
    }
    
    private static List<VisOrRange> calculateNewSisOrRangesIncludingRangeAndRange(Vis first1, Vis last1, Long count1, Vis first2, Vis last2, Long count2) {
        if (!first1.getClass().equals(first2.getClass())) {
            Log.debug("X");
            return Arrays.asList(new VisOrRange(first1, last1, count1), new VisOrRange(first2, last2, count2));
        }

        if (rangeContainsRange(first1, last1, count1, first2, last2, count2)) {
            Log.debug("Y");
            return Arrays.asList(new VisOrRange(first1, last1, count1));
        }
        if (rangeContainsRange(first2, last2, count2, first1, last1, count1)) {
            Log.debug("Z");
            return Arrays.asList(new VisOrRange(first2, last2, count2));
        }
        
        Vis tf= first1;
        Vis rf= first2;
        Vis tl= last1;
        Vis rl= last2;
        Vis tw= tl.minus(tf).divideBy(count1-1);
        Vis rw= rl.minus(rf).divideBy(count2-1);
        if (tw.equals(rw)) {
            if (tf.compareTo(rf) <= 0) {
                if (rangeContainsSingle(first2, last2, count2, tl)
                    || rangeContainsSingle(first2, last2, count2, tl.plus(tw))) {
                    Log.debug("A");
                    long steps= rl.minus(tf).flooredDivideBy(tw)+1;
                    if (steps == 1) return Arrays.asList(new VisOrRange(tf));
                    return Arrays.asList(new VisOrRange(tf, rl, steps));
                }
            } else {
                if (rangeContainsSingle(first2, last2, count2, tf)
                    || rangeContainsSingle(first2, last2, count2, tf.minus(tw))) {
                    Log.debug("B");
                    long steps= tl.minus(rf).flooredDivideBy(tw)+1;
                    if (steps == 1) return Arrays.asList(new VisOrRange(rf));
                    return Arrays.asList(new VisOrRange(rf, tl, steps));
                }
            }
        } else {
            if (tw.hasDivisor(rw)) {
                // rw is divisor of tw
                if (rangeContainsSingle(first2, last2, count2, tl)) {
                    Vis tlNew= tl.minus(tw.times(tl.minus(rf).flooredDivideBy(tw)+1));
                    Log.debug("C");
                    long steps= tlNew.minus(tf).flooredDivideBy(tw)+1;
                    VisOrRange second= (steps == 1 ? new VisOrRange(tf) : new VisOrRange(tf, tlNew, steps)); 
                    return Arrays.asList(new VisOrRange(first2, last2, count2), second);
                } else if (rangeContainsSingle(first2, last2, count2, tf)){
                    Vis tfNew= tf.plus(tw.times(rl.minus(tf).flooredDivideBy(tw)+1));
                    Log.debug("D");
                    long steps= tl.minus(tfNew).flooredDivideBy(tw)+1;
                    VisOrRange second= (steps == 1 ?  new VisOrRange(tl) : new VisOrRange(tfNew, tl, steps));
                    return Arrays.asList(new VisOrRange(first2, last2, count2), second);
                } else if (tl.compareTo(rf) < 0 || rl.compareTo(tf) < 0) {
                    // no intersection
                    Log.debug("E1");
                    return Arrays.asList(new VisOrRange(first1, last1, count1), new VisOrRange(first2, last2, count2));
                } else {
                    // complete overlap
                    Log.debug("E2");
                    Vis tlNew1= tl.minus(tw.times(tl.minus(rf).flooredDivideBy(tw)+1));
                    long steps= tlNew1.minus(tf).flooredDivideBy(tw)+1;
                    VisOrRange second= (steps == 1 ? new VisOrRange(tf) : new VisOrRange(tf, tlNew1, steps)); 
                    Vis tfNew2= tf.plus(tw.times(rl.minus(tf).flooredDivideBy(tw)+1));
                    steps= tl.minus(tfNew2).flooredDivideBy(tw)+1;
                    VisOrRange third= (steps == 1 ?  new VisOrRange(tl) : new VisOrRange(tfNew2, tl, steps));
                    return Arrays.asList(second, new VisOrRange(first2, last2, count2), third);
                }
            } else if (rw.hasDivisor(tw)) {
                // tw is divisor of rw
                if (rangeContainsSingle(first1, last1, count1, rf)) {
                    Vis rfNew= rf.plus(rw.times(tl.minus(rf).flooredDivideBy(rw)+1));
                    Log.debug("F");
                    long steps= rl.minus(rfNew).flooredDivideBy(rw)+1;
                    VisOrRange second= (steps == 1 ? new VisOrRange(rl) : new VisOrRange(rfNew, rl, steps));
                    return Arrays.asList(new VisOrRange(first1, last1, count1), second);
                } else if (rangeContainsSingle(first1, last1, count1, rl)) {
                    Vis rlNew= rl.minus(rw.times(rl.minus(tf).flooredDivideBy(rw)+1));
                    Log.debug("G");
                    long steps= rlNew.minus(rf).flooredDivideBy(rw)+1;
                    VisOrRange second= (steps == 1 ? new VisOrRange(rf) : new VisOrRange(rf, rlNew, steps));
                    return Arrays.asList(second, new VisOrRange(first1, last1, count1));
                } else if (tl.compareTo(rf) < 0 || rl.compareTo(tf) < 0) {
                    // no intersection
                    Log.debug("H1");
                    return Arrays.asList(new VisOrRange(first1, last1, count1), new VisOrRange(first2, last2, count2));
                } else {
                    // complete overlap
                    Log.debug("H2");
                    Vis rlNew1= rl.minus(rw.times(rl.minus(tf).flooredDivideBy(rw)+1));
                    long steps= rlNew1.minus(rf).flooredDivideBy(rw)+1;
                    VisOrRange second= (steps == 1 ? new VisOrRange(rf) : new VisOrRange(rf, rlNew1, steps));
                    Vis rfNew2= rf.plus(rw.times(tl.minus(rf).flooredDivideBy(rw)+1));
                    steps= rl.minus(rfNew2).flooredDivideBy(rw)+1;
                    VisOrRange third= (steps == 1 ? new VisOrRange(rl) : new VisOrRange(rfNew2, rl, steps));
                    return Arrays.asList(second, new VisOrRange(first1, last1, count1), third);
                }
            } else {
                Log.debug("I");
                return Arrays.asList(new VisOrRange(first1, last1, count1), new VisOrRange(first2, last2, count2));
            }
            
        }
        Log.debug("K");
        return Arrays.asList(new VisOrRange(first1, last1, count1), new VisOrRange(first2, last2, count2));
    }
    
    
    public long size() {
        if (isSingle()) {
            return 1;
        } else {
            return count;
        }
    }
    
    public double distanceTo(VisOrRange other) {
        if (this.isSingle()) {
            if (other.isSingle()) {
                return distanceFromSingleToSingle(single, other.single);
            } else {
                return distanceFromSingleToRange(single, other.first, other.last, other.count);
            }
        } else {
            if (other.isSingle()) {
                return distanceFromRangeToSingle(first, last, count, other.single);
            } else {
                return distanceFromRangeToRange(first, last, count, other.first, other.last, other.count);
            }
        }
    }
    
    private static double distanceFromSingleToSingle(Vis single1, Vis single2) {
        if (singleContainsSingle(single1, single2)) {
            return Vis.zero(single1.getVp()).toDouble();
        }
        return single1.minus(single2).abs().toDouble();
    }
    
    private static double distanceFromSingleToRange(Vis single1, Vis first2, Vis last2, Long count2) {
        if (singleContainsRange(single1, first2, last2, count2)) {
            return Vis.zero(single1.getVp()).toDouble();
        }
        return distanceFromRangeToSingle(first2, last2, count2, single1);
    }

    private static double distanceFromRangeToSingle(Vis first1, Vis last1, Long count1, Vis single2) {
        if (rangeContainsSingle(first1, last1, count1, single2)) {
            return Vis.zero(first1.getVp()).toDouble();
        }
        Vis df= first1.minus(single2).abs();
        Vis dl= last1.minus(single2).abs();
        return df.min(dl).toDouble();
    }

    private static double distanceFromRangeToRange(Vis first1, Vis last1, Long count1, Vis first2, Vis last2, Long count2) {
        double zero= Vis.zero(first1.getVp()).toDouble();
        if (rangeContainsRange(first1, last1, count1, first2, last2, count2)) return zero;
        if (rangeContainsRange(first2, last2, count2, first1, last1, count1)) return zero;
        if (rangeContainsSingle(first1, last1, count1, first2)) return zero;
        if (rangeContainsSingle(first1, last1, count1, last2)) return zero;
        if (rangeContainsSingle(first2, last2, count2, first1)) return zero;
        if (rangeContainsSingle(first2, last2, count2, last1)) return zero;
        
        Vis df= first1.minus(last2).abs();
        Vis dl= last1.minus(first2).abs();
        return df.min(dl).toDouble();
    }

    

    public String toString(String prefix)  {
        if (isSingle()) {
            return prefix + "SisOrRange [single=" + single.toString() + "]";
        } else {
            return prefix + "SisOrRange [first=" + first + ", last=" + last + ", count=" + count + "]";
        }
    }

    
    
    
    
    public Vis stepWidth() {
        if (isSingle()) {
            throw new UnsupportedOperationException("Cannot stepWidth for single SisOrRange");
        }
        return this.last.minus(this.first).divideBy(this.count-1);
    }
    
    
    public Vis getStep(long index) {
        if (isSingle()) {
            if (index == 0) return single;
            throw new UnsupportedOperationException("Cannot getStep("+index+") for single SisOrRange");
        }
        if (index == 0) return first;
        if (index == count) return last;
        Vis diff= last.minus(first); 
        return first.plus(diff.divideBy(count-1).times(index));
    }


    
    public Vis getFirstCeil(double lowerBound, boolean inclusive, Vp vp) {
        if (isSingle()) {
            throw new UnsupportedOperationException("Cannot getFirstCeil for single SisOrRange");
        }
        double sw= stepWidth().toDouble();
        double max= Math.max(first.toDouble(), lowerBound);
        // project onto steps:
        double result= Math.floor( (max - first.toDouble()) / sw ) * sw + first.toDouble();
        double ma= max - vp.getAccuracyOrDefault();
        while (result < ma || !inclusive && result == ma) {
            result += sw;
        }
        return Vis.fromDouble(result, vp);
    }

    public Vis getLastFloor(double upperBound, boolean inclusive, Vp vp) {
        if (isSingle()) {
            throw new UnsupportedOperationException("Cannot getLastFloor for single SisOrRange");
        }
        double sw= stepWidth().toDouble();
        double min= Math.min(upperBound, last.toDouble());
        // project onto steps:
        double result= Math.floor( (min - first.toDouble()) / sw + 1 ) * sw + first.toDouble();
        double ma= min+ vp.getAccuracyOrDefault();
        while (result > ma || !inclusive && result == ma) {
            result -= sw;
        }
        return Vis.fromDouble(result, vp);
    }



    public void validate() {
        if (isRange()) {
            Log.debug("validating VpRange "+toString());
            if (first == null || first.getSpec() == null) throw new IllegalArgumentException(getClass()+": first missing");
            if (last == null || last.getSpec() == null) throw new IllegalArgumentException(getClass()+": last missing");
            if (!first.getClass().equals(last.getClass())) throw new IllegalArgumentException(getClass()+": first and last should have equal type. observed: '"+first.getClass()+"' and '"+last.getClass()+"'");
            if (first.compareTo(last) >= 0) throw new IllegalArgumentException(getClass()+": illegal range boundary order '"+first+"' >= '"+last+"'");
            
            if (count == null) {
                count= first.countStepsFromThisTo(last);
            }
            if (count <= 1) throw new IllegalArgumentException(getClass()+": illegal range count '"+count+"'");
            
            Vp vp= null;
            if (first.getVp() != null) vp= first.getVp();
            if (vp == null && last.getVp() != null) vp= last.getVp();
            if (vp == null) {
                // if vp is null, this means "not validated yet"
            } else {
                if (!(first instanceof Vd) && count >= 2) {
                    // integer types must end up at integer steps...
                    Vis diff= last.minus(first); 
                    if (!diff.modulo(count-1).isZero()) {
                        throw new IllegalArgumentException(getClass()+": range '"+first+"'-'"+last+"' does not match count='"+count+"'");
                    }
                }
            }
        }
    }
    
    
    
    private static Random random= new Random();
    
    public Vis getRandomElement() {
        long index= Math.abs(random.nextLong()) % count;
        return getStep(index);
    }
}
