import { Button } from "@/components/ui/button";
import { Plus } from "lucide-react";

interface AddShowtimeButtonProps {
  onClick?: () => void;
}

export function AddShowtimeButton({ onClick }: AddShowtimeButtonProps) {
  return (
    <Button onClick={onClick} className="gap-2">
      <Plus className="h-4 w-4" />
      Thêm lịch chiếu
    </Button>
  );
}
