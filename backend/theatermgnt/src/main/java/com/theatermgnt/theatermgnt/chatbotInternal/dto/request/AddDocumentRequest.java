package com.theatermgnt.theatermgnt.chatbotInternal.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import com.theatermgnt.theatermgnt.chatbotInternal.enums.DocumentType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddDocumentRequest {
    @NotBlank
    String fileId;

    @NonNull
    DocumentType documentType;

    String description;

    @Min(value = 0, message = "PRIORITY_INVALID")
    Integer priority;

    boolean syncImmediately;
}
