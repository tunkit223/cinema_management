"use client"

import { useState, useEffect, useRef } from "react"
import { ChevronLeft, ChevronRight } from "lucide-react"
import { PageHeader } from "@/components/ui/PageHeader"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import MovieSelectionStep from "@/components/booking/staff/MovieSelectionStep"
import ShowtimeSelectionStep from "@/components/booking/staff/ShowtimeSelectionStep"
import SeatSelectionStep from "@/components/booking/staff/SeatSelectionStep"
import ComboSelectionStep from "@/components/booking/staff/ComboSelectionStep"
import ConfirmationStep from "@/components/booking/staff/ConfirmationStep"
import PaymentStep from "@/components/booking/staff/PaymentStep"
import SuccessStep from "@/components/booking/staff/SuccessStep"
import { useAuthStore } from "@/stores/useAuthStore"
import { 
  getAllMovies, 
  type Movie 
} from "@/services/movieService"
import {
  getShowtimesByMovie,
  type ShowtimeResponse
} from "@/services/showtimeService"
import {
  getScreeningSeatsByScreeningId,
  mapScreeningSeatToSeat,
  getScreeningById,
  mapScreeningToShowtime,
  getCombos,
  mapComboForDisplay,
  getComboItemsByComboId,
  mapComboItemDetail,
} from "@/lib/api-movie"
import {
  createBooking,
  getBookingSummary,
  updateBookingCombos,
  cancelBooking,
} from "@/services/bookingService"
import { getMyInfo, getCustomerLoyaltyPoints } from "@/services/customerService"
import type { Seat, ComboItem, Showtime } from "@/lib/types"
import { useNotificationStore } from "@/stores"

interface ExtendedShowtime extends Showtime {
  roomId: string
  roomName: string
  cinemaId: string
  cinemaName: string
}

export const TicketBookingPage = () => {
  const { cinemaId } = useAuthStore()
  const addNotification = useNotificationStore((state) => state.addNotification)

  // Step management
  const [currentStep, setCurrentStep] = useState(1)

  // Data
  const [movies, setMovies] = useState<Movie[]>([])
  const [selectedMovie, setSelectedMovie] = useState<Movie | null>(null)
  const [showtimes, setShowtimes] = useState<ShowtimeResponse[]>([])
  const [selectedShowtime, setSelectedShowtime] = useState<ExtendedShowtime | null>(null)
  const [seats, setSeats] = useState<Seat[]>([])
  const [combos, setCombos] = useState<ComboItem[]>([])

  // Selection state
  const [selectedSeats, setSelectedSeats] = useState<Seat[]>([])
  const [selectedCombos, setSelectedCombos] = useState<ComboItem[]>([])

  // Customer info
  const [customerName, setCustomerName] = useState("")
  const [customerEmail, setCustomerEmail] = useState("")

  // Booking state
  const [bookingId, setBookingId] = useState<string | null>(null)

  // Loyalty points
  const [customerPoints, setCustomerPoints] = useState(0)
  const [pointsUsed, setPointsUsed] = useState(0)
  const [pointsDiscount, setPointsDiscount] = useState(0)

  // Loading & Error states
  const [moviesLoading, setMoviesLoading] = useState(true)
  const [showtimesLoading, setShowtimesLoading] = useState(false)
  const [seatsLoading, setSeatsLoading] = useState(false)
  const [combosLoading, setCombosLoading] = useState(false)
  const [isCreatingBooking, setIsCreatingBooking] = useState(false)
  const [isUpdatingCombos, setIsUpdatingCombos] = useState(false)
  const [seatsError, setSeatsError] = useState<string | null>(null)

  const isCancellingBooking = useRef(false)

  // Fetch movies on mount
  useEffect(() => {
    const fetchMovies = async () => {
      try {
        setMoviesLoading(true)
        const data = await getAllMovies()
        // Filter movies that have showtimes
        setMovies(data)
      } catch (error: any) {
        console.error("Error fetching movies:", error)
        addNotification({
          type: "error",
          title: "Error",
          message: "Failed to load movies"
        })
      } finally {
        setMoviesLoading(false)
      }
    }

    fetchMovies()
  }, [])

  // Fetch combos on mount
  useEffect(() => {
    const fetchCombos = async () => {
      try {
        setCombosLoading(true)
        const data = await getCombos()
        const mapped = Array.isArray(data)
          ? data
              .map((combo) => mapComboForDisplay(combo))
              .filter((combo): combo is ComboItem => combo !== null)
          : []

        const combosWithItems = await Promise.all(
          mapped.map(async (combo) => {
            try {
              const items = await getComboItemsByComboId(combo.id)
              const mappedItems = Array.isArray(items)
                ? items
                    .map((item) => mapComboItemDetail(item))
                    .filter((item): item is NonNullable<ReturnType<typeof mapComboItemDetail>> => item !== null)
                : []
              return { ...combo, items: mappedItems }
            } catch (error) {
              console.error("Error fetching combo items:", error)
              return { ...combo, items: [] }
            }
          })
        )

        setCombos(combosWithItems)
      } catch (error: any) {
        console.error("Error fetching combos:", error)
        setCombos([])
      } finally {
        setCombosLoading(false)
      }
    }

    fetchCombos()
  }, [])

  // Fetch showtimes when movie is selected
  useEffect(() => {
    if (!selectedMovie) return

    const fetchShowtimes = async () => {
      try {
        setShowtimesLoading(true)
        setSeatsError(null)
        const data = await getShowtimesByMovie(selectedMovie.id)

        // Filter by cinema if cinemaId exists
        let filtered = data
        if (cinemaId) {
          filtered = data.filter(st => st.cinemaId === cinemaId)
        }

        const now = new Date()
        filtered = filtered.filter((st) => new Date(st.startTime).getTime() > now.getTime())

        // Sort by startTime
        filtered.sort((a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime())
        setShowtimes(filtered)
      } catch (error: any) {
        console.error("Error fetching showtimes:", error)
        addNotification({
          type: "error",
          title: "Error",
          message: "Failed to load showtimes"
        })
        setShowtimes([])
      } finally {
        setShowtimesLoading(false)
      }
    }

    fetchShowtimes()
  }, [selectedMovie, cinemaId])

  // Fetch seats when showtime is selected
  useEffect(() => {
    if (!selectedShowtime) return

    const fetchSeats = async () => {
      try {
        setSeatsLoading(true)
        setSeatsError(null)
        const seatData = await getScreeningSeatsByScreeningId(selectedShowtime.id)
        
        if (seatData && Array.isArray(seatData)) {
          const mappedSeats = seatData
            .map((seat, idx) => mapScreeningSeatToSeat(seat, idx))
            .filter((seat): seat is Seat => seat !== null)
            .sort((a, b) => {
              if (a.row === b.row) {
                return a.number - b.number
              }
              return a.row.localeCompare(b.row, undefined, { numeric: true, sensitivity: "base" })
            })
          setSeats(mappedSeats)
        } else {
          setSeatsError("Unable to load seats")
          setSeats([])
        }
      } catch (error: any) {
        console.error("Error fetching seats:", error)
        setSeatsError(error?.response?.data?.message || "Failed to load seats")
        setSeats([])
      } finally {
        setSeatsLoading(false)
      }
    }

    fetchSeats()
  }, [selectedShowtime])

  // Fetch booking summary when entering confirmation step
  useEffect(() => {
    if (currentStep === 5 && bookingId) {
      const fetchSummary = async () => {
        try {
          const summary = await getBookingSummary(bookingId)
          
          // Convert discount to points
          if (summary.discountAmount && summary.discountAmount > 0) {
            const points = Math.floor(summary.discountAmount / 1000)
            setPointsUsed(points)
            setPointsDiscount(summary.discountAmount)
          }
        } catch (error: any) {
          console.error("Error fetching booking summary:", error)
        }
      }

      fetchSummary()
    }
  }, [currentStep, bookingId])

  // Fetch customer points when entering confirmation
  useEffect(() => {
    if (currentStep === 5) {
      const fetchPoints = async () => {
        try {
          const userInfo = await getMyInfo()
          const customerId = userInfo?.id
          if (customerId) {
            const points = await getCustomerLoyaltyPoints(customerId)
            setCustomerPoints(points)
          }
        } catch (error: any) {
          console.error("Error fetching loyalty points:", error)
        }
      }

      fetchPoints()
    }
  }, [currentStep])

  const handleCancelBooking = async (id: string) => {
    if (isCancellingBooking.current) return
    isCancellingBooking.current = true

    try {
      await cancelBooking(id)
      console.log("Booking cancelled successfully:", id)
    } catch (error: any) {
      console.error("Error cancelling booking:", error)
    } finally {
      isCancellingBooking.current = false
    }
  }

  const handleSelectMovie = (movie: Movie) => {
    setSelectedMovie(movie)
    setCurrentStep(2)
    setSelectedShowtime(null)
    setSelectedSeats([])
    setSelectedCombos([])
    setCustomerName("")
    setCustomerEmail("")
  }

  const handleSelectShowtime = async (showtime: ShowtimeResponse) => {
    try {
      // Fetch full screening data to get price info
      const screeningData = await getScreeningById(showtime.id)
      if (screeningData) {
        const mappedShowtime = mapScreeningToShowtime(screeningData)
        setSelectedShowtime({
          ...mappedShowtime,
          roomId: showtime.roomId,
          roomName: showtime.roomName,
          cinemaId: showtime.cinemaId,
          cinemaName: showtime.cinemaName,
        } as ExtendedShowtime)
        setCurrentStep(3)
        setSelectedSeats([])
        setSelectedCombos([])
      }
    } catch (error: any) {
      console.error("Error fetching showtime details:", error)
      addNotification({
        type: "error",
        title: "Error",
        message: "Failed to load showtime details"
      })
    }
  }

  const handleGoBackToMovies = () => {
    setCurrentStep(1)
    setSelectedMovie(null)
    setSelectedShowtime(null)
    setSelectedSeats([])
    setSelectedCombos([])
    setBookingId(null)
  }

  const handleGoBackToShowtimes = async () => {
    // Cancel current booking if exists
    if (bookingId) {
      await handleCancelBooking(bookingId)
      setBookingId(null)
    }
    
    setCurrentStep(2)
    setSelectedShowtime(null)
    setSelectedSeats([])
    setSelectedCombos([])
    setCustomerName("")
    setCustomerEmail("")
  }

  const handleNextStep = async () => {
    if (currentStep === 1 && !selectedMovie) {
      addNotification({
        type: "error",
        title: "Error",
        message: "Please select a movie first"
      })
      return
    }

    if (currentStep === 2 && !selectedShowtime) {
      addNotification({
        type: "error",
        title: "Error",
        message: "Please select a showtime"
      })
      return
    }

    if (currentStep === 3 && selectedSeats.length === 0) {
      addNotification({
        type: "error",
        title: "Error",
        message: "Please select at least one seat"
      })
      return
    }

    if (currentStep === 3 && (!customerName.trim() || !customerEmail.trim())) {
      addNotification({
        type: "error",
        title: "Missing info",
        message: "Please enter customer name and email"
      })
      return
    }

    // Create booking when moving from seats to combos
    if (currentStep === 3 && !bookingId && selectedSeats.length > 0) {
      try {
        setIsCreatingBooking(true)

        const bookingRequest = {
          screeningId: selectedShowtime!.id,
          screeningSeatIds: selectedSeats.map(seat => seat.id),
          customerName: customerName.trim(),
          email: customerEmail.trim(),
          firstName: customerName.trim(),
          lastName: customerName.trim(),
        }

        const response = await createBooking(bookingRequest)
        setBookingId(response.id)
        setCurrentStep(4)
      } catch (error: any) {
        console.error("Error creating booking:", error)
        addNotification({
          type: "error",
          title: "Error",
          message: error?.response?.data?.message || "Failed to create booking"
        })
      } finally {
        setIsCreatingBooking(false)
      }
    } else if (currentStep === 4 && bookingId) {
      // Update combos
      try {
        setIsUpdatingCombos(true)

        const combosPayload = selectedCombos.map((combo) => ({
          comboId: combo.id,
          quantity: combo.quantity && combo.quantity > 0 ? combo.quantity : 1,
        }))

        await updateBookingCombos(bookingId, { combos: combosPayload })
        setCurrentStep(5)
      } catch (error: any) {
        console.error("Error updating combos:", error)
        addNotification({
          type: "error",
          title: "Error",
          message: error?.response?.data?.message || "Failed to update combos"
        })
      } finally {
        setIsUpdatingCombos(false)
      }
    } else {
      setCurrentStep(currentStep + 1)
    }
  }

  const getMaxRedeemablePoints = () => {
    const subtotal = selectedSeats.reduce((sum, seat) => sum + (seat.price || 0), 0) +
                     selectedCombos.reduce((sum, combo) => sum + (combo.price * (combo.quantity || 1)), 0)
    const fiftyPercentCap = Math.floor(subtotal * 0.5)
    const maxPointsBySubtotal = Math.floor(subtotal / 1000)
    const maxPointsByFiftyCap = Math.floor(fiftyPercentCap / 1000)
    return Math.max(0, Math.min(customerPoints, maxPointsBySubtotal, maxPointsByFiftyCap))
  }

  const handleApplyPoints = (points: number) => {
    const maxRedeem = getMaxRedeemablePoints()
    const safePoints = Math.min(points, maxRedeem)
    setPointsUsed(safePoints)
    setPointsDiscount(safePoints * 1000)
  }

  // Calculate totals
  const seatsTotal = selectedSeats.reduce((sum, seat) => sum + (seat.price || 0), 0)
  const comboTotal = selectedCombos.reduce((sum, combo) => sum + (combo.price * (combo.quantity || 1)), 0)
  const subtotal = seatsTotal + comboTotal
  const discount = pointsDiscount > 0 ? pointsDiscount : 0
  const total = Math.max(0, subtotal - discount)

  const steps = [
    { number: 1, title: "Select Movie" },
    { number: 2, title: "Select Showtime" },
    { number: 3, title: "Select Seats" },
    { number: 4, title: "Choose Combos" },
    { number: 5, title: "Confirm" },
    { number: 6, title: "Payment" },
    { number: 7, title: "Success" },
  ]

  return (
    <div className="space-y-6">
      <PageHeader
        title="Staff Ticket Booking"
        description="Book tickets for customers"
      />

      <Card>
        {/* Progress Steps */}
        <div className="px-6 py-4 border-b">
          <div className="flex items-center gap-2 overflow-x-auto pb-2">
            {steps.map((step, idx) => (
              <div key={step.number} className="flex items-center gap-2 shrink-0">
                <div
                  className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold transition-all ${
                    currentStep >= step.number
                      ? "bg-blue-600 text-white"
                      : "bg-gray-200 text-gray-600"
                  }`}
                >
                  {step.number}
                </div>
                <span className="text-sm font-medium hidden md:block">{step.title}</span>
                {idx < steps.length - 1 && <div className="w-4 h-px bg-gray-200" />}
              </div>
            ))}
          </div>
        </div>

        {/* Content */}
        <div className="p-6">
          {currentStep === 1 && (
            <MovieSelectionStep
              movies={movies}
              loading={moviesLoading}
              onSelectMovie={handleSelectMovie}
            />
          )}

          {currentStep === 2 && selectedMovie && (
            <ShowtimeSelectionStep
              movie={selectedMovie}
              showtimes={showtimes}
              loading={showtimesLoading}
              onSelectShowtime={handleSelectShowtime}
            />
          )}

          {currentStep === 3 && selectedShowtime && (
            <SeatSelectionStep
              seats={seats}
              selectedSeats={selectedSeats}
              onSelectSeats={setSelectedSeats}
              showtime={selectedShowtime}
              loading={seatsLoading}
              error={seatsError}
              customerName={customerName}
              customerEmail={customerEmail}
              onCustomerNameChange={setCustomerName}
              onCustomerEmailChange={setCustomerEmail}
            />
          )}

          {currentStep === 4 && (
            <ComboSelectionStep
              combos={combos}
              selectedCombos={selectedCombos}
              onSelectCombos={setSelectedCombos}
              loading={combosLoading}
            />
          )}

          {currentStep === 5 && selectedShowtime && (
            <ConfirmationStep
              selectedSeats={selectedSeats}
              selectedCombos={selectedCombos}
              showtime={selectedShowtime}
              movie={selectedMovie!}
              subtotal={subtotal}
              customerPoints={customerPoints}
              pointsUsed={pointsUsed}
              pointsDiscount={pointsDiscount}
              onApplyPoints={handleApplyPoints}
            />
          )}

          {currentStep === 6 && bookingId && (
            <PaymentStep
              bookingId={bookingId}
              total={total}
              onPaymentSuccess={() => setCurrentStep(7)}
            />
          )}

          {currentStep === 7 && bookingId && (
            <SuccessStep
              bookingId={bookingId}
              selectedSeats={selectedSeats}
              selectedCombos={selectedCombos}
              total={total}
              onNewBooking={() => {
                handleGoBackToMovies()
              }}
            />
          )}
        </div>

        {/* Navigation & Summary */}
        <div className="px-6 py-4 border-t bg-gray-50 dark:bg-gray-900 flex items-center justify-between">
          <Button
            variant="outline"
            onClick={() => {
              if (currentStep > 1) {
                if (currentStep === 2) {
                  handleGoBackToMovies()
                } else if (currentStep === 3) {
                  handleGoBackToShowtimes()
                } else {
                  setCurrentStep(currentStep - 1)
                }
              }
            }}
            disabled={currentStep === 1}
          >
            <ChevronLeft className="w-4 h-4 mr-2" />
            Back
          </Button>

          <div className="text-right">
            <p className="text-sm text-gray-600">Total</p>
            <p className="text-2xl font-bold text-blue-600">
              {total.toLocaleString()} VND
            </p>
          </div>

          <Button
            onClick={handleNextStep}
            disabled={
              (currentStep === 1 && !selectedMovie) ||
              (currentStep === 2 && !selectedShowtime) ||
              (currentStep === 3 && selectedSeats.length === 0) ||
              isCreatingBooking ||
              isUpdatingCombos ||
              (currentStep === 7) // Success step, no next
            }
            className="bg-blue-600 hover:bg-blue-700"
          >
            {currentStep === 7 ? "Done" : currentStep === 6 ? "Confirm" : "Next"}
            {currentStep !== 7 && <ChevronRight className="w-4 h-4 ml-2" />}
          </Button>
        </div>
      </Card>
    </div>
  )
}
