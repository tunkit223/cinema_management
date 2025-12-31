import { useState } from "react"
import { Card } from "@/components/ui/card"
import { Building2, CheckCircle2, CreditCard, Smartphone, Wallet } from "lucide-react"
import { format } from "date-fns"
import type { Seat, ComboItem, Showtime } from "../../../lib/types"
import type { Movie } from "@/services/movieService"

interface PaymentStepProps {
  bookingId: string
  total: number
  subtotal: number
  discount: number
  showtime: Showtime & { roomName: string; cinemaName: string }
  movie: Movie
  selectedSeats: Seat[]
  selectedCombos: ComboItem[]
  onPaymentSuccess: () => void
}

export default function PaymentStep({
  bookingId,
  total,
  subtotal,
  discount,
  showtime,
  movie,
  selectedSeats,
  selectedCombos,
  onPaymentSuccess,
}: PaymentStepProps) {
  const [selectedMethod, setSelectedMethod] = useState("cash")
  const [isPaying, setIsPaying] = useState(false)

  const seatTotal = selectedSeats.reduce((sum, seat) => sum + (seat.price || 0), 0)
  const comboTotal = selectedCombos.reduce((sum, combo) => sum + (combo.price * (combo.quantity || 1)), 0)

  const methods = [
    { id: "cash", label: "Cash", description: "Pay at counter", icon: Wallet },
    { id: "card", label: "Credit/Debit Card", description: "Visa, Mastercard, etc", icon: CreditCard },
  ]

  const handlePaymentSimulation = () => {
    if (isPaying) return
    setIsPaying(true)
    setTimeout(() => {
      onPaymentSuccess()
      setIsPaying(false)
    }, 1200)
  }

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm text-gray-500">Payment</p>
          <h2 className="text-2xl font-bold text-gray-900">Complete your booking</h2>
          <p className="text-sm text-gray-500">Booking ID: {bookingId}</p>
        </div>
        <div className="hidden sm:flex items-center gap-2 text-xs text-gray-500">
          <CheckCircle2 className="w-4 h-4 text-emerald-500" />
          Secure checkout
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-[1.2fr_0.8fr]">
        <Card className="p-6 space-y-6 shadow-sm">
          <div>
            <h3 className="text-lg font-semibold text-gray-900">Payment Method</h3>
            <p className="text-sm text-gray-500">Choose how you'd like to pay</p>
          </div>

          <div className="space-y-3">
            {methods.map((method) => {
              const Icon = method.icon
              const isActive = selectedMethod === method.id
              return (
                <button
                  key={method.id}
                  className={`w-full border rounded-lg px-4 py-3 text-left transition flex items-center gap-3 ${
                    isActive ? "border-blue-600 bg-blue-50" : "border-gray-200 hover:border-blue-400"
                  }`}
                  onClick={() => setSelectedMethod(method.id)}
                >
                  <span
                    className={`h-5 w-5 rounded-full border flex items-center justify-center ${
                      isActive ? "border-blue-600 bg-blue-600" : "border-gray-300"
                    }`}
                  >
                    {isActive && <span className="h-2.5 w-2.5 bg-white rounded-full" />}
                  </span>
                  <div className="flex items-center justify-between flex-1">
                    <div className="flex items-center gap-3">
                      <Icon className="w-5 h-5 text-gray-600" />
                      <div>
                        <p className="font-semibold text-gray-900">{method.label}</p>
                        <p className="text-sm text-gray-500">{method.description}</p>
                      </div>
                    </div>
                    {isActive && <CheckCircle2 className="w-5 h-5 text-blue-600" />}
                  </div>
                </button>
              )
            })}
          </div>
        </Card>

        <Card className="p-6 space-y-4 shadow-sm">
          <div>
            <h3 className="text-lg font-semibold text-gray-900">Order Summary</h3>
            <p className="text-sm text-gray-500">Review your selection before paying</p>
          </div>

          <div className="space-y-2 text-sm">
            <div className="flex items-start justify-between">
              <div>
                <p className="font-semibold text-gray-900">{movie.title}</p>
                <p className="text-gray-500">{format(new Date(showtime.time), "PPpp")}</p>
                <p className="text-gray-500">{showtime.cinemaName} • {showtime.roomName}</p>
              </div>
            </div>
          </div>

          <div className="border-t pt-4 space-y-3 text-sm">
            <div className="flex justify-between">
              <span className="text-gray-600">Seats ({selectedSeats.length})</span>
              <span className="font-semibold text-gray-900">{seatTotal.toLocaleString()} VND</span>
            </div>
            <div className="flex flex-wrap gap-2 text-gray-700">
              {selectedSeats
                .slice()
                .sort((a, b) => {
                  if (a.row === b.row) return a.number - b.number
                  return a.row.localeCompare(b.row)
                })
                .map((seat) => (
                  <span key={seat.id} className="px-2 py-1 rounded bg-gray-100 font-semibold text-xs">
                    {seat.row}{seat.number}
                  </span>
                ))}
            </div>

            {selectedCombos.length > 0 && (
              <div className="space-y-2">
                <div className="flex justify-between">
                  <span className="text-gray-600">Combos</span>
                  <span className="font-semibold text-gray-900">{comboTotal.toLocaleString()} VND</span>
                </div>
                <div className="space-y-1 text-gray-700">
                  {selectedCombos.map((combo) => (
                    <div key={combo.id} className="flex justify-between text-xs">
                      <span>{combo.name} × {combo.quantity || 1}</span>
                      <span className="font-semibold">{(combo.price * (combo.quantity || 1)).toLocaleString()} VND</span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          <div className="border-t pt-4 space-y-2 text-sm">
            <div className="flex justify-between">
              <span className="text-gray-600">Subtotal</span>
              <span className="font-semibold text-gray-900">{subtotal.toLocaleString()} VND</span>
            </div>
            {discount > 0 && (
              <div className="flex justify-between text-emerald-600">
                <span>Discount</span>
                <span>-{discount.toLocaleString()} VND</span>
              </div>
            )}
            <div className="flex justify-between items-center border-t pt-3 text-base font-bold text-blue-700">
              <span>Total</span>
              <span className="text-2xl">{total.toLocaleString()} VND</span>
            </div>
          </div>
        </Card>
      </div>
    </div>
  )
}
