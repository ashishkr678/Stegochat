package stegochat.stegochat.entity.records;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaData {
    private String mediaFileId;
    private String mediaFileName;
    private long mediaFileSize;
    private String mediaContentType;
    private Long mediaDuration;
    private boolean isStego;
}
