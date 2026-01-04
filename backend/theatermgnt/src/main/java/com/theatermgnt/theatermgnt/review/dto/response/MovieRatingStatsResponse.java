package com.theatermgnt.theatermgnt.review.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieRatingStatsResponse {

    String movieId;
    BigDecimal averageRating;
    Long totalReviews;
    Map<BigDecimal, Long> ratingDistribution;
}
