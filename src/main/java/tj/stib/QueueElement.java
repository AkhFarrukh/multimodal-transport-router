package tj.stib;

public class QueueElement {
    public final Node node;
    public final int fScore;

    QueueElement(Node node, int fScore) {
        this.node = node;
        this.fScore = fScore;
    }
}
