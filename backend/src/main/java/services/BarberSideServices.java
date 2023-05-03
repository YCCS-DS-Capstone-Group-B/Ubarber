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
import java.util.HashMap;
import java.util.List;

public class BarberSideServices {

    static Gson gson = new Gson();


    public static ResponseEntity<CollectionModel<Appointment>> allAppointmentsByBarber(String uri, long barberId) {
        HttpResponse<String> response = Http.get(uri + "/barbers/"+ barberId + "/appointments");
        Type appointmentListType = new TypeToken<ArrayList<Appointment>>(){}.getType();
        ArrayList<Appointment> appointmentList = gson.fromJson(response.body(), appointmentListType);
        CollectionModel<Appointment> collectionModel = CollectionModel.of(appointmentList);
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
        AppointmentSlot as = gson.fromJson(response.body(), AppointmentSlot.class);
        return ResponseEntity.ok(EntityModel.of(as));
    }
}