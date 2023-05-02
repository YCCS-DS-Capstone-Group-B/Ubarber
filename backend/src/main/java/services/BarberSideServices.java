package services;

import com.google.gson.Gson;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import pojos.*;
import utils.Http;

import java.net.http.HttpResponse;

public class BarberSideServices {

    static Gson gson = new Gson();

    /**
     * @param uri the uri of the database
     * @param id
     * @return
     */
    public static ResponseEntity<EntityModel<Appointment>> cancelAppointment(String uri, long id) {
        HttpResponse<String> response = Http.delete(uri + "/appointments/" + id);
        Appointment app = gson.fromJson(response.body(), Appointment.class);
        return ResponseEntity.ok(EntityModel.of(app));
    }

    public static ResponseEntity<EntityModel<AppointmentSlot>> updateSchedule(String uri, long id, AppointmentSlot appointmentSlot) {
        String json = gson.toJson(appointmentSlot);
        HttpResponse<String> response = Http.put(uri + "/barbers/" + id, json);
        AppointmentSlot as = gson.fromJson(response.body(), AppointmentSlot.class);
        return ResponseEntity.ok(EntityModel.of(as));
    }

    public static ResponseEntity<EntityModel<Barber>> updateProfile(String uri, long id, Barber barber) {
        String json = gson.toJson(barber);
        HttpResponse<String> response = Http.put(uri + "/barbers/" + id, json);
        Barber b = gson.fromJson(response.body(), Barber.class);
        return ResponseEntity.ok(EntityModel.of(b));
    }
}