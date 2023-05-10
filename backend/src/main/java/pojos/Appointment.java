package pojos;

public class Appointment {
    private Long appointmentId;
    private Long barberId;
    private Long clientId;
    private Long appointmentSlotId;

    public Appointment() {

    }

    public void setAppointmentId(Long Id) {
        this.appointmentId = Id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public Long getBarberId() {
        return barberId;
    }

    public void setBarberId(Long barberId) {
        this.barberId = barberId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getAppointmentSlotId() {
        return appointmentSlotId;
    }

    public void setAppointmentSlotId(Long appointmentSlotId) {
        this.appointmentSlotId = appointmentSlotId;
    }

    public Appointment(Long Id, Long barberId, Long clientId, Long appointmentSlotId) {
        this.appointmentId = Id;
        this.barberId = barberId;
        this.clientId = clientId;
        this.appointmentSlotId = appointmentSlotId;
    }

}
