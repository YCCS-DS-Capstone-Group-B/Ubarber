package com.ubarber.backend;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.annotation.PostConstruct;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pojos.*;
import services.*;
import utils.ShardingUtils;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
public class NodeController {


	DatabaseLeader databaseLeader = new DatabaseLeader(true);
	Map<Integer, String> servers = new HashMap<>();
	Map<Integer, String> replicas = new HashMap<>();
	Logger logger = Logger.getLogger(NodeController.class.getName());
	static long appointmentSlotId = 0;
	static long appointmentId = 0;

	@PostConstruct
	public void makeMap(){
		this.servers = databaseLeader.getServers();
		this.replicas = databaseLeader.getReplicas();
		//this.servers = Map.of(0, "http://localhost:5050", 1, "http://localhost:5050", 2, "http://localhost:5050");
		//this.replicas = Map.of(0, "http://localhost:5050", 1, "http://localhost:5050", 2, "http://localhost:5050");
	}

	@Value("${database.uri}")
	private String database;

	@PostMapping("/registerBarber")
	public ResponseEntity<EntityModel<Barber>> registerBarber(@RequestBody Barber barber) throws JsonProcessingException {
		double[] latLong = ShardingUtils.getLatLong(barber.getLocation());
		barber.setLatitude(latLong[0]);
		barber.setLongitude(latLong[1]);
		barber.setGeoHash(ShardingUtils.getGeoHash(barber.getLatitude(),barber.getLongitude()));
		int bucket = ShardingUtils.getBucket(barber.getLocation(),servers.size());
		ResponseEntity<EntityModel<Barber>> response = ProfileService.registerBarberProfile(databaseLeader.getServers().get(bucket), barber); //databaseLeader.getServers().get(bucket)
		if(response.getStatusCodeValue() == 200){ //phase 1 main passed successfully
			ResponseEntity<EntityModel<Barber>> response2 = ProfileService.registerBarberProfile(databaseLeader.getReplicas().get(bucket), barber); //databaseLeader.getReplicas().get(bucket)
			if(!databaseLeader.getServers().get(bucket).equals(databaseLeader.getReplicas().get(bucket)) && response2.getStatusCodeValue() == 200) { //phase 1 replica passed successfully
				ResponseEntity<EntityModel<Barber>> responseCommited = ProfileService.registerBarberProfile(databaseLeader.getServers().get(bucket), barber); //phase 2 main
				ResponseEntity<EntityModel<Barber>> responseCommited2 = ProfileService.registerBarberProfile(databaseLeader.getReplicas().get(bucket), barber); //phase 2 replica
			}

		}
        logger.info("Registering barber with id: " + barber.getId() + " to server: " + databaseLeader.getServers().get(bucket) + " " + response.getStatusCodeValue());
		return response;
	}
	@PostMapping("/registerClient")
	public ResponseEntity<EntityModel<Client>> registerClient(@RequestBody Client client) {
		double[] latLong = ShardingUtils.getLatLong(client.getLocation());
		client.setLatitude(latLong[0]);
		client.setLongitude(latLong[1]);
		client.setGeoHash(ShardingUtils.getGeoHash(client.getLatitude(),client.getLongitude()));
		int bucket = ShardingUtils.getBucket(client.getLocation(),servers.size());
		ResponseEntity<EntityModel<Client>> response = ProfileService.registerClientProfile(databaseLeader.getServers().get(bucket), client);
		if(response.getStatusCodeValue() == 200){ //phase 1 main passed successfully
			logger.info("phase 1 main staged");
			ResponseEntity<EntityModel<Client>> response2 = ProfileService.registerClientProfile(databaseLeader.getReplicas().get(bucket), client);
			logger.info("phase 1 replica staged");
			if(!databaseLeader.getServers().get(bucket).equals(databaseLeader.getReplicas().get(bucket)) && response2.getStatusCodeValue() == 200) { //phase 1 replica passed successfully
				ResponseEntity<EntityModel<Client>> responseCommited = ProfileService.registerClientProfile(databaseLeader.getServers().get(bucket), client); //phase 2 main
				ResponseEntity<EntityModel<Client>> responseCommited2 = ProfileService.registerClientProfile(databaseLeader.getReplicas().get(bucket), client); //phase 2 replica
				logger.info("phase 2 main and replica call sent to commit");
			}

		}
		logger.info("Registering client with id: " + client.getId() + " to server: " + databaseLeader.getServers().get(bucket) + " " + response.getStatusCodeValue());
		return response;
	}
	@GetMapping("/getBarber/{barberId}/{zip}")
	public ResponseEntity<EntityModel<Barber>> getBarber(@PathVariable long barberId, @PathVariable String zip) throws JsonProcessingException {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Barber>> response = BrowseBarberService.getBarberProfile(databaseLeader.getServers().get(bucket) , barberId);
		return response;
	}

	@GetMapping("/getBarbersNearMe/{clientZip}")
	public ResponseEntity<CollectionModel<Barber>> getBarbersNearMe(@PathVariable String clientZip) throws JsonProcessingException {
		List<Integer> buckets = ShardingUtils.getBuckets(clientZip,servers.size());
		double[] latLong = ShardingUtils.getLatLong(clientZip);
		String geoHash = ShardingUtils.getGeoHash(latLong[0],latLong[1]);
		List<CollectionModel<Barber>> barberCollections = new ArrayList<>();
		for (Integer bucket : buckets) {
			ResponseEntity<CollectionModel<Barber>> response = BrowseBarberService.getBarbersNearMe(databaseLeader.getServers().get(bucket), geoHash.substring(0, 4));
			barberCollections.add(response.getBody());
		}
		List<Barber> allBarbers = new ArrayList<>();
		for(CollectionModel<Barber> barberCollection : barberCollections) {
			allBarbers.addAll(barberCollection.getContent());
		}
		CollectionModel<Barber> combinedBarberCollection = CollectionModel.of(allBarbers);
		return ResponseEntity.ok(combinedBarberCollection);
	}

	@GetMapping("/barber/myAppointments/{barberId}/{zip}")
	public ResponseEntity<CollectionModel<Appointment>> allAppointmentsByBarber(@PathVariable long barberId, @PathVariable String zip){
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<CollectionModel<Appointment>> response = BarberSideServices.allAppointmentsByBarber(databaseLeader.getServers().get(bucket), barberId);
		ResponseEntity<CollectionModel<Appointment>> response2 = BarberSideServices.allAppointmentsByBarber(databaseLeader.getServers().get(bucket), barberId);
		return response;
	}

	@GetMapping("/barber/myAppointmentSlots/{barberId}/{zip}")
	public ResponseEntity<CollectionModel<AppointmentSlot>> allAppointmentSlotsByBarber(@PathVariable long barberId, @PathVariable String zip){
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<CollectionModel<AppointmentSlot>> response = BarberSideServices.allAppointmentSlotsByBarber(databaseLeader.getServers().get(bucket), barberId);
		ResponseEntity<CollectionModel<AppointmentSlot>> response2 = BarberSideServices.allAppointmentSlotsByBarber(databaseLeader.getServers().get(bucket), barberId);
		return response;
	}

	@GetMapping("/client/myAppointments/{clientId}/{zip}")
	public ResponseEntity<CollectionModel<Appointment>> allAppointmentsByClient(@PathVariable long clientId, @PathVariable String zip){
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<CollectionModel<Appointment>> response = BookingService.allAppointmentsByClient(databaseLeader.getServers().get(bucket), clientId);
		ResponseEntity<CollectionModel<Appointment>> response2 = BookingService.allAppointmentsByClient(databaseLeader.getServers().get(bucket), clientId);
		return response;
	}

	@PostMapping("/addAppointmentSlot/{zip}")
	public ResponseEntity<EntityModel<AppointmentSlot>> addAppointmentSlot(@RequestBody AppointmentSlot appointmentSlot, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		appointmentSlot.setAppointmentSlotId(++appointmentSlotId);
		ResponseEntity<EntityModel<AppointmentSlot>> response = BarberSideServices.newAppointmentSlot(databaseLeader.getServers().get(bucket), appointmentSlot);
		if(response.getStatusCodeValue() == 200) {
			ResponseEntity<EntityModel<AppointmentSlot>> response2 = BarberSideServices.newAppointmentSlot(databaseLeader.getReplicas().get(bucket), appointmentSlot);
			if(!databaseLeader.getServers().get(bucket).equals(databaseLeader.getReplicas().get(bucket)) && response2.getStatusCodeValue() == 200) {
				ResponseEntity<EntityModel<AppointmentSlot>> responseCommited = BarberSideServices.newAppointmentSlot(databaseLeader.getServers().get(bucket), appointmentSlot);
				ResponseEntity<EntityModel<AppointmentSlot>> responseCommited2 = BarberSideServices.newAppointmentSlot(databaseLeader.getReplicas().get(bucket), appointmentSlot);
			}
		}

		return response;
	}

	@PutMapping("/updateAppointmentSlot/{appointmentSlotId}/{zip}")
	public ResponseEntity<EntityModel<AppointmentSlot>> updateAppointmentSlot(@RequestBody AppointmentSlot appointmentSlot, @PathVariable long appointmentSlotId, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<AppointmentSlot>> response = BarberSideServices.updateAppointmentSlot(databaseLeader.getServers().get(bucket), appointmentSlotId, appointmentSlot);
		if(response.getStatusCodeValue() == 200) {
			ResponseEntity<EntityModel<AppointmentSlot>> response2 = BarberSideServices.updateAppointmentSlot(databaseLeader.getReplicas().get(bucket), appointmentSlotId, appointmentSlot);
			if(!databaseLeader.getServers().get(bucket).equals(databaseLeader.getReplicas().get(bucket)) && response2.getStatusCodeValue() == 200) {
				ResponseEntity<EntityModel<AppointmentSlot>> responseCommited = BarberSideServices.updateAppointmentSlot(databaseLeader.getServers().get(bucket), appointmentSlotId, appointmentSlot);
				ResponseEntity<EntityModel<AppointmentSlot>> responseCommited2 = BarberSideServices.updateAppointmentSlot(databaseLeader.getReplicas().get(bucket), appointmentSlotId, appointmentSlot);
			}
		}
		return response;
	}

	@PutMapping("/updateAppointment/{appointmentId}/{zip}")
	public ResponseEntity<EntityModel<Appointment>> updateAppointment(@RequestBody Appointment appointment, @PathVariable long appointmentId, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Appointment>> response = BookingService.updateAppointment(databaseLeader.getServers().get(bucket), appointmentId, appointment);
		if(response.getStatusCodeValue() == 200) {
			ResponseEntity<EntityModel<Appointment>> response2 = BookingService.updateAppointment(databaseLeader.getReplicas().get(bucket), appointmentId, appointment);
			if(!databaseLeader.getServers().get(bucket).equals(databaseLeader.getReplicas().get(bucket)) && response2.getStatusCodeValue() == 200) {
				ResponseEntity<EntityModel<Appointment>> responseCommited = BookingService.updateAppointment(databaseLeader.getServers().get(bucket), appointmentId, appointment);
				ResponseEntity<EntityModel<Appointment>> responseCommited2 = BookingService.updateAppointment(databaseLeader.getReplicas().get(bucket), appointmentId, appointment);
			}
		}
		return response;
	}

	@DeleteMapping("/deleteAppointmentSlot/{appointmentId}/{zip}")
	public ResponseEntity<EntityModel<AppointmentSlot>> deleteAppointmentSlot(@PathVariable long appointmentId, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<AppointmentSlot>> response = BarberSideServices.deleteAppointmentSlot(databaseLeader.getServers().get(bucket), appointmentId);
		if(response.getStatusCodeValue() == 200){
			ResponseEntity<EntityModel<AppointmentSlot>> response2 = BarberSideServices.deleteAppointmentSlot(databaseLeader.getReplicas().get(bucket), appointmentId);
			if(!databaseLeader.getServers().get(bucket).equals(databaseLeader.getReplicas().get(bucket)) && response2.getStatusCodeValue() == 200){
				ResponseEntity<EntityModel<AppointmentSlot>> responseCommited = BarberSideServices.deleteAppointmentSlot(databaseLeader.getServers().get(bucket), appointmentId);
				ResponseEntity<EntityModel<AppointmentSlot>> responseCommited2 = BarberSideServices.deleteAppointmentSlot(databaseLeader.getReplicas().get(bucket), appointmentId);
			}
		}
		return response;
	}

	@DeleteMapping("/barberCancelAppointment/{appointmentId}/{zip}")
	public ResponseEntity<EntityModel<Appointment>> cancelAppointment(@PathVariable long appointmentId, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Appointment>> response =  BarberSideServices.cancelAppointment(databaseLeader.getServers().get(bucket), appointmentId);
		if(response.getStatusCodeValue() == 200){
			ResponseEntity<EntityModel<Appointment>> response2 =  BarberSideServices.cancelAppointment(databaseLeader.getReplicas().get(bucket), appointmentId);
			if(!databaseLeader.getServers().get(bucket).equals(databaseLeader.getReplicas().get(bucket)) && response2.getStatusCodeValue() == 200){
				ResponseEntity<EntityModel<Appointment>> responseCommited =  BarberSideServices.cancelAppointment(databaseLeader.getServers().get(bucket), appointmentId);
				ResponseEntity<EntityModel<Appointment>> responseCommited2 =  BarberSideServices.cancelAppointment(databaseLeader.getReplicas().get(bucket), appointmentId);
			}
		}
		return response;
	}

	@PutMapping("/barberUpdateProfile/{barberId}/{zip}")
	public ResponseEntity<EntityModel<Barber>> updateProfile(@RequestBody Barber barber, @PathVariable long barberId, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Barber>> response =  ProfileService.updateProfile(databaseLeader.getServers().get(bucket), barberId, barber);
		if(response.getStatusCodeValue() == 200) {
			ResponseEntity<EntityModel<Barber>> response2 =  ProfileService.updateProfile(databaseLeader.getReplicas().get(bucket), barberId, barber);
			if(!databaseLeader.getServers().get(bucket).equals(databaseLeader.getReplicas().get(bucket)) && response2.getStatusCodeValue() == 200) {
				ResponseEntity<EntityModel<Barber>> responseCommited =  ProfileService.updateProfile(databaseLeader.getServers().get(bucket), barberId, barber);
				ResponseEntity<EntityModel<Barber>> responseCommited2 =  ProfileService.updateProfile(databaseLeader.getReplicas().get(bucket), barberId, barber);
			}
		}
		return response;
	}

	@PutMapping("/clientUpdateProfile/{clientId}/{zip}")
	public ResponseEntity<EntityModel<Client>> updateProfile(@RequestBody Client client, @PathVariable long clientId, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Client>> response =  ProfileService.updateProfile(databaseLeader.getServers().get(bucket), clientId, client);
		if(response.getStatusCodeValue() == 200) {
			ResponseEntity<EntityModel<Client>> response2 =  ProfileService.updateProfile(databaseLeader.getReplicas().get(bucket), clientId, client);
			if(!databaseLeader.getServers().get(bucket).equals(databaseLeader.getReplicas().get(bucket)) && response2.getStatusCodeValue() == 200) {
				ResponseEntity<EntityModel<Client>> responseCommited =  ProfileService.updateProfile(databaseLeader.getServers().get(bucket), clientId, client);
				ResponseEntity<EntityModel<Client>> responseCommited2 =  ProfileService.updateProfile(databaseLeader.getReplicas().get(bucket), clientId, client);
			}
		}
		return response;
	}

	@GetMapping("/barberSchedule/{barberId}/{zip}")
	public ResponseEntity<CollectionModel<AppointmentSlot>> getBarberSchedule(@PathVariable long barberId, @PathVariable String zip){
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		return BookingService.getBarberSchedule(databaseLeader.getServers().get(bucket), barberId);
	}

	@PostMapping("/newAppointment/{barberId}/{zip}")
	public ResponseEntity<EntityModel<Appointment>> newAppointment(@RequestBody String appointment, @PathVariable long barberId, @PathVariable String zip){
		Gson gson = new Gson();
		JsonElement jsonElement = gson.fromJson(appointment, JsonElement.class);
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		int clientId = jsonObject.get("clientId").getAsInt();
		int slotId = jsonObject.get("appointmentSlotId").getAsInt();
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		long apptId = ++appointmentId;
		ResponseEntity<EntityModel<Appointment>> response = BookingService.newAppointment(databaseLeader.getServers().get(bucket), barberId, clientId, slotId, apptId);
		if(response.getStatusCodeValue() == 200) {
			ResponseEntity<EntityModel<Appointment>> response2 = BookingService.newAppointment(databaseLeader.getReplicas().get(bucket), barberId, clientId, slotId, apptId);
			if(!databaseLeader.getServers().get(bucket).equals(databaseLeader.getReplicas().get(bucket)) && response2.getStatusCodeValue() == 200) {
				ResponseEntity<EntityModel<Appointment>> responseCommited = BookingService.newAppointment(databaseLeader.getServers().get(bucket), barberId, clientId, slotId, apptId);
				ResponseEntity<EntityModel<Appointment>> responseCommited2 = BookingService.newAppointment(databaseLeader.getReplicas().get(bucket), barberId, clientId, slotId, apptId);
			}
		}
		return response;
	}

	@DeleteMapping("/clientCancelAppointment/{appointmentId}/{zip}")
	public ResponseEntity<EntityModel<Appointment>> cancelAppointmentClientSide(@PathVariable long appointmentId, @PathVariable String zip){
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Appointment>> response = BookingService.cancelAppointment(databaseLeader.getServers().get(bucket), appointmentId);
		if(response.getStatusCode().value() == 200){
			ResponseEntity<EntityModel<Appointment>> response2 = BookingService.cancelAppointment(databaseLeader.getReplicas().get(bucket), appointmentId);
			if(!databaseLeader.getServers().get(bucket).equals(databaseLeader.getReplicas().get(bucket)) && response2.getStatusCode().value() == 200){
				ResponseEntity<EntityModel<Appointment>> responseCommited = BookingService.cancelAppointment(databaseLeader.getServers().get(bucket), appointmentId);
				ResponseEntity<EntityModel<Appointment>> responseCommited2 = BookingService.cancelAppointment(databaseLeader.getReplicas().get(bucket), appointmentId);
			}
		}
		return response;
	}

}
