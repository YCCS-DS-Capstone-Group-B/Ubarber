package services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import pojos.*;
import utils.Http;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class BarberSideServices {

    static Gson gson = new Gson();


    public static ResponseEntity<CollectionModel<Appointment>> allAppointmentsByBarber(String uri, long barberId) {
        HttpResponse<String> response = Http.get(uri + "/barbers/"+ barberId + "/appointments");
        Type appointmentListType = new TypeToken<ArrayList<Appointment>>(){}.getType();
        ArrayList<Appointment> appointmentList = gson.fromJson(response.body(), appointmentListType);
        CollectionModel<Appointment> collectionModel = CollectionModel.of(appointmentList);
        return ResponseEntity.ok(collectionModel);
    }

    public static String cancelAppointment(String uri, long appointmentId) {
        HttpResponse<String> response = Http.delete(uri + "/appointments/" + appointmentId);
        return response.body();
    }

    public static String newAppointmentSlot(String uri, AppointmentSlot appointmentSlot) {
        String json = gson.toJson(appointmentSlot);
        HttpResponse<String> response = Http.post(uri + "/appointmentSlots", json);
        return response.body();
    }

    public static String updateAppointmentSlot(String uri, long appointmentSlotId, AppointmentSlot appointmentSlot) {
        String json = gson.toJson(appointmentSlot);
        HttpResponse<String> response = Http.put(uri + "/appointmentSlots/" + appointmentSlotId, json);
        return response.body();
    }

    public static String deleteAppointmentSlot(String uri, long appointmentSlotId) {
        HttpResponse<String> response = Http.delete(uri + "/appointmentSlots/" + appointmentSlotId);
        return response.body();
    }
}