package BTO_Management_System;

/**
 * Represents a calendar date with day, month, and year components.
 * Provides methods for comparing dates and formatting the date as a string.
 */
public class Date {
    /**
     * The day of the month.
     */
    private int day;
    /**
     * The month of the year (1-indexed).
     */
    private int month;
    /**
     * The year.
     */
    private int year;

    /**
     * Constructs a new Date object with the specified day, month, and year.
     *
     * @param day   The day of the month.
     * @param month The month of the year (1-indexed).
     * @param year  The year.
     */
    public Date(int day, int month, int year){
        this.day = day;
        this.month = month;
        this.year = year;
    }

    /**
     * Returns the day of the month.
     *
     * @return The day.
     */
    public int getDay() {
        return day;
    }

    /**
     * Returns the month of the year (1-indexed).
     *
     * @return The month.
     */
    public int getMonth() {
        return month;
    }

    /**
     * Returns the year.
     *
     * @return The year.
     */
    public int getYear() {
        return year;
    }

    /**
     * Checks if this date is chronologically before another date.
     *
     * @param other The other Date object to compare with.
     * @return true if this date is before the other date, false otherwise.
     */
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

    /**
     * Checks if this date is chronologically after another date.
     *
     * @param other The other Date object to compare with.
     * @return true if this date is after the other date, false otherwise.
     */
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

    /**
     * Checks if this date is equal to another date.
     *
     * @param other The other Date object to compare with.
     * @return true if this date is the same as the other date, false otherwise.
     */
    public boolean isEqual(Date other) {
        return this.year == other.year && this.month == other.month && this.day == other.day;
    }

    /**
     * Returns a string representation of the date in the format "YYYY-MM-DD".
     *
     * @return The formatted date string.
     */
    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d", year, month, day);
    }
}