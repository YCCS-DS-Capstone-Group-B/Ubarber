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

public class BookingService {

    static Gson gson = new Gson();


    public static ResponseEntity<CollectionModel<Appointment>> allAppointmentsByClient(String uri, long clientId) {
        HttpResponse<String> response = Http.get(uri + "/clients/"+ clientId + "/appointments");
        Type appointmentListType = new TypeToken<ArrayList<Appointment>>(){}.getType();
        ArrayList<Appointment> appointmentList = gson.fromJson(response.body(), appointmentListType);
        CollectionModel<Appointment> collectionModel = CollectionModel.of(appointmentList);
        return ResponseEntity.ok(collectionModel);
    }

    public static ResponseEntity<CollectionModel<AppointmentSlot>> getBarberSchedule(String uri, long barberId) {
        HttpResponse<String> response = Http.get(uri + "/barbers/" + barberId + "/appointmentSlots");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<AppointmentSlot>>(){}.getType();

        ArrayList<AppointmentSlot> list = null;
        try {
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray jsonArray = jsonObject.getAsJsonObject("_embedded").getAsJsonArray("appointmentSlotList");
            list = gson.fromJson(jsonArray, listType);
        } catch (NullPointerException ignored) {}

        CollectionModel<AppointmentSlot> collectionModel = CollectionModel.of(new ArrayList<>());
        if(list != null) {
            HttpResponse<String> response2 = Http.get(uri + "/barbers/" + barberId + "/appointments");
            Type appointmentListType = new TypeToken<ArrayList<Appointment>>(){}.getType();
            try {
                JsonObject jsonObject2 = JsonParser.parseString(response2.body()).getAsJsonObject();
                JsonArray jsonArray2 = jsonObject2.getAsJsonObject("_embedded").getAsJsonArray("appointmentList");
                ArrayList<Appointment> appointmentList = gson.fromJson(jsonArray2, appointmentListType);
                if (appointmentList != null) {
                    HashMap<Long, AppointmentSlot> map = new HashMap<>(list.size());
                    for (AppointmentSlot slot : list) {
                        map.put(slot.getAppointmentSlotId(), slot);
                    }
                    for (Appointment appointment : appointmentList) {
                        if (map.containsKey(appointment.getAppointmentSlotId()))
                            list.remove(map.get(appointment.getAppointmentSlotId()));
                    }
                }
            } catch (NullPointerException ignored) {}
            collectionModel = CollectionModel.of(list);
        }
        return ResponseEntity.ok(collectionModel);
    }

    public static ResponseEntity<EntityModel<Appointment>> newAppointment(String uri, long barberId, long clientId, long slotId) {
        //TODO some how figure out how to populate the appointmentID. Currently there is only a dummy var: -1
        Appointment appointment = new Appointment(1L, barberId, clientId, slotId);
        Gson gson = new Gson();
        String json=gson.toJson(appointment);
        HttpResponse<String> response = Http.post(uri + "/appointments", json);
        if(response.statusCode() != 200) return ResponseEntity.status(response.statusCode()).build();
        Appointment responseAppointment = gson.fromJson(response.body(), Appointment.class);
        return ResponseEntity.ok(EntityModel.of(responseAppointment));
    }

    public static ResponseEntity<EntityModel<Appointment>> cancelAppointment(String uri, long appointmentId) {
        HttpResponse<String> response = Http.delete(uri + "/appointments/" + appointmentId);
        Gson gson = new Gson();
        Appointment responseAppointment = gson.fromJson(response.body(), Appointment.class);
        return ResponseEntity.ok(EntityModel.of(responseAppointment));
    }

    public static ResponseEntity<EntityModel<Appointment>> updateAppointment(String uri, long appointmentId, Appointment appointment) {
        Gson gson = new Gson();
        HttpResponse<String> response = Http.put(uri + "/appointments/" + appointmentId, gson.toJson(appointment));
        Appointment responseAppointment = gson.fromJson(response.body(), Appointment.class);
        return ResponseEntity.ok(EntityModel.of(responseAppointment));
    }

    private int notifyBarber() {
        return 0;
    }
}