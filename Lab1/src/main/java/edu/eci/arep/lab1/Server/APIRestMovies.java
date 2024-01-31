/*
 * Clase que actúa como fachada para realizar solicitudes a una API Rest externa
 */
package edu.eci.arep.lab1.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Fachada para realizar solicitudes a una API Rest externa para obtener información sobre películas.
 * 
 * @author Juan Felipe Vivas
 */
public class APIRestMovies {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String MOVIE_URL = "http://www.omdbapi.com/?apikey=f70773ff&t=";
    private final Cache cache;

    /**
     * Constructor de la clase APIRestMovies.
     */
    public APIRestMovies() {
        this.cache = Cache.getInstance();
    }

    /**
     * Busca una película específica por nombre en una API externa.
     * 
     * @param name nombre de la película a buscar
     * @throws IOException si algo falla durante la solicitud
     * @return un JsonObject con todos los datos sobre la película
     */ 
    public JsonObject searchMovie(String name) throws IOException {
        if (cache.movieInCache(name)) {
            return cache.getMovie(name);
        }

        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String apiUrl = MOVIE_URL + encodedName;

        try {
            String jsonResponse = executeGetRequest(apiUrl);
            JsonObject movieInfo = JsonParser.parseString(jsonResponse).getAsJsonObject();

            cache.addMovieToCache(name, movieInfo);
            return movieInfo;
        } catch (Exception e) {
            // Manejar excepciones al convertir la respuesta a JSON
            e.printStackTrace();
            return null;  // O manejar de otra manera según tus necesidades
        }
    }

    private String executeGetRequest(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = connection.getResponseCode();
        System.out.println("GET Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            return response.toString();
        } else {
            System.out.println("Failed to make the request");
            return "";
        }
    }
}
