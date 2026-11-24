package mycompany.Avance4AWEB.service;

import mycompany.Avance4AWEB.dto.DetalleOrdenDTO;
import mycompany.Avance4AWEB.dto.DireccionEnvioDTO;
import mycompany.Avance4AWEB.dto.EnvioDTO;
import mycompany.Avance4AWEB.dto.OrdenDTO;
import mycompany.Avance4AWEB.dto.PagoDTO;
import mycompany.Avance4AWEB.exception.ResourceNotFoundException;
import mycompany.Avance4AWEB.model.Cliente;
import mycompany.Avance4AWEB.model.DetalleOrden;
import mycompany.Avance4AWEB.model.EstadoOrden;
import mycompany.Avance4AWEB.model.ImagenProducto;
import mycompany.Avance4AWEB.model.Orden;
import mycompany.Avance4AWEB.model.Producto;
import mycompany.Avance4AWEB.model.VarianteProducto;
import mycompany.Avance4AWEB.repository.ClienteRepository;
import mycompany.Avance4AWEB.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrdenService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private OrdenRepository ordenRepository;


    public List<OrdenDTO> listarOrdenes(String correo, EstadoOrden estado) {
        Cliente cliente = clienteRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        List<Orden> ordenes;
        //por default traemos todas las ordenes
        if (estado == null) {
            ordenes = ordenRepository.findByClienteIdOrderByFechaDesc(cliente.getId());
        //o por el chip de estado seleccionado
        } else {
            ordenes = ordenRepository.findByClienteIdAndEstadoOrdenOrderByFechaDesc(cliente.getId(), estado);
        }

        List<OrdenDTO> ordenesDTO = new ArrayList<>();
        for (Orden orden : ordenes) {
            ordenesDTO.add(convertirADTO(orden));
        }
        return ordenesDTO;
    }

    private OrdenDTO convertirADTO(Orden orden) {
        List<DetalleOrdenDTO> detalles = new ArrayList<>();

        for (DetalleOrden detalle : orden.getDetalles()) {
            detalles.add(convertirDetalleADTO(detalle));
        }

        //la direccion sale de la copia guardada de la orden
        // getEstado es el de la direccion (Sonora, Chiapas, Texas), no de la orden (los chips de estado)
        DireccionEnvioDTO direccionEnvio = new DireccionEnvioDTO(
                orden.getCalle(),
                orden.getNumero(),
                orden.getCiudad(),
                orden.getEstado(),
                orden.getCodigoPostal()
        );

        PagoDTO pago = null;
        if (orden.getPago() != null) {
            pago = new PagoDTO(
                    orden.getPago().getMonto(),
                    orden.getPago().getMetodo(),
                    orden.getPago().getEstado()
            );
        }
        
        EnvioDTO envio = null;
        if (orden.getEnvio() != null) {
            envio = new EnvioDTO(
                    orden.getEnvio().getPaqueteria(),
                    orden.getEnvio().getNumeroGuia(),
                    orden.getEnvio().getFechaEnvio(),
                    orden.getEnvio().getFechaEntrega()
            );
        }

        return new OrdenDTO(
                orden.getId(),
                orden.getFolio(),
                orden.getFecha(),
                orden.getEstadoOrden(),
                orden.getTotal(),
                direccionEnvio,
                detalles,
                pago,
                envio
        );
    }

    private DetalleOrdenDTO convertirDetalleADTO(DetalleOrden detalle) {
        VarianteProducto variante = detalle.getProducto();
        return new DetalleOrdenDTO(
                detalle.getId(),
                variante.getId(),
                variante.getProducto().getNombre(),
                variante.getPlataforma(),
                imagenPrincipal(variante.getProducto()),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal()
        );
    }

    private String imagenPrincipal(Producto producto) {
        for (ImagenProducto imagen : producto.getImagenes()) {
            if (imagen.isPrincipal()) {
                return imagen.getUrl();
            }
        }
        return null;
    }
}
