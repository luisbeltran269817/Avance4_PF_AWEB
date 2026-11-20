package mycompany.Avance4AWEB.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvioDTO {
    private String paqueteria;
    private String numeroGuia;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaEntrega;
}