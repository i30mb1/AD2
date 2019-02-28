package epam;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import epam.api.GpsNavigator;
import epam.api.Path;

/**
 * This class app demonstrates how your implementation is intended to be used.
 */
public class ExampleAppVer1 {

    public static void main(String[] args) {
        final GpsNavigator navigator = new StubGpsNavigator();
        navigator.readData("C:\\Users\\i30mb1\\Desktop\\1.txt");

        final Path path = navigator.findPath("C", "F");
        System.out.println(path);
    }

    private static class StubGpsNavigator implements GpsNavigator {

        private HashMap<String, Integer> roadsWithCost = new HashMap<>();
        private Integer resultCost = 0;
        private ArrayList<String> resultPath = new ArrayList<>();

        @Override
        public void readData(String filePath) {
            // Read data from file.
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

                    roadsWithCost.put(lineName, lineTotalCost);
                }

            } catch (IOException e) {
                System.out.println("file not found");
            }
        }

        @Override
        public Path findPath(String pointA, String pointB) {
            return new Path(doWork(pointA, pointB), resultCost);
        }

        private ArrayList<String> doWork(String pointA, String pointB) {
            resultPath.add(pointA);

            try {
                do {
                    pointA = findMinCostPath(pointA);
                    resultPath.add(pointA);
                } while (!pointA.equals(pointB));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultPath;
        }

        private String findMinCostPath(String pointA) throws Exception {
            Integer minCost = null;
            String pointB = null;

            for (Map.Entry<String, Integer> entry : roadsWithCost.entrySet()) {
                String[] currentPathPoints = entry.getKey().split(" ");
                String currentPointA = currentPathPoints[0];
                String currentPointB = currentPathPoints[1];
                Integer currentCost = entry.getValue();

                boolean pathStartWithPointA = currentPointA.equals(pointA);

                if (pathStartWithPointA) {
                    boolean lessThanMinCost = (minCost == null || (currentCost < minCost));

                    if (lessThanMinCost) {
                        minCost = currentCost;
                        pointB = currentPointB;
                    }
                }
            }

            if (pointB == null) {
                throw new Exception("cannot find path with pointB in file");
            } else {
                resultCost += minCost;
                roadsWithCost.remove(pointA + " " + pointB);
                return pointB;
            }
        }
    }
}
