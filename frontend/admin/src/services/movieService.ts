import httpClient from "@/configurations/httpClient";
import type { Movie } from "@/types/MovieType/movieType";
import { handleApiResponse, type ApiResponse } from "@/utils/apiResponse";

const BASE_URL = "/movies";

// Re-export Movie type for convenience
export type { Movie };

// Get all movies
export const getAllMovies = async (): Promise<Movie[]> => {
  const response = await httpClient.get<ApiResponse<any[]>>(BASE_URL);
  const movies = response.data.result || [];
  console.log("Raw movie data from API:", movies[0]);
  
  // Map backend response to Movie type
  return movies.map((m: any) => ({
    id: m.id,
    title: m.title,
    description: m.description,
    duration: m.duration || m.durationMinutes || 0, // Handle different field names
    director: m.director,
    cast: m.cast,
    posterUrl: m.posterUrl,
    trailerUrl: m.trailerUrl,
    releaseDate: m.releaseDate,
    status: m.status,
  }));
};

// Get now showing movies
export const getNowShowingMovies = async (): Promise<Movie[]> => {
  const response = await httpClient.get<ApiResponse<any[]>>(`${BASE_URL}/now-showing`);
  const movies = response.data.result || [];
  console.log("Raw now-showing movie data:", movies[0]);
  
  return movies.map((m: any) => ({
    id: m.id,
    title: m.title,
    description: m.description,
    duration: m.duration || m.durationMinutes || 0,
    director: m.director,
    cast: m.cast,
    posterUrl: m.posterUrl,
    trailerUrl: m.trailerUrl,
    releaseDate: m.releaseDate,
    status: m.status,
  }));
};

// Get movie by ID
export const getMovieById = async (movieId: string): Promise<Movie> => {
  const response = await httpClient.get<ApiResponse<any>>(`${BASE_URL}/${movieId}`);
  const m = response.data.result;
  console.log("Raw single movie data:", m);
  
  return {
    id: m.id,
    title: m.title,
    description: m.description,
    duration: m.duration || m.durationMinutes || 0,
    director: m.director,
    cast: m.cast,
    posterUrl: m.posterUrl,
    trailerUrl: m.trailerUrl,
    releaseDate: m.releaseDate,
    status: m.status,
  };
};
