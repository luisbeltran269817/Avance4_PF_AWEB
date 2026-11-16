package mycompany.Avance4AWEB.exception;

import mycompany.Avance4AWEB.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
}
