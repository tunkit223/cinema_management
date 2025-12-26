package com.theatermgnt.theatermgnt.screening.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.theatermgnt.theatermgnt.screening.dto.request.ScreeningCreationRequest;
import com.theatermgnt.theatermgnt.screening.dto.request.ScreeningUpdateRequest;
import com.theatermgnt.theatermgnt.screening.dto.response.ScreeningResponse;
import com.theatermgnt.theatermgnt.screening.entity.Screening;

@Mapper(componentModel = "spring")
public interface ScreeningMapper {
    Screening toScreening(ScreeningCreationRequest request);

    @Mapping(target = "movieId", source = "movie.id")
    @Mapping(target = "movieName", source = "movie.title")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomName", source = "room.name")
    @Mapping(target = "cinemaId", source = "room.cinema.id")
    @Mapping(target = "cinemaName", source = "room.cinema.name")
    ScreeningResponse toScreeningResponse(Screening screening);

    void updateScreening(@MappingTarget Screening screening, ScreeningUpdateRequest request);
}
