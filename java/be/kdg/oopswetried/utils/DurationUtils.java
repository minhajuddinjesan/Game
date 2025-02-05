package be.kdg.oopswetried.utils;

import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class DurationUtils {
    private DurationUtils() {
        throw new AssertionError("This constructor should never be called!");
    }

    public static String formatSecondsToPrettyDuration(BigInteger durationInSeconds) {
        List<String> parts = new ArrayList<>();

        Duration duration = Duration.ofSeconds(durationInSeconds.longValue());

        long days = duration.toDaysPart();

        if (days > 0) {
            parts.add(days + "d");
        }

        int hours = duration.toHoursPart();

        if (hours > 0 || !parts.isEmpty()) {
            parts.add(hours + "h");
        }

        int minutes = duration.toMinutesPart();

        if (minutes > 0 || !parts.isEmpty()) {
            parts.add(minutes + "m");
        }

        int seconds = duration.toSecondsPart();

        parts.add(seconds + "s");

        return String.join(" ", parts);
    }
}
