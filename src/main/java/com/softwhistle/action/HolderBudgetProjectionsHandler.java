package com.softwhistle.action;

import static com.softwhistle.util.JsonExchangeHelper.renderObject;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Request;
import ratpack.http.Response;

import com.softwhistle.model.FixedFieldOccurrences;
import com.softwhistle.service.HolderBudgetManager;
import com.softwhistle.util.DateTimeParses;
import com.softwhistle.util.Holder;
import com.softwhistle.util.ParameterTransform;

public class HolderBudgetProjectionsHandler implements Handler
{
    private static final Logger LOG = LoggerFactory.getLogger(HolderBudgetProjectionsHandler.class);

    @Override
    public void handle(Context exchange)
    {
        String aspect = exchange.getPathTokens().get("aspect");
        if (aspect.equals("amount"))
        {
            Holder<OffsetDateTime> fromParam = new Holder<OffsetDateTime>();
            Holder<OffsetDateTime> toParam = new Holder<OffsetDateTime>();
            if (dateTimeParamTransform.queryParamTo(exchange, "from", true, fromParam)
                    && dateTimeParamTransform.queryParamTo(exchange, "to", true, toParam)) {
                renderObject(exchange, managerFor(exchange).projectExpenditures(fromParam.value,
                    toParam.value));
            }
        }
        else if (aspect.equals("occurrences")) {
            // TODO: create a model that combines a budget item's pertinent accounting
            // items - amount, accounts, merchant - along with the item's calculated
            // occurrences
            renderObject(exchange, Arrays.asList(new FixedFieldOccurrences()));
        }
    }

    protected ParameterTransform<OffsetDateTime> dateTimeParamTransform = (requestValue, receiver) -> {
        try {
            receiver.accept(DateTimeParses.parseFlexibleOffsetDateTime(requestValue));
            return null;
        }
        catch (Exception ex) {
            LOG.error("Failed to parse date time param", ex);
            return String.format("Invalid date time string; value: %s", requestValue);
        }
    };
    
    protected HolderBudgetManager managerFor(Context exchange) {
        return new ActionContextBasicLookup(exchange).budgetManagerForHolderAndBudget();
    }
}
