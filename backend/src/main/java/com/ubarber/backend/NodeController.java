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
import utils.Http;
import utils.ShardingUtils;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
public class NodeController {


//	DatabaseLeader databaseLeader = new DatabaseLeader();
	Map<Integer, String> servers = new HashMap<>();
	Map<Integer, String> replicas = new HashMap<>();
	Logger logger = Logger.getLogger(NodeController.class.getName());

	@PostConstruct
	public void makeMap(){
//		this.servers = databaseLeader.getServers();
//		this.replicas = databaseLeader.getReplicas();
		this.servers = Map.of(0, "http://localhost:5050", 1, "http://localhost:5050", 2, "http://localhost:5050");
		this.replicas = Map.of(0, "http://localhost:5050", 1, "http://localhost:5050", 2, "http://localhost:5050");
	}

	@Value("${database.uri}")
	private String database;

	@PostMapping("/registerBarber")
	public String registerBarber(@RequestBody Barber barber) throws JsonProcessingException {
		double[] latLong = ShardingUtils.getLatLong(barber.getLocation());
		barber.setLatitude(latLong[0]);
		barber.setLongitude(latLong[1]);
		barber.setGeoHash(ShardingUtils.getGeoHash(barber.getLatitude(),barber.getLongitude()));
		int bucket = ShardingUtils.getBucket(barber.getLocation(),servers.size());
//		ResponseEntity<EntityModel<Barber>> response = ProfileService.registerBarberProfile(databaseLeader.getServers().get(bucket), barber); //databaseLeader.getServers().get(bucket)
//		ResponseEntity<EntityModel<Barber>> response2 = ProfileService.registerBarberProfile(databaseLeader.getReplicas().get(bucket), barber); //databaseLeader.getReplicas().get(bucket)
		String response = ProfileService.registerBarberProfile(servers.get(bucket), barber); //databaseLeader.getServers().get(bucket)
		//Create thread to update replicas
		sendLogToReplica(response, replicas.get(bucket));
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
	@GetMapping("/getBarber/{barberId}/{zip}")
	public ResponseEntity<EntityModel<Barber>> getBarber(@PathVariable long barberId, @PathVariable String zip) throws JsonProcessingException {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Barber>> response = BrowseBarberService.getBarberProfile(servers.get(bucket) , barberId);
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

	@GetMapping("/barber/myAppointments/{barberId}/{zip}")
	public ResponseEntity<CollectionModel<Appointment>> allAppointmentsByBarber(@PathVariable long barberId, @PathVariable String zip){
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<CollectionModel<Appointment>> response = BarberSideServices.allAppointmentsByBarber(servers.get(bucket), barberId);
		ResponseEntity<CollectionModel<Appointment>> response2 = BarberSideServices.allAppointmentsByBarber(servers.get(bucket), barberId);
		return response;
	}

	@GetMapping("/client/myAppointments/{clientId}/{zip}")
	public ResponseEntity<CollectionModel<Appointment>> allAppointmentsByClient(@PathVariable long clientId, @PathVariable String zip){
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<CollectionModel<Appointment>> response = BarberSideServices.allAppointmentsByBarber(servers.get(bucket), clientId);
		ResponseEntity<CollectionModel<Appointment>> response2 = BarberSideServices.allAppointmentsByBarber(servers.get(bucket), clientId);
		return response;
	}

	@PostMapping("/addAppointmentSlot/{zip}")
	public ResponseEntity<EntityModel<AppointmentSlot>> addAppointmentSlot(@RequestBody AppointmentSlot appointmentSlot, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<AppointmentSlot>> response = BarberSideServices.newAppointmentSlot(servers.get(bucket), appointmentSlot);
		ResponseEntity<EntityModel<AppointmentSlot>> response2 = BarberSideServices.newAppointmentSlot(replicas.get(bucket), appointmentSlot);
		return response;
	}

	@PutMapping("/updateAppointmentSlot/{appointmentSlotId}/{zip}")
	public ResponseEntity<EntityModel<AppointmentSlot>> updateAppointmentSlot(@RequestBody AppointmentSlot appointmentSlot, @PathVariable long appointmentSlotId, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<AppointmentSlot>> response = BarberSideServices.updateAppointmentSlot(servers.get(bucket), appointmentSlotId, appointmentSlot);
		ResponseEntity<EntityModel<AppointmentSlot>> response2 = BarberSideServices.updateAppointmentSlot(replicas.get(bucket), appointmentSlotId, appointmentSlot);
		return response;
	}

	@PutMapping("/updateAppointment/{appointmentId}/{zip}")
	public ResponseEntity<EntityModel<Appointment>> updateAppointment(@RequestBody Appointment appointment, @PathVariable long appointmentId, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Appointment>> response = BookingService.updateAppointment(servers.get(bucket), appointmentId, appointment);
		ResponseEntity<EntityModel<Appointment>> response2 = BookingService.updateAppointment(replicas.get(bucket), appointmentId, appointment);
		return response;
	}

	@DeleteMapping("/deleteAppointmentSlot/{appointmentId}/{zip}")
	public ResponseEntity<EntityModel<AppointmentSlot>> deleteAppointmentSlot(@PathVariable long appointmentId, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<AppointmentSlot>> response = BarberSideServices.deleteAppointmentSlot(servers.get(bucket), appointmentId);
		ResponseEntity<EntityModel<AppointmentSlot>> response2 = BarberSideServices.deleteAppointmentSlot(replicas.get(bucket), appointmentId);
		return response;
	}

	@DeleteMapping("/barberCancelAppointment/{appointmentId}/{zip}")
	public ResponseEntity<EntityModel<Appointment>> cancelAppointment(@PathVariable long appointmentId, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Appointment>> response =  BarberSideServices.cancelAppointment(servers.get(bucket), appointmentId);
		ResponseEntity<EntityModel<Appointment>> response2 =  BarberSideServices.cancelAppointment(replicas.get(bucket), appointmentId);
		return response;
	}

	@PutMapping("/barberUpdateProfile/{barberId}/{zip}")
	public ResponseEntity<EntityModel<Barber>> updateProfile(@RequestBody Barber barber, @PathVariable long barberId, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Barber>> response =  ProfileService.updateProfile(servers.get(bucket), barberId, barber);
		ResponseEntity<EntityModel<Barber>> response2 =  ProfileService.updateProfile(replicas.get(bucket), barberId, barber);
		return response;
	}

	@PutMapping("/clientUpdateProfile/{clientId}/{zip}")
	public ResponseEntity<EntityModel<Client>> updateProfile(@RequestBody Client client, @PathVariable long clientId, @PathVariable String zip) {
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Client>> response =  ProfileService.updateProfile(servers.get(bucket), clientId, client);
		ResponseEntity<EntityModel<Client>> response2 =  ProfileService.updateProfile(replicas.get(bucket), clientId, client);
		return response;
	}

	@GetMapping("/barberSchedule/{barberId}/{zip}")
	public ResponseEntity<CollectionModel<AppointmentSlot>> getBarberSchedule(@PathVariable long barberId, @PathVariable String zip){
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		return BookingService.getBarberSchedule(servers.get(bucket), barberId);
	}

	@PostMapping("/newAppointment/{barberId}/{zip}")
	public ResponseEntity<EntityModel<Appointment>> newAppointment(@RequestBody String appointment, @PathVariable long barberId, @PathVariable String zip){
		Gson gson = new Gson();
		JsonElement jsonElement = gson.fromJson(appointment, JsonElement.class);
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		int clientId = jsonObject.get("clientId").getAsInt();
		int slotId = jsonObject.get("appointmentSlotId").getAsInt();
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Appointment>> response = BookingService.newAppointment(servers.get(bucket), barberId, clientId, slotId);
		ResponseEntity<EntityModel<Appointment>> response2 = BookingService.newAppointment(replicas.get(bucket), barberId, clientId, slotId);
		return response;
	}

	@DeleteMapping("/clientCancelAppointment/{appointmentId}/{zip}")
	public ResponseEntity<EntityModel<Appointment>> cancelAppointmentClientSide(@PathVariable long appointmentId, @PathVariable String zip){
		int bucket = ShardingUtils.getBucket(zip,servers.size());
		ResponseEntity<EntityModel<Appointment>> response = BookingService.cancelAppointment(servers.get(bucket), appointmentId);
		ResponseEntity<EntityModel<Appointment>> response2 = BookingService.cancelAppointment(replicas.get(bucket), appointmentId);
		return response;
	}
	private void sendLogToReplica(String log , String replicaURL){
		new Thread(() -> {
			int i = 0;
			while(i < 3){
				HttpResponse<String> response = Http.postString(replicaURL + "/updateLog", log);
				if(response.statusCode() == 200){
					break;
				}
				i++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}


}
