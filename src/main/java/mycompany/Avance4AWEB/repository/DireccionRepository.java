package mycompany.Avance4AWEB.repository;

import mycompany.Avance4AWEB.model.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findByClienteIdOrderByIdAsc(Long clienteId);

    //busca la direccion SOLO si es de ese cliente y evita usar direcciones de otros
    Optional<Direccion> findByIdAndClienteId(Long id, Long clienteId);
}
