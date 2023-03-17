package com.example.atom.services;

import com.example.atom.dto.PriorityDto;
import com.example.atom.repositories.ProductionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionPlanService {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Long getCurrentMaxPriority() {
        //find max property from
        String query = "select max(priority) from production_plan";

        List<PriorityDto> maxPriority = namedParameterJdbcTemplate.query(
                query, new BeanPropertyRowMapper<>(PriorityDto.class, false));

        if (maxPriority.isEmpty()) {
            return 0L;
        } else {
            return maxPriority.get(0) == null ||  maxPriority.get(0).getPriority() == null
                    ? 0L : maxPriority.get(0).getPriority();
        }
    }
}
