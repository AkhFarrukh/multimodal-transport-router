package tj.stib;

public class QueueElement {
    public final String stopId;
    public final int fScore;

    QueueElement(String stopId, int fScore) {
        this.stopId = stopId;
        this.fScore = fScore;
    }
}
