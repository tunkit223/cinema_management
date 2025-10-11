"use client"

import { useState } from "react"
import type { Movie, Showtime, Seat, ComboItem } from "@/lib/types"

interface ConfirmationStepProps {
  movie: Movie
  showtime: Showtime
  selectedSeats: Seat[]
  selectedCombos: ComboItem[]
  subtotal: number
  discountAmount: number
  total: number
  onApplyDiscount: (code: string) => void
}

export default function ConfirmationStep({
  movie,
  showtime,
  selectedSeats,
  selectedCombos,
  subtotal,
  discountAmount,
  total,
  onApplyDiscount,
}: ConfirmationStepProps) {
  const [discountCode, setDiscountCode] = useState("")

  return (
    <div className="bg-card dark:bg-slate-900 border border-border dark:border-slate-800 rounded-xl p-8">
      <h2 className="text-2xl font-bold mb-8">Confirm Your Booking</h2>

      {/* Movie Info */}
      <div className="mb-8 pb-8 border-b border-border dark:border-slate-800">
        <h3 className="font-bold mb-4">Movie Details</h3>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <p className="text-sm text-muted-foreground">Movie</p>
            <p className="font-semibold">{movie.title}</p>
          </div>
          <div>
            <p className="text-sm text-muted-foreground">Time</p>
            <p className="font-semibold">{showtime.time}</p>
          </div>
          <div>
            <p className="text-sm text-muted-foreground">Format</p>
            <p className="font-semibold">{showtime.format}</p>
          </div>
          <div>
            <p className="text-sm text-muted-foreground">Price per Seat</p>
            <p className="font-semibold">{showtime.price.toLocaleString()} VND</p>
          </div>
        </div>
      </div>

      {/* Seats */}
      <div className="mb-8 pb-8 border-b border-border dark:border-slate-800">
        <h3 className="font-bold mb-4">Selected Seats ({selectedSeats.length})</h3>
        <div className="flex flex-wrap gap-2">
          {selectedSeats.map((seat) => (
            <span
              key={seat.id}
              className="px-3 py-1 rounded-full bg-purple-500/20 text-purple-600 dark:text-purple-300 text-sm font-semibold"
            >
              {seat.id}
            </span>
          ))}
        </div>
      </div>

      {/* Combos */}
      {selectedCombos.length > 0 && (
        <div className="mb-8 pb-8 border-b border-border dark:border-slate-800">
          <h3 className="font-bold mb-4">Selected Combos ({selectedCombos.length})</h3>
          <div className="space-y-2">
            {selectedCombos.map((combo) => (
              <div key={combo.id} className="flex justify-between text-sm">
                <span>
                  {combo.icon} {combo.name}
                </span>
                <span className="font-semibold">{combo.price.toLocaleString()} VND</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Discount Code */}
      <div className="mb-8 pb-8 border-b border-border dark:border-slate-800">
        <h3 className="font-bold mb-4">Apply Discount Code</h3>
        <div className="flex gap-2">
          <input
            type="text"
            value={discountCode}
            onChange={(e) => setDiscountCode(e.target.value)}
            placeholder="Enter discount code (e.g., SAVE20)"
            className="flex-1 px-4 py-2 rounded-lg border border-border dark:border-slate-700 bg-background dark:bg-slate-800 focus:outline-none focus:border-purple-500 transition-colors"
          />
          <button
            onClick={() => onApplyDiscount(discountCode)}
            className="px-4 py-2 rounded-lg bg-purple-600 text-white font-semibold hover:bg-purple-700 transition-colors"
          >
            Apply
          </button>
        </div>
        <p className="text-xs text-muted-foreground mt-2">Try: SAVE20 (20% off) or SAVE10 (10% off)</p>
      </div>

      {/* Price Summary */}
      <div className="space-y-3">
        <div className="flex justify-between text-sm">
          <span className="text-muted-foreground">Subtotal</span>
          <span className="font-semibold">{subtotal.toLocaleString()} VND</span>
        </div>
        {discountAmount > 0 && (
          <div className="flex justify-between text-sm text-green-600">
            <span>Discount</span>
            <span>-{discountAmount.toLocaleString()} VND</span>
          </div>
        )}
        <div className="flex justify-between items-center pt-3 border-t border-border dark:border-slate-800">
          <span className="font-bold">Total Amount</span>
          <span className="text-2xl font-bold text-purple-600">{total.toLocaleString()} VND</span>
        </div>
      </div>
    </div>
  )
}
