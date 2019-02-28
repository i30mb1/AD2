package epam.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

public class Graph {
    private ArrayList<String> resultList = new ArrayList<>();
    private int resultCost = 0;
    private Map<String, Vertex> graph;

    public Graph(ArrayList<Road> edges, String pointA, String pointB) {
        graph = new HashMap<>(edges.size());

        for (Road e : edges) {
            if (!graph.containsKey(e.pointA)) graph.put(e.pointA, new Vertex(e.pointA));
            if (!graph.containsKey(e.pointB)) graph.put(e.pointB, new Vertex(e.pointB));
        }

        for (Road e : edges) {
            graph.get(e.pointA).neighbours.put(graph.get(e.pointB), e.cost);
        }

        setUpVertices(pointA);
        savePath(pointB);
    }

    public ArrayList<String> getResultList() {
        return resultList;
    }

    public int getResultCost() {
        return resultCost;
    }

    private void setUpVertices(String pointA) {
        if (!graph.containsKey(pointA)) {
            System.err.printf("Roads does not contain pointA \"%s\"\n", pointA);
            return;
        }
        final Vertex source = graph.get(pointA);
        NavigableSet<Vertex> q = new TreeSet<>();

        for (Vertex v : graph.values()) {
            v.previous = v == source ? source : null;
            v.cost = v == source ? 0 : Integer.MAX_VALUE;
            q.add(v);
        }

        setUpVerticesNeighbours(q);
    }

    private void setUpVerticesNeighbours(final NavigableSet<Vertex> q) {
        Vertex u, v;
        while (!q.isEmpty()) {
            u = q.pollFirst();
            if (u.cost == Integer.MAX_VALUE) break;

            for (Map.Entry<Vertex, Integer> a : u.neighbours.entrySet()) {
                v = a.getKey();

                final int alternateCost = u.cost + a.getValue();
                if (alternateCost < v.cost) {
                    q.remove(v);
                    v.cost = alternateCost;
                    v.previous = u;
                    q.add(v);
                }
            }
        }
    }

    public void savePath(String pointB) {
        if (!graph.containsKey(pointB)) {
            System.err.printf("Roads does not contain pointB \"%s\"\n", pointB);
            return;
        }

        graph.get(pointB).savePathAndCost();
    }

    public class Vertex implements Comparable<Vertex> {
        public final String name;
        public final Map<Vertex, Integer> neighbours = new HashMap<>();
        public int cost = Integer.MAX_VALUE;
        public Vertex previous = null;

        public Vertex(String name) {
            this.name = name;
        }

        private void savePathAndCost() {
            if (this == this.previous) {
                resultList.add(this.name);
            } else if (this.previous == null) {
                System.err.printf("Road to point %s does not exist\n", this.name);
            } else {
                this.previous.savePathAndCost();
                resultList.add(this.name);
                resultCost = this.cost;
            }
        }

        public int compareTo(Vertex other) {
            if (cost == other.cost) {
                return name.compareTo(other.name);
            }

            return Integer.compare(cost, other.cost);
        }

        @Override
        public String toString() {
            return "(" + name + ", " + cost + ")";
        }
    }
}
