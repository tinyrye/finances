package com.softwhistle.serialization;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.softwhistle.model.Account;

public class AccountJsonDeserializerTest extends SerializationTestSupport
{
	@Before
	public void setup() {
		jsonObjectMapper = new ObjectMapper().registerModule(new SimpleModule()
            .addDeserializer(Account.class, new AccountJsonDeserializer()));
	}

	@Test
	public void testDeserializeFullForm() throws Exception {
		forBothEach(
			readTestEntitiesFromJson(Account.class, "fullObjects"),
			asList(
				new Account().id(999).holderCode("BLUES_BROS").institutionAccountId("123456-abcdef"),
				new Account().id(5734),
				new Account().holderCode("BLUES_BROS").institutionAccountId("123456-abcdef")
					.institutionAccountName("Credit Union Checking Account"),
				new Account().holderCode("MY_PRIMARY_ACCOUNT")
			).iterator(),
			(actualEntity, expectedEntity) -> assertDeserialization(actualEntity, expectedEntity)
		);
	}

	protected void assertDeserialization(Account actualEntity, Account expectedEntity) {
		assertThat(actualEntity, equalTo(expectedEntity));
	}
}