package epam;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import epam.api.GpsNavigator;
import epam.api.Path;

/**
 * This class app demonstrates how your implementation of {@link epam.api.GpsNavigator} is intended to be used.
 */
public class ExampleApp {

    public static void main(String[] args) {
        final GpsNavigator navigator = new StubGpsNavigator();
        navigator.readData("C:\\Users\\i30mb1\\Desktop\\1.txt");

        final Path path = navigator.findPath("F", "B");
        System.out.println(path);
    }

    private static class StubGpsNavigator implements GpsNavigator {

        private ArrayList<Graph.Line> graph = new ArrayList<>();

        @Override
        public void readData(String filePath) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
                for (String line : lines) {
                    String[] lineParts = line.split(" ");
                    String startPath = lineParts[0];
                    String endPath = lineParts[1];
                    String length = lineParts[2];
                    String cost = lineParts[3];

                    String lineName = startPath + " " + endPath;
                    Integer lineTotalCost = Integer.valueOf(cost) * Integer.valueOf(length);

                    graph.add(new Graph.Line(startPath, endPath, lineTotalCost));
                }

            } catch (IOException e) {
                System.out.println("file not found");
            }
        }

        @Override
        public Path findPath(String pointA, String pointB) {
            Graph g = new Graph(graph);
            g.dijkstra(pointA);
            g.printPath(pointB);
            return new Path(Arrays.asList("A", "C", "D", "E"), 22);
        }
    }
}
