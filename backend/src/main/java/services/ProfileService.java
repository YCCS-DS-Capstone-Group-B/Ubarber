package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import pojos.Barber;
import pojos.Client;
import utils.Http;
import java.net.http.HttpResponse;

public class ProfileService {
    static Gson gson = new Gson();

    public static String registerBarberProfile(String uri, Barber barber) throws JsonProcessingException {
        String json=gson.toJson(barber);
        HttpResponse<String> response = Http.post(uri + "/barbers", json);
        return response.body();
    }

    public static String registerClientProfile(String uri, Client client) {
        String json=gson.toJson(client);
        HttpResponse<String> response = Http.post(uri + "/clients", json);
        return response.body();
    }

    public static String updateProfile(String uri, long barberId, Barber barber) {
        String json = gson.toJson(barber);
        HttpResponse<String> response = Http.put(uri + "/barbers/" + barberId, json);
        return response.body();
    }

    public static String updateProfile(String uri, long clientId, Client client) {
        String json = gson.toJson(client);
        HttpResponse<String> response = Http.put(uri + "/clients/" + clientId, json);
        return response.body();
    }
}