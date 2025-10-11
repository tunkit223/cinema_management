import { useState, useMemo } from "react";
import {
  ShowtimeCard,
  ShowtimeFilters,
  AddShowtimeButton,
} from "@/components/showtimes";
import type {
  Showtime,
  ShowtimeFilters as FiltersType,
} from "@/types/showtime";

// Mock data - replace with actual API call
const mockShowtimes: Showtime[] = [
  {
    id: "1",
    movieTitle: "Avatar: The Way of Water",
    date: "2025-01-10",
    time: "09:00",
    room: "Phòng 1",
    price: 80000,
    availableSeats: 45,
    totalSeats: 120,
  },
  {
    id: "2",
    movieTitle: "Avatar: The Way of Water",
    date: "2025-01-10",
    time: "12:30",
    room: "Phòng 2",
    price: 90000,
    availableSeats: 78,
    totalSeats: 150,
  },
  {
    id: "3",
    movieTitle: "Top Gun: Maverick",
    date: "2025-01-10",
    time: "14:00",
    room: "Phòng 1",
    price: 80000,
    availableSeats: 32,
    totalSeats: 120,
  },
  {
    id: "4",
    movieTitle: "The Batman",
    date: "2025-01-10",
    time: "16:30",
    room: "Phòng 3",
    price: 85000,
    availableSeats: 95,
    totalSeats: 100,
  },
];

export function ShowtimeList() {
  const [filters, setFilters] = useState<FiltersType>({});

  // Filter showtimes based on active filters
  const filteredShowtimes = useMemo(() => {
    return mockShowtimes.filter((showtime) => {
      // Room filter
      if (filters.room && showtime.room !== filters.room) {
        return false;
      }

      // Date filter
      if (filters.date && showtime.date !== filters.date) {
        return false;
      }

      // Search filter
      if (filters.searchQuery) {
        const query = filters.searchQuery.toLowerCase();
        if (!showtime.movieTitle.toLowerCase().includes(query)) {
          return false;
        }
      }

      return true;
    });
  }, [filters]);

  const handleEdit = (showtime: Showtime) => {
    console.log("Edit showtime:", showtime);
    // TODO: Implement edit functionality
  };

  const handleViewDetails = (showtime: Showtime) => {
    console.log("View details:", showtime);
    // TODO: Implement view details functionality
  };

  const handleAddShowtime = () => {
    console.log("Add new showtime");
    // TODO: Implement add showtime functionality
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between border-b border-border pb-4">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Lịch chiếu</h1>
          <p className="text-muted-foreground mt-1">
            Quản lý lịch chiếu phim theo phòng và thời gian
          </p>
        </div>
        <AddShowtimeButton onClick={handleAddShowtime} />
      </div>

      {/* Filters */}
      <ShowtimeFilters
        filters={filters}
        onFiltersChange={setFilters}
        rooms={["Phòng 1", "Phòng 2", "Phòng 3"]}
      />

      {/* Showtimes Grid */}
      {filteredShowtimes.length > 0 ? (
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {filteredShowtimes.map((showtime) => (
            <ShowtimeCard
              key={showtime.id}
              showtime={showtime}
              onEdit={handleEdit}
              onViewDetails={handleViewDetails}
            />
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center rounded-lg border-2 border-dashed border-gray-300 p-12 text-center">
          <p className="text-lg font-medium text-gray-900">
            Không tìm thấy lịch chiếu
          </p>
          <p className="mt-1 text-sm text-gray-500">
            Thử thay đổi bộ lọc hoặc thêm lịch chiếu mới
          </p>
        </div>
      )}
    </div>
  );
}
