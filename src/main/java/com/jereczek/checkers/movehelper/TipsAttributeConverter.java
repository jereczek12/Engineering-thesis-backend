package com.jereczek.checkers.movehelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jereczek.checkers.controller.dto.MoveDTO;
import jakarta.persistence.AttributeConverter;

import java.lang.reflect.Type;
import java.util.List;

public class TipsAttributeConverter implements AttributeConverter<List<MoveDTO>, String> {
    private final Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(List<MoveDTO> moveDTOS) {
        return gson.toJson(moveDTOS);
    }

    @Override
    public List<MoveDTO> convertToEntityAttribute(String string) {
        Type listType = new TypeToken<List<MoveDTO>>() {
        }.getType();
        return gson.fromJson(string, listType);
    }
}
