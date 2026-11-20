package mycompany.Avance4AWEB.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mycompany.Avance4AWEB.model.EstadoPago;
import mycompany.Avance4AWEB.model.MetodoPago;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDTO {
    private BigDecimal monto;
    private MetodoPago metodo;
    private EstadoPago estado;
}