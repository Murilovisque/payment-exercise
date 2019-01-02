package com.payment.checkout.resources.models;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JSONRequiredFieldsDeserializer<T> implements JsonDeserializer<T> {

    private static final Gson gson = new Gson();

    @Override
    public T deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        T pojo = gson.fromJson(json, type);
        try {
            checkFields(pojo, pojo.getClass().getDeclaredFields());
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new JsonParseException(ex);
        }
        return pojo;
    }

    private void checkFields(Object pojo, Field[] fields) throws JsonParseException, IllegalArgumentException, IllegalAccessException {
        for (Field f : fields) {
            f.setAccessible(true);
            Object pojoChild = f.get(pojo);
            if (pojoChild == null)
                throw new JsonParseException("Missing field in JSON");
            if (pojoChild.getClass().isAnnotationPresent(JSONRequiredFieldsAnnotation.class))
                checkFields(pojoChild, pojoChild.getClass().getDeclaredFields());
        }
    }

}