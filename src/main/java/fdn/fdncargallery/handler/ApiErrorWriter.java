package fdn.fdncargallery.handler;

import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
@Component
@RequiredArgsConstructor
public class ApiErrorWriter {

    private final ObjectMapper objectMapper;

    public void write(HttpServletRequest request,
                      HttpServletResponse response,
                      MessageType messageType) throws IOException {

        HttpStatus status = messageType.getHttpStatus();

        Exception<String> detail = new Exception<>();
        detail.setMessage(new ErrorMessage(messageType, null).prepareErrorMessage());
        detail.setPath(request.getRequestURI());
        detail.setCreationDate(LocalDateTime.now());

        ApiError<String> body = new ApiError<>();
        body.setStatusCode(status.value());
        body.setException(detail);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
