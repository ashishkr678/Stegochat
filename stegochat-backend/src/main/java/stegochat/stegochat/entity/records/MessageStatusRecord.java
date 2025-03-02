package stegochat.stegochat.entity.records;

import java.time.LocalDateTime;

import stegochat.stegochat.entity.enums.MessageStatus;

public record MessageStatusRecord(
    MessageStatus status, 
    LocalDateTime timestamp
) {}
