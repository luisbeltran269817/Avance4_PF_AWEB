package mycompany.Avance4AWEB.service;

import jakarta.transaction.Transactional;
import mycompany.Avance4AWEB.dto.DireccionDTO;
import mycompany.Avance4AWEB.dto.DireccionRequest;
import mycompany.Avance4AWEB.exception.ResourceNotFoundException;
import mycompany.Avance4AWEB.model.Cliente;
import mycompany.Avance4AWEB.model.Direccion;
import mycompany.Avance4AWEB.repository.ClienteRepository;
import mycompany.Avance4AWEB.repository.DireccionRepository;
import mycompany.Avance4AWEB.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DireccionService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    DireccionRepository direccionRepository;

    @Transactional
    public DireccionDTO registrarDireccion(DireccionRequest request, String correo){

        Cliente cliente = clienteRepository.findByCorreo(correo).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        Direccion direccion = Direccion.builder()
                .calle(request.getCalle())
                .numero(request.getNumero())
                .ciudad(request.getCiudad())
                .estado(request.getEstado())
                .codigoPostal(request.getCodigoPostal())
                .cliente(cliente)
                .build();

        //guardar la direccion tambien la guarda en la lista de direcciones del cliente
        Direccion guardada = direccionRepository.save(direccion);

        return direccionADTO(guardada);
    }

    public List<DireccionDTO> encontrarDireccionesDeCliente(String correo){
        Cliente cliente = clienteRepository.findByCorreo(correo).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        List<Direccion> direcciones = direccionRepository.findByClienteIdOrderByIdAsc(cliente.getId());
        List<DireccionDTO> direccionesDTO = new ArrayList<>();
        for(Direccion d: direcciones){
            direccionesDTO.add(direccionADTO(d));
        }
        return direccionesDTO;
    }

    public DireccionDTO direccionADTO(Direccion direccion){
        return new DireccionDTO(
                direccion.getId(),
                direccion.getCalle(),
                direccion.getNumero(),
                direccion.getCiudad(),
                direccion.getEstado(),
                direccion.getCodigoPostal()
        );
    }
}
