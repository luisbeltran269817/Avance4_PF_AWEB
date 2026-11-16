package mycompany.Avance4AWEB.controller;

import jakarta.validation.Valid;
import mycompany.Avance4AWEB.dto.LoginRequest;
import mycompany.Avance4AWEB.dto.LoginResponse;
import mycompany.Avance4AWEB.model.Usuario;
import mycompany.Avance4AWEB.repository.UsuarioRepository;
import mycompany.Avance4AWEB.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    //@Valid activa las validaciones de la clase LoginRequest
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        //se busca por el correo porque no manejamos username
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo()).orElse(null);

        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
        //se usa matches de BCrypt porque en la BD las contraseñas estan cifradas, equals no me jalaba por lo mismo
        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
        //el token guarda el correo como identificador
        String token = jwtService.generateToken(usuario.getCorreo());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}