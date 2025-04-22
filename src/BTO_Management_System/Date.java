package BTO_Management_System;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Date {
    private int day;
    private int month;
    private int year;

    // Constructor for Date class (e.g., "yyyy-MM-dd")
    public Date(int day, int month, int year){
        this.day = day;
        this.month = month;
        this.year = year;
    }

    // Getter methods
    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public boolean isBefore(Date other) {
        if (this.year < other.year) {
            return true;
        } else if (this.year == other.year) {
            if (this.month < other.month) {
                return true;
            } else if (this.month == other.month) {
                return this.day < other.day;
            }
        }
        return false;
    }

    public boolean isAfter(Date other) {
        if (this.year > other.year) {
            return true;
        } else if (this.year == other.year) {
            if (this.month > other.month) {
                return true;
            } else if (this.month == other.month) {
                return this.day > other.day;
            }
        }
        return false;
    }

    public boolean isEqual(Date other) {
        return this.year == other.year && this.month == other.month && this.day == other.day;
    }

    public String toString() {
        return String.format("%04d-%02d-%02d", year, month, day);
    }
}

