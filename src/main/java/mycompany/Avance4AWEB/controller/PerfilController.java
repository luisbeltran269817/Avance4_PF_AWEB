package mycompany.Avance4AWEB.controller;

import jakarta.servlet.http.HttpServletRequest;
import mycompany.Avance4AWEB.dto.PerfilResponse;
import mycompany.Avance4AWEB.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

    @Autowired
    ClienteService clienteService;

    @GetMapping
    public ResponseEntity<PerfilResponse> verMiPerfil(HttpServletRequest request) {
        //se recupera el correo que JwtFilter extrajo del subject del JWT
        //y guardó como atributo del request. Se hace cast a String.
        String correo = (String) request.getAttribute("correo");

        //se delega al service con el correo obtenido
        return ResponseEntity.ok(clienteService.verMiPerfil(correo));
    }
}
