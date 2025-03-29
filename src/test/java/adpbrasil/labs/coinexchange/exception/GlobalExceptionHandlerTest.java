package adpbrasil.labs.coinexchange.exception;


import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    public void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Test illegal argument");
        ResponseEntity<?> response = handler.handleIllegalArgumentException(ex);
        assertEquals(400, response.getStatusCodeValue());
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Test illegal argument", body.get("error"));
    }

    @Test
    public void testHandleInsufficientCoinsException() {
        InsufficientCoinsException ex = new InsufficientCoinsException("Not enough coins");
        ResponseEntity<?> response = handler.handleInsufficientCoinsException(ex);
        assertEquals(400, response.getStatusCodeValue());
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Not enough coins", body.get("error"));
    }

    @Test
    public void testHandleValidationException() throws Exception {
        // Cria um binding result com um erro de validação
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "field", "must not be null"));

        // Cria um MethodParameter usando um método dummy
        Method method = GlobalExceptionHandlerTest.class.getMethod("dummyMethod", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);
        ResponseEntity<?> response = handler.handleValidationException(ex);
        assertEquals(400, response.getStatusCodeValue());
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        // Verifica se o erro para o campo "field" foi mapeado
        assertEquals("must not be null", body.get("field"));
    }

    // Método dummy necessário para criar um MethodParameter
    public void dummyMethod(String param) { }

    @Test
    public void testHandleGeneralException() {
        Exception ex = new Exception("Some error");
        ResponseEntity<?> response = handler.handleGeneralException(ex);
        assertEquals(500, response.getStatusCodeValue());
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        // Para exceções genéricas, o handler retorna uma mensagem padrão
        assertEquals("Internal server error", body.get("error"));
    }
}
