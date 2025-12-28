import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Badge } from "@/components/ui/badge";
import { Film, MapPin, Clock, Calendar, DoorOpen } from "lucide-react";
import { format } from "date-fns";
import { vi } from "date-fns/locale";
import type { ShowtimeResponse } from "@/services/showtimeService";

interface ShowtimeDetailModalProps {
  showtime: ShowtimeResponse | null;
  open: boolean;
  onClose: () => void;
}

export function ShowtimeDetailModal({
  showtime,
  open,
  onClose,
}: ShowtimeDetailModalProps) {
  if (!showtime) return null;

  const startTime = new Date(showtime.startTime);
  const endTime = new Date(showtime.endTime);
  const duration = Math.round((endTime.getTime() - startTime.getTime()) / (1000 * 60));

  // Status display
  const statusConfig = {
    SCHEDULED: { label: "Scheduled", color: "bg-blue-100 text-blue-700" },
    ONGOING: { label: "Ongoing", color: "bg-green-100 text-green-700" },
    COMPLETED: { label: "Completed", color: "bg-gray-100 text-gray-600" },
  };

  const status = statusConfig[showtime.status] || statusConfig.SCHEDULED;

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <Film className="h-5 w-5" />
            Showtime Details
          </DialogTitle>
        </DialogHeader>

        <div className="space-y-6">
          {/* Movie Title */}
          <div>
            <h3 className="text-2xl font-bold text-gray-900">
              {showtime.movieName}
            </h3>
            <Badge className={`mt-2 ${status.color}`}>
              {status.label}
            </Badge>
          </div>

          {/* Details Grid */}
          <div className="grid gap-4 md:grid-cols-2">
            {/* Cinema */}
            <div className="flex items-start gap-3 rounded-lg border border-gray-200 bg-gray-50 p-4">
              <MapPin className="mt-0.5 h-5 w-5 shrink-0 text-gray-600" />
              <div>
                <p className="text-sm font-medium text-gray-600">Cinema</p>
                <p className="mt-1 font-semibold text-gray-900">
                  {showtime.cinemaName}
                </p>
              </div>
            </div>

            {/* Room */}
            <div className="flex items-start gap-3 rounded-lg border border-gray-200 bg-gray-50 p-4">
              <DoorOpen className="mt-0.5 h-5 w-5 shrink-0 text-gray-600" />
              <div>
                <p className="text-sm font-medium text-gray-600">Room</p>
                <p className="mt-1 font-semibold text-gray-900">
                  {showtime.roomName}
                </p>
              </div>
            </div>

            {/* Date */}
            <div className="flex items-start gap-3 rounded-lg border border-gray-200 bg-gray-50 p-4">
              <Calendar className="mt-0.5 h-5 w-5 shrink-0 text-gray-600" />
              <div>
                <p className="text-sm font-medium text-gray-600">Date</p>
                <p className="mt-1 font-semibold text-gray-900">
                  {format(startTime, "EEEE, dd/MM/yyyy", { locale: vi })}
                </p>
              </div>
            </div>

            {/* Time */}
            <div className="flex items-start gap-3 rounded-lg border border-gray-200 bg-gray-50 p-4">
              <Clock className="mt-0.5 h-5 w-5 shrink-0 text-gray-600" />
              <div>
                <p className="text-sm font-medium text-gray-600">Time</p>
                <p className="mt-1 font-semibold text-gray-900">
                  {format(startTime, "HH:mm")} - {format(endTime, "HH:mm")}
                </p>
                <p className="text-sm text-gray-500">
                  ({duration} minutes)
                </p>
              </div>
            </div>
          </div>

          {/* Additional Information */}
          <div className="rounded-lg border border-gray-200 bg-gray-50 p-4">
            <h4 className="mb-3 font-semibold text-gray-900">
              Additional Information
            </h4>
            <div className="grid gap-2 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-600">Showtime ID:</span>
                <span className="font-mono font-medium text-gray-900">
                  {showtime.id}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Movie ID:</span>
                <span className="font-mono font-medium text-gray-900">
                  {showtime.movieId}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Room ID:</span>
                <span className="font-mono font-medium text-gray-900">
                  {showtime.roomId}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Cinema ID:</span>
                <span className="font-mono font-medium text-gray-900">
                  {showtime.cinemaId}
                </span>
              </div>
            </div>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
