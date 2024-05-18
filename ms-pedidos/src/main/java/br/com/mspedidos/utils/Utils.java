package br.com.mspedidos.utils;

import br.com.mspedidos.interfaces.StandardError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Utils {

    public StandardError getErrorMessageFeignIntegration(String message) throws JsonProcessingException {
        Pattern pattern = Pattern.compile("\\{[^}]*\\}");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String json = matcher.group();
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            return objectMapper.readValue(json, StandardError.class);
        }
        return null;
    }

}
