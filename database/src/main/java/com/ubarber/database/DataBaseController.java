package com.ubarber.database;

import org.springframework.beans.factory.annotation.Value;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final Logger consoleLogger = Logger.getLogger(DataBaseController.class + "console");
    private final AtomicInteger logCounter = new AtomicInteger(0);
    private final AtomicBoolean batchCatchUp = new AtomicBoolean(false);
    private final HashMap<Integer, String> undoneLogs = new HashMap<>();
    private final HashMap<String, Integer> stagedCommits = new HashMap<>();
    @Value("${server.logs}")
    private String logFile;



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
            List<String> logs = LogHandler.readLogFile(this.logFile);
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
        String log =  " post " + "/barbers " + newBarber.toString();
        EntityModel<Barber> entityModel;
        if(stagedCommits.containsKey(log)) {
            consoleLogger.info("phase 2 commit");
            logger.info(logCounter.incrementAndGet() + log);
            stagedCommits.remove(log);
            entityModel = EntityModel.of(barberRepository.save(newBarber));
        }
        else {
            consoleLogger.info("phase 1 commit");
            consoleLogger.info( log);
            stagedCommits.put(log, logCounter.get());
            entityModel = EntityModel.of(newBarber);
        }
        return ResponseEntity.ok().body(entityModel);
    }

    /**
     * @param newClient
     * @return ResponseEntity<EntityModel<Client>>
     * @apiNote This method is used to create a new client profile in the database
     */
    @PostMapping("/clients")
    protected ResponseEntity<EntityModel<Client>> newClient(@RequestBody Client newClient) {
        String log =  " post " + "/clients " + newClient.toString();
        EntityModel<Client> entityModel;
        if(stagedCommits.containsKey(log)) {
            consoleLogger.info("phase 2 commit");
            logger.info(logCounter.incrementAndGet() + log);
            stagedCommits.remove(log);
            entityModel = EntityModel.of(clientRepository.save(newClient));
        }
        else {
            consoleLogger.info("phase 1 commit");
            consoleLogger.info( log);
            stagedCommits.put(log, logCounter.get());
            entityModel = EntityModel.of(newClient);
        }

        return ResponseEntity.ok().body(entityModel);
    }

    /**
     * @param newAppointment
     * @return ResponseEntity<EntityModel<Appointment>>
     * @apiNote This method is used to create a new appointment in the database.
     * An appointment is created when a client selects a barber and a time slot.
     */
    @PostMapping("/appointments")
    protected ResponseEntity<EntityModel<Appointment>> newAppointment(@RequestBody Appointment newAppointment) {
        String log =  " post " + "/appointments " + newAppointment.toString();
        EntityModel<Appointment> entityModel;
        if(stagedCommits.containsKey(log)) {
            consoleLogger.info("phase 2 commit");
            logger.info(logCounter.incrementAndGet() + log);
            stagedCommits.remove(log);
            entityModel = EntityModel.of(appointmentsRepository.save(newAppointment));
        }
        else {
            consoleLogger.info("phase 1 commit");
            consoleLogger.info( log);
            stagedCommits.put(log, logCounter.get() + 1);
            entityModel = EntityModel.of(newAppointment);
        }

        return ResponseEntity.ok().body(entityModel);
    }

    /**
     * @param newAppointmentSlot
     * @return ResponseEntity<EntityModel<AppointmentSlot>>
     * @apiNote This method is used to create a new appointment slot in the database.
     * An appointment slot is created when a barber says this time slot is available for him to be booked.
     */
    @PostMapping("/appointmentSlots")
    protected ResponseEntity<EntityModel<AppointmentSlot>> newAppointmentSlot(@RequestBody AppointmentSlot newAppointmentSlot) {
        String log = " post " + "/appointmentSlots " + newAppointmentSlot.toString();
        EntityModel<AppointmentSlot> entityModel;
        if(stagedCommits.containsKey(log)) {
            consoleLogger.info("phase 2 commit");
            logger.info(logCounter.incrementAndGet() + log);
            stagedCommits.remove(log);
            entityModel = EntityModel.of(appointmentSlotRepository.save(newAppointmentSlot));
        }
        else {
            consoleLogger.info("phase 1 commit");
            consoleLogger.info( log);
            stagedCommits.put(log, logCounter.get() + 1);
            entityModel = EntityModel.of(newAppointmentSlot);
        }

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
     * @return ResponseEntity<CollectionModel<Appointment>>
     * @apiNote This method is used to get all the appointments in the database
     */
    @GetMapping("/appointments")
    protected ResponseEntity<CollectionModel<Appointment>> allAppointments() {
        CollectionModel<Appointment> collectionModel = CollectionModel.of(appointmentsRepository.findAll());
        return ResponseEntity.ok(collectionModel);
    }

    /**
     * @return ResponceEntity<EntityModel<Barber>>
     * This method is used to get a specific barber from the database
     */
    @GetMapping("/barbers/{barberId}")
    protected ResponseEntity<EntityModel<Barber>> oneBarber(@PathVariable Long barberId) {
        EntityModel<Barber> entityModel = EntityModel.of(barberRepository.findById(barberId).orElseThrow(() -> new IllegalArgumentException("Barber not found")));
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
    @GetMapping("/clients/{clientId}")
    protected ResponseEntity<EntityModel<Client>> oneClient(@PathVariable Long clientId) {
        EntityModel<Client> entityModel = EntityModel.of(clientRepository.findById(clientId).orElseThrow(() -> new IllegalArgumentException("Client not found")));
        return ResponseEntity.ok(entityModel);
    }

    /**
       @return ResponseEntity<EntityModel<oneAppointment>>
       @apiNote this method is used to get one appointment based on ID
     */
    @GetMapping("/appointments/{appointmentId}")
    protected ResponseEntity<EntityModel<Appointment>> oneAppointment(@PathVariable Long appointmentId){
        EntityModel<Appointment> entityModel = EntityModel.of(appointmentsRepository.findById(appointmentId).orElseThrow(() -> new IllegalArgumentException("Client not appointment")));
        return ResponseEntity.ok(entityModel);
    }

    /**
     * @return ResponseEntity<EntityModel<AppointmentSlot>>
     * @apiNote This method is used to get a specific appointment slot from the database
     */
    @GetMapping("/appointmentSlots/{appointmentSlotId}")
    protected ResponseEntity<EntityModel<AppointmentSlot>> oneAppointmentSlot(@PathVariable Long appointmentSlotId) {
        EntityModel<AppointmentSlot> entityModel = EntityModel.of(appointmentSlotRepository.findById(appointmentSlotId).orElseThrow(() -> new IllegalArgumentException("Appointment slot not found")));
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
     * @return ResponseEntity<EntityModel<Barber>>
     * @apiNote This method is used to update a specific barber from the database
     */
    @PutMapping("/barbers/{barberId}")
    protected ResponseEntity<EntityModel<Barber>> updateBarber(@RequestBody Barber newBarber, @PathVariable Long barberId) {
        String log = " put " + "/barbers/" + barberId + " " + newBarber.toString();
        EntityModel<Barber> entityModel;
        if(stagedCommits.containsKey(log)) {
            consoleLogger.info("phase 2 commit");
            stagedCommits.remove(log);
            Barber updatedBarber = barberRepository.findById(barberId)
                    .map(barber -> {
                        barber.setFirstName(newBarber.getFirstName());
                        barber.setLastName(newBarber.getLastName());
                        barber.setMiddleName(newBarber.getMiddleName());
                        barber.setId(newBarber.getId());
                        barber.setLocation(newBarber.getLocation());
                        return barberRepository.save(barber);
                    })
                    .orElseGet(() -> {
                        newBarber.setId(barberId);
                        return barberRepository.save(newBarber);
                    });
            entityModel = EntityModel.of(updatedBarber);
            Link link = linkTo(methodOn(this.getClass()).updateBarber(newBarber, barberId)).withSelfRel();
            entityModel.add(link);
            logger.info(logCounter.incrementAndGet() + log);
        } else{
            consoleLogger.info("phase 1 commit");
            consoleLogger.info( log);
            stagedCommits.put(log, logCounter.get() + 1);
            entityModel = EntityModel.of(newBarber);
        }
        return ResponseEntity.created(entityModel.getRequiredLink("self").toUri()).body(entityModel);
    }

    /**
     * @return ResponseEntity<EntityModel<Client>>
     * @apiNote This method is used to update a specific client from the database
     */
    @PutMapping("/clients/{clientID}")
    protected ResponseEntity<EntityModel<Client>> updateClient(@RequestBody Client newClient, @PathVariable Long clientId) {
        String log = " put " + "/clients/" + clientId + " " + newClient.toString();
        EntityModel<Client> entityModel;
        if(stagedCommits.containsKey(log)) {
            consoleLogger.info("phase 2 commit");
            stagedCommits.remove(log);
            Client updatedClient = clientRepository.findById(clientId)
                    .map(client -> {
                        client.setFirstName(newClient.getFirstName());
                        client.setLastName(newClient.getLastName());
                        client.setMiddleName(newClient.getMiddleName());
                        client.setId(newClient.getId());
                        client.setLocation(newClient.getLocation());
                        return clientRepository.save(client);
                    })
                    .orElseGet(() -> {
                        newClient.setId(clientId);
                        return clientRepository.save(newClient);
                    });
            entityModel = EntityModel.of(updatedClient);
            logger.info(logCounter.incrementAndGet() + log);
        } else{
            consoleLogger.info("phase 1 commit");
            consoleLogger.info( log);
            stagedCommits.put(log, logCounter.get() + 1);
            entityModel = EntityModel.of(newClient);
        }
        return ResponseEntity.created(entityModel.getRequiredLink("self").toUri()).body(entityModel);
    }

    /**
     * @return ResponseEntity<EntityModel<AppointmentSlot>>
     * @apiNote This method is used to update a specific appointment slot from the database
     */
    @PutMapping("/appointments/{appointmentId}")
    protected ResponseEntity<EntityModel<Appointment>> updateAppointment(@RequestBody Appointment newAppointment, @PathVariable Long appointmentId) {
        String log = " put " + "/appointments/" + appointmentId + " " + newAppointment.toString();
        EntityModel<Appointment> entityModel;
        if(stagedCommits.containsKey(log)) {
            consoleLogger.info("phase 2 commit");
            stagedCommits.remove(log);
            Appointment updatedAppointment = appointmentsRepository.findById(appointmentId)
                    .map(appointment -> {
                        appointment.setBarberId(newAppointment.getBarberId());
                        appointment.setClientId(newAppointment.getClientId());
                        appointment.setAppointmentSlotId(newAppointment.getAppointmentSlotId());
                        appointment.setAppointmentId(newAppointment.getAppointmentId());
                        return appointmentsRepository.save(appointment);
                    })
                    .orElseGet(() -> {
                        newAppointment.setAppointmentId(appointmentId);
                        return appointmentsRepository.save(newAppointment);
                    });
            entityModel = EntityModel.of(updatedAppointment);
            logger.info(logCounter.incrementAndGet() + log);
        } else{
            consoleLogger.info("phase 1 commit");
            consoleLogger.info( log);
            stagedCommits.put(log, logCounter.get() + 1);
            entityModel = EntityModel.of(newAppointment);
        }
        return ResponseEntity.created(entityModel.getRequiredLink("self").toUri()).body(entityModel);
    }

    /**
     * @return ResponseEntity<EntityModel<AppointmentSlot>>
     * @apiNote This method is used to update a specific appointment slot from the database
     */

    @PutMapping("/appointmentSlots/{appointmentSlotId}")
//    protected ResponseEntity<EntityModel<AppointmentSlot>> updateAppointmentSlot(@RequestBody AppointmentSlot newAppointmentSlot, @PathVariable Long appointmentSlotId) {
//        AppointmentSlot updatedAppointmentSlot = appointmentSlotRepository.findById(appointmentSlotId)
//                .map(existingAppointmentSlot -> {
//                    existingAppointmentSlot.setBarberId(newAppointmentSlot.getBarberId());
//                    existingAppointmentSlot.setAppointmentSlotId(newAppointmentSlot.getAppointmentSlotId());
//                    existingAppointmentSlot.setStartTime(newAppointmentSlot.getStartTime());
//                    existingAppointmentSlot.setEndTime(newAppointmentSlot.getEndTime());
//                    return appointmentSlotRepository.save(existingAppointmentSlot);
//                })
//                .orElseGet(() -> {
//                    newAppointmentSlot.setAppointmentSlotId(appointmentSlotId);
//                    return appointmentSlotRepository.save(newAppointmentSlot);
//                });
//        EntityModel<AppointmentSlot> entityModel = EntityModel.of(updatedAppointmentSlot);
//        logger.info(logCounter.incrementAndGet() + " put " + "/appointmentSlots/" + appointmentSlotId + " " + entityModel.toString());
//        return ResponseEntity.created(entityModel.getRequiredLink("self").toUri()).body(entityModel);
//    }
    protected ResponseEntity<EntityModel<AppointmentSlot>> updateAppointmentSlot(@PathVariable Long appointmentSlotId, @RequestBody AppointmentSlot newAppointmentSlot) {
        String log = " put " + "/appointmentSlots/" + appointmentSlotId + " " + newAppointmentSlot.toString();
        EntityModel<AppointmentSlot> entityModel;
        if(stagedCommits.containsKey(log)) {
            consoleLogger.info("phase 2 commit");
            stagedCommits.remove(log);
            logger.info(logCounter.incrementAndGet() + log);
            AppointmentSlot existingAppointmentSlot = appointmentSlotRepository.findById(appointmentSlotId)
                    .orElseThrow(() -> new RuntimeException("AppointmentSlot not found with id " + appointmentSlotId));
            existingAppointmentSlot.setBarberId(newAppointmentSlot.getBarberId());
            existingAppointmentSlot.setDate(newAppointmentSlot.getDate());
            existingAppointmentSlot.setStartTime(newAppointmentSlot.getStartTime());
            existingAppointmentSlot.setEndTime(newAppointmentSlot.getEndTime());
            entityModel = EntityModel.of(appointmentSlotRepository.save(existingAppointmentSlot));
        } else{
            consoleLogger.info("phase 1 commit");
            consoleLogger.info( log);
            stagedCommits.put(log, logCounter.get() + 1);
            entityModel = EntityModel.of(newAppointmentSlot);
        }
        return ResponseEntity.ok().body(entityModel);
    }


    /**
     * @param barberId
     * @return ResponseEntity<EntityModel<Barber>>
     * @apiNote This method is used to delete a specific barber from the database
     */
    @DeleteMapping("/barbers/{barberId}")
    protected ResponseEntity<EntityModel<Barber>> deleteBarber(@PathVariable Long barberId) {
        String log = " delete " + "/barbers/" + barberId;
        if(stagedCommits.containsKey(log)){
            consoleLogger.info("phase 2 commit");
            stagedCommits.remove(log);
            logger.info(logCounter.incrementAndGet() + log);
            barberRepository.deleteById(barberId);
        } else{
            consoleLogger.info("phase 1 commit");
            consoleLogger.info( log);
            stagedCommits.put(log, logCounter.get() + 1);

        }
        return ResponseEntity.ok().build();
    }

    /**
     * @param clientId
     * @return ResponseEntity<EntityModel<Client>>
     * @apiNote This method is used to delete a specific client from the database
     */
    @DeleteMapping("/clients/{clientId}")
    protected ResponseEntity<EntityModel<Client>> deleteClient(@PathVariable Long clientId) {
        String log = " delete " + "/clients/" + clientId;
        if(stagedCommits.containsKey(log)){
            consoleLogger.info("phase 2 commit");
            stagedCommits.remove(log);
            logger.info(logCounter.incrementAndGet() + log);
            clientRepository.deleteById(clientId);

        } else {
            consoleLogger.info("phase 1 commit");
            consoleLogger.info( log);
            stagedCommits.put(log, logCounter.get() + 1);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * @param appointmentId
     * @return ResponseEntity<EntityModel<Appointment>>
     * @apiNote This method is used to delete a specific appointment from the database
     */
    @DeleteMapping("/appointments/{appointmentId}")
    protected ResponseEntity<EntityModel<Appointment>> deleteAppointment(@PathVariable Long appointmentId) {
        String log = " delete " + "/appointments/" + appointmentId;
        if(stagedCommits.containsKey(log)){
            consoleLogger.info("phase 2 commit");
            stagedCommits.remove(log);
            logger.info(logCounter.incrementAndGet() + log);
            appointmentsRepository.deleteById(appointmentId);
        } else {
            consoleLogger.info("phase 1 commit");
            consoleLogger.info( log);
            stagedCommits.put(log, logCounter.get() + 1);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * @param appointmentSlotId
     * @return ResponseEntity<EntityModel<AppointmentSlot>>
     * @apiNote This method is used to delete a specific appointment slot from the database
     */
    @DeleteMapping("/appointmentSlots/{appointmentSlotId}")
    protected ResponseEntity<EntityModel<AppointmentSlot>> deleteAppointmentSlot(@PathVariable Long appointmentSlotId) {
        String log = " delete " + "/appointmentSlots/" + appointmentSlotId;
        if(stagedCommits.containsKey(log)){
            consoleLogger.info("phase 2 commit");
            stagedCommits.remove(log);
            appointmentSlotRepository.deleteById(appointmentSlotId);
            logger.info(logCounter.incrementAndGet() + log);
        }
        else {
            consoleLogger.info("phase 1 commit");
            consoleLogger.info( log);
            stagedCommits.put(log, logCounter.get() + 1);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * @param barberId
     * @return ResponseEntity<EntityModel<Barber>>
     * @apiNote This method is used to get all the appointments booked for a specific barber from the database
     */
    @GetMapping("/barbers/{barberId}/appointments")
    protected ResponseEntity<CollectionModel<Appointment>> allAppointmentsByBarber(@PathVariable Long barberId) {
        CollectionModel<Appointment> collectionModel = CollectionModel.of(appointmentsRepository.findByBarberId(barberId));

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
     * @param barberId
     * @return ResponseEntity<CollectionsModel<Appointment>>
     * @apiNote This method is used to get all the appointment slots for a specific barber from the database
     */
    @GetMapping("/barbers/{barberId}/appointmentSlots")
    protected ResponseEntity<CollectionModel<AppointmentSlot>> allAppointmentSlotsByBarber(@PathVariable Long barberId) {
        CollectionModel<AppointmentSlot> collectionModel = CollectionModel.of(appointmentSlotRepository.findByBarberId(barberId));
        return ResponseEntity.ok(collectionModel);
    }

    /**
     * @param clientId
     * @return ResponseEntity<CollectionsModel<AppointmentSlot>>
     * @apiNote This method is used to get all the appointments booked by a specific client from the database
     */
    @GetMapping("/clients/{clientId}/appointments")
    protected ResponseEntity<CollectionModel<Appointment>> allAppointmentsByClient(@PathVariable Long clientId) {
        CollectionModel<Appointment> collectionModel = CollectionModel.of(appointmentsRepository.findByClientId(clientId));
        return ResponseEntity.ok(collectionModel);
    }

    @PutMapping("/updateDatabase/{url}")
    public List<String> updateDatabase(@PathVariable String url){
        consoleLogger.info("was told to update database");
        String uri = "http://" + url + "/getDatabaseLogs/" + logCounter.get();
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
            batchCatchUp.set(true);
            for (String log : logs) {
                implementLogChanges(log);
            }
            while(!undoneLogs.isEmpty()){
                implementLogChanges(undoneLogs.get(logCounter.addAndGet(1)));
            }
            batchCatchUp.set(false);
        } else {
            System.out.println("the response was null");
        }

        //TODO http://url/getDatabaseLogs
        //TODO compare the logs in that database with the logs in the current database
        // TODO if there are any differences, update the current database with the logs from the other database
        return null;
    }

    private ResponseEntity implementLogChanges(String log) {
        String[] details = log.split(" ", 5);
        if(Integer.parseInt(details[1]) != logCounter.get() +1) return null;
        String request = details[2];
        switch (request){
            case "post" -> {
                return handlePost(details[3], details[4]);
            }
            case "put" -> {
                return handlePut(details[3], details[4]);
            }
            case "delete" -> {
                return handleDelete(details[3]);
            }
        }
        System.out.println(log);
        return null;
    }

    private ResponseEntity handleDelete(String extension) {
        String[] pieces = extension.split("/");
        switch (pieces[0]){
            case "/barbers" -> {
                return deleteBarber((long) Integer.parseInt(pieces[1]));
            }
            case "/clients" -> {
                return deleteClient((long) Integer.parseInt(pieces[1]));
            }
            case "/appointments" -> {
                return deleteAppointment((long) Integer.parseInt(pieces[1]));
            }
            case "/appointmentSlots" -> {
                return deleteAppointmentSlot((long) Integer.parseInt(pieces[1]));
            }
        }
        return null;
    }

    private ResponseEntity handlePut(String extension, String json) {
        String[] pieces = extension.split("/");
        Gson gson = new Gson();
        switch (pieces[0]){
            case "/barbers" -> {
                return updateBarber(gson.fromJson(json, Barber.class), (long) Integer.parseInt(pieces[1]));
            }
            case "/clients" -> {
                return updateClient(gson.fromJson(json, Client.class), (long) Integer.parseInt(pieces[1]));
            }
            case "/appointments" -> {
                return updateAppointment(gson.fromJson(json, Appointment.class), (long) Integer.parseInt(pieces[1]));
            }
            case "/appointmentSlots" -> {
                return updateAppointmentSlot((long) Integer.parseInt(pieces[1]), gson.fromJson(json, AppointmentSlot.class));
            }
        }
        return null;
    }

    private ResponseEntity handlePost(String extension, String json) {
        Gson gson = new Gson();
        switch (extension){
            case "/barbers" -> {
                return newBarber(gson.fromJson(json, Barber.class));
            }
            case "/clients" -> {
                return newClient(gson.fromJson(json, Client.class));
            }
            case "/appointments" -> {
                return newAppointment(gson.fromJson(json, Appointment.class));
            }
            case "/appointmentSlots" -> {
                return newAppointmentSlot(gson.fromJson(json, AppointmentSlot.class));
            }
        }
        return null;
    }

    @PutMapping("/updateLog")
    public ResponseEntity addOneLog(@RequestBody String log){
        if (batchCatchUp.get()) {
            undoneLogs.put(Integer.parseInt(log.split(" ", 5)[1]), log);
            return null;
        }
        ResponseEntity implementation = implementLogChanges(log);
        if(implementation == null){
            if(Integer.parseInt(log.split(" ", 5)[1]) <= logCounter.get())
                return ResponseEntity.ok().build();
            undoneLogs.put(Integer.parseInt(log.split(" ", 5)[1]), log);
        }
        return implementation;
    }

    @GetMapping("/getDatabaseLogs/{id}")
    public ResponseEntity<List<String>> getDatabaseLogs(@PathVariable int id){
        List<String> collectionModel;
        try {
            collectionModel = LogHandler.readLogFile(this.logFile);
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

    @GetMapping("/getOneLog/{id}")
    public ResponseEntity<String> getOneLog(@PathVariable int id){
        List<String> collectionModel;
        try {
            collectionModel = LogHandler.readLogFile(this.logFile);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }

        String log = collectionModel.get(id*2);
        return ResponseEntity.ok(log);
    }

    @GetMapping("/checkStaged/{url}")
    public ResponseEntity<Boolean> checkStaged(@PathVariable String url) {
        consoleLogger.info("checking staged commits");
        if(!stagedCommits.isEmpty()){
            for(String log: stagedCommits.keySet()){
                int logNum = stagedCommits.get(log);
                String uri = "http://" + url + "/getOneLog/" + logNum;
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
                   String savedLog = response.body().split(" ", 1)[1];
                   savedLog = savedLog.substring(0, savedLog.length()-2);
                     if(!savedLog.equals(log)) {
                          addOneLog(response.body().substring(1, response.body().length()-2));
                     }
                     else
                    {
                        stagedCommits.remove(log);
                    }
                }
                else
                {
                    stagedCommits.remove(log);
                }


            }
        }
        return ResponseEntity.ok().build();
    }

}
