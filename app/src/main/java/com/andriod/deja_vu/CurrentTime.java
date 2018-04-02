package com.andriod.deja_vu;

import java.time.LocalDateTime;

/**
 * Created by Seth D'Agostino on 3/7/2018.
 */

public interface CurrentTime {
    LocalDateTime getLocalDateTime();
    String toString();
    boolean equals(CurrentTime other);
}
