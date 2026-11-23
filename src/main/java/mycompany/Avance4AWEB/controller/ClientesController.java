package mycompany.Avance4AWEB.controller;

import jakarta.validation.Valid;
import mycompany.Avance4AWEB.dto.ClienteResponse;
import mycompany.Avance4AWEB.dto.RegistroClienteRequest;
import mycompany.Avance4AWEB.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class ClientesController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> registrar(@Valid @RequestBody RegistroClienteRequest request) {
        ClienteResponse creado = clienteService.registrarCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
