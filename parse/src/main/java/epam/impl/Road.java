package epam.impl;

public class Road {
    public final String pointA, pointB;
    public final int cost;

    public Road(String pointA, String pointB, int cost) {
        this.pointA = pointA;
        this.pointB = pointB;
        this.cost = cost;
    }
}
