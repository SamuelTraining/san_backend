package com.app.gestiondocumental.service;

import com.app.gestiondocumental.dto.ia.PeticionIaDto;
import com.app.gestiondocumental.dto.ia.RespuestaIaDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class IaClasificacionService {

    private static final Logger log = LoggerFactory.getLogger(IaClasificacionService.class);
    private final WebClient iaWebClient; // El "teléfono" que configuramos

    // Spring inyecta el WebClient que creamos en WebClientConfig
    public IaClasificacionService(WebClient iaWebClient) {
        this.iaWebClient = iaWebClient;
    }

    /**
     * Llama a la API de IA de Python para clasificar un archivo.
     */
    public Mono<RespuestaIaDto> clasificarArchivo(String rutaAbsoluta) {
        log.info("Llamando a la IA para clasificar: {}", rutaAbsoluta);

        PeticionIaDto peticion = new PeticionIaDto(rutaAbsoluta);

        return iaWebClient.post() // Hacer una llamada POST
                .uri("/clasificar") // a la URL /clasificar
                .bodyValue(peticion) // Enviando el JSON con la ruta
                .retrieve() // Recuperar la respuesta
                .bodyToMono(RespuestaIaDto.class) // Convertir el JSON de respuesta a nuestra clase
                .doOnError(e -> log.error("Error al llamar a la IA: {}", e.getMessage()));
    }
}