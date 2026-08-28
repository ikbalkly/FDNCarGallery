package fdn.fdncargallery.handler;

import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<ApiError<?>> handleBaseException(BaseException exception, WebRequest request) {

        MessageType messageType = exception.getMessageType();
        HttpStatus status = messageType != null ? messageType.getHttpStatus() : HttpStatus.BAD_REQUEST;

        log.warn("İş kuralı ihlali ({}): {}", status.value(), exception.getMessage());

        return ResponseEntity.status(status).body(createApiError(exception.getMessage(), request, status));
    }
    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<ApiError<?>> handleAccessDenied(AccessDeniedException exception, WebRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createApiError(messageOf(MessageType.UNAUTHORIZED), request, HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(value = {OptimisticLockingFailureException.class})
    public ResponseEntity<ApiError<?>> handleOptimisticLock(OptimisticLockingFailureException exception, WebRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createApiError(messageOf(MessageType.CONCURRENT_MODIFICATION), request, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<ApiError<?>> handleDataIntegrityViolation(DataIntegrityViolationException exception, WebRequest request) {
        log.warn("Veri bütünlüğü ihlali: {}", exception.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createApiError(messageOf(MessageType.DATA_INTEGRITY_VIOLATION), request, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(value = {java.lang.Exception.class})
    public ResponseEntity<ApiError<?>> handleUnexpected(java.lang.Exception exception, WebRequest request) {
        log.error("Beklenmeyen hata", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createApiError(messageOf(MessageType.GENERAL_EXCEPTION), request, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, List<String>> map = new HashMap<>();
        for (ObjectError objectError : exception.getBindingResult().getAllErrors()) {
            String fieldName = ((FieldError) objectError).getField();
            if (map.containsKey(fieldName)) {
                map.put(fieldName, addValue(map.get(fieldName), objectError.getDefaultMessage()));
            } else {
                map.put(fieldName, addValue(new ArrayList<>(), objectError.getDefaultMessage()));
            }
        }
        return new ResponseEntity<>(createApiError(map, request, HttpStatus.BAD_REQUEST), headers, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(java.lang.Exception exception,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {

        HttpStatus status = HttpStatus.valueOf(statusCode.value());
        log.warn("İstek işlenemedi ({}): {}", status.value(), exception.getMessage());

        return new ResponseEntity<>(createApiError(messageOf(messageTypeFor(status)), request, status), headers, status);
    }

    private MessageType messageTypeFor(HttpStatus status) {
        if (status == HttpStatus.NOT_FOUND) {
            return MessageType.NO_RECORD_EXIST;
        }
        if (status == HttpStatus.METHOD_NOT_ALLOWED) {
            return MessageType.UNSUPPORTED_OPERATION;
        }
        if (status.is4xxClientError()) {
            return MessageType.VALIDATION_ERROR;
        }
        return MessageType.GENERAL_EXCEPTION;
    }

    private String messageOf(MessageType messageType) {
        return new ErrorMessage(messageType, null).prepareErrorMessage();
    }

    public <E> ApiError<E> createApiError(E message, WebRequest webRequest, HttpStatus status) {
        ApiError<E> apiError = new ApiError<>();
        apiError.setStatusCode(status.value());

        Exception<E> exception = new Exception<>();
        exception.setMessage(message);
        exception.setPath(webRequest.getDescription(false).substring(4));
        exception.setCreationDate(LocalDateTime.now());

        apiError.setException(exception);
        return apiError;
    }


    private List<String> addValue(List<String> list, String value) {
        list.add(value);
        return list;
    }
}
