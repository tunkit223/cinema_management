package com.theatermgnt.theatermgnt.movie.repository;

import com.theatermgnt.theatermgnt.movie.entity.AgeRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgeRatingRepository extends JpaRepository<AgeRating, String> {
    Optional<AgeRating> findByCode(String code);
}
