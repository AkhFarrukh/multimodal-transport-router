package tj.stib;

public class QueueElement {
    public final Edge edge;
    public final int fScore;

    QueueElement(Edge edge, int fScore) {
        this.edge = edge;
        this.fScore = fScore;
    }
}
