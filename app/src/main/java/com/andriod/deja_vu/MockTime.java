package com.andriod.deja_vu;

import java.time.LocalDateTime;

/**
 * Created by Seth D'Agostino on 3/7/2018.
 */

public class MockTime implements CurrentTime {
    LocalDateTime localDateTime;

    public MockTime() {
        localDateTime = LocalDateTime.now();
    }

    public MockTime(LocalDateTime mockTime) {
        int year = mockTime.getYear();
        int day = mockTime.getDayOfMonth();
        int month = mockTime.getMonthValue();
        int hour = mockTime.getHour();
        int minute = mockTime.getMinute();
        int second = mockTime.getSecond();
        localDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
    }

    public LocalDateTime getLocalDateTime() {return localDateTime;}

    public String toString() {
        return localDateTime.toString();
    }

    public boolean equals(CurrentTime other) {
        if(other instanceof MockTime) {
            return ((MockTime) other).getLocalDateTime().equals(localDateTime);
        }
        else
            return false;
    }
}
