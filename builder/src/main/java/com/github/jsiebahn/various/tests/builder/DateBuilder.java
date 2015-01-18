package com.github.jsiebahn.various.tests.builder;

import java.util.Calendar;
import java.util.Date;

/**
 * $Id$
 *
 * @author  jsiebahn
 * @since 21.09.14 11:25
 */
public class DateBuilder {

    private Calendar calendar;

    private DateBuilder() {
        this.calendar = Calendar.getInstance();
    }

    public static enum Month {
        JANUARY(Calendar.JANUARY),
        FEBRUARY(Calendar.FEBRUARY),
        MARCH(Calendar.MARCH),
        APRIL(Calendar.APRIL),
        MAY(Calendar.MAY),
        JUNE(Calendar.JUNE),
        JULY(Calendar.JULY),
        AUGUST(Calendar.AUGUST),
        SEPTEMBER(Calendar.SEPTEMBER),
        OCTOBER(Calendar.OCTOBER),
        NOVEMBER(Calendar.NOVEMBER),
        DECEMBER(Calendar.DECEMBER);

        private int intValue;

        private Month(int intValue) {
            this.intValue = intValue;
        }

        private int getIntValue() {
            return intValue;
        }

    }

    public static DateBuilder date() {
        return new DateBuilder();
    }

    public static DateBuilder date(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return date(calendar);
    }

    public static DateBuilder date(Calendar calendar) {
        DateBuilder dateBuilder = new DateBuilder();
        return dateBuilder
                .withYear(calendar.get(Calendar.YEAR))
                .withMonth(calendar.get(Calendar.MONTH))
                .withDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH))
                .withHour(calendar.get(Calendar.HOUR_OF_DAY))
                .withMinute(calendar.get(Calendar.MINUTE))
                .withSecond(calendar.get(Calendar.SECOND))
                .withMillisecond(calendar.get(Calendar.MILLISECOND));
    }


    //
    // relatives
    //

    public DateBuilder addMonth(int months) {
        this.calendar.add(Calendar.MONTH, months);
        return this;
    }

    public DateBuilder addWeeks(int weeks) {
        this.calendar.add(Calendar.DAY_OF_MONTH, 7 * weeks);
        return this;
    }

    public DateBuilder addDays(int days) {
        this.calendar.add(Calendar.DAY_OF_MONTH, days);
        return this;
    }

    public DateBuilder addHours(int hours) {
        this.calendar.add(Calendar.HOUR, hours);
        return this;
    }

    public DateBuilder addMinutes(int minutes) {
        this.calendar.add(Calendar.MINUTE, minutes);
        return this;
    }

    public DateBuilder addSeconds(int seconds) {
        this.calendar.add(Calendar.SECOND, seconds);
        return this;
    }


    //
    // absolutes
    //

    public DateBuilder withYear(int year) {
        this.calendar.set(Calendar.YEAR, year);
        return this;
    }

    public DateBuilder withMonth(Month month) {
        this.calendar.set(Calendar.MONTH, month.getIntValue());
        return this;
    }

    private DateBuilder withMonth(int month) {
        this.calendar.set(Calendar.MONTH, month);
        return this;
    }

    public DateBuilder withDayOfMonth(int dayOfMonth) {
        this.calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return this;
    }

    public DateBuilder withHour(int hourOfDay) {
        this.calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        return this;
    }

    public DateBuilder withMinute(int minute) {
        this.calendar.set(Calendar.MINUTE, minute);
        return this;
    }

    public DateBuilder withSecond(int second) {
        this.calendar.set(Calendar.SECOND, second);
        return this;
    }

    public DateBuilder withoutMilliseconds() {
        return this.withMillisecond(0);
    }

    public DateBuilder withMillisecond(int millisecond) {
        this.calendar.set(Calendar.MILLISECOND, millisecond);
        return this;
    }

    public DateBuilder withMidnight() {
        return this.withTime(0, 0, 0, 0);
    }

    public DateBuilder withTime(int hourOfDay) {
        return this.withTime(hourOfDay, 0, 0, 0);
    }

    public DateBuilder withTime(int hourOfDay, int minuteOfHour) {
        return this.withTime(hourOfDay, minuteOfHour, 0, 0);
    }

    public DateBuilder withTime(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        return this.withTime(hourOfDay, minuteOfHour, secondOfMinute, 0);
    }

    public DateBuilder withTime(int hourOfDay, int minuteOfHour, int secondOfMinute, int millisecond) {
        this.calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        this.calendar.set(Calendar.MINUTE, minuteOfHour);
        this.calendar.set(Calendar.SECOND, secondOfMinute);
        this.calendar.set(Calendar.MILLISECOND, millisecond);
        return this;
    }

    public Date build() {
        return this.calendar.getTime();
    }
}
