package epam.api;

import java.util.List;

public class Path {

    private static String TO_STRING_PATTERN = "Path: %s; of cost %d";

    /**
     * All points of the path in the order we need to visit it.
     */
    final List<String> path;

    /**
     * Total cost of the path.
     */
    final int cost;

    public Path(List<String> path, int cost) {
        this.path = path;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_PATTERN, String.join(" ", path), cost);
    }
}
