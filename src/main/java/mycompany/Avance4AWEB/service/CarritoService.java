package mycompany.Avance4AWEB.service;

import jakarta.transaction.Transactional;
import mycompany.Avance4AWEB.dto.CarritoDTO;
import mycompany.Avance4AWEB.dto.DetalleCarritoDTO;
import mycompany.Avance4AWEB.exception.ResourceNotFoundException;
import mycompany.Avance4AWEB.model.*;
import mycompany.Avance4AWEB.repository.CarritoRepository;
import mycompany.Avance4AWEB.repository.ClienteRepository;
import mycompany.Avance4AWEB.repository.DetalleCarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CarritoService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private DetalleCarritoRepository detalleCarritoRepository;

    //sirve para la pantalla del carrito y para el resumen de finalizarCompra
    @Transactional
    public CarritoDTO verCarrito(String correo) {
        Cliente cliente = buscarCliente(correo);
        Carrito carrito = cliente.getCarrito();

        //clientes registrados antes de que el carrito se creara junto con el cliente pueden no tener uno
        if (carrito == null) {
            return new CarritoDTO(null, new ArrayList<>(), 0, BigDecimal.ZERO);
        }

        return convertirADTO(carrito);
    }

    //botones -/+: cantidad es la cantidad NUEVA, y con 0 se elimina el detalle
    @Transactional
    public CarritoDTO actualizarCantidad(String correo, Long detalleId, int cantidad) {
        Cliente cliente = buscarCliente(correo);

        DetalleCarrito detalle = detalleCarritoRepository.findById(detalleId).orElseThrow(() -> new ResourceNotFoundException("El detalle del carrito no existe"));

        Carrito carrito = detalle.getCarrito();

        if (!carrito.getCliente().getId().equals(cliente.getId())) {
            throw new ResourceNotFoundException("El detalle del carrito no existe");
        }

        if (cantidad <= 0) {
            //orphanRemoval = true en Carrito  al quitarlo de la lista se borra de la BD
            carrito.getDetalles().removeIf(d -> d.getId().equals(detalleId));
        } else {
            VarianteProducto variante = detalle.getProducto();
            if (cantidad > variante.getStock()) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "No hay stock suficiente de " + variante.getProducto().getNombre());
            }
            detalle.setCantidad(cantidad);
        }

        carritoRepository.saveAndFlush(carrito);
        return convertirADTO(carrito);
    }

    private Cliente buscarCliente(String correo) {
        return clienteRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }

    private CarritoDTO convertirADTO(Carrito carrito) {
        //se copia la lista para ordenarla por id sin tocar la coleccion de Hibernate
        List<DetalleCarrito> detalles = new ArrayList<>(carrito.getDetalles());
        detalles.sort(Comparator.comparing(DetalleCarrito::getId));

        List<DetalleCarritoDTO> detallesDTO = new ArrayList<>();
        int totalArticulos = 0;
        BigDecimal total = BigDecimal.ZERO;

        for (DetalleCarrito detalle : detalles) {
            DetalleCarritoDTO dto = convertirDetalleADTO(detalle);
            detallesDTO.add(dto);
            totalArticulos += dto.getCantidad();
            total = total.add(dto.getSubtotal());
        }

        return new CarritoDTO(carrito.getId(), detallesDTO, totalArticulos, total);
    }

    private DetalleCarritoDTO convertirDetalleADTO(DetalleCarrito detalle) {
        VarianteProducto variante = detalle.getProducto();
        Producto producto = variante.getProducto();
        BigDecimal subtotal = variante.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad()));

        return new DetalleCarritoDTO(
                detalle.getId(),
                variante.getId(),
                producto.getNombre(),
                variante.getPlataforma(),
                imagenPrincipal(producto),
                variante.getPrecio(),
                detalle.getCantidad(),
                subtotal,
                variante.getStock()
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