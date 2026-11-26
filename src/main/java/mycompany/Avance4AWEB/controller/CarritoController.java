package mycompany.Avance4AWEB.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mycompany.Avance4AWEB.dto.ActualizarCantidadRequest;
import mycompany.Avance4AWEB.dto.CarritoDTO;
import mycompany.Avance4AWEB.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping
    public ResponseEntity<CarritoDTO> verCarrito(HttpServletRequest request) {
        //correo que JwtFilter saco del token
        String correo = (String) request.getAttribute("correo");
        return ResponseEntity.ok(carritoService.verCarrito(correo));
    }

    //botones -/+ del carrito
    @PatchMapping("/detalles/{id}")
    public ResponseEntity<CarritoDTO> actualizarCantidad(HttpServletRequest request, @PathVariable Long id, @Valid @RequestBody ActualizarCantidadRequest body) {
        String correo = (String) request.getAttribute("correo");
        return ResponseEntity.ok(carritoService.actualizarCantidad(correo, id, body.getCantidad()));
    }
}