package com.andriod.deja_vu;

import java.time.LocalDateTime;

/**
 * Created by Seth D'Agostino on 3/7/2018.
 */

public class ActualTime implements CurrentTime {
    LocalDateTime localDateTime;

    public ActualTime() {
        localDateTime = LocalDateTime.now();
    }

    public LocalDateTime getLocalDateTime() {return localDateTime;}

    public String toString() {
        return localDateTime.toString();
    }

    public boolean equals(CurrentTime other) {
        if(other instanceof ActualTime) {
            return ((ActualTime) other).getLocalDateTime().equals(localDateTime);
        }
        else
            return false;
    }
}
