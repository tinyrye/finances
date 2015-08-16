package com.tinyrye;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.dbcp2.BasicDataSource;

import ratpack.func.Action;
import ratpack.jackson.Jackson;
import ratpack.registry.RegistrySpec;
import ratpack.server.RatpackServer;

import com.tinyrye.action.*;
import com.tinyrye.dao.*;
import com.tinyrye.service.*;

public class Application implements Runnable
{
    private RatpackServer server;
    
    public static void main(String[] args) throws Exception {
        new Application().run();
    }
    
    @Override
    public void run() {
        try { buildServer().start(); }
        catch (Exception ex) { throw new RuntimeException(ex); }
    }
    
    protected RatpackServer buildServer() throws Exception
    {
        return (server = RatpackServer.of(serverSpec -> serverSpec
            .registryOf(registrySpec -> {
                registrySpec.add(new AccountManager(new AccountsDao(primaryDataSource())))
                    .add(new CreateAccount())
                    .add(new CreateAccountHolder())
                    .add(new GetAccountSummary())
                    .add(new ListExpenses())
                    .with(jsonObjectSerdeRegistryAction);
                ObjectMapper objectSerde = jsonObjectSerde();
                Jackson.Init.register(registrySpec, objectSerde, objectSerde.writer());
            })
            .serverConfig(config -> config.port(8088))
            .handlers(chain -> chain
                .post("account", CreateAccount.class)
                .post("account/holder", CreateAccountHolder.class)
                .get("account/:id/summary", GetAccountSummary.class)
                .get("account/:id/expenses", ListExpenses.class)
            )));
    }
    
    protected ObjectMapper jsonObjectSerde() {
        return new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    protected Action<? super RegistrySpec> jsonObjectSerdeRegistryAction = (registrySpec -> {
        ObjectMapper objectSerde = jsonObjectSerde();
        Jackson.Init.register(registrySpec, objectSerde, objectSerde.writer());
    });

    protected DataSource primaryDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5433/personal_financial_accounting");
        dataSource.setUsername("professorfalkin");
        dataSource.setPassword("joshua");
        dataSource.setDriverClassName("org.postgresql.Driver");
        return dataSource;
    }
}