package com.example.atom.readers;

import com.example.atom.dto.RequestDto;
import com.example.atom.dto.queryobjects.DemoDbObject;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DemoReader {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<DemoDbObject> getDemoDbObjects(RequestDto requestDto) {
        if(requestDto.getId() == null && requestDto.getStringProperty() == null) {
            throw new RuntimeException("query is broken");
        }
        Boolean whereWasUsed = false;
        String query = "select * from demo";
        Map<String, Object> mapParameters = new HashMap<>();
        if (requestDto.getId() != null) {
            mapParameters.put("id", requestDto.getId());
            query += !whereWasUsed? " where" : " and";
            query += " id = :id";
            whereWasUsed = true;
        }
        if (requestDto.getStringProperty() != null) {
            mapParameters.put("stringProperty", requestDto.getStringProperty());
            query += !whereWasUsed? " where" : " and";
            query += " string_property = :stringProperty";
            whereWasUsed = true;
        }

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValues(mapParameters);;

        List<DemoDbObject> demoDbObjects = namedParameterJdbcTemplate
                .query(query, namedParameters,
                new BeanPropertyRowMapper<>(DemoDbObject.class, false));

        return demoDbObjects;
    }


}
