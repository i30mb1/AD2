package epam.api;

public interface GpsNavigator {

    /**
     * This method needs to be called before using the {@link #findPath(String, String)}.
     *
     * @param filePath path to file, which contains data in the following format:
     *                 A B 3 2
     *                 A C 4 2
     *                 C B 5 3
     *                 D E 7 4
     *                 C D 4 5
     *                 D Pizza 7 8
     *                 Pizza Metro 9 9
     *                 etc.
     *                 Where the first two columns represent the road considering its direction, the third one represents
     *                 its length and the fourth one represents cost of transportation.
     */
    void readData(String filePath);

    /**
     * Find path between two points.
     *
     * @param pointA starting point.
     * @param pointB end point.
     * @return object, which describes the found path.
     */
    Path findPath(String pointA, String pointB);
}
