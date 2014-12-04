package uk.co.rockstable.experiements.proberecord;

public class Record {
    private final long callId;
    private final long timestamp;
    private final Stage stage;


    public Record(long callId, long timestamp, Stage stage) {
        this.callId = callId;
        this.timestamp = timestamp;
        this.stage = stage;
    }

    @Override
    public String toString() {
        return "Record{" +
                "callId=" + callId +
                ", timestamp=" + timestamp +
                ", stage=" + stage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        if (callId != record.callId) return false;
        if (timestamp != record.timestamp) return false;
        if (stage != record.stage) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (callId ^ (callId >>> 32));
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + stage.hashCode();
        return result;
    }
}
