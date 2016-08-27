package com.softwhistle.model;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Assert;
import org.junit.Test;

import com.softwhistle.Application;

public class BudgetTest
{
    @Test
    public void testJson() throws Exception
    {
        String budgetJson = "{\"id\": 999, \"name\": \"foobar\", \"startsAt\": \"2015-01-01T00:00:00+00:00\" }";
        ObjectMapper applicationJsonMapper = Application.jsonObjectSerde();
        Budget expectedBudget = new Budget()
            .id((Integer) 999).name("foobar")
            .startsAt(OffsetDateTime.parse("2015-01-01T00:00:00+00:00",
                DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        Assert.assertEquals(expectedBudget, applicationJsonMapper.reader(Budget.class).readValue(budgetJson));
    }
}
