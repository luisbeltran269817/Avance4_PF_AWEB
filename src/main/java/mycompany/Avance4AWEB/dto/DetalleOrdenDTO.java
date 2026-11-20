package mycompany.Avance4AWEB.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrdenDTO {
    private Long id;
    private Long varianteId;
    private String nombre;
    private String plataforma;
    private String imagenUrl;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
