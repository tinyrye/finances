package com.softwhistle.util;

import static java.time.format.DateTimeFormatter.*;
import static java.util.Collections.unmodifiableList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeParses
{
	private static final Logger LOG = LoggerFactory.getLogger(DateTimeParses.class);

	private static final List<DateTimeFormatter> localDateFormats = Arrays.asList(
		ISO_LOCAL_DATE
	);
	private static final List<DateTimeFormatter> localDateTimeFormats = Arrays.asList(
		ISO_LOCAL_DATE_TIME
	);
	private static final List<DateTimeFormatter> offsetDateTimeFormats = Arrays.asList(
		ISO_OFFSET_DATE_TIME,
		ISO_OFFSET_DATE
	);

	public static class DateTimeParsesException extends RuntimeException {
		public final List<DateTimeParseException> attempts;
		public DateTimeParsesException(List<DateTimeParseException> attempts) {
			this.attempts = unmodifiableList(attempts);
		}
	}

	public static OffsetDateTime parseFlexibleOffsetDateTime(String value) {
		List<DateTimeParseException> parseExceptions = new ArrayList<DateTimeParseException>();
		for (DateTimeFormatter offsetDateTimeFormat: offsetDateTimeFormats) {
			LOG.debug("Matching date string to offset date time format: {}", offsetDateTimeFormat);
			try {
				return OffsetDateTime.parse(value, offsetDateTimeFormat);
			} catch (DateTimeParseException ex) {
				parseExceptions.add(ex);
			}
		}
		for (DateTimeFormatter localDateTimeFormat: localDateTimeFormats) {
			LOG.debug("Matching date string to offset date format: {}", localDateTimeFormat);
			try {
				return LocalDateTime.parse(value, localDateTimeFormat).atOffset(ZoneOffset.UTC);
			} catch (DateTimeParseException ex) {
				parseExceptions.add(ex);
			}
		}
		for (DateTimeFormatter localDateFormat: localDateFormats) {
			LOG.debug("Matching date string to local date format: {}", localDateFormat);
			try {
				return LocalDate.parse(value, localDateFormat).atStartOfDay().atOffset(ZoneOffset.UTC);
			} catch (DateTimeParseException ex) {
				parseExceptions.add(ex);
			}
		}
		throw new DateTimeParsesException(parseExceptions);
	}
}