package stegochat.stegochat.entity.records;

import java.time.LocalDateTime;

public record DeletionRecord(
    String messageId,  // ID of the deleted message
    String deletedBy, 
    String deleteType, // "FOR_ME" or "FOR_EVERYONE"
    LocalDateTime deletedAt
) {}
