"use client"

import Link from "next/link"
import { CheckCircle, Download, Share2 } from "lucide-react"
import type { Movie, Showtime, Seat, ComboItem } from "@/lib/types"

interface SuccessStepProps {
  movie: Movie
  showtime: Showtime
  selectedSeats: Seat[]
  selectedCombos: ComboItem[]
  total: number
}

export default function SuccessStep({ movie, showtime, selectedSeats, selectedCombos, total }: SuccessStepProps) {
  const bookingId = `CINEPLEX-${Date.now()}`
  const bookingDate = new Date().toLocaleDateString()

  return (
    <div className="bg-card dark:bg-slate-900 border border-border dark:border-slate-800 rounded-xl p-8">
      {/* Success Message */}
      <div className="text-center mb-12">
        <div className="flex justify-center mb-4">
          <CheckCircle size={64} className="text-green-500" />
        </div>
        <h2 className="text-3xl font-bold mb-2">Booking Confirmed!</h2>
        <p className="text-muted-foreground">Your tickets have been successfully booked</p>
      </div>

      {/* Booking Details */}
      <div className="bg-muted dark:bg-slate-800 rounded-lg p-6 mb-8">
        <div className="grid grid-cols-2 gap-6 mb-6">
          <div>
            <p className="text-sm text-muted-foreground mb-1">Booking ID</p>
            <p className="font-mono font-bold">{bookingId}</p>
          </div>
          <div>
            <p className="text-sm text-muted-foreground mb-1">Booking Date</p>
            <p className="font-semibold">{bookingDate}</p>
          </div>
        </div>

        <div className="border-t border-border dark:border-slate-700 pt-6">
          <h3 className="font-bold mb-4">Movie Information</h3>
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
              <p className="text-sm text-muted-foreground">Seats</p>
              <p className="font-semibold">{selectedSeats.map((s) => s.id).join(", ")}</p>
            </div>
          </div>
        </div>
      </div>

      {/* Ticket QR Code */}
      <div className="mb-8 flex justify-center">
        <div className="w-48 h-48 bg-muted dark:bg-slate-800 rounded-lg flex items-center justify-center border-2 border-dashed border-border dark:border-slate-700">
          <div className="text-center">
            <p className="text-4xl mb-2">🎫</p>
            <p className="text-sm text-muted-foreground">Ticket QR Code</p>
            <p className="text-xs text-muted-foreground mt-2">(Show at entrance)</p>
          </div>
        </div>
      </div>

      {/* Order Summary */}
      <div className="bg-purple-500/10 dark:bg-purple-900/20 border border-purple-500/30 rounded-lg p-6 mb-8">
        <h3 className="font-bold mb-4">Order Summary</h3>
        <div className="space-y-2 mb-4">
          <div className="flex justify-between text-sm">
            <span>Seats ({selectedSeats.length})</span>
            <span className="font-semibold">{(selectedSeats.length * showtime.price).toLocaleString()} VND</span>
          </div>
          {selectedCombos.length > 0 && (
            <div className="flex justify-between text-sm">
              <span>Combos ({selectedCombos.length})</span>
              <span className="font-semibold">
                {selectedCombos.reduce((sum, c) => sum + c.price, 0).toLocaleString()} VND
              </span>
            </div>
          )}
        </div>
        <div className="border-t border-purple-500/30 pt-4 flex justify-between items-center">
          <span className="font-bold">Total Paid</span>
          <span className="text-2xl font-bold text-purple-600">{total.toLocaleString()} VND</span>
        </div>
      </div>

      {/* Actions */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
        <button className="px-4 py-3 rounded-lg border border-border dark:border-slate-700 hover:bg-muted dark:hover:bg-slate-800 transition-colors font-semibold flex items-center justify-center gap-2">
          <Download size={20} />
          Download Ticket
        </button>
        <button className="px-4 py-3 rounded-lg border border-border dark:border-slate-700 hover:bg-muted dark:hover:bg-slate-800 transition-colors font-semibold flex items-center justify-center gap-2">
          <Share2 size={20} />
          Share Booking
        </button>
        <Link
          href="/"
          className="px-4 py-3 rounded-lg gradient-primary text-white font-semibold hover:shadow-lg transition-all flex items-center justify-center gap-2"
        >
          Back to Home
        </Link>
      </div>

      {/* Important Info */}
      <div className="bg-yellow-500/10 border border-yellow-500/30 rounded-lg p-4">
        <p className="text-sm font-semibold mb-2">Important Information</p>
        <ul className="text-sm text-muted-foreground space-y-1">
          <li>• Please arrive 15 minutes before the showtime</li>
          <li>• Show your ticket QR code at the entrance</li>
          <li>• A confirmation email has been sent to your registered email</li>
          <li>• You can cancel up to 2 hours before showtime for a full refund</li>
        </ul>
      </div>
    </div>
  )
}
