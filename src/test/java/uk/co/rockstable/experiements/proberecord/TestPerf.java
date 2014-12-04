package uk.co.rockstable.experiements.proberecord;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

public class TestPerf {
    public static final int TEST_DURATION_SECONDS = 60;
    public static final Random r = new Random();
    public static final int NO_OF_THREADS = 8;
    public static final String FILENAME = "/tmp/foo";

    private static final AtomicLong sequencer = new AtomicLong();


    @Test
    public void testWriter() throws Exception {
        String file = FILENAME;

        Probe probe = new MemoryMappedProbe(file);
        long deadLine = System.currentTimeMillis() + (TEST_DURATION_SECONDS * 1000);
        Runnable r = () -> {
            while (System.currentTimeMillis() < deadLine) {
                long callId = getNextCallId();
                probe.record(Stage.CREATED, callId);
                waitRandom();

                probe.record(Stage.SERIALIZATION, callId);
                waitRandom();

                probe.record(Stage.BAR, callId);
                waitRandom();

                probe.record(Stage.FOO, callId);

                if (callId % 10_000 == 0) {
                    System.out.println("Call ID: " + callId);
                }
            }
        };

        for (int i = 0; i < NO_OF_THREADS; i++) {
            new Thread(r).start();
        }

        Thread.sleep(TEST_DURATION_SECONDS * 1000);

    }

    @Test
    public void testBasicReader() {
        ProbeReader probeReader = new ProbeReader(FILENAME);
        for (;;) {
            Record record = probeReader.read();
            System.out.println(record);
        }
    }

    private void waitRandom() {
//        LockSupport.parkNanos(r.nextInt(10_000));
    }

    private long getNextCallId() {
        return sequencer.getAndIncrement();
    }
}
