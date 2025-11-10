package com.theatermgnt.theatermgnt.movie.repository;

import com.theatermgnt.theatermgnt.movie.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, String> {
    Optional<Genre> findByName(String name);
}
