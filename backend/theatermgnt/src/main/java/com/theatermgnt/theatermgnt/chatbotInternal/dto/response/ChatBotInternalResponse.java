package com.theatermgnt.theatermgnt.chatbotInternal.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatBotInternalResponse {
    String answer;
    List<SourceInfo> sources;
}
