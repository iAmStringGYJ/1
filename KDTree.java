package org.swufe;

import java.util.*;

class Point {
    private int x; // Indicates that this is a private class
    private int y; // Indicates that this is a private class

    public Point(int x, int y) {  // Class initialization
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }  // Return value of x

    public int getY() {
        return y;
    } // Return value of y

    public Integer getItem(int i) { // Return value of x or y by input
        assert i == 0 || i == 1;  // This is the key
        if (i == 0) return x;
        else return y;
    }

    @Override
    public String toString() { // Override toString function
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) { // Override equals function to judge the size of two numbers
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0; // Compare the size and return the result
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    } // Rewrite function hashCode to give Class a number to represent Class;
}

class Rectangle {
    private Point lower; // Indicates that this is a private class
    private Point upper; // Indicates that this is a private class

    public Rectangle(Point lower, Point upper) { // Class initialization
        this.lower = lower;
        this.upper = upper;
    }

    public Point getLower() {
        return lower;
    } // Return lower

    public Point getUpper() {
        return upper;
    } // Return upper

    public boolean isContain(Point p) { // Judge whether p is in the Class
        return this.lower.getX() <= p.getX() && this.upper.getX() >= p.getX() &&
                this.lower.getY() <= p.getY() && this.upper.getY() >= p.getY();
        // By comparing the size of x and y
    }

    @Override
    public String toString() { // Override toString function
        return "Rectangle{" +
                "lower=" + lower +
                ", upper=" + upper +
                '}';
    }
}

public class KDTree {

    private static class Node { // Inner class
        private Point location; // Indicates that this is a private class
        private Node left; // Indicates that this is a private class
        private Node right; // Indicates that this is a private class

        public Node(Point location, Node left, Node right) { // Class initialization
            this.location = location;
            this.left = left;
            this.right = right;
        }
    }

    private Node root; // Indicates that this is a private class
    private int size; // Indicates that this is a private class

    public KDTree() { // Class initialization
        root = null;
        size = 0;
    }

    int u = 0;

    // Build a KDTree by recursion
    public Node build(int l, int r, List<Point> p, boolean ifis) {
        if (ifis == false) {
            p.subList(l, r + 1).sort(new Comparator<Point>() { // Override compare function
                @Override
                public int compare(Point o1, Point o2) {
                    return o1.getX() - o2.getX();
                }
            });
        }
        else{
            p.subList(l, r + 1).sort(new Comparator<Point>() {  // Override compare function
                @Override
                public int compare(Point o1, Point o2) {
                    return o1.getY() - o2.getY();
                }
            });
        }
        int mid = (l + r) / 2; // The instructions are dichotomies
        Node node = new Node(p.get(mid), null, null);
        if(u == 0){
            root = node;
            u = 1;
        }
        if (l < mid) {
            node.left = build(l, mid - 1, p, !ifis); // Left child's Recursion
        }
        if (mid < r) {
            node.right = build(mid + 1, r, p, !ifis); // Right child's Recursion
        }
        return node;
    }

    // insert function
    public void insert(List<Point> p) { // Insert p into the KDTree
        build(0, p.size() - 1, p, false);
    }

    public List<Point> range(Rectangle rectangle) { // Find all the point in the rectangle
        List<Point>arr = new ArrayList<>();
        range(rectangle, root, false, arr);
//        System.out.println(arr.toString());
        return arr;
    }

    // Look up meet the requirement by recursion
    public void range(Rectangle rectangle, Node node, boolean ifis, List<Point> arr){
        if(null == node){
            return;
        }

        Point point = node.location;
        if (rectangle.isContain(point)) arr.add(point); // Call function

        // Determine whether it is x or y by judgement
        int value = !ifis ? point.getX() : point.getY(); // Judge user x or y by ifis
        int min = !ifis ? rectangle.getLower().getX() : rectangle.getLower().getY();
        int max = !ifis ? rectangle.getUpper().getX() : rectangle.getUpper().getY();
        ifis = !ifis;
        // If yes, continue nesting
        if (min <= value) {
            range(rectangle, node.left, ifis, arr);
        }
        if (max >= value) {
            range(rectangle, node.right, ifis, arr);
        }
    }

    public int PointDistance(Point a, Point b){ // Return distance between two points
        int m1 = a.getX();
        int n1 = a.getY();
        int m2 = b.getX();
        int n2 = b.getY();
        return (m1 - m2) * (m1 - m2) + (n1 - n2) * (n1 - n2);
        // This is the square of the distance between them
    }

    public Point nearest(Point target){ // Return the point closest the target
        Point t = nearest(target, root, null, false);
        System.out.println(t.getX() + " " + t.getY());
        return t;
    }

    public Point nearest(Point target, Node node, Point currentBest, boolean isVertical){ // By recursion
        if(null == node) return currentBest;

        // Judge whether to compare x or y
        int value1 = !isVertical ? target.getX() : target.getY();
        int value2 = !isVertical ? node.location.getX() : node.location.getY();

        // Judge next
        Node next = value1 < value2 ? node.left : node.right;
        // Judge whether the other half meets the conditions
        Node other = value1 < value2 ? node.right : node.left;
        Point nextBest = nearest(target, next, node.location, !isVertical);

        int currentDistance = 0;
        int nextDistance = PointDistance(nextBest, target);
        currentDistance = (currentBest == null) ? nextDistance :
                (Math.min(PointDistance(currentBest, target), nextDistance));
        // These are the two possible scenarios
        if(currentBest == null){
            currentDistance = nextDistance;
            currentBest = nextBest;
        }
        else{
            currentDistance = PointDistance(currentBest, target);
            if(currentDistance > nextDistance){
                currentDistance = nextDistance;
                currentBest = nextBest;
            }
        }

        // Find out if your partner is satisfied
        if ((other != null) && (PointDistance(node.location, target) < currentDistance)) {
            currentBest = nearest(target, other, currentBest, isVertical);
        }
        return currentBest;
    }

    public static void main(String[] args) {
        List<Point> points = new ArrayList<>();

        // Input data
        for (int i = 0; i < 2000; i++) {
            for (int j = 0; j < 2000; j++) {
                points.add(new Point(i, j));
            }
        }

        Point lower = new Point(500, 500);
        Point upper = new Point(504, 504);
        Rectangle rectangle = new Rectangle(lower, upper);

        // naive method
        double start = System.currentTimeMillis();
        List<Point> result1 = points.stream().filter(rectangle::isContain).toList();
        double end = System.currentTimeMillis();
        System.out.printf("Naive method: %fms\n", end - start);

        KDTree kdTree = new KDTree();
        kdTree.insert(points);
        // kd tree
        start = System.currentTimeMillis();
        List<Point> result2 = kdTree.range(rectangle);
        end = System.currentTimeMillis();
        System.out.printf("kd tree: %fms\n", end - start);

        // Find the point nearest the point
        Point t = new Point(1998,2001);
        kdTree.nearest(t);
    }
}