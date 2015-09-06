package com.tinyrye;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.apache.commons.dbcp2.BasicDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ratpack.func.Action;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;
import ratpack.path.internal.TokenPathBinder;
import ratpack.registry.RegistrySpec;
import ratpack.server.RatpackServer;

import com.tinyrye.action.*;
import com.tinyrye.dao.*;
import com.tinyrye.model.*;
import com.tinyrye.serialization.WrappedInheritableJsonDeserializer;
import com.tinyrye.service.*;

public class Application implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    
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
            .registryOf(registrySpec -> registrySpec.add(new AccountHandler())
                .add(new AccountHolderHandler())
                .add(new GetAccountSummary())
                .add(new ListExpenses())
                .add(new ActiveBudgetHandler())
                .add(new HolderActiveBudgetItemHandler())
                .add(new BudgetOccurrenceHandler())
                .add(ServiceExchange.class, new ServiceExchangeImpl())
                .with(jsonObjectSerdeRegistryAction)
            )
            .serverConfig(config -> config.port(8088))
            .handlers(chain -> chain
                .path("account/:id", addEntityIdHandler)
                .path("account/:id?", AccountHandler.class)
                .path("account/holder/:id", addEntityIdHandler)
                .path("account/holder/:id?", AccountHolderHandler.class)
                
                .path("account/holder/:id/budget", addEntityIdHandler)
                .path("account/holder/:id/budget", ActiveBudgetHandler.class)
                .path("account/holder/:id/budget/item", addEntityIdHandler)
                .path("account/holder/:id/budget/item", HolderActiveBudgetItemHandler.class)

                .get("account/:id/summary", addEntityIdHandler)
                .get("account/:id/summary", GetAccountSummary.class)
                .get("account/:id/expenses", addEntityIdHandler)
                .get("account/:id/expenses", ListExpenses.class)

                .path("budget/occurrence/:id", addEntityIdHandler)
                .path("budget/occurrence/:id", BudgetOccurrenceHandler.class)
            )));
    }
    
    public static ObjectMapper jsonObjectSerde() {
        return new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .registerModule(new SimpleModule() 
                .addDeserializer(OccurrenceSchedule.class,
                    WrappedInheritableJsonDeserializer.instanceForEntity(
                    OccurrenceSchedule.class)));
    }
    
    protected Action<? super RegistrySpec> jsonObjectSerdeRegistryAction = (registrySpec -> {
        ObjectMapper objectSerde = jsonObjectSerde();
        Jackson.Init.register(registrySpec, objectSerde, objectSerde.writer());
    });
    
    protected Handler addEntityIdHandler = (exchange -> {
        LOG.debug("Extracting id from request URL: url={}", exchange.getRequest().getUri());
        exchange.getRequest().add(new EntityId(new Integer(exchange.getPathTokens().get("id"))));
        exchange.next();
    });
    
    protected DataSource primaryDataSource() {
        BasicDataSource primaryDataSource = new BasicDataSource();
        primaryDataSource.setUrl("jdbc:postgresql://localhost:5433/finances");
        primaryDataSource.setUsername("professorfalkin");
        primaryDataSource.setPassword("joshua");
        primaryDataSource.setDriverClassName("org.postgresql.Driver");
        return primaryDataSource;
    }
    
    protected class ServiceExchangeImpl implements ServiceExchange
    {
        private Map<Class,Object> services = new HashMap<Class,Object>();
        
        public ServiceExchangeImpl() {
            services.put(DataSource.class, primaryDataSource());
            addService(new AccountService(this));
            addService(new BudgetService(this));
            addService(new AccountsDao(get(DataSource.class)));
            addService(new BudgetDao(get(DataSource.class)));
        }
        
        @Override
        public <T> T get(Class<T> serviceClass) {
            return (T) services.get(serviceClass);
        }
        
        public <T> ServiceExchangeImpl addService(T service) {
            services.put(service.getClass(), service);
            return this;
        }
    }
}