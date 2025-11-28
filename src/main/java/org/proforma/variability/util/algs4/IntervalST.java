package org.proforma.variability.util.algs4;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.proforma.variability.util.Log;


// source: https://algs4.cs.princeton.edu/93intersection/IntervalST.java.html
public class IntervalST<T>  {

    private Node root;   // root of the BST
    private Random random= new Random(1L);

    // BST helper node data type
    private class Node {
        Interval1D interval;      // key
        T value;                  // associated data
        Node left, right;         // left and right subtrees
        int N;                    // size of subtree rooted at this node
        double max;               // max endpoint in subtree rooted at this node

        Node(Interval1D interval, T value) {
            this.interval = interval;
            this.value    = value;
            this.N        = 1;
            this.max      = interval.max;
        }
    }


    public Interval1D getRootInterval() {
    	if (root == null) return null;
    	return root.interval;
    }
    
   /***************************************************************************
    *  BST search
    ***************************************************************************/

    public boolean contains(Interval1D interval) {
        return (get(interval) != null);
    }

    // return value associated with the given key
    // if no such value, return null
    public T get(Interval1D interval) {
        return get(root, interval);
    }

    private T get(Node x, Interval1D interval) {
        if (x == null)                  return null;
        int cmp = interval.compareTo(x.interval);
        if      (cmp < 0) return get(x.left, interval);
        else if (cmp > 0) return get(x.right, interval);
        else              return x.value;
    }


   /***************************************************************************
    *  randomized insertion
    ***************************************************************************/
    public void put(Interval1D interval, T value) {
        if (contains(interval)) {
        	Log.debug("Removing "+interval);
        	remove(interval);  
        }
        root = randomizedInsert(root, interval, value);
    }

    // make new node the root with uniform probability
    private Node randomizedInsert(Node x, Interval1D interval, T value) {
        if (x == null) return new Node(interval, value);
        if (random.nextDouble() * size(x) < 1.0) return rootInsert(x, interval, value);
        int cmp = interval.compareTo(x.interval);
        if (cmp < 0)  x.left  = randomizedInsert(x.left,  interval, value);
        else          x.right = randomizedInsert(x.right, interval, value);
        fix(x);
        return x;
    }

    private Node rootInsert(Node x, Interval1D interval, T value) {
        if (x == null) return new Node(interval, value);
        int cmp = interval.compareTo(x.interval);
        if (cmp < 0) { x.left  = rootInsert(x.left,  interval, value); x = rotR(x); }
        else         { x.right = rootInsert(x.right, interval, value); x = rotL(x); }
        return x;
    }


   /***************************************************************************
    *  deletion
    ***************************************************************************/
    private Node joinLR(Node a, Node b) { 
        if (a == null) return b;
        if (b == null) return a;

        if (random.nextDouble() * (size(a) + size(b)) < size(a))  {
            a.right = joinLR(a.right, b);
            fix(a);
            return a;
        }
        else {
            b.left = joinLR(a, b.left);
            fix(b);
            return b;
        }
    }

    // remove and return value associated with given interval;
    // if no such interval exists return null
    public T remove(Interval1D interval) {
        T value = get(interval);
        root = remove(root, interval);
        return value;
    }

    private Node remove(Node h, Interval1D interval) {
        if (h == null) return null;
        int cmp = interval.compareTo(h.interval);
        if      (cmp < 0) h.left  = remove(h.left,  interval);
        else if (cmp > 0) h.right = remove(h.right, interval);
        else              h = joinLR(h.left, h.right);
        fix(h);
        return h;
    }


   /***************************************************************************
    *  Interval searching
    ***************************************************************************/

    // return an interval in data structure that intersects the given interval;
    // return null if no such interval exists
    // running time is proportional to log N
    public Interval1D search(Interval1D interval) {
        return search(root, interval);
    }

    // look in subtree rooted at x
    public Interval1D search(Node x, Interval1D interval) {
        while (x != null) {
            if (interval.intersects(x.interval)) return x.interval;
            else if (x.left == null)             x = x.right;
            else if (x.left.max < interval.min)  x = x.right;
            else                                 x = x.left;
        }
        return null;
    }


    // return *all* intervals in data structure that intersect the given interval
    // running time is proportional to R log N, where R is the number of intersections
    public List<Interval1D> searchAll(Interval1D interval) {
        LinkedList<Interval1D> list = new LinkedList<Interval1D>();
        searchAll(root, interval, list);
        return list;
    }

    // return *all* intervals in data structure
    public List<Interval1D> searchAll() {
        LinkedList<Interval1D> list = new LinkedList<Interval1D>();
        searchAll(root, list);
        return list;
    }

    // look in subtree rooted at x
    public boolean searchAll(Node x, Interval1D interval, LinkedList<Interval1D> list) {
         boolean found1 = false;
         boolean found2 = false;
         boolean found3 = false;
         if (x == null)
            return false;
        if (interval.intersects(x.interval)) {
            list.add(x.interval);
            found1 = true;
        }
        if (x.left != null && x.left.max >= interval.min)
            found2 = searchAll(x.left, interval, list);
        if (found2 || x.left == null || x.left.max < interval.min)
            found3 = searchAll(x.right, interval, list);
        return found1 || found2 || found3;
    }


    // look in subtree rooted at x
    public void searchAll(Node x, LinkedList<Interval1D> list) {
    	if (x == null) return;
        list.add(x.interval);
        searchAll(x.left, list);
        searchAll(x.right, list);
    }


   /***************************************************************************
    *  useful binary tree functions
    ***************************************************************************/

    // return number of nodes in subtree rooted at x
    public int size() { return size(root); }
    private int size(Node x) { 
        if (x == null) return 0;
        else           return x.N;
    }

    // height of tree (empty tree height = 0)
    public int height() { return height(root); }
    private int height(Node x) {
        if (x == null) return 0;
        return 1 + Math.max(height(x.left), height(x.right));
    }


   /***************************************************************************
    *  helper BST functions
    ***************************************************************************/

    // fix auxilliar information (subtree count and max fields)
    private void fix(Node x) {
        if (x == null) return;
        x.N = 1 + size(x.left) + size(x.right);
        x.max = max3(x.interval.max, max(x.left), max(x.right));
    }

    private double max(Node x) {
        if (x == null) return -Double.MIN_VALUE;
        return x.max;
    }

    // precondition: a is not null
    private double max3(double a, double b, double c) {
        return Math.max(a, Math.max(b, c));
    }

    // right rotate
    private Node rotR(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        fix(h);
        fix(x);
        return x;
    }

    // left rotate
    private Node rotL(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        fix(h);
        fix(x);
        return x;
    }
    

}