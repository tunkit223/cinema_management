import type { Showtime } from "./types"

// Mock Cinema Data
export const mockCinemas = [
  {
    id: "cinema-1",
    name: "CINEPLEX Downtown",
    location: "123 Main St, City Center",
    phone: "0912-123-456",
  },
  {
    id: "cinema-2",
    name: "CINEPLEX East",
    location: "456 Park Ave, East District",
    phone: "0912-789-012",
  },
  {
    id: "cinema-3",
    name: "CINEPLEX West Mall",
    location: "789 Shopping Blvd, West Side",
    phone: "0912-345-678",
  }
]

// Mock Showtimes - Lịch chiếu cho các phim
export const mockShowtimes: Showtime[] = [
  // Movie ID "76cacceb-291c-41ac-8333-c11b9ff5566d" - From Backend API
  {
    id: "show-uuid-1",
    movieId: "76cacceb-291c-41ac-8333-c11b9ff5566d",
    time: "10:00",
    date: "2025-12-23",
    format: "2D",
    price: 50000,
    availableSeats: 48,
    roomId: "room-1",
    cinemaId: "cinema-1"
  },
  {
    id: "show-uuid-2",
    movieId: "76cacceb-291c-41ac-8333-c11b9ff5566d",
    time: "13:30",
    date: "2025-12-23",
    format: "3D",
    price: 65000,
    availableSeats: 36,
    roomId: "room-2",
    cinemaId: "cinema-1"
  },
  {
    id: "show-uuid-3",
    movieId: "76cacceb-291c-41ac-8333-c11b9ff5566d",
    time: "16:45",
    date: "2025-12-23",
    format: "IMAX",
    price: 85000,
    availableSeats: 24,
    roomId: "room-1",
    cinemaId: "cinema-2"
  },
  {
    id: "show-uuid-4",
    movieId: "76cacceb-291c-41ac-8333-c11b9ff5566d",
    time: "20:00",
    date: "2025-12-23",
    format: "2D",
    price: 55000,
    availableSeats: 52,
    roomId: "room-3",
    cinemaId: "cinema-3"
  },
  {
    id: "show-uuid-5",
    movieId: "76cacceb-291c-41ac-8333-c11b9ff5566d",
    time: "09:30",
    date: "2025-12-24",
    format: "2D",
    price: 50000,
    availableSeats: 60,
    roomId: "room-1",
    cinemaId: "cinema-1"
  },
  {
    id: "show-uuid-6",
    movieId: "76cacceb-291c-41ac-8333-c11b9ff5566d",
    time: "14:15",
    date: "2025-12-24",
    format: "3D",
    price: 65000,
    availableSeats: 42,
    roomId: "room-2",
    cinemaId: "cinema-2"
  },
  {
    id: "show-uuid-7",
    movieId: "76cacceb-291c-41ac-8333-c11b9ff5566d",
    time: "18:30",
    date: "2025-12-24",
    format: "IMAX",
    price: 85000,
    availableSeats: 28,
    roomId: "room-1",
    cinemaId: "cinema-3"
  },
  {
    id: "show-uuid-8",
    movieId: "76cacceb-291c-41ac-8333-c11b9ff5566d",
    time: "22:00",
    date: "2025-12-24",
    format: "2D",
    price: 55000,
    availableSeats: 45,
    roomId: "room-3",
    cinemaId: "cinema-1"
  },

  // Movie ID "1" - The Quantum Paradox
  {
    id: "show-1-1",
    movieId: "1",
    time: "10:00",
    date: "2025-12-23",
    format: "2D",
    price: 50000,
    availableSeats: 48,
    roomId: "room-1",
    cinemaId: "cinema-1"
  },
  {
    id: "show-1-2",
    movieId: "1",
    time: "13:30",
    date: "2025-12-23",
    format: "3D",
    price: 65000,
    availableSeats: 36,
    roomId: "room-2",
    cinemaId: "cinema-1"
  },
  {
    id: "show-1-3",
    movieId: "1",
    time: "16:45",
    date: "2025-12-23",
    format: "IMAX",
    price: 85000,
    availableSeats: 24,
    roomId: "room-1",
    cinemaId: "cinema-2"
  },
  {
    id: "show-1-4",
    movieId: "1",
    time: "20:00",
    date: "2025-12-23",
    format: "2D",
    price: 55000,
    availableSeats: 52,
    roomId: "room-3",
    cinemaId: "cinema-3"
  },
  {
    id: "show-1-5",
    movieId: "1",
    time: "09:30",
    date: "2025-12-24",
    format: "2D",
    price: 50000,
    availableSeats: 60,
    roomId: "room-1",
    cinemaId: "cinema-1"
  },
  {
    id: "show-1-6",
    movieId: "1",
    time: "14:15",
    date: "2025-12-24",
    format: "3D",
    price: 65000,
    availableSeats: 42,
    roomId: "room-2",
    cinemaId: "cinema-2"
  },
  {
    id: "show-1-7",
    movieId: "1",
    time: "18:30",
    date: "2025-12-24",
    format: "IMAX",
    price: 85000,
    availableSeats: 28,
    roomId: "room-1",
    cinemaId: "cinema-3"
  },
  {
    id: "show-1-8",
    movieId: "1",
    time: "22:00",
    date: "2025-12-24",
    format: "2D",
    price: 55000,
    availableSeats: 45,
    roomId: "room-3",
    cinemaId: "cinema-1"
  },

  // Movie ID "2" - Echoes of Tomorrow
  {
    id: "show-2-1",
    movieId: "2",
    time: "11:00",
    date: "2025-12-23",
    format: "2D",
    price: 50000,
    availableSeats: 55,
    roomId: "room-2",
    cinemaId: "cinema-1"
  },
  {
    id: "show-2-2",
    movieId: "2",
    time: "15:30",
    date: "2025-12-23",
    format: "2D",
    price: 50000,
    availableSeats: 38,
    roomId: "room-1",
    cinemaId: "cinema-2"
  },
  {
    id: "show-2-3",
    movieId: "2",
    time: "19:00",
    date: "2025-12-23",
    format: "3D",
    price: 65000,
    availableSeats: 44,
    roomId: "room-3",
    cinemaId: "cinema-3"
  },
  {
    id: "show-2-4",
    movieId: "2",
    time: "10:30",
    date: "2025-12-24",
    format: "2D",
    price: 50000,
    availableSeats: 62,
    roomId: "room-2",
    cinemaId: "cinema-1"
  },
  {
    id: "show-2-5",
    movieId: "2",
    time: "16:00",
    date: "2025-12-24",
    format: "3D",
    price: 65000,
    availableSeats: 40,
    roomId: "room-1",
    cinemaId: "cinema-2"
  },

  // Movie ID "3" - Shadow Protocol
  {
    id: "show-3-1",
    movieId: "3",
    time: "12:00",
    date: "2025-12-23",
    format: "2D",
    price: 55000,
    availableSeats: 50,
    roomId: "room-1",
    cinemaId: "cinema-1"
  },
  {
    id: "show-3-2",
    movieId: "3",
    time: "17:00",
    date: "2025-12-23",
    format: "IMAX",
    price: 90000,
    availableSeats: 32,
    roomId: "room-2",
    cinemaId: "cinema-2"
  },
  {
    id: "show-3-3",
    movieId: "3",
    time: "21:30",
    date: "2025-12-23",
    format: "3D",
    price: 70000,
    availableSeats: 26,
    roomId: "room-3",
    cinemaId: "cinema-3"
  },
  {
    id: "show-3-4",
    movieId: "3",
    time: "13:00",
    date: "2025-12-24",
    format: "2D",
    price: 55000,
    availableSeats: 58,
    roomId: "room-1",
    cinemaId: "cinema-1"
  },

  // Movie ID "4" - Laughter & Chaos
  {
    id: "show-4-1",
    movieId: "4",
    time: "10:30",
    date: "2025-12-23",
    format: "2D",
    price: 45000,
    availableSeats: 70,
    roomId: "room-3",
    cinemaId: "cinema-1"
  },
  {
    id: "show-4-2",
    movieId: "4",
    time: "14:00",
    date: "2025-12-23",
    format: "2D",
    price: 45000,
    availableSeats: 65,
    roomId: "room-2",
    cinemaId: "cinema-2"
  },
  {
    id: "show-4-3",
    movieId: "4",
    time: "18:00",
    date: "2025-12-23",
    format: "2D",
    price: 45000,
    availableSeats: 48,
    roomId: "room-1",
    cinemaId: "cinema-3"
  },
  {
    id: "show-4-4",
    movieId: "4",
    time: "11:30",
    date: "2025-12-24",
    format: "2D",
    price: 45000,
    availableSeats: 72,
    roomId: "room-3",
    cinemaId: "cinema-1"
  },

  // Movie ID "5" - The Last Guardian
  {
    id: "show-5-1",
    movieId: "5",
    time: "09:00",
    date: "2025-12-23",
    format: "3D",
    price: 70000,
    availableSeats: 40,
    roomId: "room-1",
    cinemaId: "cinema-1"
  },
  {
    id: "show-5-2",
    movieId: "5",
    time: "13:00",
    date: "2025-12-23",
    format: "IMAX",
    price: 90000,
    availableSeats: 28,
    roomId: "room-2",
    cinemaId: "cinema-2"
  },
  {
    id: "show-5-3",
    movieId: "5",
    time: "17:30",
    date: "2025-12-23",
    format: "3D",
    price: 70000,
    availableSeats: 35,
    roomId: "room-3",
    cinemaId: "cinema-3"
  },
  {
    id: "show-5-4",
    movieId: "5",
    time: "21:00",
    date: "2025-12-23",
    format: "2D",
    price: 55000,
    availableSeats: 52,
    roomId: "room-1",
    cinemaId: "cinema-1"
  },
  {
    id: "show-5-5",
    movieId: "5",
    time: "12:00",
    date: "2025-12-24",
    format: "3D",
    price: 70000,
    availableSeats: 44,
    roomId: "room-2",
    cinemaId: "cinema-2"
  },
  {
    id: "show-5-6",
    movieId: "5",
    time: "19:30",
    date: "2025-12-24",
    format: "IMAX",
    price: 90000,
    availableSeats: 30,
    roomId: "room-3",
    cinemaId: "cinema-3"
  },
]

// Hook to use mock booking data
export function useMockBookingData() {
  const getShowtimes = (movieId: string): Showtime[] => {
    return mockShowtimes.filter(st => st.movieId === movieId)
  }

  const getShowtimeById = (showtimeId: string): Showtime | undefined => {
    return mockShowtimes.find(st => st.id === showtimeId)
  }

  const getCinema = (cinemaId: string) => {
    return mockCinemas.find(c => c.id === cinemaId)
  }

  return {
    getShowtimes,
    getShowtimeById,
    getCinema,
    mockShowtimes,
    mockCinemas
  }
}

// Format currency to VND
export function formatCurrency(amount: number): string {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount)
}

// Format date to readable format
export function formatDate(dateString: string): string {
  const date = new Date(dateString)
  const today = new Date()
  const tomorrow = new Date(today)
  tomorrow.setDate(tomorrow.getDate() + 1)

  // Check if it's today
  if (date.toDateString() === today.toDateString()) {
    return "Today"
  }

  // Check if it's tomorrow
  if (date.toDateString() === tomorrow.toDateString()) {
    return "Tomorrow"
  }

  // Otherwise return formatted date
  return date.toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
