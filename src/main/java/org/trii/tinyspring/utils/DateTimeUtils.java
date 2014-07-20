package org.trii.tinyspring.utils;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * User: Sebastian MA
 * Date: June 22, 2014
 * Time: 23:17
 * <p/>
 * DateTimeUtils using JodaTime library
 */
public class DateTimeUtils {

	static DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

	static DateFormat hourMinuteFormatter = new SimpleDateFormat("HH:mm");

	/**
	 * formats date to HH:mm
	 *
	 * @param date
	 * @return
	 */
	public static String toHHmm(Date date) {

		return hourMinuteFormatter.format(date);
	}

	/**
	 * @param date
	 * @return
	 */
	public static String toDateString(Date date) {

		return dateFormatter.format(date);
	}

	public static Timestamp toTimestamp(String dateString) {

		try {
			return new Timestamp(dateFormatter.parse(dateString).getTime());
		} catch(ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * a offset equals to zero means same day as the given time.a offset less than zero
	 * means [offset] days before the given time. a offset greater than zero means
	 * [offset] days after the given time.<br></br>
	 *
	 * @param time
	 * @param offset offset in days.
	 * @return
	 */
	public static Date toStartOfDay(Date time, int offset) {

		DateTime jodaTime = new DateTime(time.getTime());
		jodaTime = jodaTime
				.hourOfDay().withMinimumValue()
				.minuteOfHour().withMinimumValue()
				.secondOfMinute().withMinimumValue();

		jodaTime = jodaTime.plusDays(offset);
		return new Date(jodaTime.getMillis());
	}

	public static Date toEndOfDay(Date time, int offset) {

		DateTime jodaTime = new DateTime(time.getTime());
		jodaTime = jodaTime
				.hourOfDay().withMaximumValue()
				.minuteOfHour().withMaximumValue()
				.secondOfMinute().withMaximumValue();
		jodaTime = jodaTime.plusDays(offset);
		return new Date(jodaTime.getMillis());
	}

	public static Date toStartOfMonth(int year, int month, int offset) {

		return new MutableDateTime()
				.year().set(year)
				.monthOfYear().set(month)
				.toDateTime()
				.dayOfMonth().withMinimumValue()
				.hourOfDay().withMinimumValue()
				.minuteOfHour().withMinimumValue()
				.secondOfMinute().withMinimumValue()
				.millisOfSecond().withMinimumValue()
				.plusMonths(offset)
				.toDate();
	}

	public static Date toEndOfMonth(int year, int month, int offset) {

		return new MutableDateTime()
				.year().set(year)
				.monthOfYear().set(month)
				.toDateTime()
				.dayOfMonth().withMaximumValue()
				.hourOfDay().withMaximumValue()
				.minuteOfHour().withMaximumValue()
				.secondOfMinute().withMaximumValue()
				.millisOfSecond().withMaximumValue()
				.plusMonths(offset)
				.toDate();
	}

	public static Timestamp now() {

		return new Timestamp(System.currentTimeMillis());
	}
}
