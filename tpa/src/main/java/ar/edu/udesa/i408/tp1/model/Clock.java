package ar.edu.udesa.i408.tp1.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Clock {
    public LocalDate today() {
        return LocalDate.now();
    }

    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
