package org.dandoy.utils;

import java.util.concurrent.TimeUnit;

public class ElapsedStopWatch {
    private final TimeUnit _timeUnit;
    private long _lastTime;

    public ElapsedStopWatch() {
        this(TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("WeakerAccess")
    public ElapsedStopWatch(TimeUnit timeUnit) {
        _timeUnit = timeUnit;
        _lastTime = System.currentTimeMillis();
    }

    public String toString() {
        final long now = System.currentTimeMillis();
        try {
            final long l = TimeUnit.MILLISECONDS.convert(now - _lastTime, _timeUnit);
            return Long.toString(l) + getUnit();
        } finally {
            _lastTime = now;
        }
    }

    private String getUnit() {
        switch (_timeUnit) {
            case NANOSECONDS:
                return "ns";
            case MICROSECONDS:
                return "μs";
            case MILLISECONDS:
                return "ms";
            case SECONDS:
                return "s";
            case MINUTES:
                return "m";
            case HOURS:
                return "h";
            case DAYS:
                return "d";
            default:
                throw new IllegalStateException();
        }
    }
}
