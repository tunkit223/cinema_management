import { Card } from "@/components/ui/card"
import { CreditCard } from "lucide-react"

interface PaymentStepProps {
  bookingId: string
  total: number
  onPaymentSuccess: () => void
}

export default function PaymentStep({
  bookingId,
  total,
  onPaymentSuccess,
}: PaymentStepProps) {
  const handlePaymentSimulation = async () => {
    // In a real app, this would integrate with a payment gateway
    // For now, we'll simulate successful payment
    
    setTimeout(() => {
      onPaymentSuccess()
    }, 1500)
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-bold mb-4">Payment</h2>
      </div>

      <Card className="p-8">
        <div className="max-w-md mx-auto space-y-6">
          {/* Booking ID */}
          <div className="bg-gray-50 p-4 rounded-lg">
            <p className="text-sm text-gray-600 mb-1">Booking ID</p>
            <p className="font-mono text-lg font-bold">{bookingId}</p>
          </div>

          {/* Total Amount */}
          <div className="border-t pt-6 space-y-2">
            <p className="text-gray-600">Total Amount</p>
            <p className="text-4xl font-bold text-blue-600">
              {total.toLocaleString()}
            </p>
            <p className="text-gray-600">VND</p>
          </div>

          {/* Payment Methods */}
          <div className="border-t pt-6 space-y-3">
            <p className="font-semibold mb-3">Select Payment Method</p>
            
            <button className="w-full p-4 border rounded-lg hover:border-blue-600 hover:bg-blue-50 transition-all text-left group">
              <div className="flex items-center gap-3">
                <CreditCard className="w-5 h-5 text-gray-600 group-hover:text-blue-600" />
                <div>
                  <p className="font-semibold">Credit Card</p>
                  <p className="text-sm text-gray-600">Visa, Mastercard, etc</p>
                </div>
              </div>
            </button>

            <button className="w-full p-4 border rounded-lg hover:border-blue-600 hover:bg-blue-50 transition-all text-left group">
              <div className="flex items-center gap-3">
                <div className="w-5 h-5 bg-red-600 rounded text-white flex items-center justify-center text-xs font-bold">
                  Z
                </div>
                <div>
                  <p className="font-semibold">ZaloPay</p>
                  <p className="text-sm text-gray-600">Mobile wallet</p>
                </div>
              </div>
            </button>

            <button className="w-full p-4 border rounded-lg hover:border-blue-600 hover:bg-blue-50 transition-all text-left group">
              <div className="flex items-center gap-3">
                <div className="w-5 h-5 bg-blue-700 rounded text-white flex items-center justify-center text-xs font-bold">
                  M
                </div>
                <div>
                  <p className="font-semibold">Momo</p>
                  <p className="text-sm text-gray-600">Mobile wallet</p>
                </div>
              </div>
            </button>
          </div>

          {/* Proceed Button */}
          <button
            onClick={handlePaymentSimulation}
            className="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg font-bold transition-colors"
          >
            Proceed to Payment
          </button>

          {/* Test Mode Info */}
          <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-3">
            <p className="text-xs text-yellow-800">
              <span className="font-semibold">Test Mode:</span> This is a simulated payment. Click "Proceed to Payment" to complete.
            </p>
          </div>
        </div>
      </Card>
    </div>
  )
}
