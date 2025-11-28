package org.proforma.variability.util.algs4;


// source: https://algs4.cs.princeton.edu/93intersection/Interval1D.java.html
public class Interval1D implements Comparable<Interval1D> {
    public final double min;  // min endpoint
    public final double max;  // max endpoint

    // precondition: min <= max
    public Interval1D(double min, double max) {
        if (min <= max) {
            this.min = min;
            this.max = max;
        }
        else throw new RuntimeException("Illegal interval");
    }

    // does this interval intersect that one?
    public boolean intersects(Interval1D that) {
        if (that.max < this.min) return false;
        if (this.max < that.min) return false;
        return true;
    }

    // does this interval a intersect b?
    public boolean contains(double x) {
        return (min <= x) && (x <= max);
    }

    public int compareTo(Interval1D that) {
        if      (this.min < that.min) return -1;
        else if (this.min > that.min) return +1;
        else if (this.max < that.max) return -1;
        else if (this.max > that.max) return +1;
        else                          return  0;
    }

    public String toString() {
        return "[" + min + ", " + max + "]";
    }
}