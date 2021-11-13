package a05;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.RedBlackBST;

/**
 * Create a symbol table data type whose keys are two-dimensional points. 
 * Use a 2d-tree to support efficient range search (find all of the points contained in a 
 * query rectangle) and nearest neighbor search (find a closest point to a query point). 
 * 
 * 2d-trees have numerous applications, ranging from classifying astronomical objects 
 * to computer animation, to speeding up neural networks, to mining data, to image retrieval.
 * 
 * @author Nathan Clark
 * @author Kevin Mora
 * @author Dawood Ahmed
 */
public class PointST<Value> {
	private RedBlackBST<Point2D, Value> rbTree;

	/**
	 * Creates an empty ordered Symbol Table of points.
	 */
	public PointST() {
		rbTree = new RedBlackBST<>();
	}

	/**
	 * Is the symbol table empty?
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Number of points.
	 */
	public int size() {
		return rbTree.size();
	}

	/**
	 * Associates the value with its respective point.
	 */
	public void put(Point2D p, Value val) {
		if (p == null) {
			throw new NullPointerException();
		}
		rbTree.put(p, val);
	}

	/**
	 * Value associated with point p.
	 */
	public Value get(Point2D p) {
		if (p == null) {
			throw new NullPointerException("Point cannot be null; error in method get()");
		}
		return rbTree.get(p);
	}

	/**
	 * Does the symbol table contain point p?
	 */
	public boolean contains(Point2D p) {
		if (p == null) {
			throw new NullPointerException("Point cannot be null; error in method contains()");
		}
		return rbTree.contains(p);
	}

	/**
	 * All points in the symbol table.
	 */
	public Iterable<Point2D> points() {
		return rbTree.keys();
	}

	/**
	 * All points that are inside the rectangle.
	 */
	public Iterable<Point2D> range(RectHV rect) {
		if (rect == null) {
			throw new NullPointerException("Rectangle cannot be null; error in method range()");
		}
		Queue<Point2D> queue2D = new Queue<>();
		for (Point2D p : rbTree.keys()) {
			if (rect.contains(p)) {
				queue2D.enqueue(p);
			}
		}
		return queue2D;
	}

	/**
	 * A nearest neighbor to point p; null if the symbol table is empty.
	 */
	public Point2D nearest(Point2D p) {
		if (p == null) {
			throw new NullPointerException("Point cannot be null; error in method nearest()");
		}
		Point2D nearest = rbTree.max();
		for (Point2D point : rbTree.keys()) {
			if (p.distanceSquaredTo(point) < p.distanceSquaredTo(nearest)) {
				nearest = point;
			}
		}
		return nearest;
	}

	public static void main(String[] args) {}
}