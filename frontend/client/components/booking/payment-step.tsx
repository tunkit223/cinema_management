"use client"

import { useState, useEffect } from "react"
import { Loader2 } from "lucide-react"

interface PaymentStepProps {
  bookingId: string
  total: number
  onPaymentSuccess: () => void
}

export default function PaymentStep({ bookingId, total, onPaymentSuccess }: PaymentStepProps) {
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const initiatePayment = async () => {
      try {
        setIsLoading(true)
        setError(null)

        // Step 1: Create invoice
        console.log("Creating invoice for booking:", bookingId)
        const invoiceResponse = await fetch(
          `${process.env.NEXT_PUBLIC_API_URL}/bookings/${bookingId}/create-invoice`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
            },
          }
        )

        if (!invoiceResponse.ok) {
          throw new Error(`Failed to create invoice: ${invoiceResponse.status}`)
        }

        const invoiceData = await invoiceResponse.json()
        const invoiceId = invoiceData.result.id

        console.log("Invoice created:", invoiceId)

        // Step 2: Create VNPay payment
        console.log("Creating VNPay payment for invoice:", invoiceId)
        const paymentResponse = await fetch(
          `${process.env.NEXT_PUBLIC_API_URL}/payment/vnpay/${invoiceId}`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
            },
          }
        )

        if (!paymentResponse.ok) {
          throw new Error(`Failed to create payment: ${paymentResponse.status}`)
        }

        const paymentData = await paymentResponse.json()
        const paymentUrl = paymentData.result.paymentUrl

        console.log("Payment URL received:", paymentUrl)

        // Step 3: Redirect to VNPay
        if (paymentUrl) {
          window.location.href = paymentUrl
        } else {
          throw new Error("No payment URL received from server")
        }
      } catch (err) {
        console.error("Error initiating payment:", err)
        setError(err instanceof Error ? err.message : "Failed to initiate payment")
        setIsLoading(false)
      }
    }

    initiatePayment()
  }, [bookingId])

  if (isLoading) {
    return (
      <div className="bg-card dark:bg-slate-900 border border-border dark:border-slate-800 rounded-xl p-8">
        <h2 className="text-2xl font-bold mb-8">Payment</h2>
        <div className="flex flex-col items-center justify-center min-h-[400px]">
          <Loader2 className="w-12 h-12 animate-spin text-purple-600 mb-4" />
          <p className="text-lg font-semibold mb-2">Preparing Payment</p>
          <p className="text-sm text-muted-foreground text-center max-w-sm">
            Creating invoice and redirecting to VNPay...
          </p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="bg-card dark:bg-slate-900 border border-border dark:border-slate-800 rounded-xl p-8">
        <h2 className="text-2xl font-bold mb-8">Payment</h2>
        <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-6 mb-6">
          <h3 className="font-bold text-red-800 dark:text-red-300 mb-2">Payment Error</h3>
          <p className="text-red-700 dark:text-red-400 text-sm">{error}</p>
        </div>
        <button
          onClick={() => window.location.reload()}
          className="w-full px-6 py-3 rounded-lg gradient-primary text-white font-semibold hover:shadow-lg transition-all"
        >
          Try Again
        </button>
      </div>
    )
  }

  return (
    <div className="bg-card dark:bg-slate-900 border border-border dark:border-slate-800 rounded-xl p-8">
      <h2 className="text-2xl font-bold mb-8">Payment</h2>
      <div className="flex flex-col items-center justify-center min-h-[400px]">
        <Loader2 className="w-12 h-12 animate-spin text-purple-600 mb-4" />
        <p className="text-lg font-semibold">Redirecting to VNPay...</p>
      </div>
    </div>
  )
}
