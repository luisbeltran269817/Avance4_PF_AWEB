package mycompany.Avance4AWEB.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mycompany.Avance4AWEB.model.MetodoPago;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenRequest {

    @NotNull(message = "Selecciona una dirección de envío")
    private Long direccionId;

    @NotNull(message = "Selecciona un método de pago")
    private MetodoPago metodoPago;
}
