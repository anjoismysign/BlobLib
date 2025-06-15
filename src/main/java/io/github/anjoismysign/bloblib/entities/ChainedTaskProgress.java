package io.github.anjoismysign.bloblib.entities;

public class ChainedTaskProgress {
    private final int size;
    private int processed, progress;
    private final ChainedTask task;

    public ChainedTaskProgress(int size, ChainedTask task) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than zero.");
        }
        this.size = size;
        this.processed = 0;
        this.progress = 0;
        this.task = task;
    }

    public void run() {
        processed++;
        double percentComplete = (double) processed / size * 100.0;

        // Check if we have reached the next whole percentage
        if ((int) percentComplete > progress) {
            progress = (int) percentComplete;
            task.accept(progress);
        }
    }
}
