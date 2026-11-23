package mycompany.Avance4AWEB.service;

import mycompany.Avance4AWEB.dto.*;
import mycompany.Avance4AWEB.exception.ResourceNotFoundException;
import mycompany.Avance4AWEB.model.*;
import mycompany.Avance4AWEB.repository.ProductoRepository;
import mycompany.Avance4AWEB.repository.VarianteProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ProductoService {

    @Autowired
    ProductoRepository productoRepository;

    @Autowired
    VarianteProductoRepository varianteProductoRepository;

    /**
     * consigue todos los productos con repository, despues los convierte a dto
     * @return lista de todos los productos como DTO
     * NOTA: este no lo termine usando para la pantalla, pero lo dejare porque puede
     * ser util en alguna otra pantalla que se necesiten mostrar todos los productos
     */
    public List<ProductoDTO> listarProductos() {
        return productoRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * busca una variante por su id y recupera los datos de su producto relacionado
     * @param id
     * @return el productoDTO con una sola variatne, la seleccionada
     */
    public ProductoDTO buscarVariantePorId(Long id){
        VarianteProducto vp = varianteProductoRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("El producto que se busca no existe"));
        ProductoDTO dto = convertirSinVariantesADTO(vp.getProducto());
        dto.setVariantes(List.of(convertirVarianteADTO(vp)));
        return dto;
    }

    //metodo auxiliar que convierte un producto en productodto
    private ProductoDTO convertirADTO(Producto producto) {
        List<ImagenProductoDTO> imagenes = producto.getImagenes().stream()
                .sorted(Comparator.comparingInt(ImagenProducto::getOrden))
                .map(this::convertirImagenADTO)
                .toList();

        List<VarianteProductoDTO> variantes = producto.getVariantes().stream()
                .map(this::convertirVarianteADTO)
                .toList();

        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setCategoria(convertirCategoriaADTO(producto.getCategoria()));
        dto.setImagenes(imagenes);
        dto.setVariantes(variantes);
        return dto;
    }

    private ImagenProductoDTO convertirImagenADTO(ImagenProducto imagen) {
        return new ImagenProductoDTO(
                imagen.getId(),
                imagen.getUrl(),
                imagen.getOrden(),
                imagen.isPrincipal()
        );
    }

    private VarianteProductoDTO convertirVarianteADTO(VarianteProducto variante) {
        List<IdiomaDTO> idiomas = variante.getIdiomas().stream()
                .map(this::convertirIdiomaADTO)
                .toList();

        VarianteProductoDTO dto = new VarianteProductoDTO();
        dto.setId(variante.getId());
        dto.setActivo(variante.isActivo());
        dto.setDimensiones(variante.getDimensiones());
        dto.setPlataforma(variante.getPlataforma());
        dto.setPrecio(variante.getPrecio());
        dto.setRegion(variante.getRegion());
        dto.setSoporteFisico(variante.getSoporteFisico());
        dto.setStock(variante.getStock());
        dto.setIdiomas(idiomas);
        return dto;
    }

    private IdiomaDTO convertirIdiomaADTO(Idioma idioma) {
        return new IdiomaDTO(idioma.getId(), idioma.getNombre());
    }

    private CategoriaDTO convertirCategoriaADTO(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        return new CategoriaDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }

    //este lo ocupo cuando se construye el producto a partir de una variante
    //porque el anterior daba todas las variantes y al seleccionar una varianteProducto
    //en el catalogo se debe traer la info de esa variante y el producto sin las demas variantes
    private ProductoDTO convertirSinVariantesADTO(Producto producto) {
        List<ImagenProductoDTO> imagenes = producto.getImagenes().stream()
                .sorted(Comparator.comparingInt(ImagenProducto::getOrden))
                .map(this::convertirImagenADTO)
                .toList();

        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setCategoria(convertirCategoriaADTO(producto.getCategoria()));
        dto.setImagenes(imagenes);
        return dto;
    }
}
