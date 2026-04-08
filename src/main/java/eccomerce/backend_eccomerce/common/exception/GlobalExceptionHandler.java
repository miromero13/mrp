package eccomerce.backend_eccomerce.common.exception;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseMessage<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> validationMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
            .map(error -> error.getDefaultMessage())
                .distinct()
                .collect(Collectors.toList());

        String message;
        String errorDetail;

        if (validationMessages.isEmpty()) {
            message = "Datos de entrada invalidos";
            errorDetail = null;
        } else if (validationMessages.size() == 1) {
            message = validationMessages.get(0);
            errorDetail = null;
        } else {
            message = "Se encontraron errores de validacion";
            errorDetail = String.join(", ", validationMessages);
        }

        ResponseMessage<Void> response = ResponseMessage.error(
            message,
            errorDetail,
            HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ResponseMessage<Void>> handleSecurityException(SecurityException ex) {
        ResponseMessage<Void> response = ResponseMessage.error(
            "Acceso denegado",
            ex.getMessage(),
            HttpStatus.FORBIDDEN.value()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
