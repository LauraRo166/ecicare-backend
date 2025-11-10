package edu.escuelaing.ecicare.services;

import java.util.Map;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MapperService {

    private final ObjectMapper objectMapper;

    public Map<String, Object> covertDtoToMap(Object object) {
        return objectMapper.convertValue(object, Map.class);
    }

}