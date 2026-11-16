package mycompany.Avance4AWEB.controller;

import mycompany.Avance4AWEB.dto.ProductoDTO;
import mycompany.Avance4AWEB.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductosController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<ProductoDTO> listar(){
        return productoService.listarProductos();
    }
}
