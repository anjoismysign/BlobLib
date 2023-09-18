package us.mytheria.bloblib.entities;

public enum MinecraftTimeUnit {
    NANOSECONDS(1L),
    MICROSECONDS(1000L),
    MILLISECONDS(1000000L),
    TICKS(50000000L),
    SECONDS(1000000000L),
    MINUTES(60000000000L),
    HOURS(3600000000000L),
    DAYS(86400000000000L);
    private final long scale;

    MinecraftTimeUnit(long scale) {
        this.scale = scale;
    }

    /**
     * Will convert the given duration in the given unit to this unit.
     * For example, to convert 4 ticks to milliseconds, use:
     * <pre> {@code
     * long ticks = MinecraftTimeUnit.MILLISECONDS.convert(4, MinecraftTimeUnit.TICKS);
     * }</pre>
     *
     * @param sourceDuration the source duration
     * @param timeUnit       the source unit of the duration
     * @return the converted duration
     */
    public double convert(long sourceDuration, MinecraftTimeUnit timeUnit) {
        if (timeUnit.scale < this.scale) {
            return (double) sourceDuration / ((double) this.scale / timeUnit.scale);
        } else {
            return ((double) timeUnit.scale / this.scale) * sourceDuration;
        }
    }
}