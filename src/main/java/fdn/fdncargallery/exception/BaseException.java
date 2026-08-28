package fdn.fdncargallery.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final MessageType messageType;

    public BaseException(ErrorMessage message) {
        super(message.prepareErrorMessage());
        this.messageType = message.getMessageType();
    }
}
