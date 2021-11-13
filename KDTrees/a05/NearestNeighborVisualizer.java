package a05;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;

/**
 * Nearest neighbor search. 
 * 
 * To find a closest point to a given query point, we start at the root and 
 * recursively search in both subtrees using the following pruning rule: 
 * 
 * If the closest point discovered so far is closer than the distance 
 * between the query point and the rectangle corresponding to a node, 
 * then there is no need to explore that node (or its subtrees). 
 * 
 * Therefore, you should search a node only if it might contain 
 * a point that is closer than the best one found so far. 
 * 
 * The effectiveness of the pruning rule depends on quickly finding a nearby point. 
 * To do this, organize your recursive method so that when there are two possible 
 * subtrees to go down, you choose first the subtree that is on the same side of 
 * the splitting line as the query point; the closest point found while exploring 
 * the first subtree may enable pruning of the second subtree.
 *
 * @author Kevin Mora
 */
public class NearestNeighborVisualizer {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
        String filename = "src/points/input100K.txt";
        In in = new In(filename);

        StdDraw.show(0);

        // initialize the two data structures with point from standard input
        PointST<Integer> brute = new PointST<Integer>();
        KdTreeST<Integer> kdtree = new KdTreeST<Integer>();
        for (int i = 0; !in.isEmpty(); i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.put(p, i);
            brute.put(p, i);
        }

        while (true) {
            // the location (x, y) of the mouse
            double x = StdDraw.mouseX();
            double y = StdDraw.mouseY();
            Point2D query = new Point2D(x, y);

            // draw all of the points
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(.01);
            for (Point2D p : brute.points())
                p.draw();

            // draw in red the nearest neighbor according to the brute-force algorithm
            StdDraw.setPenRadius(.03);
            StdDraw.setPenColor(StdDraw.RED);
            brute.nearest(query).draw();
            StdDraw.setPenRadius(.02);

            // draw in blue the nearest neighbor according to the kd-tree algorithm
            StdDraw.setPenColor(StdDraw.BLUE);
            kdtree.nearest(query).draw();
            StdDraw.show(0);
            StdDraw.show(40);
        }
    }
}