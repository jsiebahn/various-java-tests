package com.github.jsiebahn.various.tests.builder;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static com.github.jsiebahn.various.tests.builder.DateBuilder.date;
import static org.junit.Assert.assertEquals;

/**
 * $Id$
 *
 * @author  jsiebahn
 * @since 21.09.14 11:58
 */
public class DateBuilderTest {

    @Test
    public void testAdd() {
        Calendar expected = Calendar.getInstance();
        expected.set(2014, Calendar.APRIL, 28, 23, 59, 0);
        expected.set(Calendar.MILLISECOND, 0);

        Calendar in = Calendar.getInstance();
        in.set(2014, Calendar.FEBRUARY, 28, 23, 59, 0);

        Date result = date(in)
                .withoutMilliseconds()
                .addMonth(2)
                .addWeeks(-1)
                .addDays(7)
                .addHours(24)
                .addMinutes(-1441)
                .addSeconds(60)
                .build();
        assertEquals(expected.getTime(), result);

    }

    @Test
    public void testMonth() {

        Calendar expected = Calendar.getInstance();

        Date date = date(expected).withMonth(DateBuilder.Month.DECEMBER).build();

        expected.set(Calendar.MONTH, Calendar.DECEMBER);


        assertEquals(expected.getTime(), date);
    }

    @Test
    public void testWithTime() throws Exception {

        Calendar expected = Calendar.getInstance();
        expected.set(Calendar.HOUR_OF_DAY, 1);
        expected.set(Calendar.MINUTE, 0);
        expected.set(Calendar.SECOND, 0);
        expected.set(Calendar.MILLISECOND, 0);

        Date date = date().withTime(1).build();

        assertEquals(expected.getTime(), date);
    }

    @Test
    public void testWithTime2() throws Exception {

        Calendar expected = Calendar.getInstance();
        expected.set(Calendar.HOUR_OF_DAY, 1);
        expected.set(Calendar.MINUTE, 2);
        expected.set(Calendar.SECOND, 0);
        expected.set(Calendar.MILLISECOND, 0);

        Date date = date().withTime(1, 2).build();

        assertEquals(expected.getTime(), date);
    }

    @Test
    public void testWithTime3() throws Exception {

        Calendar expected = Calendar.getInstance();
        expected.set(Calendar.HOUR_OF_DAY, 1);
        expected.set(Calendar.MINUTE, 2);
        expected.set(Calendar.SECOND, 3);
        expected.set(Calendar.MILLISECOND, 0);

        Date date = date().withTime(1, 2, 3).build();

        assertEquals(expected.getTime(), date);
    }

    @Test
    public void testWithTime4() throws Exception {

        Calendar expected = Calendar.getInstance();
        expected.set(Calendar.HOUR_OF_DAY, 1);
        expected.set(Calendar.MINUTE, 2);
        expected.set(Calendar.SECOND, 3);
        expected.set(Calendar.MILLISECOND, 4);

        Date date = date().withTime(1, 2, 3, 4).build();

        assertEquals(expected.getTime(), date);
    }


    @Test
    public void testWithoutMilliseconds() throws Exception {

        Calendar expected = Calendar.getInstance();
        expected.set(Calendar.MILLISECOND, 0);

        Date date = date().withoutMilliseconds().build();
        assertEquals(expected.getTime(), date);
    }

    @Test
    public void testWithMidnight() throws Exception {

        Calendar expected = Calendar.getInstance();
        expected.set(Calendar.HOUR_OF_DAY, 0);
        expected.set(Calendar.MINUTE, 0);
        expected.set(Calendar.SECOND, 0);
        expected.set(Calendar.MILLISECOND, 0);

        Date date = date().withMidnight().build();
        assertEquals(expected.getTime(), date);
    }

    @Test
    public void testDateFromDate() throws Exception {
        Date date = Calendar.getInstance().getTime();
        assertEquals(date, date(date).build());
    }

    @Test
    public void testDateFromCalendar() throws Exception {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        assertEquals(date, date(calendar).build());
    }
}
