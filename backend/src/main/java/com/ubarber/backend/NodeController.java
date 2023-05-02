package com.ubarber.backend;


import jakarta.annotation.PostConstruct;
import services.ListEBSEnvironmentInstances;
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

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
public class NodeController {


	DatabaseLeader databaseLeader = new DatabaseLeader();
	Map<Integer, String> servers = new HashMap<>();
	Map<Integer, String> replicas = new HashMap<>();
	Logger logger = Logger.getLogger(NodeController.class.getName());

	@PostConstruct
	public void makeMap(){
		this.servers = databaseLeader.getServers();
		this.replicas = databaseLeader.getReplicas();

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
		ResponseEntity<EntityModel<Barber>> response2 = ProfileService.registerBarberProfile(databaseLeader.getReplicas().get(bucket), barber); //databaseLeader.getReplicas().get(bucket)
		//potential option is to not change the server map if the server is down but instead check the response code
		//of each and if the server is down return the response2 of the replica instead
//		if(response.getStatusCodeValue() != 200 && response2.getStatusCodeValue() == 200){
//			return response2;
//		}
        logger.info("Registering barber with id: " + barber.getId() + " to server: " + servers.get(bucket) + " " + response.getStatusCodeValue());
		return response;
	}
	@PostMapping("/registerClient")
	public ResponseEntity<EntityModel<Client>> registerClient(@RequestBody Client client) {
		double[] latLong = ShardingUtils.getLatLong(client.getLocation());
		client.setLatitude(latLong[0]);
		client.setLongitude(latLong[1]);
		client.setGeoHash(ShardingUtils.getGeoHash(client.getLatitude(),client.getLongitude()));
		int bucket = ShardingUtils.getBucket(client.getLocation(),servers.size());
		ResponseEntity<EntityModel<Client>> response = ProfileService.registerClientProfile(servers.get(bucket), client);
		ResponseEntity<EntityModel<Client>> response2 = ProfileService.registerClientProfile(replicas.get(bucket), client);
		return response;
	}
	@GetMapping("/getBarber/{id}/{zip}")
	public ResponseEntity<EntityModel<Barber>> getBarber(@PathVariable long id, @PathVariable String zip) throws JsonProcessingException {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Barber>> response = BrowseBarberService.getBarberProfile(servers.get(bucket) , id);
		return response;
	}

	@GetMapping("/getBarbersNearMe/{clientZip}")
	public ResponseEntity<CollectionModel<Barber>> getBarbersNearMe(@PathVariable String clientZip) throws JsonProcessingException {
		List<Integer> buckets = ShardingUtils.getBuckets(clientZip,servers.size());
		double[] latLong = ShardingUtils.getLatLong(clientZip);
		String geoHash = ShardingUtils.getGeoHash(latLong[0],latLong[1]);
		List<CollectionModel<Barber>> barberCollections = new ArrayList<>();
		for (Integer bucket : buckets) {
			ResponseEntity<CollectionModel<Barber>> response = BrowseBarberService.getBarbersNearMe(servers.get(bucket), geoHash.substring(0, 4));
			barberCollections.add(response.getBody());
		}
		List<Barber> allBarbers = new ArrayList<>();
		for(CollectionModel<Barber> barberCollection : barberCollections) {
			allBarbers.addAll(barberCollection.getContent());
		}
		CollectionModel<Barber> combinedBarberCollection = CollectionModel.of(allBarbers);
		return ResponseEntity.ok(combinedBarberCollection);
	}

	@GetMapping("/updateSchedule/{id}/{zip}")
	public ResponseEntity<EntityModel<AppointmentSlot>> updateSchedule(@RequestBody AppointmentSlot appointmentSlot, @PathVariable long id, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<AppointmentSlot>> response = BarberSideServices.updateSchedule(servers.get(bucket), id, appointmentSlot);
		ResponseEntity<EntityModel<AppointmentSlot>> response2 = BarberSideServices.updateSchedule(replicas.get(bucket), id, appointmentSlot);
		return response;
	}

	@GetMapping("/cancelAppointment/{id}/{zip}")
	public ResponseEntity<EntityModel<Appointment>> cancelAppointment(@PathVariable long id, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Appointment>> response =  BarberSideServices.cancelAppointment(servers.get(bucket), id);
		ResponseEntity<EntityModel<Appointment>> response2 =  BarberSideServices.cancelAppointment(replicas.get(bucket), id);
		return response;
	}

	@PutMapping("/updateProfile/{id}/{zip}")
	public ResponseEntity<EntityModel<Barber>> updateProfile(@RequestBody Barber barber, @PathVariable long id, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Barber>> response =  BarberSideServices.updateProfile(servers.get(bucket), id, barber);
		ResponseEntity<EntityModel<Barber>> response2 =  BarberSideServices.updateProfile(replicas.get(bucket), id, barber);
		return response;
	}

	@GetMapping("/barberSchedule/{id}/{zip}")
	public ResponseEntity<CollectionModel<AppointmentSlot>> getBarberSchedule(@RequestBody String barberJson, @PathVariable long id, @PathVariable String zip){
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		return BookingService.getBarberSchedule(servers.get(bucket), id);
	}

	@PostMapping("/appointments/{id}/{zip}")
	public ResponseEntity<EntityModel<Appointment>> bookBarber(@RequestBody String appointmentJson, @PathVariable long id, @PathVariable String zip){
		Gson gson = new Gson();
		int clientId = gson.fromJson("clientID", Integer.TYPE);
		int slotId = gson.fromJson("slotID", Integer.TYPE);
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Appointment>> response = BookingService.bookBarber(servers.get(bucket), id, clientId, slotId);
		ResponseEntity<EntityModel<Appointment>> response2 = BookingService.bookBarber(replicas.get(bucket), id, clientId, slotId);
		return response;
	}

	@DeleteMapping("/appointments/{id}/{zip}")
	public ResponseEntity<EntityModel<Appointment>> cancelAppointmentClientSide(@PathVariable long id, @PathVariable String zip){
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Appointment>> response = BookingService.cancelAppointment(servers.get(bucket), id);
		ResponseEntity<EntityModel<Appointment>> response2 = BookingService.cancelAppointment(replicas.get(bucket), id);
		return response;
	}

}
