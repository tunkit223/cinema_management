export interface Movie {
  id: string;
  title: string;
  description: string;
  duration: number; // in minutes
  director: string;
  cast: string;
  posterUrl?: string;
  trailerUrl?: string;
  releaseDate: string;
  status: MovieStatus;
}

export const MovieStatus = {
  NOW_SHOWING: "NOW_SHOWING",
  COMING_SOON: "COMING_SOON",
  ENDED: "ENDED",
} as const;

export type MovieStatus = typeof MovieStatus[keyof typeof MovieStatus];
