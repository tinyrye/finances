package com.softwhistle.dao;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.Iterator;
import javax.sql.DataSource;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.IOUtils;

import com.softwhistle.model.CustomOccurrenceSchedule;

public class BudgetDaoTest
{
	private static Config config = ConfigFactory.load();
	private static BasicDataSource databaseDataSource;
	private static BudgetDao testedObject;

	@BeforeClass
	public static void setupDb() throws Exception {
		databaseDataSource = new BasicDataSource();
		databaseDataSource.setUrl(config.getString("datasource.url"));
		databaseDataSource.setUsername(config.getString("datasource.username"));
		databaseDataSource.setPassword(config.getString("datasource.password"));
		databaseDataSource.getConnection().createStatement().executeUpdate("DELETE FROM budget_custom_occurrences");
		databaseDataSource.getConnection().createStatement().executeUpdate("DELETE FROM budget_occurrence_schedule");
		testedObject = new BudgetDao(databaseDataSource);
	}

	@Test
	public void testInsertOccurrenceSchedule() {
		CustomOccurrenceSchedule testSchedule = new CustomOccurrenceSchedule();
		testSchedule.addOccurrence(OffsetDateTime.parse("2017-03-05T00:00:00Z", ISO_OFFSET_DATE_TIME));
		testSchedule.addOccurrence(OffsetDateTime.parse("2017-03-17T00:00:00Z", ISO_OFFSET_DATE_TIME));
		testSchedule.addOccurrence(OffsetDateTime.parse("2017-03-21T00:00:00Z", ISO_OFFSET_DATE_TIME));
		testedObject.insert(testSchedule);
		assertThat(testSchedule.id, notNullValue());
		CustomOccurrenceSchedule actualSchedule = (CustomOccurrenceSchedule) testedObject.getCustomOccurrenceScheduleById(testSchedule.id);
		assertThat(actualSchedule, notNullValue());
		assertThat(actualSchedule.id, equalTo(testSchedule.id));
		assertThat(actualSchedule.occurrences.size(), equalTo(testSchedule.occurrences.size()));

		OffsetDateTime occurrencesFromFilter = OffsetDateTime.parse("2017-01-01T00:00:00Z", ISO_OFFSET_DATE_TIME);
		OffsetDateTime occurrencesToFilter = OffsetDateTime.parse("2017-04-01T00:00:00Z", ISO_OFFSET_DATE_TIME);
		Iterator<OffsetDateTime> actualOccurrences = actualSchedule.occurrences(occurrencesFromFilter, occurrencesToFilter);
		assertThat(actualOccurrences.next(), equalTo(testSchedule.occurrences.get(0)));
		assertThat(actualOccurrences.next(), equalTo(testSchedule.occurrences.get(1)));
		assertThat(actualOccurrences.next(), equalTo(testSchedule.occurrences.get(2)));
		assertThat(actualOccurrences.hasNext(), is(false));
	}
}