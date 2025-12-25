"use client"

import { useState } from "react"
import type { Movie, Showtime, Seat, ComboItem } from "@/lib/types"

interface ConfirmationStepProps {
  movie: Movie
  showtime: Showtime
  selectedSeats: Seat[]
  selectedCombos: ComboItem[]
  subtotal: number
  pointsUsed?: number
  pointsDiscount?: number
  total: number
  customerPoints?: number
  onApplyPoints?: (pointsToUse: number) => void
}

export default function ConfirmationStep({
  movie,
  showtime,
  selectedSeats,
  selectedCombos,
  subtotal,
  pointsUsed = 0,
  pointsDiscount = 0,
  total,
  customerPoints = 0,
  onApplyPoints,
}: ConfirmationStepProps) {
  const [pointsInput, setPointsInput] = useState(pointsUsed)
  const maxPointsCanUse = Math.min(customerPoints, Math.floor(subtotal / 1000)) // 1000 points = 1000 VND

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
                  {combo.icon} {combo.name} x{combo.quantity || 1}
                </span>
                <span className="font-semibold">{(combo.price * (combo.quantity || 1)).toLocaleString()} VND</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Discount Code */}
      <div className="mb-8 pb-8 border-b border-border dark:border-slate-800">
        <h3 className="font-bold mb-4">Use Loyalty Points</h3>
        <div className="bg-purple-500/10 dark:bg-purple-900/20 border border-purple-500/30 rounded-lg p-4 mb-4">
          <div className="flex justify-between items-center mb-2">
            <span className="text-sm font-semibold">Available Points</span>
            <span className="text-2xl font-bold text-purple-600">{customerPoints.toLocaleString()}</span>
          </div>
          <p className="text-xs text-muted-foreground">1,000 points = 1,000 VND discount</p>
        </div>
        <div className="flex gap-2">
          <input
            type="number"
            value={pointsInput}
            onChange={(e) => {
              const value = Math.min(Math.max(0, parseInt(e.target.value) || 0), maxPointsCanUse)
              setPointsInput(value)
            }}
            placeholder="Enter points to redeem"
            max={maxPointsCanUse}
            className="flex-1 px-4 py-2 rounded-lg border border-border dark:border-slate-700 bg-background dark:bg-slate-800 focus:outline-none focus:border-purple-500 transition-colors"
          />
          <button
            onClick={() => onApplyPoints?.(pointsInput)}
            className="px-4 py-2 rounded-lg bg-purple-600 text-white font-semibold hover:bg-purple-700 transition-colors"
          >
            Apply
          </button>
        </div>
        <p className="text-xs text-muted-foreground mt-2">Maximum points you can use: {maxPointsCanUse.toLocaleString()}</p>
      </div>

      {/* Price Summary */}
      <div className="space-y-3">
        <div className="flex justify-between text-sm">
          <span className="text-muted-foreground">Subtotal</span>
          <span className="font-semibold">{subtotal.toLocaleString()} VND</span>
        </div>
        {pointsDiscount > 0 && (
          <div className="flex justify-between text-sm text-green-600">
            <span>Points Discount ({pointsUsed.toLocaleString()} points)</span>
            <span>-{pointsDiscount.toLocaleString()} VND</span>
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
