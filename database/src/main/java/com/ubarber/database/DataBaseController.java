package com.ubarber.database;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class DataBaseController {

    private final BarberRepository barberRepository;
    private final ClientRepository clientRepository;
    private final AppointmentsRepository appointmentsRepository;
    private final AppointmentSlotRepository appointmentSlotRepository;
    private final Logger logger = Logger.getLogger(String.valueOf(DataBaseController.class));
    private final AtomicInteger logCounter = new AtomicInteger(0);
    LogHandler logHandler = new LogHandler();


    public DataBaseController(BarberRepository barberRepository, ClientRepository clientRepository, AppointmentsRepository appointmentsRepository, AppointmentSlotRepository appointmentSlotRepository) {
        this.barberRepository = barberRepository;
        this.clientRepository = clientRepository;
        this.appointmentsRepository = appointmentsRepository;
        this.appointmentSlotRepository = appointmentSlotRepository;
        FileHandler fh = null;
        try {
            fh = new FileHandler("h2Logs.txt", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        try {
            List<String> logs = LogHandler.readLogFile();
            String latestLog = logs.get(logs.size() -1);
            String[] splitLog = latestLog.split(" ", 3);
            logCounter.set(Integer.parseInt(splitLog[1]));
        } catch (Exception e) {

        }

    }

    /**
     * @param newBarber
     * @return ResponseEntity<EntityModel<Barber>>
     * @apiNote This method is used to create a new barber profile in the database
     */
    @PostMapping("/barbers")
    protected ResponseEntity<EntityModel<Barber>> newBarber(@RequestBody Barber newBarber) {
        logger.info(logCounter.incrementAndGet() +  " post " + "/barbers " + newBarber.toString());
        EntityModel<Barber> entityModel = EntityModel.of(barberRepository.save(newBarber));
        return ResponseEntity.ok().body(entityModel);
    }

    /**
     * @param newClient
     * @return ResponseEntity<EntityModel<Client>>
     * @apiNote This method is used to create a new client profile in the database
     */
    @PostMapping("/clients")
    protected ResponseEntity<EntityModel<Client>> newClient(@RequestBody Client newClient) {
        logger.info(logCounter.incrementAndGet() + " post " + "/clients " + newClient.toString());
        EntityModel<Client> entityModel = EntityModel.of(clientRepository.save(newClient));
        return ResponseEntity.ok().body(entityModel);
    }

    /**
     * @param newAppointment
     * @return ResponseEntity<EntityModel<Appointments>>
     * @apiNote This method is used to create a new appointment in the database.
     * An appointment is created when a client selects a barber and a time slot.
     */
    @PostMapping("/appointments")
    protected ResponseEntity<EntityModel<Appointments>> newAppointment(@RequestBody Appointments newAppointment) {
        logger.info(logCounter.incrementAndGet() + " post " + "/appointments " + newAppointment.toString());
        EntityModel<Appointments> entityModel = EntityModel.of(appointmentsRepository.save(newAppointment));
        return ResponseEntity.ok().body(entityModel);
    }

    /**
     * @param newAppointmentSlot
     * @return ResponseEntity<EntityModel<AppoitmentSlot>>
     * @apiNote This method is used to create a new appointment slot in the database.
     * An appointment slot is created when a barber says this time slot is available for him to be booked.
     */
    @PostMapping("/appointmentSlots")
    protected ResponseEntity<EntityModel<AppointmentSlot>> newAppoitmentSlot(@RequestBody AppointmentSlot newAppointmentSlot) {
        logger.info(logCounter.incrementAndGet() + " post " + "/appointmentSlots " + newAppointmentSlot.toString());
        EntityModel<AppointmentSlot> entityModel = EntityModel.of(appointmentSlotRepository.save(newAppointmentSlot));
        return ResponseEntity.ok().body(entityModel);
    }

    /**
     * @return ResponseEntity<CollectionModel<Barber>>
     * @apiNote This method is used to get all the barbers in the database
     */
    @GetMapping("/barbers")
    protected ResponseEntity<CollectionModel<Barber>> allBarbers() {
        CollectionModel<Barber> collectionModel = CollectionModel.of(barberRepository.findAll());
        return ResponseEntity.ok(collectionModel);
    }

    /**
     * @return ResponseEntity<CollectionModel<Client>>
     * @apiNote This method is used to get all the clients in the database
     */
    @GetMapping("/clients")
    protected ResponseEntity<CollectionModel<Client>> allClients() {
        CollectionModel<Client> collectionModel = CollectionModel.of(clientRepository.findAll());
        return ResponseEntity.ok(collectionModel);
    }

   /**
     * @return ResponseEntity<CollectionModel<Appointments>>
     * @apiNote This method is used to get all the appointments in the database
     */
    @GetMapping("/appointments")
    protected ResponseEntity<CollectionModel<Appointments>> allAppointments() {
        CollectionModel<Appointments> collectionModel = CollectionModel.of(appointmentsRepository.findAll());
        return ResponseEntity.ok(collectionModel);
    }

    /**
     * @return ResponceEntity<EntityModel<Barber>>
     * This method is used to get a specific barber from the database
     */
    @GetMapping("/barbers/{id}")
    protected ResponseEntity<EntityModel<Barber>> oneBarber(@PathVariable Long id) {
        EntityModel<Barber> entityModel = EntityModel.of(barberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Barber not found")));
        return ResponseEntity.ok(entityModel);
    }
    @GetMapping("/getAllBarbers")
    protected ResponseEntity<CollectionModel<Barber>> getAllBarbers() {
        CollectionModel<Barber> collectionModel = CollectionModel.of(barberRepository.findAll());
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/getBarbersNearMe/{geoHash}")
    protected ResponseEntity<CollectionModel<Barber>> getBarbersNearMe(@PathVariable String geoHash) {
        List<Barber> barbers = barberRepository.findByGeoHashStartingWith(geoHash);
        CollectionModel<Barber> collectionModel = CollectionModel.of(barbers);
        return ResponseEntity.ok(collectionModel);
    }


    /**
     * @return ResponceEntity<EntityModel<Client>>
     * This method is used to get a specific client from the database
     */
    @GetMapping("/clients/{id}")
    protected ResponseEntity<EntityModel<Client>> oneClient(@PathVariable Long id) {
        EntityModel<Client> entityModel = EntityModel.of(clientRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Client not found")));
        return ResponseEntity.ok(entityModel);
    }

    /**
       @return ResponseEntity<EntityModel<oneAppointment>>
       @apiNote this method is used to get one appointment based on ID
     */
    @GetMapping("/appointments/{id}")
    protected ResponseEntity<EntityModel<Appointments>> oneAppointment(@PathVariable Long id){
        EntityModel<Appointments> entityModel = EntityModel.of(appointmentsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Client not appointment")));
        return ResponseEntity.ok(entityModel);
    }

    /**
     * @return ResponseEntity<EntityModel<AppointmentSlot>>
     * @apiNote This method is used to get a specific appointment slot from the database
     */
    @GetMapping("/appointmentSlots/{id}")
    protected ResponseEntity<EntityModel<AppointmentSlot>> oneAppointmentSlot(@PathVariable Long id) {
        EntityModel<AppointmentSlot> entityModel = EntityModel.of(appointmentSlotRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Appointment slot not found")));
        return ResponseEntity.ok(entityModel);
    }

    /**
     * @return ResponseEntity<CollectionModel<AppointmentSlot>>
     * @apiNote This method is used to get all the appointment slots in the database
     */
    @GetMapping("/appointmentSlots")
    protected ResponseEntity<CollectionModel<AppointmentSlot>> allAppointmentSlots() {
        CollectionModel<AppointmentSlot> collectionModel = CollectionModel.of(appointmentSlotRepository.findAll());
        return ResponseEntity.ok(collectionModel);
    }


    /**
     * @param id
     * @return ResponseEntity<EntityModel<Barber>>
     * @apiNote This method is used to update a specific barber from the database
     */
    @PutMapping("/barbers/{id}")
    protected ResponseEntity<EntityModel<Barber>> updateBarber(@RequestBody Barber newBarber, @PathVariable Long id) {
        Barber updatedBarber = barberRepository.findById(id)
                .map(barber -> {
                    barber.setFirstName(newBarber.getFirstName());
                    barber.setLastName(newBarber.getLastName());
                    barber.setMiddleName(newBarber.getMiddleName());
                    barber.setId(newBarber.getId());
                    barber.setLocation(newBarber.getLocation());
                    return barberRepository.save(barber);
                })
                .orElseGet(() -> {
                    newBarber.setId(id);
                    return barberRepository.save(newBarber);
                });
        EntityModel<Barber> entityModel = EntityModel.of(updatedBarber);
        Link link = linkTo(methodOn(this.getClass()).updateBarber(newBarber, id)).withSelfRel();
        entityModel.add(link);
        logger.info(logCounter.incrementAndGet() + " put " + "/barbers/" + id + " " + entityModel.toString());
        return ResponseEntity.created(entityModel.getRequiredLink("self").toUri()).body(entityModel);
    }

    /**
     * @param id
     * @return ResponseEntity<EntityModel<Client>>
     * @apiNote This method is used to update a specific client from the database
     */
    @PutMapping("/clients/{id}")
    protected ResponseEntity<EntityModel<Client>> updateClient(@RequestBody Client newClient, @PathVariable Long id) {
        Client updatedClient = clientRepository.findById(id)
                .map(client -> {
                    client.setFirstName(newClient.getFirstName());
                    client.setLastName(newClient.getLastName());
                    client.setMiddleName(newClient.getMiddleName());
                    client.setId(newClient.getId());
                    client.setLocation(newClient.getLocation());
                    return clientRepository.save(client);
                })
                .orElseGet(() -> {
                    newClient.setId(id);
                    return clientRepository.save(newClient);
                });
        EntityModel<Client> entityModel = EntityModel.of(updatedClient);
        logger.info(logCounter.incrementAndGet() + " put " + "/clients/" + id + " " + entityModel.toString());
        return ResponseEntity.created(entityModel.getRequiredLink("self").toUri()).body(entityModel);
    }

    /**
     * @param id
     * @return ResponseEntity<EntityModel<AppointmentSlot>>
     * @apiNote This method is used to update a specific appointment slot from the database
     */
    @PutMapping("/appointments/{id}")
    protected ResponseEntity<EntityModel<Appointments>> updateAppointment(@RequestBody Appointments newAppointment, @PathVariable Long id) {
        Appointments updatedAppointment = appointmentsRepository.findById(id)
                .map(appointment -> {
                    appointment.setBarberId(newAppointment.getBarberId());
                    appointment.setClientId(newAppointment.getClientId());
                    appointment.setAppointmentSlotId(newAppointment.getAppointmentSlotId());
                    appointment.setId(newAppointment.getId());
                    return appointmentsRepository.save(appointment);
                })
                .orElseGet(() -> {
                    newAppointment.setId(id);
                    return appointmentsRepository.save(newAppointment);
                });
        EntityModel<Appointments> entityModel = EntityModel.of(updatedAppointment);
        logger.info(logCounter.incrementAndGet() + " put " + "/appointments/" + id + " " + entityModel.toString());
        return ResponseEntity.created(entityModel.getRequiredLink("self").toUri()).body(entityModel);
    }

    /**
     * @param id
     * @return ResponseEntity<EntityModel<AppointmentSlot>>
     * @apiNote This method is used to update a specific appointment slot from the database
     */
    @PutMapping("/appointmentSlots/{id}")
    protected ResponseEntity<EntityModel<AppointmentSlot>> updateAppointmentSlot(@RequestBody AppointmentSlot newAppointmentSlot, @PathVariable Long id) {
        AppointmentSlot updatedAppointmentSlot = appointmentSlotRepository.findById(id)
                .map(appointmentSlot -> {
                    appointmentSlot.setBarberId(newAppointmentSlot.getBarberId());
                    appointmentSlot.setAppointmentSlotId(newAppointmentSlot.getAppointmentSlotId());
                    appointmentSlot.setStartTime(newAppointmentSlot.getStartTime());
                    appointmentSlot.setEndTime(newAppointmentSlot.getEndTime());
                    return appointmentSlotRepository.save(appointmentSlot);
                })
                .orElseGet(() -> {
                    newAppointmentSlot.setAppointmentSlotId(id);
                    return appointmentSlotRepository.save(newAppointmentSlot);
                });
        EntityModel<AppointmentSlot> entityModel = EntityModel.of(updatedAppointmentSlot);
        logger.info(logCounter.incrementAndGet() + " put " + "/appointmentSlots/" + id + " " + entityModel.toString());
        return ResponseEntity.created(entityModel.getRequiredLink("self").toUri()).body(entityModel);
    }

    /**
     * @param id
     * @return ResponseEntity<EntityModel<Barber>>
     * @apiNote This method is used to delete a specific barber from the database
     */
    @DeleteMapping("/barbers/{id}")
    protected ResponseEntity<EntityModel<Barber>> deleteBarber(@PathVariable Long id) {
        barberRepository.deleteById(id);
        logger.info(logCounter.incrementAndGet() + " delete " + "/barbers/" + id);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param id
     * @return ResponseEntity<EntityModel<Client>>
     * @apiNote This method is used to delete a specific client from the database
     */
    @DeleteMapping("/clients/{id}")
    protected ResponseEntity<EntityModel<Client>> deleteClient(@PathVariable Long id) {
        clientRepository.deleteById(id);
        logger.info(logCounter.incrementAndGet() + " delete " + "/clients/" + id);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param id
     * @return ResponseEntity<EntityModel<Appointment>>
     * @apiNote This method is used to delete a specific appointment from the database
     */
    @DeleteMapping("/appointments/{id}")
    protected ResponseEntity<EntityModel<Appointments>> deleteAppointment(@PathVariable Long id) {
        logger.info(logCounter.incrementAndGet() + " delete " + "/appointments/" + id);
        appointmentsRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param id
     * @return ResponseEntity<EntityModel<AppointmentSlot>>
     * @apiNote This method is used to delete a specific appointment slot from the database
     */
    @DeleteMapping("/appointmentSlots/{id}")
    protected ResponseEntity<EntityModel<AppointmentSlot>> deleteAppointmentSlot(@PathVariable Long id) {
        appointmentSlotRepository.deleteById(id);
        logger.info(logCounter.incrementAndGet() + " delete " + "/appointmentSlots/" + id);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param id
     * @return ResponseEntity<EntityModel<Barber>>
     * @apiNote This method is used to get all the appointments booked for a specific barber from the database
     */
    @GetMapping("/barbers/{id}/appointments")
    protected ResponseEntity<CollectionModel<Appointments>> allAppointmentsByBarber(@PathVariable Long id) {
        CollectionModel<Appointments> collectionModel = CollectionModel.of(appointmentsRepository.findByBarberId(id));

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * @param startTime
     * @return ResponseEntity<CollectionModel<AppointmentSlot>>
     * @apiNote This method is used to get all the appointments available for a specific start time from the database
     */
    @GetMapping("/appointmentSlots/{startTime}/appointments")
    protected ResponseEntity<CollectionModel<AppointmentSlot>> allAppointmentsByAppointmentStartTime(@PathVariable String startTime) {
        CollectionModel<AppointmentSlot> collectionModel = CollectionModel.of(appointmentSlotRepository.findByStartTime(startTime));
        return ResponseEntity.ok(collectionModel);
    }

    /**
     * @param id
     * @return ResponseEntity<CollectionsModel<Appointment>>
     * @apiNote This method is used to get all the appointments available for a specific barber from the database
     */
    @GetMapping("/barbers/{id}/appointmentSlots")
    protected ResponseEntity<CollectionModel<AppointmentSlot>> allAppointmentSlotsByBarber(@PathVariable Long id) {
        CollectionModel<AppointmentSlot> collectionModel = CollectionModel.of(appointmentSlotRepository.findByBarberId(id));
        return ResponseEntity.ok(collectionModel);
    }

    /**
     * @param id
     * @return ResponseEntity<CollectionsModel<AppointmentSlot>>
     * @apiNote This method is used to get all the appointments booked by a specific client from the database
     */
    @GetMapping("/clients/{id}/appointments")
    protected ResponseEntity<CollectionModel<Appointments>> allAppointmentsByClient(@PathVariable Long id) {
        CollectionModel<Appointments> collectionModel = CollectionModel.of(appointmentsRepository.findByClientId(id));
        return ResponseEntity.ok(collectionModel);
    }

    @PostMapping("/updateDatabase/{url}")
    public List<String> updateDatabase(@PathVariable String url){
        String uri = "http://" + url + "/getDatabaseLogs/" + 0;
        HttpRequest request = HttpRequest.newBuilder().
                GET()
                .uri(URI.create(uri))
                .header("Content-Type", "application/charset-8")
                .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("problem in sending it");
            e.printStackTrace();
        }

        if (response != null) {
            Gson gson = new Gson();
            String[] logs = gson.fromJson(response.body(), String[].class);
            for (String log : logs) {
                implementLogChanges(log);
            }
        } else {
            System.out.println("the response was null");
        }

        //TODO http://url/getDatabaseLogs
        //TODO compare the logs in that database with the logs in the current database
        // TODO if there are any differences, update the current database with the logs from the other database
        return null;
    }

    private void implementLogChanges(String log) {
        System.out.println(log);
    }

    @GetMapping("/getDatabaseLogs/{id}")
    public ResponseEntity<List<String>> getDatabaseLogs(@PathVariable int id){
        List<String> collectionModel;
        try {
            collectionModel = LogHandler.readLogFile();
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
        List<String> finalModel = new ArrayList<>();
        for (int i = id*2; i < collectionModel.size(); i++) {
            if(i % 2 == 0) continue;
            finalModel.add(collectionModel.get(i));
        }
        return ResponseEntity.ok(finalModel);
    }

}
