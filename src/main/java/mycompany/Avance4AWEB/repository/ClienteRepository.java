package mycompany.Avance4AWEB.repository;

import mycompany.Avance4AWEB.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCorreo(String correo);

    //para solo validar si existe ese correo sin traer entidad
    boolean existsByCorreo(String correo);
}
