package mycompany.Avance4AWEB.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mycompany.Avance4AWEB.model.EstadoOrden;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

//envio puede ser null mientras la orden no se haya enviado
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDTO {
    private Long id;
    private String folio;
    private LocalDateTime fecha;
    private EstadoOrden estado;
    private BigDecimal total;
    private DireccionEnvioDTO direccionEnvio;
    private List<DetalleOrdenDTO> detalles;
    private PagoDTO pago;
    private EnvioDTO envio;
}