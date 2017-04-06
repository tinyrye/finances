package com.softwhistle.serialization;

import static com.fasterxml.jackson.core.JsonToken.*;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.softwhistle.model.Account;

public class AccountJsonDeserializer extends JsonDeserializer<Account>
{
	private static final Logger LOG = LoggerFactory.getLogger(AccountJsonDeserializer.class);
	private final JsonParsingSupport scanHelper = new JsonParsingSupport();
	private final ObjectMapper tabulaRasaJsonObjectMapper = new ObjectMapper();

	@Override
	public Account deserialize(JsonParser parser, DeserializationContext objectParseContext)
		throws IOException, JsonProcessingException
	{
		if (parser.getCurrentToken() == VALUE_STRING) {
			return new Account().holderCode(parser.getText());
		}
		else if (parser.getCurrentToken() == START_OBJECT) {
			return tabulaRasaJsonObjectMapper.readValues(parser, Account.class).next();
		}
		else {
			throw scanHelper.newParseException("Require an object", parser);
		}
	}
}