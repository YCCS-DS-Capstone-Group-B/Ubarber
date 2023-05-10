package services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import pojos.*;
import utils.Http;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BarberSideServices {

    static Gson gson = new Gson();

    public static ResponseEntity<CollectionModel<Appointment>> allAppointmentsByBarber(String uri, long barberId) {
        HttpResponse<String> response = Http.get(uri + "/barbers/" + barberId + "/appointments");
        if (response.body() == null || response.body().isEmpty() || response.body().equals("{}")) {
            // Return an empty collection model if the response body is empty or null
            return ResponseEntity.ok(CollectionModel.empty());
        }
        Type appointmentListType = new TypeToken<ArrayList<Appointment>>(){}.getType();
        ArrayList<Appointment> list = null;
        try {
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray jsonArray = jsonObject.getAsJsonObject("_embedded").getAsJsonArray("appointmentList");
            list = gson.fromJson(jsonArray, appointmentListType);
        } catch (NullPointerException ignored) {}

        CollectionModel<Appointment> collectionModel = CollectionModel.of(new ArrayList<>());
        if(list != null) {
            collectionModel = CollectionModel.of(list);
        }
        return ResponseEntity.ok(collectionModel);
    }

    public static ResponseEntity<CollectionModel<AppointmentSlot>> allAppointmentSlotsByBarber(String uri, long barberId) {
        HttpResponse<String> response = Http.get(uri + "/barbers/" + barberId + "/appointmentSlots");
        if (response.body() == null || response.body().isEmpty() || response.body().equals("{}")) {
            // Return an empty collection model if the response body is empty or null
            return ResponseEntity.ok(CollectionModel.empty());
        }
        Type appointmentSlotListType = new TypeToken<ArrayList<AppointmentSlot>>(){}.getType();
        ArrayList<AppointmentSlot> list = null;
        try {
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray jsonArray = jsonObject.getAsJsonObject("_embedded").getAsJsonArray("appointmentSlotList");
            list = gson.fromJson(jsonArray, appointmentSlotListType);
        } catch (NullPointerException ignored) {}

        CollectionModel<AppointmentSlot> collectionModel = CollectionModel.of(new ArrayList<>());
        if(list != null) {
            collectionModel = CollectionModel.of(list);
        }
        return ResponseEntity.ok(collectionModel);
    }

    public static ResponseEntity<EntityModel<Appointment>> cancelAppointment(String uri, long appointmentId) {
        HttpResponse<String> response = Http.delete(uri + "/appointments/" + appointmentId);
        Appointment app = gson.fromJson(response.body(), Appointment.class);
        return ResponseEntity.ok(EntityModel.of(app));
    }

    public static ResponseEntity<EntityModel<AppointmentSlot>> newAppointmentSlot(String uri, AppointmentSlot appointmentSlot) {
        String json = gson.toJson(appointmentSlot);
        HttpResponse<String> response = Http.post(uri + "/appointmentSlots", json);
        AppointmentSlot as = gson.fromJson(response.body(), AppointmentSlot.class);
        return ResponseEntity.ok(EntityModel.of(as));
    }

    public static ResponseEntity<EntityModel<AppointmentSlot>> updateAppointmentSlot(String uri, long appointmentSlotId, AppointmentSlot appointmentSlot) {
        String json = gson.toJson(appointmentSlot);
        HttpResponse<String> response = Http.put(uri + "/appointmentSlots/" + appointmentSlotId, json);
        AppointmentSlot as = gson.fromJson(response.body(), AppointmentSlot.class);
        return ResponseEntity.ok(EntityModel.of(as));
    }

    public static ResponseEntity<EntityModel<AppointmentSlot>> deleteAppointmentSlot(String uri, long appointmentSlotId) {
        HttpResponse<String> response = Http.delete(uri + "/appointmentSlots/" + appointmentSlotId);
        int statusCode = response.statusCode();

        if (statusCode == 204) {
            return ResponseEntity.noContent().build();
        } else {
            AppointmentSlot as = gson.fromJson(response.body(), AppointmentSlot.class);
            return ResponseEntity.ok(EntityModel.of(as));
        }

    }
}