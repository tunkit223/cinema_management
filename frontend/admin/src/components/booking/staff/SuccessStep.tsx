import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { CheckCircle, Download, Printer } from "lucide-react"
import type { Seat, ComboItem } from "../../../lib/types"

interface SuccessStepProps {
  bookingId: string
  selectedSeats: Seat[]
  selectedCombos: ComboItem[]
  total: number
  onNewBooking: () => void
}

export default function SuccessStep({
  bookingId,
  selectedSeats,
  selectedCombos,
  total,
  onNewBooking,
}: SuccessStepProps) {
  return (
    <div className="space-y-6">
      <div className="flex justify-center">
        <CheckCircle className="w-16 h-16 text-green-600" />
      </div>

      <div className="text-center">
        <h2 className="text-2xl font-bold mb-2">Booking Successful!</h2>
        <p className="text-gray-600">Your ticket booking has been completed</p>
      </div>

      <Card className="p-8 bg-gradient-to-r from-green-50 to-blue-50">
        <div className="max-w-md mx-auto space-y-6">
          {/* Booking ID */}
          <div className="bg-white rounded-lg p-4 border-2 border-green-200">
            <p className="text-sm text-gray-600 mb-2">Booking ID</p>
            <p className="font-mono text-xl font-bold text-green-700">{bookingId}</p>
          </div>

          {/* Summary */}
          <div className="space-y-4">
            <div>
              <p className="text-sm font-semibold text-gray-600 mb-2">Tickets</p>
              <div className="bg-white rounded-lg p-3 space-y-1">
                {selectedSeats
                  .sort((a, b) => {
                    if (a.row === b.row) {
                      return a.number - b.number
                    }
                    return a.row.localeCompare(b.row)
                  })
                  .map((seat) => (
                    <div key={seat.id} className="text-sm flex justify-between">
                      <span>Seat {seat.row}{seat.number}</span>
                      <span className="font-semibold">{(seat.price || 0).toLocaleString()} VND</span>
                    </div>
                  ))}
              </div>
            </div>

            {selectedCombos.length > 0 && (
              <div>
                <p className="text-sm font-semibold text-gray-600 mb-2">Combos</p>
                <div className="bg-white rounded-lg p-3 space-y-1">
                  {selectedCombos.map((combo) => (
                    <div key={combo.id} className="text-sm flex justify-between">
                      <span>{combo.name} × {combo.quantity}</span>
                      <span className="font-semibold">
                        {(combo.price * (combo.quantity || 1)).toLocaleString()} VND
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Total */}
          <div className="bg-white rounded-lg p-4 border-2 border-blue-200">
            <div className="flex justify-between items-center">
              <span className="font-semibold text-gray-700">Total Paid</span>
              <span className="text-2xl font-bold text-blue-600">
                {total.toLocaleString()} VND
              </span>
            </div>
          </div>

          {/* Actions */}
          <div className="space-y-2 pt-4">
            <button className="w-full flex items-center justify-center gap-2 px-4 py-2 border rounded-lg hover:bg-gray-50 transition-colors text-gray-700 font-semibold">
              <Printer className="w-4 h-4" />
              Print Ticket
            </button>
            <button className="w-full flex items-center justify-center gap-2 px-4 py-2 border rounded-lg hover:bg-gray-50 transition-colors text-gray-700 font-semibold">
              <Download className="w-4 h-4" />
              Download Receipt
            </button>
          </div>

          {/* New Booking Button */}
          <Button
            onClick={onNewBooking}
            className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3"
          >
            Create New Booking
          </Button>
        </div>
      </Card>

      <div className="text-center text-sm text-gray-600">
        <p>Booking reference has been sent to the customer email</p>
      </div>
    </div>
  )
}
