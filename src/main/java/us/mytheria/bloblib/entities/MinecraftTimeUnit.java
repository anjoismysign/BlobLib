package us.mytheria.bloblib.entities;

enum MinecraftTimeUnit {
    NANOSECONDS(MinecraftTimeUnit.NANOSECONDS_SCALE),
    MICROSECONDS(MinecraftTimeUnit.MICROSECONDS_SCALE),
    MILLISECONDS(MinecraftTimeUnit.MILLISECONDS_SCALE),
    TICKS(MinecraftTimeUnit.TICKS_SCALE),
    SECONDS(MinecraftTimeUnit.SECONDS_SCALE),
    MINUTES(MinecraftTimeUnit.MINUTES_SCALE),
    HOURS(MinecraftTimeUnit.HOURS_SCALE),
    DAYS(MinecraftTimeUnit.DAYS_SCALE);

    private static final long NANOSECONDS_SCALE = 1;
    private static final long MICROSECONDS_SCALE = NANOSECONDS_SCALE * 1000;
    private static final long MILLISECONDS_SCALE = MICROSECONDS_SCALE * 1000;
    private static final long TICKS_SCALE = MILLISECONDS_SCALE * 50;
    private static final long SECONDS_SCALE = TICKS_SCALE * 20;
    private static final long MINUTES_SCALE = SECONDS_SCALE * 60;
    private static final long HOURS_SCALE = MINUTES_SCALE * 60;
    private static final long DAYS_SCALE = HOURS_SCALE * 24;

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