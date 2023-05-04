package pojos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Entity
public class AppointmentSlot {
    private Long appointmentSlotId;
    private Long barberId;
    private String startTime;
    private String endTime;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public AppointmentSlot() {

    }

    public Long getAppointmentSlotId() {
        return appointmentSlotId;
    }

    public void setAppointmentSlotId(Long appointmentSlotId) {
        this.appointmentSlotId = appointmentSlotId;
    }

    public Long getBarberId() {
        return barberId;
    }

    public void setBarberId(Long barberId) {
        this.barberId = barberId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }



    public AppointmentSlot(Long appointmentSlotId, Long barberId, String startTime, String endTime) {
        this.appointmentSlotId = appointmentSlotId;
        this.barberId = barberId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private static class CustomDateDeserializer extends JsonDeserializer<Date> {
        private static final String DATE_FORMAT = "yyyy-MM-dd";

        @Override
        public Date deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            String dateStr = parser.getText();
            try {
                return dateFormat.parse(dateStr);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
