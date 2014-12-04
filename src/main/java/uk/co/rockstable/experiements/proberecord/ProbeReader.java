package uk.co.rockstable.experiements.proberecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static uk.co.rockstable.experiements.proberecord.Constants.*;

public class ProbeReader {
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(RECORD_SIZE_BYTES);
    private final FileChannel channel;

    public ProbeReader(String filename) {
        File file = new File(filename);
        try {
            channel = new RandomAccessFile(file, "rw").getChannel();
        } catch (FileNotFoundException e) {
            throw Utils.rethrow(e);
        }
    }

    public Record read() {
        try {
            long timestamp;
            long callId;
            byte stageIdByte;
            do {
                int read = 0;
                do {
                    read += channel.read(buffer);
                } while (read != RECORD_SIZE_BYTES);
                buffer.rewind();

                timestamp = buffer.getLong();
                callId = buffer.getLong();
                stageIdByte = buffer.get();
                buffer.rewind();
            } while (stageIdByte == Stage.IN_PROCESS.ordinal());

            Stage stage = Stage.values()[stageIdByte];
            return new Record(callId, timestamp, stage);
        } catch (IOException e) {
            throw Utils.rethrow(e);
        }
    }

}
