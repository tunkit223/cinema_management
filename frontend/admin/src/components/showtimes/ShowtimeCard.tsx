import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Clock } from "lucide-react";
import type { Showtime } from "@/types/showtime";
import { SeatProgressBar } from "./SeatProgressBar";

interface ShowtimeCardProps {
  showtime: Showtime;
  onEdit?: (showtime: Showtime) => void;
  onViewDetails?: (showtime: Showtime) => void;
}

export function ShowtimeCard({
  showtime,
  onEdit,
  onViewDetails,
}: ShowtimeCardProps) {
  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  return (
    <Card className="overflow-hidden transition-shadow hover:shadow-md">
      <CardContent className="p-0">
        <div className="space-y-4 p-5">
          {/* Header */}
          <div>
            <h3 className="text-lg font-semibold text-gray-900">
              {showtime.movieTitle}
            </h3>
            <div className="mt-1 flex items-center gap-1.5 text-sm text-blue-600">
              <Clock className="h-4 w-4" />
              <span>
                {formatDate(showtime.date)} • {showtime.time}
              </span>
            </div>
          </div>

          {/* Info Grid */}
          <div className="space-y-2.5">
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">Phòng chiếu</span>
              <span className="font-medium">{showtime.room}</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">Giá vé</span>
              <span className="font-medium text-blue-600">
                {formatPrice(showtime.price)}
              </span>
            </div>

            {/* Seat Progress Bar */}
            <SeatProgressBar
              availableSeats={showtime.availableSeats}
              totalSeats={showtime.totalSeats}
            />
          </div>

          {/* Action Buttons */}
          <div className="grid grid-cols-2 gap-2.5 pt-2">
            <Button
              variant="outline"
              className="w-full"
              onClick={() => onEdit?.(showtime)}
            >
              Chỉnh sửa
            </Button>
            <Button
              variant="default"
              className="w-full"
              onClick={() => onViewDetails?.(showtime)}
            >
              Xem chi tiết
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
