package epam;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import epam.api.GpsNavigator;
import epam.api.Path;
import epam.impl.Graph;
import epam.impl.Road;

/**
 * This class app demonstrates how your implementation of {@link epam.api.GpsNavigator} is intended to be used.
 */
public class ExampleApp {

    public static void main(String[] args) {
        final GpsNavigator navigator = new StubGpsNavigator();
        navigator.readData("C:\\Users\\i30mb1\\Desktop\\1.txt");

        final Path path = navigator.findPath("C", "F");
        System.out.println(path);
    }

    private static class StubGpsNavigator implements GpsNavigator {

        private ArrayList<Road> roads = new ArrayList<>();

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

                    int lineTotalCost = Integer.valueOf(cost) * Integer.valueOf(length);

                    roads.add(new Road(startPath, endPath, lineTotalCost));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Path findPath(String pointA, String pointB) {
            Graph graph = new Graph(roads, pointA, pointB);
            return new Path(graph.getResultList(), graph.getResultCost());
        }
    }
}
