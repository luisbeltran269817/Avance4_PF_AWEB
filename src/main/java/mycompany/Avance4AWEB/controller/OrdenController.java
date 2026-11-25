package mycompany.Avance4AWEB.controller;

import jakarta.servlet.http.HttpServletRequest;
import mycompany.Avance4AWEB.dto.OrdenDTO;
import mycompany.Avance4AWEB.model.EstadoOrden;
import mycompany.Avance4AWEB.service.OrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @GetMapping
    public ResponseEntity<List<OrdenDTO>> listarMisOrdenes(HttpServletRequest request, @RequestParam(required = false) EstadoOrden estado) {
        String correo = (String) request.getAttribute("correo");
        return ResponseEntity.ok(ordenService.listarOrdenes(correo, estado));
    }
}
