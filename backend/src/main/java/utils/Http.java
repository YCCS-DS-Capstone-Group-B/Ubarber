package utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Http {
	public static HttpResponse<String> post(String uri, String json) {
		HttpRequest request = HttpRequest.newBuilder().
				POST(HttpRequest.BodyPublishers.ofString(json))
				.uri(URI.create(uri))
				.header("Content-Type", "application/json")
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
			System.out.println(response.body());
		} else {
			System.out.println("the response was null");
		}
		return response;
	}
	public static HttpResponse<String> get(String uri) {
		HttpRequest request = HttpRequest.newBuilder().
				GET()
				.uri(URI.create(uri))
				.header("Content-Type", "application/json")
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
			System.out.println(response.body());
		} else {
			System.out.println("the response was null");
		}
		return response;
	}

	public static HttpResponse<String> delete(String uri) {
		HttpRequest request = HttpRequest.newBuilder().
				DELETE()
				.uri(URI.create(uri))
				.header("Content-Type", "application/json")
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
			System.out.println(response.body());
		} else {
			System.out.println("the response was null");
		}
		return response;
	}

	public static HttpResponse<String> put(String uri, String json) {
		HttpRequest request = HttpRequest.newBuilder().
				PUT(HttpRequest.BodyPublishers.ofString(json))
				.uri(URI.create(uri))
				.header("Content-Type", "application/json")
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
			System.out.println(response.body());
		} else {
			System.out.println("the response was null");
		}

		return response;
	}
}
