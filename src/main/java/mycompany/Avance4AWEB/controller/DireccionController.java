package mycompany.Avance4AWEB.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mycompany.Avance4AWEB.dto.ClienteResponse;
import mycompany.Avance4AWEB.dto.DireccionDTO;
import mycompany.Avance4AWEB.dto.DireccionRequest;
import mycompany.Avance4AWEB.service.DireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/direcciones")
public class DireccionController {

    @Autowired
    DireccionService direccionService;

    @PostMapping
    public ResponseEntity<DireccionDTO> registrarDireccion(@Valid @RequestBody DireccionRequest request, HttpServletRequest sesion) {
        String correo = (String) sesion.getAttribute("correo");
        DireccionDTO creado = direccionService.registrarDireccion(request, correo);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<DireccionDTO>> encontrarDireccionesDeCliente(HttpServletRequest sesion){
        String correo = (String) sesion.getAttribute("correo");
        List<DireccionDTO> direcciones = direccionService.encontrarDireccionesDeCliente(correo);
        return ResponseEntity.status(HttpStatus.CREATED).body(direcciones);
    }

}
