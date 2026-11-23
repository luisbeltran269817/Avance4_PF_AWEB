package mycompany.Avance4AWEB.service;

import jakarta.transaction.Transactional;
import mycompany.Avance4AWEB.dto.ClienteResponse;
import mycompany.Avance4AWEB.dto.PerfilResponse;
import mycompany.Avance4AWEB.dto.RegistroClienteRequest;
import mycompany.Avance4AWEB.exception.ResourceNotFoundException;
import mycompany.Avance4AWEB.model.Carrito;
import mycompany.Avance4AWEB.model.Cliente;
import mycompany.Avance4AWEB.model.Usuario;
import mycompany.Avance4AWEB.repository.ClienteRepository;
import mycompany.Avance4AWEB.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public ClienteResponse registrarCliente(RegistroClienteRequest request) {
        String correo = request.getCorreo().trim().toLowerCase();

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya esta registrado");
        }

        Cliente cliente = Cliente.builder()
                .nombre(request.getNombre().trim())
                .correo(correo)
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .telefono(request.getTelefono())
                .fechaRegistro(LocalDateTime.now())
                .build();

        //se crea un carrito junto con el cliente
        Carrito carrito = Carrito.builder()
                .cliente(cliente)
                .fechaCreacion(LocalDateTime.now())
                .build();
        cliente.setCarrito(carrito);

        Cliente guardado;
        try {
            guardado = clienteRepository.saveAndFlush(cliente);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya esta registrado");
        }

        return new ClienteResponse(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getCorreo(),
                guardado.getTelefono(),
                guardado.getFechaRegistro()
        );
    }

    public PerfilResponse verMiPerfil(String correo){
        Usuario perfil = usuarioRepository.findByCorreo(correo).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return new PerfilResponse(
                perfil.getNombre(),
                perfil.getCorreo()
        );
    }
}
