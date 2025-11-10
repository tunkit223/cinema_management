package com.theatermgnt.theatermgnt.movie.mapper;

import com.theatermgnt.theatermgnt.movie.dto.request.CreateAgeRatingRequest;
import com.theatermgnt.theatermgnt.movie.dto.response.AgeRatingResponse;
import com.theatermgnt.theatermgnt.movie.entity.AgeRating;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgeRatingMapper {

    AgeRatingResponse toAgeRatingResponse(AgeRating ageRating);

    List<AgeRatingResponse> toAgeRatingResponseList(List<AgeRating> ageRatings);

    AgeRating toAgeRating(CreateAgeRatingRequest request);
}
