package edu.eci.arep.lab1.Server;

import java.io.*;
import java.net.*;

import com.google.gson.JsonObject;

/**
 * Clase del servidor web para la aplicación web.
 * 
 * @author Juan Felipe Vivas 
 */
public class MovieServer {

    private static final int PORT = 35000;
    private static final APIRestMovies apf = new APIRestMovies();

    /**
     * Constructor por defecto.
     */
    public MovieServer() {
    }

    /**
     * Método para iniciar el servidor web.
     * 
     * @throws IOException si ocurre un error de entrada/salida
     */
    public static void startServer() throws IOException {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            boolean readingFirst = true;
            String petition = "";

            while ((inputLine = in.readLine()) != null) {
                if (readingFirst) {
                    petition = inputLine.split(" ")[1];
                    readingFirst = false;
                }
                if (!in.ready()) {
                    break;
                }
            }

            outputLine = (petition.startsWith("/film")) ? movieInfoStyled(petition.replace("/film?name=", "")) : mainPage();

            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }

        serverSocket.close();
    }

    /**
     * Lee la primera línea de un BufferedReader.
     * 
     * @param in el BufferedReader de entrada
     * @return la primera línea
     * @throws IOException si ocurre un error de entrada/salida
     */
    private static String readFirstLine(BufferedReader in) throws IOException {
        String inputLine = null;
        boolean readingFirst = true;

        while ((inputLine = in.readLine()) != null) {
            if (readingFirst) {
                return inputLine.split(" ")[1];
            }
            if (!in.ready()) {
                break;
            }
            readingFirst = false;
        }

        return null;
    }

    /**
     * Retorna una estructura HTML con información de la película.
     * 
     * @param name el nombre de la película
     * @return una estructura HTML con información de la película y encabezados
     */
    private static String movieInfoStyled(String name) {
        try {
            JsonObject resp = apf.searchMovie(name);
            return "HTTP/1.1 200 OK\r\n" +
                    "Content-Type:text/html; charset=utf-8\\r\\n" +
                    "\r\n" 
                    +"<!DOCTYPE html>\r\n" +
                    "<html lang=\"en\">\r\n" +
                    "\r\n" +
                    "<head>\r\n" +
                    "    <meta charset=\"UTF-8\">\r\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" +
                    "    <title>Movie</title>\r\n" +
                    "    <style>\r\n" +
                    "        * {\r\n" +
                    "            margin: 0;\r\n" +
                    "            padding: 0;\r\n" +
                    "            align-items: center;\r\n" +
                    "            justify-content: center;\r\n" +
                    "            font-family: 'Jost', sans-serif;\r\n" +
                    "        }\r\n" +
                    "    </style>\r\n" +
                    "</head>\r\n" +
                    "\r\n" +
                    "<body>\r\n" +
                    "    <h1>Title:" + resp.get("Title") + "</h1>\r\n" +
                    "    <img src=" + resp.get("Poster") + ">\r\n" +
                    "    <h2>Director(s):" + resp.get("Director") + "</h2>\r\n" +
                    "    <h2>Plot:" + resp.get("Plot") + "</h2>\r\n" +
                    "</body>\r\n" +
                    "\r\n" +
                    "</html>";
        } catch (Exception e) {
            System.err.println("Error al procesar la película:");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retorna la página HTML principal.
     * 
     * @return la página principal de la aplicación
     */
    private static String mainPage() {
        return "HTTP/1.1 200 OK\r\n" +
                "Content-Type:text/html; charset=utf-8\\r\\n" +
                "\r\n"
                + "<!DOCTYPE html>\r\n"
                + "<html lang=\"en\">\r\n"
                + "\r\n"
                + "<head>\r\n"
                + "    <meta charset=\"UTF-8\">\r\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
                + "    <title>Movies</title>\r\n"
                + "    <style>\r\n"
                + "        body {\r\n"
                + "            font-family: 'Arial', sans-serif;\r\n"
                + "            background-color: #f4f4f4;\r\n"
                + "            margin: 0;\r\n"
                + "            padding: 0;\r\n"
                + "            display: flex;\r\n"
                + "            align-items: center;\r\n"
                + "            justify-content: center;\r\n"
                + "            height: 100vh;\r\n"
                + "        }\r\n"
                + "\r\n"
                + "        .container {\r\n"
                + "            background-color: #fff;\r\n"
                + "            border-radius: 8px;\r\n"
                + "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\r\n"
                + "            padding: 20px;\r\n"
                + "            width: 400px;\r\n"
                + "            text-align: center;\r\n"
                + "        }\r\n"
                + "\r\n"
                + "        label, input, button {\r\n"
                + "            margin-bottom: 15px;\r\n"
                + "        }\r\n"
                + "\r\n"
                + "        input {\r\n"
                + "            width: 100%;\r\n"
                + "            padding: 10px;\r\n"
                + "            box-sizing: border-box;\r\n"
                + "        }\r\n"
                + "\r\n"
                + "        button {\r\n"
                + "            background-color: #4caf50;\r\n"
                + "            color: white;\r\n"
                + "            padding: 10px 15px;\r\n"
                + "            border: none;\r\n"
                + "            border-radius: 4px;\r\n"
                + "            cursor: pointer;\r\n"
                + "            font-size: 16px;\r\n"
                + "        }\r\n"
                + "    </style>\r\n"
                + "</head>\r\n"
                + "\r\n"
                + "<body>\r\n"
                + "    <div class=\"container\">\r\n"
                + "        <label>Escriba el nombre de la pelicula a consultar</label>\r\n"
                + "        <input type=\"text\" id=\"nombre-pelicula\" placeholder=\"Ingrese el nombre de la pelicula\" name=\"name\">\r\n"
                + "        <button onclick=\"consultMovie()\">Consultar</button>\r\n"
                + "        <div id=\"pelicula\"></div>\r\n"
                + "    </div>\r\n"
                + "\r\n"
                + "    <script>\r\n"
                + "        function consultMovie() {\r\n"
                + "            let nameMovie = document.getElementById(\"nombre-pelicula\").value;\r\n"
                + "            console.log(nameMovie);\r\n"
                + "            const xhttp = new XMLHttpRequest();\r\n"
                + "            xhttp.onload = function () {\r\n"
                + "                document.getElementById(\"pelicula\").innerHTML =\r\n"
                + "                    this.responseText;\r\n"
                + "            }\r\n"
                + "            xhttp.open(\"GET\", \"/film?name=\" + nameMovie);\r\n"
                + "            xhttp.send();\r\n"
                + "        }\r\n"
                + "    </script>\r\n"
                + "</body>\r\n"
                + "\r\n"
                + "</html>";
    }
}
