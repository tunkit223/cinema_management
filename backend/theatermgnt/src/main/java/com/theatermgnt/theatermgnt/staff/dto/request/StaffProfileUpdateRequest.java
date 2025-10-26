package com.theatermgnt.theatermgnt.staff.dto.request;

import com.theatermgnt.theatermgnt.common.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffProfileUpdateRequest {
    String firstName;
    String lastName;
    String jobTitle;
    String cinemaId;
    String address;
    String avatarUrl;
    Gender gender;
    LocalDate dob;
    List<String> roles;
}
