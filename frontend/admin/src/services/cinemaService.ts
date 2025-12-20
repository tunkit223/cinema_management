import httpClient from "@/configurations/httpClient";
import type { Cinema } from "@/types/CinemaType/cinemaType";
import { handleApiResponse, type ApiResponse } from "@/utils/apiResponse";
import { CONFIG } from "@/configurations/configuration";

// Re-export Cinema type for convenience
export type { Cinema };

export interface CreateCinemaRequest {
  name: string;
  address?: string;
  phone?: string;
}

export interface UpdateCinemaRequest {
  name?: string;
  address?: string;
  phone?: string;
}

// Get all cinemas for selection
export const getAllCinemas = async (): Promise<Cinema[]> => {
  return handleApiResponse<Cinema[]>(
    httpClient.get<ApiResponse<Cinema[]>>("/cinemas")
  );
};

export const getCinemaById = async (cinemaId: string) => {
  return await httpClient.get(`${CONFIG.API}/cinemas/${cinemaId}`);
};

export const createCinema = async (data: CreateCinemaRequest) => {
  return await httpClient.post(`${CONFIG.API}/cinemas`, data);
};

export const updateCinema = async (cinemaId: string, data: UpdateCinemaRequest) => {
  return await httpClient.put(`${CONFIG.API}/cinemas/${cinemaId}`, data);
};

export const deleteCinema = async (cinemaId: string) => {
  return await httpClient.delete(`${CONFIG.API}/cinemas/${cinemaId}`);
};