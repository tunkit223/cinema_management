package com.theatermgnt.theatermgnt.customer.dto.request;

import com.theatermgnt.theatermgnt.common.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerProfileUpdateRequest {
    String firstName;
    String lastName;
    String address;
    String avatarUrl;
    Gender gender;
    LocalDate dob;
}
