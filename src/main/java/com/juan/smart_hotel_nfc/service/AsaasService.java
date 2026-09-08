package com.juan.smart_hotel_nfc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.juan.smart_hotel_nfc.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.juan.smart_hotel_nfc.dto.AsaasPixQrCodeResponseDTO;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.apache.hc.core5.http.message.MessageSupport.header;

@Service
public class AsaasService {


    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

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

    public String ListarClientes() {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/customers"))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "smart-hotel-nfc/1.0")
                    .header("access_token", apiToken)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao listar CLientes no Asaas: " + response.body());
            }

            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar clientes no Asaas", e);
        }
    }


    public String buscarCLientePorCpfCnpj(String cpfCnpj) {
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/customers?cpfCnpj=" + cpfCnpj))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "smart-hotel-nfc/1.0")
                    .header("access_token", apiToken)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() <200 || response.statusCode() >=300){
                throw new RuntimeException("Erro ao buscar cliente por CPF/CNPJ no ASAAS: " + response.body());
            }
            return response.body();
        }catch (Exception e){
            throw new RuntimeException("Erro ao buscar cliente por CPF/CNPJ no ASAAS", e);
        }

    }
    public AsaasCobrancaResponseDTO criarCobranca(AsaasCobrancaRequestDTO cobranca){
        try {
            String json = objectMapper.writeValueAsString(cobranca);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/payments"))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "smart-hotel-nfc/1.0")
                    .header("access_token", apiToken)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao criar cobrança no Asaas: " + response.body());
            }

            return objectMapper.readValue(response.body(), AsaasCobrancaResponseDTO.class);

        }catch (Exception e){
            throw new RuntimeException("Erro ao criar cobrança no Asaas", e);

        }
    }

    public AsaasPixQrCodeResponseDTO buscarQrCodePix(String id) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/payments/" + id + "/pixQrCode"))
                    .header("Content-type", "application/json")
                    .header("User-Agent", "smart-hotel-nfc/1.0")
                    .header("access_token", apiToken)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao buscar QR Code Pix no Asaas: " + response.body());
            }
            return objectMapper.readValue(response.body(), AsaasPixQrCodeResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar QR Code Pix no Asaas", e);
        }

    }





}


