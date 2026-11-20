package mycompany.Avance4AWEB.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCarritoDTO {
    private Long id;
    private Long varianteId;
    private String nombre;
    private String plataforma;
    private String imagenUrl;
    private BigDecimal precioUnitario;
    private int cantidad;
    private BigDecimal subtotal;
    private int stock;
}
