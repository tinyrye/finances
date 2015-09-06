package com.tinyrye.serialization;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import com.tinyrye.model.OccurrenceSchedule;
import com.tinyrye.util.PackageClassScanner;

public class WrappedInheritableJsonDeserializer<T> extends JsonDeserializer<T>
{
    private static final Logger LOG = LoggerFactory.getLogger(WrappedInheritableJsonDeserializer.class);
    
    public static <T> WrappedInheritableJsonDeserializer<T> instanceForEntity(Class<T> entityClass) {
        return new WrappedInheritableJsonDeserializer<T>(entityClass);
    }
    
    private final JsonParsingSupport scanHelper = new JsonParsingSupport();
    
    private Class<?> entityClass;
    private final Map<String,Class<? extends T>> entitiesByTypeCode = new HashMap<String,Class<? extends T>>();
    
    public WrappedInheritableJsonDeserializer(Class<T> entityClass) {
        this.entityClass = entityClass;
        defineEntityImplementations();
    }
    
    @Override
    public T deserialize(final JsonParser parser, final DeserializationContext objectParseContext)
        throws IOException, JsonProcessingException
    {
        if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
            return scanHelper.readFirstField(parser, (fieldName, p) -> {
                return scanHelper.mapOrThrow(
                    parser, entitiesByTypeCode.get(fieldName),
                    (entityType) -> scanHelper.readValue(parser, objectParseContext, entityType),
                    () -> String.format("Unknown named type: %s", fieldName));
            });
        }
        else {
            return null;
        }
    }
    
    protected void defineEntityImplementations()
    {
        LOG.info("Scanning entity implementations: entityClass={}", entityClass);
        try
        {
            new PackageClassScanner().findClasses(entityClass.getPackage().getName())
                .stream().filter(packageClass -> entityClass.isAssignableFrom(packageClass))
                .forEach(packageClass -> mapEntityType(packageClass));
            LOG.info("Classes by type name to sniff: {}", entitiesByTypeCode);
        }
        catch (ClassNotFoundException ex) { throw new RuntimeException(ex); }
        catch (IOException ex) { throw new RuntimeException(ex); }
    }

    protected void mapEntityType(Class entityImplClass) {
        Optional.ofNullable((NamedType) entityImplClass.getAnnotation(NamedType.class))
            .ifPresent(namedType -> {
                LOG.info("Found Entity Implementation with @NamedType: implementationClass={}; namedType={}", new Object[] { 
                    entityImplClass.getName(), namedType
                });
                entitiesByTypeCode.put(namedType.value(), entityImplClass);
            });
    }
}