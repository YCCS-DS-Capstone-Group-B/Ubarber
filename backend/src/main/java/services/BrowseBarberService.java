package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import pojos.Barber;
import utils.ShardingUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.util.Collections.singletonList;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;


public class BrowseBarberService {

    public static ResponseEntity<CollectionModel<Barber>> getBarbersNearMe(String uri, String geoHash) throws JsonProcessingException {
        //get the right DB that has this clients closest barbers
        RestTemplate restTemplate = restTemplate();
        ResponseEntity<CollectionModel<Barber>> response = restTemplate.exchange(
                uri + "/getBarbersNearMe/" + geoHash,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});
        return response;
    }
    private static int absoluteValDifference(String a, String b) {
        return Math.abs(Integer.parseInt(a) - Integer.parseInt(b));
    }

    public static ResponseEntity<EntityModel<Barber>> getBarberProfile(String uri, long barberId) throws JsonProcessingException {
        RestTemplate restTemplate = restTemplate();
        ResponseEntity<EntityModel<Barber>> response = restTemplate.exchange(
                uri + "/barbers/" + barberId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<EntityModel<Barber>>() {},
                barberId);
        return response;
    }

    private static ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new Jackson2HalModule());

        return objectMapper;
    }

    private static MappingJackson2HttpMessageConverter converter() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        converter.setSupportedMediaTypes(singletonList(HAL_JSON));
        converter.setObjectMapper(objectMapper());

        return converter;
    }

    private static RestTemplate restTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return builder.messageConverters(converter()).build();
    }
}