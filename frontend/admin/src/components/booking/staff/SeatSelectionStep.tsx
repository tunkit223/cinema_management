import type { Seat, Showtime } from "../../../lib/types"
import { useEffect, useState } from "react"

interface SeatSelectionStepProps {
  seats: Seat[]
  selectedSeats: Seat[]
  onSelectSeats: (seats: Seat[]) => void
  showtime: Showtime & { roomName: string; cinemaName: string }
  loading: boolean
  error: string | null
  customerName: string
  customerEmail: string
  onCustomerNameChange: (value: string) => void
  onCustomerEmailChange: (value: string) => void
}

export default function SeatSelectionStep({
  seats,
  selectedSeats,
  onSelectSeats,
  showtime,
  loading,
  error,
  customerName,
  customerEmail,
  onCustomerNameChange,
  onCustomerEmailChange,
}: SeatSelectionStepProps) {
  const [groupedByRow, setGroupedByRow] = useState<Record<string, Seat[]>>({})

  useEffect(() => {
    const grouped = seats.reduce((acc, seat) => {
      if (!acc[seat.row]) {
        acc[seat.row] = []
      }
      acc[seat.row].push(seat)
      return acc
    }, {} as Record<string, Seat[]>)

    // Sort each row by seat number
    Object.keys(grouped).forEach(row => {
      grouped[row].sort((a, b) => a.number - b.number)
    })

    setGroupedByRow(grouped)
  }, [seats])

  const handleSeatClick = (seat: Seat) => {
    if (!seat.isAvailable) return

    const isSelected = selectedSeats.some((s) => s.id === seat.id)
    if (isSelected) {
      onSelectSeats(selectedSeats.filter((s) => s.id !== seat.id))
    } else {
      onSelectSeats([...selectedSeats, seat])
    }
  }

  const getSeatColor = (seat: Seat) => {
    const isSelected = selectedSeats.some((s) => s.id === seat.id)

    if (!seat.isAvailable) {
      return "bg-red-500 dark:bg-red-600 cursor-not-allowed opacity-60"
    }

    if (isSelected) {
      return "bg-blue-600 dark:bg-blue-500 text-white"
    }

    switch (seat.type) {
      case "vip":
        return "bg-amber-400 dark:bg-amber-500 hover:bg-amber-500 dark:hover:bg-amber-600 text-gray-900 font-bold"
      case "couple":
        return "bg-pink-400 dark:bg-pink-500 hover:bg-pink-500 dark:hover:bg-pink-600 text-white font-bold"
      default:
        return "bg-gray-200 dark:bg-gray-400 hover:bg-gray-300 dark:hover:bg-gray-500 text-gray-900 font-bold"
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-center">
          <div className="inline-block w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mb-4"></div>
          <p className="text-gray-600">Loading seats...</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-6 text-center">
        <p className="text-red-800 font-semibold">Error: {error}</p>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-bold mb-2">Select Seats</h2>
        <p className="text-gray-600 text-sm">
          {showtime.roomName} • {new Date(showtime.time).toLocaleString()}
        </p>
      </div>

      {/* Legend */}
      <div className="bg-gray-50 rounded-lg p-4 flex flex-wrap gap-4 justify-center">
        <div className="flex items-center gap-2">
          <div className="w-6 h-6 bg-gray-200 rounded text-xs"></div>
          <span className="text-sm">Standard</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-6 h-6 bg-amber-400 rounded text-xs"></div>
          <span className="text-sm">VIP</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-6 h-6 bg-pink-400 rounded text-xs"></div>
          <span className="text-sm">Couple</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-6 h-6 bg-red-500 rounded text-xs"></div>
          <span className="text-sm">Booked</span>
        </div>
      </div>

      {/* Screen */}
      <div className="text-center">
        <div className="inline-block bg-gray-800 text-white px-12 py-2 rounded-t-3xl text-sm font-semibold">
          SCREEN
        </div>
      </div>

      {/* Seats */}
      <div className="flex justify-center overflow-x-auto pb-4">
        <div className="inline-block space-y-3">
          {Object.entries(groupedByRow).map(([row, rowSeats]) => (
            <div key={row} className="flex items-center gap-3">
              <div className="w-6 text-center font-bold text-gray-600 text-sm">{row}</div>
              <div className="flex gap-2">
                {rowSeats.map((seat) => (
                  <button
                    key={seat.id}
                    onClick={() => handleSeatClick(seat)}
                    className={`
                      w-8 h-8 rounded text-xs font-bold transition-all
                      ${getSeatColor(seat)}
                      ${seat.isAvailable && !selectedSeats.some(s => s.id === seat.id) ? "cursor-pointer" : ""}
                    `}
                    disabled={!seat.isAvailable}
                    title={`Seat ${seat.row}${seat.number}`}
                  >
                    {seat.number}
                  </button>
                ))}
              </div>
              <div className="w-6 text-center font-bold text-gray-600 text-sm">{row}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Selected Seats Summary */}
      {selectedSeats.length > 0 && (
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
          <p className="text-sm font-semibold text-blue-900 mb-2">
            Selected ({selectedSeats.length}):
          </p>
          <div className="flex flex-wrap gap-2">
            {selectedSeats
              .sort((a, b) => {
                if (a.row === b.row) {
                  return a.number - b.number
                }
                return a.row.localeCompare(b.row)
              })
              .map((seat) => (
                <span key={seat.id} className="inline-block bg-blue-600 text-white px-2 py-1 rounded text-sm">
                  {seat.row}{seat.number}
                </span>
              ))}
          </div>
        </div>
      )}

      {/* Customer info */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 bg-gray-50 border border-gray-200 rounded-lg p-4">
        <div className="space-y-2">
          <label className="text-sm font-medium text-gray-700">Customer Name</label>
          <input
            type="text"
            value={customerName}
            onChange={(e) => onCustomerNameChange(e.target.value)}
            placeholder="Enter customer full name"
            className="w-full rounded-md border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <div className="space-y-2">
          <label className="text-sm font-medium text-gray-700">Customer Email</label>
          <input
            type="email"
            value={customerEmail}
            onChange={(e) => onCustomerEmailChange(e.target.value)}
            placeholder="example@email.com"
            className="w-full rounded-md border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
      </div>
    </div>
  )
}
