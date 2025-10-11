"use client"

import React, { useState } from "react"
import Link from "next/link"
import { use } from "react" // 👈 cần import use() từ react
import { ChevronLeft, ChevronRight } from "lucide-react"
import { nowShowingMovies, comingSoonMovies, showtimes, generateSeats, combos } from "@/lib/mock-data"
import type { Seat, ComboItem } from "@/lib/types"
import SeatSelectionStep from "@/components/booking/seat-selection-step"
import ComboSelectionStep from "@/components/booking/combo-selection-step"
import ConfirmationStep from "@/components/booking/confirmation-step"
import PaymentStep from "@/components/booking/payment-step"
import SuccessStep from "@/components/booking/success-step"

export default function BookingPage({
  params,
}: {
  params: Promise<{ movieId: string; showtimeId: string }>
}) {
  // ✅ unwrap Promise của params
  const { movieId, showtimeId } = use(params)

  const allMovies = [...nowShowingMovies, ...comingSoonMovies]
  const movie = allMovies.find((m) => m.id === movieId)
  const showtime = showtimes[movieId]?.find((s) => s.id === showtimeId)

  const [currentStep, setCurrentStep] = useState(1)
  const [selectedSeats, setSelectedSeats] = useState<Seat[]>([])
  const [selectedCombos, setSelectedCombos] = useState<ComboItem[]>([])
  const [discountCode, setDiscountCode] = useState("")
  const [discountAmount, setDiscountAmount] = useState(0)
  const [paymentSuccess, setPaymentSuccess] = useState(false)

  if (!movie || !showtime) {
    return (
      <div className="min-h-screen bg-background dark:bg-slate-950 flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-3xl font-bold mb-4">Booking Not Found</h1>
          <Link href="/" className="text-purple-600 hover:text-purple-700 font-semibold">
            Back to Home
          </Link>
        </div>
      </div>
    )
  }

  const seatPrice = showtime.price
  const comboTotal = selectedCombos.reduce((sum, combo) => sum + combo.price, 0)
  const subtotal = selectedSeats.length * seatPrice + comboTotal
  const total = Math.max(0, subtotal - discountAmount)

  const handleApplyDiscount = (code: string) => {
    if (code.toUpperCase() === "SAVE20") {
      setDiscountAmount(subtotal * 0.2)
      setDiscountCode(code)
    } else if (code.toUpperCase() === "SAVE10") {
      setDiscountAmount(subtotal * 0.1)
      setDiscountCode(code)
    }
  }

  const steps = [
    { number: 1, title: "Select Seats" },
    { number: 2, title: "Choose Combos" },
    { number: 3, title: "Confirm Booking" },
    { number: 4, title: "Payment" },
    { number: 5, title: "Success" },
  ]

  return (
    <div className="min-h-screen bg-background dark:bg-slate-950 pt-20 pb-12">
      <div className="container-max px-4 md:px-8">
        {/* Header */}
        <div className="mb-8">
          <Link
            href={`/movies/${movieId}`}
            className="flex items-center gap-2 text-purple-600 hover:text-purple-700 font-semibold mb-6"
          >
            <ChevronLeft size={20} />
            Back to Movie
          </Link>

          <div className="flex items-center justify-between mb-8">
            <div>
              <h1 className="text-3xl font-bold mb-2">{movie.title}</h1>
              <p className="text-muted-foreground">
                {showtime.time} • {showtime.format} • {showtime.price.toLocaleString()} VND
              </p>
            </div>
          </div>
        </div>

        {/* Progress Steps */}
        <div className="mb-12">
          <div className="flex items-center justify-between">
            {steps.map((step, idx) => (
              <div key={step.number} className="flex items-center flex-1">
                <div
                  className={`w-10 h-10 rounded-full flex items-center justify-center font-bold transition-all ${
                    currentStep >= step.number
                      ? "bg-purple-600 text-white"
                      : "bg-muted dark:bg-slate-800 text-muted-foreground"
                  }`}
                >
                  {step.number}
                </div>
                <p className="text-sm font-medium ml-2 hidden md:block">{step.title}</p>
                {idx < steps.length - 1 && (
                  <div
                    className={`flex-1 h-1 mx-4 rounded-full transition-all ${
                      currentStep > step.number ? "bg-purple-600" : "bg-muted dark:bg-slate-800"
                    }`}
                  />
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Step Content */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2">
            {currentStep === 1 && (
              <SeatSelectionStep
                seats={generateSeats()}
                selectedSeats={selectedSeats}
                onSelectSeats={setSelectedSeats}
                seatPrice={seatPrice}
              />
            )}
            {currentStep === 2 && (
              <ComboSelectionStep combos={combos} selectedCombos={selectedCombos} onSelectCombos={setSelectedCombos} />
            )}
            {currentStep === 3 && (
              <ConfirmationStep
                movie={movie}
                showtime={showtime}
                selectedSeats={selectedSeats}
                selectedCombos={selectedCombos}
                subtotal={subtotal}
                discountAmount={discountAmount}
                total={total}
                onApplyDiscount={handleApplyDiscount}
              />
            )}
            {currentStep === 4 && (
              <PaymentStep
                total={total}
                onPaymentSuccess={() => {
                  setPaymentSuccess(true)
                  setCurrentStep(5)
                }}
              />
            )}
            {currentStep === 5 && (
              <SuccessStep
                movie={movie}
                showtime={showtime}
                selectedSeats={selectedSeats}
                selectedCombos={selectedCombos}
                total={total}
              />
            )}
          </div>

          {/* Sidebar Summary */}
          {currentStep < 5 && (
            <div className="bg-card dark:bg-slate-900 border border-border dark:border-slate-800 rounded-xl p-6 h-fit sticky top-24">
              <h3 className="text-lg font-bold mb-4">Booking Summary</h3>

              <div className="space-y-4 mb-6 pb-6 border-b border-border dark:border-slate-800">
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Seats ({selectedSeats.length})</span>
                  <span className="font-semibold">{(selectedSeats.length * seatPrice).toLocaleString()} VND</span>
                </div>
                {selectedCombos.length > 0 && (
                  <div className="flex justify-between text-sm">
                    <span className="text-muted-foreground">Combos ({selectedCombos.length})</span>
                    <span className="font-semibold">{comboTotal.toLocaleString()} VND</span>
                  </div>
                )}
                {discountAmount > 0 && (
                  <div className="flex justify-between text-sm text-green-600">
                    <span>Discount</span>
                    <span>-{discountAmount.toLocaleString()} VND</span>
                  </div>
                )}
              </div>

              <div className="flex justify-between items-center mb-6">
                <span className="font-bold">Total</span>
                <span className="text-2xl font-bold text-purple-600">{total.toLocaleString()} VND</span>
              </div>

              {/* Navigation Buttons */}
              <div className="space-y-3">
                {currentStep > 1 && (
                  <button
                    onClick={() => setCurrentStep(currentStep - 1)}
                    className="w-full px-4 py-3 rounded-lg border border-border dark:border-slate-700 hover:bg-muted dark:hover:bg-slate-800 transition-colors font-semibold flex items-center justify-center gap-2"
                  >
                    <ChevronLeft size={20} />
                    Previous
                  </button>
                )}
                {currentStep < 4 && (
                  <button
                    onClick={() => setCurrentStep(currentStep + 1)}
                    disabled={(currentStep === 1 && selectedSeats.length === 0) || (currentStep === 3 && total === 0)}
                    className="w-full px-4 py-3 rounded-lg gradient-primary text-white font-semibold hover:shadow-lg transition-all flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Next
                    <ChevronRight size={20} />
                  </button>
                )}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
