package services;

import utils.Http;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class DatabaseLeader extends Thread {
    ConcurrentHashMap<String, String> databases = new ConcurrentHashMap<>();
    ConcurrentHashMap<Integer, String> idToServers = new ConcurrentHashMap<>(); // map of ids to server ip addresses
    ConcurrentHashMap<Integer, String> idToReplicas = new ConcurrentHashMap<>(); // map of ids to replica ip addresses
    ConcurrentHashMap<String, Integer> ipToId = new ConcurrentHashMap<>(); // map of ip addresses to its id
    LinkedBlockingQueue<Integer> downServers = new LinkedBlockingQueue<>(); // id's of down servers
    LinkedBlockingQueue<Integer> downReplicas = new LinkedBlockingQueue<>(); // id's of down replicas
    LinkedBlockingQueue<String> waitingServers = new LinkedBlockingQueue<>(); // IP addreses of servers up but not in the list
    int numDatabases;
    Logger logger;
    AtomicInteger counter = new AtomicInteger(0);

    public DatabaseLeader(boolean test){
        if(test){
            this.databases.put("0", "http://localhost:5050");
            this.databases.put("1", "http://localhost:5050");
            this.databases.put("2", "http://localhost:5050");
            this.databases.put("3", "http://localhost:5051");
            this.databases.put("4", "http://localhost:5051");
            this.databases.put("5", "http://localhost:5051");
        }
        else {
            this.databases = ListEBSEnvironmentInstances.getAllDatabase(); //get all the databases upon creation
        }
        numDatabases = databases.size() / 2; //half of the databases are servers and the other half are replicas
        setIdToServers();
        setIdToReplicas();
        if(!test){
            awsTalker.start();
        }
        else {
            testSimulation.start();
        }
        this.logger = Logger.getLogger(String.valueOf(DatabaseLeader.class));
        validateStaged.start();
    }


    Thread awsTalker = new Thread()
    {
        @Override
        public void run () {
        //super.run();
        while (true) { //check every 10 seconds if a database is down
            logger.info("try number " + counter.incrementAndGet());
            ConcurrentHashMap<String, String> newMap = ListEBSEnvironmentInstances.getAllDatabase();
            if (DatabaseLeader.this.databases.size() > 0) {
                for (String key : DatabaseLeader.this.databases.keySet()) {
                    if (!newMap.containsKey(key)) { //if the database is down
                        logger.info("Database " + ipToId.get(databases.get(key)) + " at " + databases.get(key) + " is down");
                        if(idToServers.containsValue(databases.get(key))){
                            if(waitingServers.peek() != null){ //if there is a server waiting to be added to the list
                                String url = waitingServers.peek();
                                idToServers.put(ipToId.get(databases.get(key)), waitingServers.poll()); //add the waiting server to the list
                                ipToId.put(url, ipToId.get(databases.get(key))); //add the id to the ip
                                logger.info("had a database waiting, database " + ipToId.get(databases.get(key)) + " at " + databases.get(key) + " is now up");
                            }
                            else {
                                downServers.add(ipToId.get(databases.get(key))); //add the id of the down server to the list of down servers
                                idToServers.put(ipToId.get(databases.get(key)), idToReplicas.get(ipToId.get(databases.get(key)))); //server is down so only send to its replica,
                            }
                            ipToId.remove(databases.get(key));
                        }
                        else if(idToReplicas.containsValue(databases.get(key))){ //replica is down
                            //TODO do not know what to do if the replica is down for now just ignore it and hope main stays till it is back up
                            logger.info("replica " + ipToId.get(databases.get(key)) + " at " + databases.get(key) + " is down");
                            downReplicas.add(ipToId.get(databases.get(key))); //add the id of the down replica to the list of down replicas
                            idToReplicas.put(ipToId.get(databases.get(key)), idToServers.get(ipToId.get(databases.get(key)))); //replica is down so only send to its server
                        }
                    }
                }
            }
            if(newMap.size() > DatabaseLeader.this.databases.size()){ // a new database came up
                for(String url: newMap.values()){
                    if(!ipToId.containsKey(url)){ //if this is the new database
                        if(downServers.peek() == null){ //if there are no down servers
                            if(downReplicas.peek() == null){ //if there are no down replicas
                                logger.info("Database " + url + " is up but no down servers or replicas to replace");
                                waitingServers.add(url);
                                //TODO if there are 2 more servers up make one a database the other its replica
                                //TODO repartition the data Professor Sacknovitz said don't need to deal with this
                                continue;
                            }
                            else{
                                int replicaId = downReplicas.poll();
                                idToReplicas.put(replicaId, url); //add the new database to the list of replicas to replace the down replica
                                ipToId.put(url, ipToId.get(databases.get(url)));
                                //tell the new replica up to get the data from the main
                                HttpRequest request = HttpRequest.newBuilder()
                                        .uri(java.net.URI.create("http://" + url + "/updateDatabase/"+idToServers.get(replicaId)))
                                        .build();

                                HttpResponse<String> response = null;
                                try {
                                    response = HttpClient.newBuilder()
                                            .build()
                                            .send(request, HttpResponse.BodyHandlers.ofString());
                                } catch (IOException | InterruptedException e) {
                                    logger.warning("problem in sending the updateDatabase request");
                                    e.printStackTrace();
                                }

                                if (response != null) {
                                    logger.info(response.body());
                                } else {
                                    logger.warning("the update Database response was null");
                                }
                                logger.info("Database replica" + replicaId + " is up at " + url);
                                continue;
                            }
                        }
                        int serverId = downServers.poll();
                        idToServers.put(serverId, url); //add the new database to the list of servers to replace the down server
                        ipToId.put(url, ipToId.get(databases.get(url)));
                        //tell the new server up to get the data from the replica (/the main) while it was down
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(java.net.URI.create("http://" + url + "/updateDatabase/"+idToReplicas.get(serverId)))
                                .build();

                        HttpResponse<String> response = null;
                        try {
                            response = HttpClient.newBuilder()
                                    .build()
                                    .send(request, HttpResponse.BodyHandlers.ofString());
                        } catch (IOException | InterruptedException e) {
                            logger.warning("problem in sending the updateDatabase request");
                            e.printStackTrace();
                        }
                        if (response != null) {
                            logger.info(response.body());
                        } else {
                            logger.warning("the update Database response was null");
                        }
                        logger.info("Database " + serverId + " is up at " + url);
                    }
                }
            }
            DatabaseLeader.this.databases = newMap; //update the databases
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    };

    public ConcurrentHashMap<String, String> getDatabases() {
        return this.databases;
    }

    private void setIdToServers(){
        int i = 1;
        int j = 0;
        for(String url: databases.values()){
            if(i <= databases.size() / 2){ //first half of the databases are servers
                idToServers.put(j, url);
                ipToId.put(url, j);
                j++;
            }
            i++;
        }
    }

    private void setIdToReplicas(){
        int i = 1;
        int j = 0;
        for(String url: databases.values()){ //second half of the databases are replicas
            if(i > databases.size() / 2){
                idToReplicas.put(j, url);
                ipToId.put(url, j);
                j++;
            }
            i++;
        }
    }

    private void validatesStaged(){
        Random rand = new Random();
        int randomNum = rand.nextInt((idToServers.size()) +1);
        String serverFull = idToServers.get(randomNum);
        String replicaFull = idToReplicas.get(randomNum);
        if(serverFull == null || replicaFull == null) return;
        String serverUrl = serverFull.split("//")[1];
        String replicaUrl = replicaFull.split("//")[1];
        //TODO send a request to the server to check staged of the replica
        Http.get("http://" + serverUrl + "/checkStaged/" + replicaUrl);
        logger.info("sent a request to main " + randomNum + " to compare staged with replica " + randomNum);
        //TODO send a request to replica to check staged of the server
        Http.get("http://" + replicaUrl + "/checkStaged/" + serverUrl);
        logger.info("sent a request to replica " + randomNum + " to compare staged with main " + randomNum);
    }

    Thread validateStaged = new Thread(){
        @Override
        public void run() {
            while(true){
                validatesStaged();
                try {
                    Thread.sleep(21000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    Thread testSimulation = new Thread(){
        @Override
        public void run() {
            //TODO wait for 1 min
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //TODO shut down a server remove it from the database map and server map and add it to the down servers list
            logger.info("Database " + ipToId.get(databases.get("0")) + " at " + databases.get("0") + " is down");
            //TODO log that this server is down and its replica is now the main for that geohash
            downServers.add(ipToId.get(databases.get("0"))); //add the id of the down server to the list of down servers
            idToServers.put(ipToId.get(databases.get("0")), idToReplicas.get(ipToId.get(databases.get("0")))); //server is down so only send to its replica,
            ///TODO wait for 1 min
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //TODO bring back up the server and log that it is back
            int serverId = downServers.poll();
            String myUrl = "localhost:5050";
            String repurl = "localhost:5051";
            idToServers.put(serverId, myUrl); //add the new database to the list of servers to replace the down server
            ipToId.put(myUrl, 0);
            //tell the new server up to get the data from the replica (/the main) while it was down
//            Http.p("http://" + url + "/updateDatabase/" + url);
            HttpRequest request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString(""))
                    .uri(java.net.URI.create("http://" + myUrl + "/updateDatabase/" + repurl))
                    .build();

            HttpResponse<String> response = null;
            try {
                response = HttpClient.newBuilder()
                        .build()
                        .send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                logger.warning("problem in sending the updateDatabase request");
                e.printStackTrace();
            }
            if (response != null) {
                logger.info(response.body());
            } else {
                logger.warning("the update Database response was null");
            }
            logger.info("Database " + serverId + " is up at " + myUrl);
        }
            //then can look at the database logs and see the call to update data

    };


    public ConcurrentHashMap<Integer, String> getReplicas() {
        return idToReplicas;
    }

    public ConcurrentHashMap<Integer, String> getServers(){
        return idToServers;
    }
}
