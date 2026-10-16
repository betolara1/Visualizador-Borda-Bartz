package com.bartz.config;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import io.github.cdimascio.dotenv.Dotenv;

public class ApiConsumer {
    private static final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    // Busca o arquivo .env na raiz do projeto
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static final String HOST = dotenv.get("DB2_HOST");
    private static final String PORTA = dotenv.get("DB2_PORT");
    private static final String CAMINHO = dotenv.get("CAMINHO_API");
    private static final String LOGIN = dotenv.get("USERNAME_LOGIN");
    private static final String PASSWORD = dotenv.get("PASSWORD_LOGIN");
    private static final String CAMINHO_LOGIN = dotenv.get("CAMINHO_LOGIN");

    private static String token; 

    // Método para buscar dados na API
    public static String buscarDados(String codigoBarra){
        try{
            // Faz login na API
            login();

            // Cria a requisição HTTP
            HttpRequest request = HttpRequest.newBuilder() //cria uma nova requisição
                .uri(URI.create("http://" + HOST + ":" + PORTA + "/" + CAMINHO + codigoBarra)) //endpoint da API
                .GET().header("Authorization", "Bearer " + token) //header para indicar que a resposta deve ser em JSON
                .build(); //finaliza a construção da requisição

            // Envia a requisição
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Verifica o status da resposta
            System.out.println("\nStatus: " + response.statusCode());

            // Se a requisição for bem sucedida, imprime os dados
            if (response.statusCode() == 200){
                System.out.println("Dados encontrados com sucesso.\n" + response.body());
                return response.body();
            }

            // Se a requisição falhar, imprime o erro
            else{
                System.out.println("Item não encontrado: " + response.body());
                return null;
            }
        }
        // Se ocorrer qualquer erro, imprime a mensagem de erro
        catch(Exception e){
            System.err.println("Erro ao buscar dados da API: " + e.getMessage());
            return null;
        }
    }

    public static String login(){
        try{
            // Cria a requisição HTTP
            HttpRequest request = HttpRequest.newBuilder() //cria uma nova requisição
                .uri(URI.create("http://" + HOST + ":" + PORTA + "/" + CAMINHO_LOGIN + "?username=" + LOGIN + "&password=" + PASSWORD)) //endpoint da API
                .GET()
                .header("Content-Type", "application/json") //header para indicar que a resposta deve ser em JSON
                .build(); //finaliza a construção da requisição

            // Envia a requisição
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Verifica o status da resposta
            System.out.println("Status: " + response.statusCode());

            // Se a requisição for bem sucedida, imprime os dados
            if (response.statusCode() == 200){
                System.out.println("Token encontrado com sucesso.\n" + response.body());
                token = response.body();
                return token;
            }

            // Se a requisição falhar, imprime o erro
            else{
                System.out.println("Erro ao gerar token da API: " + response.body());
                return null;
            }
        }
        // Se ocorrer qualquer erro, imprime a mensagem de erro
        catch(Exception e){
            System.err.println("Erro ao gerar token da API: " + e.getMessage());
            return null;
        }
    }
}
