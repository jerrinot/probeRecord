package uk.co.rockstable.experiements.proberecord;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicLong;

import static uk.co.rockstable.experiements.proberecord.Constants.*;

public class MemoryMappedProbe implements Probe {
    private static final boolean USE_MEMBAR = !Boolean.getBoolean("skip.fences");

    private final AtomicLong position;
    private final MappedByteBuffer map;

    public MemoryMappedProbe(String path) {
        position = new AtomicLong(1);
        File file = new File(path);
        try {
            FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
            map = channel.map(FileChannel.MapMode.READ_WRITE, 0, NO_OF_SLOTS * RECORD_SIZE_BYTES);
        } catch (FileNotFoundException e) {
            throw Utils.rethrow(e);
        } catch (IOException e) {
            throw Utils.rethrow(e);
        }
    }

    @Override
    public void record(Stage stage, long operationId) {
        int offset = claimPosition() << 5; //same as multiple by 32 (RECORD_SIZE_BYTES)

        byte stageByte = (byte) stage.ordinal();
        long timestamp = System.nanoTime();

        map.put(offset + 16, (byte) Stage.IN_PROCESS.ordinal()); //invalidate current record. TODO: Is it worth?
        if (USE_MEMBAR) {
            Utils.storeFence();
        }

        map.putLong(offset, timestamp);
        map.putLong(offset + 8, operationId);
        map.put(offset + 16, stageByte); //commit current record

        if (USE_MEMBAR) {
            Utils.storeFence();
        }
    }

    private int claimPosition() {
        long l = position.getAndIncrement();
        return (int) Utils.modPowerOfTwo(l, NO_OF_SLOTS);
    }
}
