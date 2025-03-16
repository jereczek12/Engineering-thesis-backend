package com.jereczek.checkers.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.jereczek.checkers.game.Move;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.lang.reflect.Type;
import java.util.List;

@Converter
public class MovesListConverter implements AttributeConverter<List<List<Move>>, String> {
    private final Gson gson = new Gson();
    private final Type listType = new TypeToken<List<List<Move>>>() {
    }.getType();

    @Override
    public String convertToDatabaseColumn(List<List<Move>> lists) {
        return gson.toJson(lists);
    }

    @Override
    public List<List<Move>> convertToEntityAttribute(String string) {
        JsonArray jsonObject = gson.fromJson(string, JsonArray.class);

        return gson.fromJson(jsonObject, listType);
    }
}
