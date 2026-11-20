package mycompany.Avance4AWEB.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionDTO {
    private Long id;
    private String calle;
    private String numero;
    private String ciudad;
    private String estado;
    private String codigoPostal;
}