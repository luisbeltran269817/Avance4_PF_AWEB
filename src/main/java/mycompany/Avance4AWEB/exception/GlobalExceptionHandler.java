package mycompany.Avance4AWEB.exception;

import mycompany.Avance4AWEB.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDTO> manejarErrorNoEncontrado(ResourceNotFoundException ex){
        List<String> detalles = new ArrayList<>();

        detalles.add(ex.getMessage());

        ErrorDTO errorDTO = new ErrorDTO("El recurso no existe", detalles, LocalDateTime.now());
        return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> manejarErroresDeValidacion(MethodArgumentNotValidException ex){
        List<String> detalles = new ArrayList<>();

        for(FieldError error: ex.getBindingResult().getFieldErrors()){
            detalles.add(error.getDefaultMessage());
        }

        ErrorDTO errorDTO = new ErrorDTO("Error en la validacion de datos enviados", detalles, LocalDateTime.now());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    //al intentar buscar un id como string cuando es long me daba MethodArgumentTypeMismatchException
    //entonces hice este exception handler
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDTO> manejarErrorMalFormato(MethodArgumentTypeMismatchException ex){
        List<String> detalles = new ArrayList<>();

        detalles.add("Use el tipo de dato correcto al hacer una request");

        ErrorDTO errorDTO = new ErrorDTO("Error al buscar", detalles, LocalDateTime.now());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDTO> manejarResponseStatus(ResponseStatusException ex) {
        List<String> detalles = new ArrayList<>();

        if(ex.getReason()!=null){
            detalles.add(ex.getReason());
        }else{
            detalles.add("Error al procesar la solicitud");
        }

        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        ErrorDTO errorDTO = new ErrorDTO(status.getReasonPhrase(), detalles, LocalDateTime.now());
        return new ResponseEntity<>(errorDTO, status);
    }

    //esta excepcion se lanza si se llama un metodo http del controlador que no existe (get, post, put, etc)
    //me paso por menso por no actualizar los metodos http al probar endpoints xd
    //y por si acaso pasa, es mejor que se vea el mensaje de excepcion controlada en vez del stacktrace gigante y feo
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDTO> manejarErrorMalMetodo(HttpRequestMethodNotSupportedException ex){
        List<String> detalles = new ArrayList<>();

        detalles.add("Use el metodo correcto de peticion para la api");

        ErrorDTO errorDTO = new ErrorDTO("Error al hacer peticion", detalles, LocalDateTime.now());
        return new ResponseEntity<>(errorDTO, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
