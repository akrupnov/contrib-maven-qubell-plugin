/*
 * Copyright 2013 Qubell, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qubell.client;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple json parser
 *
 * @author Alex Krupnov
 */
public class JsonParser {
    /**
     * Parses a key value pair map from simple json expression
     *
     * @param value string representation of json object
     * @return parsed value or null for invalid input
     */
    public static Map<String, Object> parseMap(String value) {
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<
                HashMap<String, Object>
                >() {
        };
        HashMap<String, Object> parsedMap;

        try {
            parsedMap
                    = mapper.readValue(value, typeRef);

            return parsedMap;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Serializes a free form map to json string
     *
     * @param obj object to be serialized to string
     * @return string json
     */
    public static String serialize(Object obj) {
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);

        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            return null;
        }
    }
}