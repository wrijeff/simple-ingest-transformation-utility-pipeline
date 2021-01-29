package com.amazon.dataprepper.plugins.buffer;

import com.amazon.dataprepper.model.buffer.Buffer;
import com.amazon.dataprepper.model.record.Record;
import com.amazon.dataprepper.model.CheckpointState;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

public class TestBuffer implements Buffer<Record<String>> {
    private final Queue<Record<String>> buffer;
    private final int batchSize;
    private final boolean imitateTimeout;

    public TestBuffer(final Queue<Record<String>> buffer, final int batchSize) {
        this.buffer = buffer;
        this.batchSize = batchSize;
        this.imitateTimeout = false;
    }

    public TestBuffer(final Queue<Record<String>> buffer, final int batchSize, final boolean imitateTimeout) {
        this.buffer = buffer;
        this.batchSize = batchSize;
        this.imitateTimeout = imitateTimeout;
    }

    @Override
    public void write(Record<String> record, int timeoutInMillis) throws TimeoutException {
        if(imitateTimeout) {
            throw new TimeoutException();
        }
        buffer.add(record);
    }

    @Override
    public Map.Entry<Collection<Record<String>>, CheckpointState> read(int timeoutInMillis) {
        final List<Record<String>> records = new ArrayList<>();
        int index = 0;
        Record<String> record;
        while(index < batchSize && (record = buffer.poll()) != null) {
            records.add(record);
            index++;
        }
        final CheckpointState checkpointState = new CheckpointState(records.size());
        return new AbstractMap.SimpleEntry<>(records, checkpointState);
    }

    @Override
    public void checkpoint(final CheckpointState checkpointState) {

    }

    public int size() {
        return buffer.size();
    }
}
