import { Search } from "lucide-react";
import { Input } from "@/components/ui/input";
import type { ShowtimeFilters } from "@/types/showtime";

interface ShowtimeFiltersProps {
  filters: ShowtimeFilters;
  onFiltersChange: (filters: ShowtimeFilters) => void;
  rooms?: string[];
}

export function ShowtimeFilters({
  filters,
  onFiltersChange,
  rooms = ["Phòng 1", "Phòng 2", "Phòng 3"],
}: ShowtimeFiltersProps) {
  const handleRoomChange = (room: string) => {
    onFiltersChange({
      ...filters,
      room: filters.room === room ? undefined : room,
    });
  };

  const handleDateChange = (date: string) => {
    onFiltersChange({
      ...filters,
      date,
    });
  };

  const handleSearchChange = (searchQuery: string) => {
    onFiltersChange({
      ...filters,
      searchQuery,
    });
  };

  return (
    <div className="space-y-4">
      {/* Search Bar */}
      <div className="relative">
        <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          type="text"
          placeholder="Tìm kiếm phim..."
          value={filters.searchQuery || ""}
          onChange={(e) => handleSearchChange(e.target.value)}
          className="pl-9"
        />
      </div>

      {/* Room and Date Filters */}
      <div className="flex flex-wrap gap-3">
        {/* Room Filters */}
        <div className="flex flex-wrap gap-2">
          {rooms.map((room) => (
            <button
              key={room}
              onClick={() => handleRoomChange(room)}
              className={`rounded-full px-4 py-2 text-sm font-medium transition-colors ${
                filters.room === room
                  ? "bg-blue-600 text-white"
                  : "bg-gray-100 text-gray-700 hover:bg-gray-200"
              }`}
            >
              {room}
            </button>
          ))}
        </div>

        {/* Date Picker */}
        <div className="flex items-center gap-2">
          <Input
            type="date"
            value={filters.date || ""}
            onChange={(e) => handleDateChange(e.target.value)}
            className="w-auto"
          />
        </div>
      </div>

      {/* Active Filters Summary */}
      {(filters.room || filters.date || filters.searchQuery) && (
        <div className="flex items-center gap-2 text-sm text-muted-foreground">
          <span>Đang lọc:</span>
          <div className="flex flex-wrap gap-2">
            {filters.room && (
              <span className="rounded-md bg-blue-50 px-2 py-1 text-blue-700">
                {filters.room}
              </span>
            )}
            {filters.date && (
              <span className="rounded-md bg-blue-50 px-2 py-1 text-blue-700">
                {new Date(filters.date).toLocaleDateString("vi-VN")}
              </span>
            )}
            {filters.searchQuery && (
              <span className="rounded-md bg-blue-50 px-2 py-1 text-blue-700">
                "{filters.searchQuery}"
              </span>
            )}
            <button
              onClick={() =>
                onFiltersChange({
                  room: undefined,
                  date: undefined,
                  searchQuery: undefined,
                })
              }
              className="text-blue-600 hover:underline"
            >
              Xóa bộ lọc
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
