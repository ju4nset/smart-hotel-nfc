package com.juan.smart_hotel_nfc.service;

import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.juan.smart_hotel_nfc.dto.AsaasClienteRequestDTO;
import com.juan.smart_hotel_nfc.dto.AsaasClienteResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AsaasService {


    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${asaas.api.url}")
    private String apiUrl;

    @Value("${asaas.api.token}")
    private String apiToken;


    public AsaasClienteResponseDTO criarCliente(
            AsaasClienteRequestDTO cliente) {

        try {

            // transforma o DTO em JSON
            String json = objectMapper.writeValueAsString(cliente);

            // monta a requisição HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/customers"))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "smart-hotel-nfc/1.0")
                    .header("access_token", apiToken)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();


            // envia a requisição
            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );


            // verifica se deu erro
            if (response.statusCode() < 200 ||
                    response.statusCode() >= 300) {

                throw new RuntimeException(
                        "Erro ao criar cliente no Asaas: "
                                + response.body()
                );
            }


            // pega o JSON devolvido pela Asaas
            JsonNode respostaJson =
                    objectMapper.readTree(response.body());


            // transforma o JSON em DTO de resposta
            return new AsaasClienteResponseDTO(
                    respostaJson.path("id").asText(),
                    respostaJson.path("name").asText(),
                    respostaJson.path("cpfCnpj").asText(),
                    respostaJson.path("email").asText()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao criar cliente no Asaas",
                    e
            );
        }
    }
}