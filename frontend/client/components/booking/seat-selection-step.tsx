"use client"

import type { Seat } from "@/lib/types"

interface SeatSelectionStepProps {
  seats: Seat[]
  selectedSeats: Seat[]
  onSelectSeats: (seats: Seat[]) => void
  seatPrice: number
}

export default function SeatSelectionStep({ seats, selectedSeats, onSelectSeats, seatPrice }: SeatSelectionStepProps) {
  const handleSeatClick = (seat: Seat) => {
    if (!seat.isAvailable) return

    const isSelected = selectedSeats.some((s) => s.id === seat.id)
    if (isSelected) {
      onSelectSeats(selectedSeats.filter((s) => s.id !== seat.id))
    } else {
      onSelectSeats([...selectedSeats, seat])
    }
  }

  const rows = Array.from(new Set(seats.map((s) => s.row)))

  return (
    <div className="bg-card dark:bg-slate-900 border border-border dark:border-slate-800 rounded-xl p-8">
      <h2 className="text-2xl font-bold mb-8">Select Your Seats</h2>

      {/* Screen */}
      <div className="mb-12 text-center">
        <div className="inline-block w-full max-w-2xl h-2 bg-gradient-to-r from-purple-600 to-pink-600 rounded-full mb-8 relative">
          <p className="absolute -bottom-8 left-1/2 transform -translate-x-1/2 text-sm font-semibold text-muted-foreground">
            SCREEN
          </p>
        </div>
      </div>

      {/* Seats Grid */}
      <div className="space-y-4 mb-12">
        {rows.map((row) => (
          <div key={row} className="flex items-center justify-center gap-2">
            <span className="w-8 font-bold text-muted-foreground">{row}</span>
            <div className="flex gap-2 flex-wrap justify-center">
              {seats
                .filter((s) => s.row === row)
                .map((seat) => {
                  const isSelected = selectedSeats.some((s) => s.id === seat.id)
                  return (
                    <button
                      key={seat.id}
                      onClick={() => handleSeatClick(seat)}
                      disabled={!seat.isAvailable}
                      className={`w-8 h-8 rounded transition-all ${
                        !seat.isAvailable
                          ? "bg-muted dark:bg-slate-700 cursor-not-allowed opacity-50"
                          : isSelected
                            ? "bg-purple-600 text-white"
                            : "bg-muted dark:bg-slate-700 hover:bg-purple-500/50"
                      }`}
                      title={`Seat ${seat.id}`}
                    >
                      {seat.number}
                    </button>
                  )
                })}
            </div>
          </div>
        ))}
      </div>

      {/* Legend */}
      <div className="flex flex-wrap gap-6 justify-center pt-8 border-t border-border dark:border-slate-800">
        <div className="flex items-center gap-2">
          <div className="w-6 h-6 rounded bg-muted dark:bg-slate-700" />
          <span className="text-sm text-muted-foreground">Available</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-6 h-6 rounded bg-purple-600" />
          <span className="text-sm text-muted-foreground">Selected</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-6 h-6 rounded bg-muted dark:bg-slate-700 opacity-50" />
          <span className="text-sm text-muted-foreground">Booked</span>
        </div>
      </div>

      {selectedSeats.length > 0 && (
        <div className="mt-8 p-4 bg-purple-500/10 dark:bg-purple-900/20 border border-purple-500/30 rounded-lg">
          <p className="text-sm">
            <span className="font-semibold">Selected Seats:</span> {selectedSeats.map((s) => s.id).join(", ")}
          </p>
          <p className="text-sm mt-2">
            <span className="font-semibold">Price per seat:</span> {seatPrice.toLocaleString()} VND
          </p>
        </div>
      )}
    </div>
  )
}
