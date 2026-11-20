package mycompany.Avance4AWEB.repository;

import mycompany.Avance4AWEB.model.EstadoOrden;
import mycompany.Avance4AWEB.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByClienteIdOrderByFechaDesc(Long clienteId);

    List<Orden> findByClienteIdAndEstadoOrdenOrderByFechaDesc(Long clienteId, EstadoOrden estadoOrden);
}
