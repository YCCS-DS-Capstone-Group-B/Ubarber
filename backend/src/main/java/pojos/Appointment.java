package pojos;

public class Appointment {
    private Long Id;
    private Long barberId;
    private Long clientId;
    private Long appointmentSlotId;

    public Appointment() {

    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public Long getId() {
        return Id;
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
        this.Id = Id;
        this.barberId = barberId;
        this.clientId = clientId;
        this.appointmentSlotId = appointmentSlotId;
    }

}
