import { cn } from "@/lib/utils";

interface SeatProgressBarProps {
  availableSeats: number;
  totalSeats: number;
  className?: string;
}

export function SeatProgressBar({
  availableSeats,
  totalSeats,
  className,
}: SeatProgressBarProps) {
  const percentage = ((totalSeats - availableSeats) / totalSeats) * 100;
  const bookedSeats = totalSeats - availableSeats;

  return (
    <div className={cn("space-y-2", className)}>
      <div className="flex items-center justify-between text-sm">
        <span className="text-muted-foreground">Ghế trống</span>
        <span className="font-medium">
          {availableSeats}/{totalSeats}
        </span>
      </div>
      <div className="relative h-2 w-full overflow-hidden rounded-full bg-gray-200">
        <div
          className="h-full bg-cyan-500 transition-all duration-300"
          style={{ width: `${percentage}%` }}
        />
      </div>
    </div>
  );
}
