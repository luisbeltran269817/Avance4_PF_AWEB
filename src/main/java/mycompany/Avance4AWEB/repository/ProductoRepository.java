package mycompany.Avance4AWEB.repository;

import mycompany.Avance4AWEB.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
