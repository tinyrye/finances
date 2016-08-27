package com.softwhistle;

import static com.softwhistle.util.JsonExchangeHelper.renderObject;
import static com.softwhistle.util.Values.notBlankOpt;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.apache.commons.dbcp2.BasicDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ratpack.error.ServerErrorHandler;
import ratpack.func.Action;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;
import ratpack.path.internal.TokenPathBinder;
import ratpack.registry.RegistrySpec;
import ratpack.server.RatpackServer;

import com.softwhistle.action.*;
import com.softwhistle.dao.*;
import com.softwhistle.model.*;
import com.softwhistle.serialization.WrappedInheritableJsonDeserializer;
import com.softwhistle.service.*;

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
                .add(new AccountSummaryHandler())
                .add(new ActiveBudgetHandler())
                .add(new HolderBudgetItemHandler())
                .add(new HolderBudgetItemsHandler())
                .add(new HolderBudgetProjectionsHandler())
                .add(new BudgetOccurrenceHandler())
                .add(ServiceExchange.class, new ServiceExchangeImpl())
                .add(ServerErrorHandler.class, new ServerErrorHandler() {
                    @Override public void error(Context context, Throwable ex) {
                        ex = root(ex);
                        if (ex instanceof EntityNotFoundException) {
                            LOG.error("Request entity not found?", ex);
                            context.getResponse().status(404);
                            renderObject(context, ex);
                        }
                        else if (ex instanceof EntityAlreadyExistsException) {
                            context.getResponse().status(302);
                            context.getResponse().getHeaders().add("Location", String.format("http://localhost:8088/account/holder/%d", ((EntityAlreadyExistsException) ex).entityId));
                            renderObject(context, ex);
                        }
                        else {
                            LOG.error(String.format("What do we have here? %s", ex.getClass().getName()), ex);
                            context.getResponse().status(500);
                        }
                    }
                })
                .with(jsonObjectSerdeRegistryAction)
            )
            .serverConfig(config -> config.port(8088))
            .handlers(chain -> chain
                .prefix("account/:id?:\\d+", subChain -> subChain
                    .all(entityIdRegistrar(Account.class))
                    .path(AccountHandler.class)
                    .path("summary", AccountSummaryHandler.class))
                
                .prefix("account/holder/:id?:\\d+", subChain -> subChain
                    .all(entityIdRegistrar(AccountHolder.class))
                    .path(AccountHolderHandler.class))

                .prefix("account/holder/:id:\\d+", subChain -> subChain
                    .all(entityIdRegistrar(AccountHolder.class))
                    .path(AccountHolderHandler.class)
                    .path("budget", ActiveBudgetHandler.class)
                    .post("budget/item", HolderBudgetItemHandler.class)
                    .post("budget/:budgetName/item", HolderBudgetItemHandler.class)
                    .path("budget/items", HolderBudgetItemsHandler.class)
                    .path("budget/:budgetName/items", HolderBudgetItemsHandler.class)
                    .get("budget/projection/:aspect", HolderBudgetProjectionsHandler.class)
                    .get("budget/:budgetName/projection/:aspect", HolderBudgetProjectionsHandler.class))

                .prefix("budget/occurrence/:id:\\d+", subChain -> subChain
                    .all(entityIdRegistrar(OccurrenceSchedule.class))
                    .path(BudgetOccurrenceHandler.class))
            )));
    }
    
    public static ObjectMapper jsonObjectSerde() {
        return new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .registerModule(new SimpleModule() .addDeserializer(OccurrenceSchedule.class,
                WrappedInheritableJsonDeserializer.instanceForEntity(
                OccurrenceSchedule.class)));
    }
    
    protected Action<? super RegistrySpec> jsonObjectSerdeRegistryAction = (registrySpec) -> {
        ObjectMapper objectSerde = jsonObjectSerde();
        Jackson.Init.register(registrySpec, objectSerde, objectSerde.writer());
    };
    
    protected Handler entityIdRegistrar(final Class entityType) {
        return (exchange) -> {
            notBlankOpt(exchange.getAllPathTokens().get("id")).ifPresent(idVal ->
            {
                LOG.debug("Extracting id from request URL: url={}; targetEntityType={}; targetEntity={}", new Object[] {
                    exchange.getRequest().getUri(), entityType,
                    new EntityId(new Integer(exchange.getAllPathTokens().get("id"))).entityType(entityType)
                });
                exchange.getRequest().add(new EntityId(new Integer(exchange.getAllPathTokens().get("id")))
                    .entityType(entityType));
            });
            exchange.next();
        };
    }
    
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

    protected Throwable root(Throwable ex) {
        if ((ex != null) && (ex.getCause() != null)) return root(ex.getCause());
        else return ex;
    }
}
