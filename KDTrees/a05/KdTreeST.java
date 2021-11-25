package a05;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/**
 * Representation of the 2D points of a geometric 
 * primitive, and axis-aligned rectangles in the plane:
 * 
 * 
 *                               axis-aligned rectangle r            
 * distance to r = 0.0 ─────────┐ ┌────────────────────┐   (0.8, 0.6)
 *                              │ │                    │             
 * distance to r = 0.3 ─ ─ ─    │ │              ┌─────┴───────┐     
 *      (0.1, 0.4)          │   └─┼──────▶ ◯     │intersects r │     
 *                           ─ ─ ─│              ├─────────────┤     
 *                                │              └─────┬───────┘     
 * distance to r = 0.5 ─ ─ ─ ─ ─ ─│                    │             
 *       (0.0, 0.0)                └────────────────────┘             
 *                                (0.4, 0.3)                         
 * 
 * The mutable data type is a symbol table with Point2D – a RedBlackBST was also implemented
 * to identify the points in the nodes, using the x- and y-coordinates of the points as keys 
 * in strictly alternating sequence, starting with the x-coordinates.
 * 
 * @author Kevin Mora
 * @author Nathan Clark
 * @author Dawood Ahmed
 */
public class KdTreeST<Value> {
	private int size;
	private Node root;
	
	private class Node {
		private Point2D point;
		private Value val;
		private RectHV rect;
		private Node left;
		private Node right;

		public Node(Point2D point, Value val, RectHV rect) {
			this.point = point;
			this.val = val;
			this.rect = rect;
		}
	}

	/**
	 * Creates an empty symbol table of points.
	 */
	public KdTreeST() {
		size = 0;
		root = null;
	}
	
	/**
	 * Returns the number of points.
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns the status of the symbol table.
	 */
	public boolean isEmpty() {
		return size() == 0;
	}
	
	/**
	 * Determines if Symbol Table has a point. 
	 * @param point ––> Point2D
	 */
	public boolean contains(Point2D point){
		if(point == null) {
			throw new NullPointerException();
		}
		return get(point) != null;
	}

	/**
	 * Private helper method for put().
	 * 
	 * The algorithms for search and insert are similar to those for BSTs, but at the root we use the x-coordinate 
	 * (if the point to be inserted has a smaller x-coordinate than the point at the root, go left; otherwise go right); 
	 * then at the next level, we use the y-coordinate (if the point to be inserted has a smaller y-coordinate than 
	 * the point in the node, go left; otherwise go right); then at the next level the x-coordinate, and so forth.
	 */
	private Node put(Node parent, Node node, Point2D p, Value val, boolean vertical) {
		if (node == null) {
			size++;
			return new Node(p, val, rectCreator(parent, p, vertical));
		}
		if (compareXY(node, p, vertical) < 0)
			node.left = put(node, node.left, p, val, !vertical);
		else if (node.point.equals(p))
			node.val = val;
		else
			node.right = put(node, node.right, p, val, !vertical);
		return node;
	}
	
	/**
	 * Associates the value with the point.
	 */
	public void put(Point2D point, Value val) {
		if (point == null || val == null) {
			throw new NullPointerException();
		}
		root = put(null, root, point, val, true);
	}

	/**
	 * Returns the value associated with a point.
	 */
	public Value get(Point2D p) {
		if (p == null) {
			throw new NullPointerException("arguments can't be null");
		}
		return get(root, p, true);
	}

	/**
	 * Private helper method for get().
	 */
	private Value get(Node node, Point2D p, boolean isVertical) {
		if (node == null) {
			return null;
		}
		if (compareXY(node, p, isVertical) < 0)
			return get(node.left, p, !isVertical);
		else if (node.point.equals(p))
			return node.val;
		else
			return get(node.right, p, !isVertical);
	}
	
	/**
	 * Returns all the points in the Symbol Table.
	 */
	public Iterable<Point2D> points(){
		Queue<Point2D> queue = new Queue<>();
		if(size ==0) {
			return queue;
		}
		
		Queue<Node> nodeQ = new Queue<>();
		nodeQ.enqueue(root);
		while(!nodeQ.isEmpty()){
			Node temp = nodeQ.dequeue();
			queue.enqueue(temp.point);
			if (temp.left != null) {
				nodeQ.enqueue(temp.left);
			}
			if (temp.right != null) {
				nodeQ.enqueue(temp.right);
			}
		}
		return queue;
	}
	
	/**
	 * Returns all the points that are inside the rectangle.
	 */
	public Iterable<Point2D> range(RectHV rect){
		if (rect == null) {
			throw new NullPointerException();
		}
		Queue<Point2D> rectPoints = new Queue<>();
		range(rect, rectPoints, root);
		return rectPoints;
	}
	
	private void range(RectHV rect, Queue<Point2D> rectPoints, Node node) {
		if (node == null || !rect.intersects(node.rect)) return;
		if (rect.contains(node.point)) {
			rectPoints.enqueue(node.point);
		}
		range(rect, rectPoints, node.left);
		range(rect, rectPoints, node.right);
	}
	
	/**
	 * Returns the nearest neighbor to point p; 
	 * null if the Symbol Table is empty.
	 */
	public Point2D nearest(Point2D p){
		if(p == null) {
			throw new NullPointerException();
		}
		return nearest(p, root, root.point);
	}
	
	/**
	 * Private helper method for nearest.
	 */
	private Point2D nearest(Point2D p, Node node, Point2D paragon) {
		if (node == null) {
			return paragon;
		}
		if (node.rect.distanceSquaredTo(p) > paragon.distanceSquaredTo(p)) return paragon;
		if (p.distanceSquaredTo(node.point) < p.distanceSquaredTo(paragon)) paragon = node.point;
		
		if (node.left != null && node.left.rect.contains(p)) {
			paragon = nearest(p, node.left, paragon);
			paragon = nearest(p, node.right, paragon);
		} else {
			paragon = nearest(p, node.right, paragon);
			paragon = nearest(p, node.left, paragon);
		}
		return paragon;
	}
	
	/**
	 * Takes a point (X or Y), and returns its node in 2D.
	 */
	private double compareXY(Node node, Point2D p, boolean isVertical){
		if(isVertical) {
			return p.x() - node.point.x();
		}
		else return p.y() - node.point.y();
	}
	
	/**
	 * NOTE: If you are running JUnits, delete this comment.
	 * Initializes a new rectangle of points in a plane.
	 * [<em>xmin</em>, <em>xmax</em>] x [<em>ymin</em>, <em>ymax</em>].
	 * 
	 * Parameters for instantiation:
     	 * @param parent ––––––> Node
	 * @param p –––––––––––> Point2D
	 * @param isVertical ––> boolean
    	 *
       	 * Creation parameters:
    	 * @param  xmin the <em>x</em>-coordinate of the lower-left endPoint
    	 * @param  xmax the <em>x</em>-coordinate of the upper-right endPoint
    	 * @param  ymin the <em>y</em>-coordinate of the lower-left endPoint
     	 * @param  ymax the <em>y</em>-coordinate of the upper-right endPoint
	 */
	private RectHV rectCreator(Node parent, Point2D p, boolean isVertical) {
		if (parent == null) {
			return new RectHV(- Double.MAX_VALUE, - Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
		}
		double compare = compareXY(parent, p, !isVertical);
		
		if (isVertical  && compare >= 0) 
			return new RectHV(parent.rect.xmin(), parent.point.y(), parent.rect.xmax(), parent.rect.ymax());

		if (isVertical  && compare <  0) 
			return new RectHV(parent.rect.xmin(), parent.rect.ymin(), parent.rect.xmax(), parent.point.y());

		if (!isVertical && compare >= 0) 
			return new RectHV(parent.point.x(), parent.rect.ymin(), parent.rect.xmax(), parent.rect.ymax());

		if (!isVertical && compare <  0) 
			return new RectHV(parent.rect.xmin(), parent.rect.ymin(), parent.point.x(), parent.rect.ymax());
		
		return null;
	}
	
	/**
	 * Client application.
	 */
	public static void main(String[] args) {
		String filename = "src/points/input100K.txt";
        In in = new In(filename);
        
        KdTreeST<Integer> kdtree = new KdTreeST<>();
        for (int i = 0; !in.isEmpty(); i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.put(p, i);
        }
        StdOut.println("Starting");
		long starttime = System.currentTimeMillis();
		int numberOfTimesToCalculate = 10000000;
		for (int i = 0; i < numberOfTimesToCalculate; i++) {
			kdtree.nearest(new Point2D(StdRandom.uniform(), StdRandom.uniform()));
		}
		long endtime = System.currentTimeMillis();
		double timeTakenInSeconds = (endtime-starttime)/1000.0;
		StdOut.println("Total time: " + timeTakenInSeconds);
		StdOut.println("Average per second: " + numberOfTimesToCalculate / timeTakenInSeconds);
	}
}
