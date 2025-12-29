package com.theatermgnt.theatermgnt.notification.dto.request;

import com.theatermgnt.theatermgnt.account.entity.Account;

import lombok.*;
import lombok.experimental.FieldDefaults;
import sibModel.SendSmtpEmailAttachment;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailBuilderRequest {
    Account account;
    String subject;
    String htmlContent;
    String emailTypeForLog;
    List<SendSmtpEmailAttachment> attachments;
}
