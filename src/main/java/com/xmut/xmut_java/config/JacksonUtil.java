package com.xmut.xmut_java.config;

import org.springframework.beans.BeansException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtil {
	private static ObjectMapper objectMapper;
	
	public static void setObjectMapper(ObjectMapper mapper) throws BeansException {
        objectMapper = mapper;
    }

    public static ObjectMapper getObjectMapper() {
        if(objectMapper == null){
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }
}
