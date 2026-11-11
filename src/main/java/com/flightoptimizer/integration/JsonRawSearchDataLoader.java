package com.flightoptimizer.integration;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonRawSearchDataLoader {

    private final ObjectMapper objectMapper;

    public JsonRawSearchDataLoader(
        ObjectMapper objectMapper
    ){
        if(objectMapper == null){
            throw new IllegalArgumentException();
        }
        this.objectMapper = objectMapper;
    }

    public RawSearchData loadFromClasspath(String resourceName){
        if(resourceName == null){
            throw new IllegalArgumentException(
                "json resource name must not be null"
            );
        }

        ClassLoader classLoader = RawSearchData.class.getClassLoader();
        try(
            InputStream inputStream = classLoader.getResourceAsStream(resourceName)
        ){
            if(inputStream == null){
                throw new IllegalArgumentException(
                    "resource was not found: " + resourceName
                );
            }

            return objectMapper.readValue(
                inputStream,
                RawSearchData.class
            );

        }catch(IOException exception){
            throw new IllegalArgumentException(
                "could not read JSON resource: " + resourceName, 
                exception
            );
        }
            
    }
}
