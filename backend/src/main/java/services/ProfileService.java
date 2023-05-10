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

    public static ResponseEntity<EntityModel<Barber>> registerBarberProfile(String uri, Barber barber) throws JsonProcessingException {
        String json=gson.toJson(barber);
        HttpResponse<String> response = Http.post(uri + "/barbers", json);
        Barber b = gson.fromJson(response.body(), Barber.class);
        return ResponseEntity.ok(EntityModel.of(b));
    }

    public static ResponseEntity<EntityModel<Client>> registerClientProfile(String uri, Client client) {
        String json=gson.toJson(client);
        HttpResponse<String> response = Http.post(uri + "/clients", json);
        Client c = gson.fromJson(response.body(), Client.class);
        return ResponseEntity.ok(EntityModel.of(c));
    }

    public static ResponseEntity<EntityModel<Barber>> updateProfile(String uri, long barberId, Barber barber) {
        String json = gson.toJson(barber);
        HttpResponse<String> response = Http.put(uri + "/barbers/" + barberId, json);
        Barber b = gson.fromJson(response.body(), Barber.class);
        return ResponseEntity.ok(EntityModel.of(b));
    }

    public static ResponseEntity<EntityModel<Client>> updateProfile(String uri, long clientId, Client client) {
        String json = gson.toJson(client);
        HttpResponse<String> response = Http.put(uri + "/clients/" + clientId, json);
        Client c = gson.fromJson(response.body(), Client.class);
        return ResponseEntity.ok(EntityModel.of(c));
    }
}