package com.theatermgnt.theatermgnt.review.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateReviewRequest {

    @DecimalMin(value = "0.5", message = "RATING_MIN_0_5")
    @DecimalMax(value = "10.0", message = "RATING_MAX_10")
    BigDecimal rating;

    @Size(max = 5000, message = "COMMENT_TOO_LONG")
    String comment;

    Boolean isSpoiler;
}
