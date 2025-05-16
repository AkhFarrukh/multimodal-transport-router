package tj.stib;

import tj.stib.enums.RouteType;

public class Edge {
    public final Node endNode;
    public final int weight; // time in seconds
    public final RouteType routeType;

    public Edge(Node endNode, int weight, RouteType routeType) {
        this.endNode = endNode;
        this.weight = weight;
        this.routeType = routeType;
    }
}


