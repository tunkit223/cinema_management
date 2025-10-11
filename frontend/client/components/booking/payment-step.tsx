"use client"

import { useState } from "react"
import { Copy, Check } from "lucide-react"

interface PaymentStepProps {
  total: number
  onPaymentSuccess: () => void
}

export default function PaymentStep({ total, onPaymentSuccess }: PaymentStepProps) {
  const [copied, setCopied] = useState(false)

  const bankInfo = {
    bankName: "Vietcombank",
    accountNumber: "1234567890",
    accountHolder: "CINEPLEX CINEMA",
    amount: total,
  }

  const handleCopy = (text: string) => {
    navigator.clipboard.writeText(text)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  return (
    <div className="bg-card dark:bg-slate-900 border border-border dark:border-slate-800 rounded-xl p-8">
      <h2 className="text-2xl font-bold mb-8">Payment</h2>

      {/* QR Code */}
      <div className="mb-8 flex justify-center">
        <div className="w-64 h-64 bg-muted dark:bg-slate-800 rounded-lg flex items-center justify-center border-2 border-dashed border-border dark:border-slate-700">
          <div className="text-center">
            <p className="text-4xl mb-2">📱</p>
            <p className="text-sm text-muted-foreground">QR Code for Payment</p>
            <p className="text-xs text-muted-foreground mt-2">(Demo - Scan to pay)</p>
          </div>
        </div>
      </div>

      {/* Bank Transfer Info */}
      <div className="mb-8 pb-8 border-b border-border dark:border-slate-800">
        <h3 className="font-bold mb-4">Bank Transfer Details</h3>
        <div className="space-y-4">
          <div className="bg-muted dark:bg-slate-800 p-4 rounded-lg">
            <p className="text-sm text-muted-foreground mb-1">Bank Name</p>
            <div className="flex items-center justify-between">
              <p className="font-semibold">{bankInfo.bankName}</p>
              <button
                onClick={() => handleCopy(bankInfo.bankName)}
                className="p-2 hover:bg-background dark:hover:bg-slate-700 rounded transition-colors"
              >
                {copied ? <Check size={16} /> : <Copy size={16} />}
              </button>
            </div>
          </div>

          <div className="bg-muted dark:bg-slate-800 p-4 rounded-lg">
            <p className="text-sm text-muted-foreground mb-1">Account Number</p>
            <div className="flex items-center justify-between">
              <p className="font-semibold font-mono">{bankInfo.accountNumber}</p>
              <button
                onClick={() => handleCopy(bankInfo.accountNumber)}
                className="p-2 hover:bg-background dark:hover:bg-slate-700 rounded transition-colors"
              >
                {copied ? <Check size={16} /> : <Copy size={16} />}
              </button>
            </div>
          </div>

          <div className="bg-muted dark:bg-slate-800 p-4 rounded-lg">
            <p className="text-sm text-muted-foreground mb-1">Account Holder</p>
            <div className="flex items-center justify-between">
              <p className="font-semibold">{bankInfo.accountHolder}</p>
              <button
                onClick={() => handleCopy(bankInfo.accountHolder)}
                className="p-2 hover:bg-background dark:hover:bg-slate-700 rounded transition-colors"
              >
                {copied ? <Check size={16} /> : <Copy size={16} />}
              </button>
            </div>
          </div>

          <div className="bg-purple-500/10 dark:bg-purple-900/20 border border-purple-500/30 p-4 rounded-lg">
            <p className="text-sm text-muted-foreground mb-1">Amount to Transfer</p>
            <div className="flex items-center justify-between">
              <p className="text-2xl font-bold text-purple-600">{bankInfo.amount.toLocaleString()} VND</p>
              <button
                onClick={() => handleCopy(bankInfo.amount.toString())}
                className="p-2 hover:bg-background dark:hover:bg-slate-700 rounded transition-colors"
              >
                {copied ? <Check size={16} /> : <Copy size={16} />}
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Confirmation */}
      <div className="space-y-4">
        <p className="text-sm text-muted-foreground">
          After completing the bank transfer, click the button below to confirm your payment.
        </p>
        <button
          onClick={onPaymentSuccess}
          className="w-full px-6 py-4 rounded-lg gradient-primary text-white font-semibold hover:shadow-lg transition-all"
        >
          Confirm Payment
        </button>
      </div>
    </div>
  )
}
