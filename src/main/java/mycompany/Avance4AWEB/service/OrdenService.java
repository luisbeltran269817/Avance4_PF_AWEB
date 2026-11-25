package mycompany.Avance4AWEB.service;

import jakarta.transaction.Transactional;
import mycompany.Avance4AWEB.dto.*;
import mycompany.Avance4AWEB.exception.ResourceNotFoundException;
import mycompany.Avance4AWEB.model.*;
import mycompany.Avance4AWEB.repository.ClienteRepository;
import mycompany.Avance4AWEB.repository.DireccionRepository;
import mycompany.Avance4AWEB.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdenService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private DireccionRepository direccionRepository;


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

    @Transactional
    public OrdenDTO crearOrden(OrdenRequest request, String correo) {
        Cliente cliente = clienteRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        Direccion direccion = direccionRepository.findByIdAndClienteId(request.getDireccionId(), cliente.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Direccion no encontrada"));

        Carrito carrito = cliente.getCarrito();
        if (carrito == null || carrito.getDetalles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "El carrito esta vacio");
        }
        Orden orden = nuevaOrden(cliente, direccion);
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleCarrito item : carrito.getDetalles()) {
            total = total.add(agregarDetalle(orden, item));
        }
        orden.setTotal(total);
        orden.setPago(nuevoPago(total, request.getMetodoPago()));

        Orden guardada = ordenRepository.save(orden);

        //compra ya hecha, se vacia el carrito
        carrito.getDetalles().clear();

        return convertirADTO(guardada);
    }

    private Orden nuevaOrden(Cliente cliente, Direccion direccion) {
        Orden orden = new Orden();
        orden.setFolio("GH-" + System.currentTimeMillis());
        orden.setFecha(LocalDateTime.now());
        orden.setEstadoOrden(EstadoOrden.PENDIENTE);
        orden.setCliente(cliente);
        orden.setDireccion(direccion);
        orden.setCalle(direccion.getCalle());
        orden.setNumero(direccion.getNumero());
        orden.setCiudad(direccion.getCiudad());
        orden.setEstado(direccion.getEstado());
        orden.setCodigoPostal(direccion.getCodigoPostal());
        return orden;
    }

    //revisa el stock, crea el detalle, descuenta el stock y regresa el subtotal
    private BigDecimal agregarDetalle(Orden orden, DetalleCarrito item) {
        VarianteProducto variante = item.getProducto();
        if (variante.getStock() < item.getCantidad()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "No hay stock suficiente de " + variante.getProducto().getNombre());
        }
        BigDecimal subtotal = variante.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
        DetalleOrden detalle = new DetalleOrden();
        detalle.setCantidad(item.getCantidad());
        detalle.setPrecioUnitario(variante.getPrecio());
        detalle.setSubtotal(subtotal);
        detalle.setProducto(variante);
        detalle.setOrden(orden);
        orden.getDetalles().add(detalle);
        variante.setStock(variante.getStock() - item.getCantidad());
        return subtotal;
    }

    //orden y pago se crean al mismo tiempo
    private Pago nuevoPago(BigDecimal total, MetodoPago metodo) {
        Pago pago = new Pago();
        pago.setMonto(total);
        pago.setMetodo(metodo);
        pago.setEstado(EstadoPago.PAGADO);
        return pago;
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
