"use client"

import { useState, useEffect } from "react"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { CheckCircle, Download, Printer, ChevronLeft, ChevronRight, Eye, FileText } from "lucide-react"
import QRCode from "qrcode"
import type { Seat, ComboItem } from "../../../lib/types"
import { ticketService, type TicketResponse } from "@/services/ticketService"

interface SuccessStepProps {
  bookingId: string
  selectedSeats: Seat[]
  selectedCombos: ComboItem[]
  total: number
  onNewBooking: () => void
  movie?: {
    title: string
  }
  showtime?: {
    time: string
    format: string
  }
}

export default function SuccessStep({
  bookingId,
  selectedSeats,
  selectedCombos,
  total,
  onNewBooking,
  movie,
  showtime,
}: SuccessStepProps) {
  const [tickets, setTickets] = useState<TicketResponse[]>([])
  const [currentTicketIndex, setCurrentTicketIndex] = useState(0)
  const [qrCodeUrls, setQrCodeUrls] = useState<string[]>([])
  const [isLoadingTickets, setIsLoadingTickets] = useState(false)
  const [viewMode, setViewMode] = useState<"payment" | "tickets" | "summary">("payment")
  const [hasLoadedTickets, setHasLoadedTickets] = useState(false)

  // Fetch tickets only when user clicks "View Tickets"
  const handleViewTickets = async () => {
    if (hasLoadedTickets) {
      // Already loaded, just switch view
      setViewMode("tickets")
      return
    }

    try {
      setIsLoadingTickets(true)
      setViewMode("tickets")
      const fetchedTickets = await ticketService.getTicketsByBooking(bookingId)
      setTickets(fetchedTickets)

      // Generate QR codes for all tickets
      const qrPromises = fetchedTickets.map((ticket) =>
        QRCode.toDataURL(ticket.qrContent || ticket.ticketCode, {
          width: 300,
          margin: 2,
          color: {
            dark: "#000000",
            light: "#FFFFFF",
          },
        })
      )
      const qrUrls = await Promise.all(qrPromises)
      setQrCodeUrls(qrUrls)
      setHasLoadedTickets(true)
    } catch (error) {
      console.error("Failed to fetch tickets:", error)
    } finally {
      setIsLoadingTickets(false)
    }
  }

  const handlePrevTicket = () => {
    setCurrentTicketIndex((prev) => (prev > 0 ? prev - 1 : tickets.length - 1))
  }

  const handleNextTicket = () => {
    setCurrentTicketIndex((prev) => (prev < tickets.length - 1 ? prev + 1 : 0))
  }

  const currentTicket = tickets[currentTicketIndex]

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

          {/* View Mode Toggle */}
          {viewMode === "payment" ? (
            // Payment Confirmation View (Default)
            <div className="space-y-4">
              <h3 className="font-bold text-xl mb-4 text-center">Payment Details</h3>
              
              {/* Payment Summary */}
              <div className="bg-white rounded-lg p-4 space-y-3">
                {movie && (
                  <div className="pb-3 border-b border-gray-200">
                    <p className="text-sm text-gray-600 mb-1">Movie</p>
                    <p className="text-lg font-semibold">{movie.title}</p>
                  </div>
                )}

                {showtime && (
                  <div className="pb-3 border-b border-gray-200">
                    <p className="text-sm text-gray-600 mb-1">Showtime</p>
                    <p className="text-lg font-semibold">{showtime.time}</p>
                    <p className="text-sm text-gray-600">{showtime.format}</p>
                  </div>
                )}

                {selectedSeats.length > 0 && (
                  <div className="pb-3 border-b border-gray-200">
                    <p className="text-sm text-gray-600 mb-1">Seats ({selectedSeats.length})</p>
                    <div className="flex flex-wrap gap-2 mt-2">
                      {selectedSeats
                        .sort((a, b) => {
                          if (a.row === b.row) {
                            return a.number - b.number
                          }
                          return a.row.localeCompare(b.row)
                        })
                        .map((seat) => (
                          <span key={seat.id} className="px-2 py-1 bg-blue-100 text-blue-700 rounded text-sm font-semibold">
                            {seat.row}{seat.number}
                          </span>
                        ))}
                    </div>
                  </div>
                )}

                {selectedCombos.length > 0 && (
                  <div className="pb-3 border-b border-gray-200">
                    <p className="text-sm text-gray-600 mb-2">Combos</p>
                    <div className="space-y-1">
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

                <div className="pt-2">
                  <div className="flex justify-between items-center">
                    <span className="font-bold text-gray-700">Total Paid</span>
                    <span className="text-2xl font-bold text-green-600">
                      {total.toLocaleString()} VND
                    </span>
                  </div>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="space-y-2 pt-2">
                <Button
                  onClick={handleViewTickets}
                  className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 flex items-center justify-center gap-2"
                  disabled={isLoadingTickets}
                >
                  <Eye className="w-5 h-5" />
                  {isLoadingTickets ? "Loading Tickets..." : "View Tickets"}
                </Button>
                
                {selectedSeats.length > 0 && (
                  <Button
                    onClick={() => setViewMode("summary")}
                    variant="outline"
                    className="w-full flex items-center justify-center gap-2"
                  >
                    <FileText className="w-4 h-4" />
                    View Booking Summary
                  </Button>
                )}
              </div>
            </div>
          ) : viewMode === "tickets" ? (
            // Ticket View
            <div>
              <div className="flex items-center justify-between mb-4">
                <Button
                  onClick={() => setViewMode("payment")}
                  variant="ghost"
                  size="sm"
                  className="gap-1"
                >
                  <ChevronLeft className="w-4 h-4" />
                  Back to Payment
                </Button>
                <h3 className="font-bold text-xl">Tickets</h3>
                <div className="w-24"></div>
              </div>
              
              {isLoadingTickets ? (
                <div className="flex justify-center py-12">
                  <div className="text-center">
                    <div className="inline-block w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                    <p className="text-gray-600 mt-4">Loading tickets...</p>
                  </div>
                </div>
              ) : tickets.length > 0 ? (
                <div className="relative">
                  {/* Ticket Counter */}
                  <div className="text-center mb-4">
                    <div className="inline-flex items-center gap-3">
                      <button
                        onClick={handlePrevTicket}
                        className="p-2 rounded-full hover:bg-gray-200 transition-colors disabled:opacity-30 disabled:cursor-not-allowed"
                        disabled={tickets.length <= 1}
                        aria-label="Previous ticket"
                      >
                        <ChevronLeft size={24} />
                      </button>
                      <span className="text-lg font-semibold min-w-[100px]">
                        Ticket {currentTicketIndex + 1} / {tickets.length}
                      </span>
                      <button
                        onClick={handleNextTicket}
                        className="p-2 rounded-full hover:bg-gray-200 transition-colors disabled:opacity-30 disabled:cursor-not-allowed"
                        disabled={tickets.length <= 1}
                        aria-label="Next ticket"
                      >
                        <ChevronRight size={24} />
                      </button>
                    </div>
                  </div>

                  {/* Ticket Card */}
                  {currentTicket && (
                    <div className="bg-gradient-to-br from-blue-50 to-purple-50 border-2 border-blue-300 rounded-2xl p-6 shadow-lg">
                      {/* QR Code */}
                      <div className="bg-white rounded-xl p-4 mb-4 flex justify-center">
                        {qrCodeUrls[currentTicketIndex] ? (
                          <img
                            src={qrCodeUrls[currentTicketIndex]}
                            alt="Ticket QR Code"
                            className="w-48 h-48"
                          />
                        ) : (
                          <div className="w-48 h-48 flex items-center justify-center text-gray-400">
                            Loading QR...
                          </div>
                        )}
                      </div>

                      {/* Ticket Details */}
                      <div className="space-y-3 text-center">
                        <div className="pb-3 border-b border-blue-200">
                          <p className="text-sm text-gray-600 mb-1">Seat</p>
                          <p className="text-2xl font-bold text-blue-600">{currentTicket.seatName}</p>
                        </div>

                        {movie && (
                          <div className="pb-3 border-b border-blue-200">
                            <p className="text-sm text-gray-600 mb-1">Movie</p>
                            <p className="text-lg font-semibold">{movie.title}</p>
                          </div>
                        )}

                        {showtime && (
                          <div className="pb-3 border-b border-blue-200">
                            <p className="text-sm text-gray-600 mb-1">Showtime</p>
                            <p className="text-lg font-semibold">{showtime.time}</p>
                            <p className="text-sm text-gray-600">{showtime.format}</p>
                          </div>
                        )}

                        <div className="pb-3 border-b border-blue-200">
                          <p className="text-sm text-gray-600 mb-1">Ticket Code</p>
                          <p className="text-lg font-mono font-bold text-blue-600">{currentTicket.ticketCode}</p>
                        </div>

                        <div>
                          <p className="text-sm text-gray-600 mb-1">Price</p>
                          <p className="text-lg font-semibold">{currentTicket.price.toLocaleString()} VND</p>
                        </div>
                      </div>

                      {/* Show at entrance notice */}
                      <div className="mt-4 bg-yellow-100 border border-yellow-300 rounded-lg p-3">
                        <p className="text-sm text-center font-semibold text-yellow-800">
                          📱 Show this QR code at the entrance
                        </p>
                      </div>
                    </div>
                  )}

                  {/* Action buttons below ticket */}
                  <div className="space-y-2 mt-6">
                    <Button
                      onClick={() => setViewMode("summary")}
                      variant="outline"
                      className="w-full flex items-center justify-center gap-2"
                    >
                      <FileText className="w-4 h-4" />
                      View Booking Summary
                    </Button>
                  </div>
                </div>
              ) : (
                <div className="text-center py-8 text-gray-600">
                  <p>No tickets found. Please contact support.</p>
                </div>
              )}
            </div>
          ) : (
            // Summary View
            <div className="space-y-4">
              <div className="flex items-center justify-between mb-4">
                <Button
                  onClick={() => setViewMode("payment")}
                  variant="ghost"
                  size="sm"
                  className="gap-1"
                >
                  <ChevronLeft className="w-4 h-4" />
                  Back to Payment
                </Button>
                <h3 className="font-bold text-xl">Booking Summary</h3>
                <div className="w-24"></div>
              </div>
              
              {selectedSeats.length > 0 && (
                <div>
                  <p className="text-sm font-semibold text-gray-600 mb-2">Seats ({selectedSeats.length})</p>
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
              )}

              {selectedCombos.length > 0 && (
                <div>
                  <p className="text-sm font-semibold text-gray-600 mb-2">Combos ({selectedCombos.length})</p>
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

              {/* Total */}
              {total > 0 && (
                <div className="bg-white rounded-lg p-4 border-2 border-blue-200">
                  <div className="flex justify-between items-center">
                    <span className="font-semibold text-gray-700">Total Paid</span>
                    <span className="text-2xl font-bold text-blue-600">
                      {total.toLocaleString()} VND
                    </span>
                  </div>
                </div>
              )}

              {/* Actions */}
              <div className="space-y-2 pt-4">
                <Button
                  onClick={() => setViewMode("tickets")}
                  className="w-full bg-blue-600 hover:bg-blue-700 flex items-center justify-center gap-2"
                >
                  <Eye className="w-4 h-4" />
                  View Tickets
                </Button>
                <button className="w-full flex items-center justify-center gap-2 px-4 py-2 border rounded-lg hover:bg-gray-50 transition-colors text-gray-700 font-semibold">
                  <Printer className="w-4 h-4" />
                  Print Ticket
                </button>
                <button className="w-full flex items-center justify-center gap-2 px-4 py-2 border rounded-lg hover:bg-gray-50 transition-colors text-gray-700 font-semibold">
                  <Download className="w-4 h-4" />
                  Download Receipt
                </button>
              </div>
            </div>
          )}

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
