"use client"

import { useState, useEffect, useRef } from "react"
import { CheckCircle2, ChevronLeft, ChevronRight } from "lucide-react"
import { PageHeader } from "@/components/ui/PageHeader"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { CheckCircle, Loader2, XCircle } from "lucide-react"
import MovieSelectionStep from "@/components/booking/staff/MovieSelectionStep"
import ShowtimeSelectionStep from "@/components/booking/staff/ShowtimeSelectionStep"
import SeatSelectionStep from "@/components/booking/staff/SeatSelectionStep"
import ComboSelectionStep from "@/components/booking/staff/ComboSelectionStep"
import ConfirmationStep from "@/components/booking/staff/ConfirmationStep"
import PaymentStep from "@/components/booking/staff/PaymentStep"
import SuccessStep from "@/components/booking/staff/SuccessStep"
import { useAuthStore } from "@/stores/useAuthStore"
import httpClient from "@/configurations/httpClient"
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
  redeemBookingPoints,
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
  const [checkoutMode, setCheckoutMode] = useState<"guest" | "member">("guest")

  // Booking state
  const [bookingId, setBookingId] = useState<string | null>(null)
  const [customerId, setCustomerId] = useState<string | null>(null)

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
  const [isRedeemingPoints, setIsRedeemingPoints] = useState(false)
  const [seatsError, setSeatsError] = useState<string | null>(null)
  const [skipCancelOnUnload, setSkipCancelOnUnload] = useState(false)
  const skipCancelOnUnloadRef = useRef(false)

  // VNPay return handling
  const [isVerifyingVnp, setIsVerifyingVnp] = useState(false)
  const [vnpReturnResult, setVnpReturnResult] = useState<{ code?: string; message?: string; bookingId?: string; txnRef?: string; amount?: number; orderInfo?: string } | null>(null)
  const [vnpReturnError, setVnpReturnError] = useState<string | null>(null)

  const isCancellingBooking = useRef(false)

  // Keep customer inputs blank when guest checkout is selected
  useEffect(() => {
    if (checkoutMode === "guest") {
      setCustomerName("")
      setCustomerEmail("")
    }
  }, [checkoutMode])

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

  // Detect VNPay return (when redirected back to admin with vnp_ params)
  useEffect(() => {
    if (typeof window === "undefined") return
    const search = window.location.search || ""
    if (!search.includes("vnp_")) return

    const queryString = search.startsWith("?") ? search.slice(1) : search

    const verifyReturn = async () => {
      try {
        skipCancelOnUnloadRef.current = true
        setSkipCancelOnUnload(true)
        setIsVerifyingVnp(true)
        setVnpReturnError(null)

        const response = await httpClient.get(`/payment/vnpay-return?${queryString}`)
        const data = response.data?.result || response.data || {}
        setVnpReturnResult(data)

        if (data?.code === "00") {
          setCurrentStep(7)
        }

        // Clean URL params after verification (replace history)
        if (typeof window !== "undefined") {
          window.history.replaceState(null, "", window.location.pathname)
        }
      } catch (error: any) {
        console.error("Error verifying VNPay return (admin):", error)
        setVnpReturnError(error?.response?.data?.message || error?.message || "Failed to verify payment")
      } finally {
        setIsVerifyingVnp(false)
      }
    }

    verifyReturn()
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
      console.log("Fetching booking summary for booking ID:", bookingId)
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
    if (currentStep === 5 && customerId) {
      console.log("Fetching loyalty points for customer ID:", customerId)
      const fetchPoints = async () => {
        try {
          const points = await getCustomerLoyaltyPoints(customerId)
          setCustomerPoints(points)
        } catch (error: any) {
          console.error("Error fetching loyalty points:", error)
        }
      }

      fetchPoints()
    }
  }, [currentStep, customerId])

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

  // Ensure booking is cancelled if user leaves the page (navigation away or tab close)
  useEffect(() => {
    const handleUnload = () => {
      const shouldSkip = skipCancelOnUnloadRef.current
      if (bookingId && !isCancellingBooking.current && !shouldSkip) {
        void cancelBooking(bookingId)
      }
    }

    window.addEventListener("beforeunload", handleUnload)

    return () => {
      window.removeEventListener("beforeunload", handleUnload)
      const shouldSkip = skipCancelOnUnloadRef.current
      if (bookingId && !isCancellingBooking.current && !shouldSkip) {
        void handleCancelBooking(bookingId)
      }
    }
  }, [bookingId])

  const handleSelectMovie = (movie: Movie) => {
    setSelectedMovie(movie)
    setCurrentStep(2)
    setSelectedShowtime(null)
    setSelectedSeats([])
    setSelectedCombos([])
    setCustomerName("")
    setCustomerEmail("")
    setCheckoutMode("guest")
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
        setCheckoutMode("guest")
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
    setCustomerId(null)
  }

  const handleGoBackToShowtimes = async () => {
    // Cancel current booking if exists
    if (bookingId) {
      await handleCancelBooking(bookingId)
      setBookingId(null)
      setCustomerId(null)
    }
    
    setCurrentStep(2)
    setSelectedShowtime(null)
    setSelectedSeats([])
    setSelectedCombos([])
    setCustomerName("")
    setCustomerEmail("")
    setCheckoutMode("guest")
  }

  const handleBack = async () => {
    if (currentStep <= 1) return

    const nextStep = currentStep - 1

    // If booking was already created (from combo step onward) and user moves back before combo,
    // cancel booking to release seats
    if (currentStep >= 4 && nextStep < 4 && bookingId) {
      await handleCancelBooking(bookingId)
      setBookingId(null)
      setCustomerId(null)
    }

    if (currentStep === 2) {
      handleGoBackToMovies()
    } else if (currentStep === 3) {
      await handleGoBackToShowtimes()
    } else if (currentStep === 4) {
      // Returning from combo to seats: refresh seats to reflect any changes
      setSelectedSeats([])
      setCurrentStep(3)
      if (selectedShowtime) {
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
          console.error("Error reloading seats:", error)
          setSeatsError(error?.response?.data?.message || "Failed to load seats")
          setSeats([])
        } finally {
          setSeatsLoading(false)
        }
      }
    } else {
      setCurrentStep(nextStep)
    }
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

    if (currentStep === 3 && checkoutMode === "member" && (!customerName.trim() || !customerEmail.trim())) {
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
          ...(checkoutMode === "member"
            ? {
                customerName: customerName.trim(),
                email: customerEmail.trim()
              }
            : {}),
        }

        const response = await createBooking(bookingRequest)
        setBookingId(response.id)
        setCustomerId(response.customerId || null)
        console.log("Booking created with ID:", response.id)
        console.log("Associated customer ID:", response.customerId)
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
    } else if (currentStep === 5 && bookingId) {
      // Redeem points (or send 0) before payment if we know the customer
      if (customerId) {
        try {
          setIsRedeemingPoints(true)
          const summary = await redeemBookingPoints(bookingId, { pointsToRedeem: pointsUsed || 0 })
          const discount = summary.discountAmount || 0
          setPointsDiscount(discount)
          setPointsUsed(discount > 0 ? Math.floor(discount / 1000) : 0)
        } catch (error: any) {
          console.error("Error redeeming points:", error)
          addNotification({
            type: "error",
            title: "Error",
            message: error?.response?.data?.message || "Failed to apply loyalty points"
          })
        } finally {
          setIsRedeemingPoints(false)
        }
      }

      setCurrentStep(6)
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

  const visualSteps = [
    {
      id: 1,
      title: "Select Screening",
      description: "Choose movie & time",
      isDone: currentStep > 2,
      isActive: currentStep <= 2,
    },
    {
      id: 2,
      title: "Select Seats",
      description: "Pick your seats",
      isDone: currentStep > 3,
      isActive: currentStep === 3,
    },
    {
      id: 3,
      title: "Confirm booking",
      description: "Summary details",
      isDone: currentStep > 5,
      isActive: currentStep === 4 || currentStep === 5,
    },
    {
      id: 4,
      title: "Payment",
      description: "Complete booking",
      isDone: currentStep >= 7,
      isActive: currentStep >= 6 && currentStep < 7,
    },
  ]

  const primaryCtaLabel = (() => {
    switch (currentStep) {
      case 1:
        return "Select showtime"
      case 2:
        return "Select seats"
      case 3:
        return "Choose combos"
      case 4:
        return "Review booking"
      case 5:
        return "Continue to payment"
      case 6:
        return "Complete booking"
      default:
        return "Next"
    }
  })()
  return (
    <div className="space-y-6">
      
      <PageHeader
        title="Staff Ticket Booking"
        description="Book tickets for customers"
      />

      <Card className="shadow-sm">
        {/* Progress Steps */}
        <div className="px-6 py-5 border-b bg-gray-50">
          <div className="flex items-center gap-4 pb-2">
            {visualSteps.map((step, idx) => (
              <div key={step.id} className="flex items-center gap-4 flex-1">
                <div
                  className={`w-10 h-10 rounded-full border-2 flex items-center justify-center text-sm font-bold flex-shrink-0 ${
                    step.isDone
                      ? "bg-emerald-50 border-emerald-500 text-emerald-600"
                      : step.isActive
                        ? "bg-blue-50 border-blue-500 text-blue-700"
                        : "bg-white border-gray-300 text-gray-500"
                  }`}
                >
                  {step.isDone ? <CheckCircle2 className="w-5 h-5" /> : step.id}
                </div>
                <div className="hidden sm:block">
                  <p className="text-sm font-semibold text-gray-900">{step.title}</p>
                  <p className="text-xs text-gray-500">{step.description}</p>
                </div>
                {idx < visualSteps.length - 1 && (
                  <div className={`h-[2px] flex-1 ${step.isDone ? "bg-emerald-500" : "bg-gray-200"}`} />
                )}
              </div>
            ))}
          </div>
          {(isVerifyingVnp || vnpReturnResult || vnpReturnError) ? (
            <div className="min-h-[40vh] flex items-center justify-center">
              <div className="w-full max-w-md">
                {isVerifyingVnp ? (
                  <div className="flex flex-col items-center gap-3 text-center py-8">
                    <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
                    <div className="text-lg font-semibold">Verifying VNPay payment...</div>
                    <div className="text-sm text-gray-600">Please wait a moment</div>
                  </div>
                ) : (
                  <div className="space-y-4">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <Badge variant={(vnpReturnResult?.code === "00") ? "default" : "destructive"} className="uppercase">
                          {(vnpReturnResult?.code === "00") ? "Success" : "Failed"}
                        </Badge>
                        <span className="text-sm text-gray-500">VNPay</span>
                      </div>
                    </div>

                    <div className="text-center space-y-2">
                      {(vnpReturnResult?.code === "00") ? (
                        <>
                          <CheckCircle className="w-12 h-12 text-emerald-600 mx-auto" />
                          <div className="text-xl font-bold text-emerald-700">Payment successful</div>
                          <div className="text-gray-600 text-sm">{vnpReturnResult?.message}</div>
                        </>
                      ) : (
                        <>
                          <XCircle className="w-12 h-12 text-red-600 mx-auto" />
                          <div className="text-xl font-bold text-red-700">Payment failed</div>
                          <div className="text-gray-600 text-sm">{vnpReturnError || vnpReturnResult?.message || "Transaction unsuccessful"}</div>
                        </>
                      )}
                    </div>

                    <div className="grid gap-2 text-sm text-left bg-gray-50 rounded-md p-3">
                      {vnpReturnResult?.txnRef && (
                        <div className="flex justify-between"><span className="text-gray-600">Txn Ref</span><span className="font-semibold text-xs">{vnpReturnResult.txnRef}</span></div>
                      )}
                      {typeof vnpReturnResult?.amount === "number" && (
                        <div className="flex justify-between"><span className="text-gray-600">Amount</span><span className="font-semibold">{vnpReturnResult.amount.toLocaleString()} VND</span></div>
                      )}
                      {vnpReturnResult?.orderInfo && (
                        <div className="flex justify-between"><span className="text-gray-600">Order Info</span><span className="font-semibold text-xs">{vnpReturnResult.orderInfo}</span></div>
                      )}
                      {vnpReturnResult?.bookingId && (
                        <div className="flex justify-between"><span className="text-gray-600">Booking ID</span><span className="font-semibold text-xs">{vnpReturnResult.bookingId}</span></div>
                      )}
                    </div>

                    <div className="flex flex-wrap gap-2 pt-2">
                      <Button
                        className="bg-blue-600 hover:bg-blue-700 flex-1"
                        onClick={() => {
                          setCurrentStep(vnpReturnResult?.code === "00" ? 7 : 1)
                          if (vnpReturnResult?.code !== "00") {
                            handleGoBackToMovies()
                          }
                        }}
                      >
                        Continue
                      </Button>
                      <Button variant="outline" className="flex-1" onClick={() => handleGoBackToMovies()}>
                        Back to booking
                      </Button>
                    </div>
                  </div>
                )}
              </div>
            </div>
          ) : (
            <>
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
                  checkoutMode={checkoutMode}
                  onCustomerNameChange={setCustomerName}
                  onCustomerEmailChange={setCustomerEmail}
                  onCheckoutModeChange={setCheckoutMode}
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

              {currentStep === 6 && bookingId && selectedShowtime && selectedMovie && (
                <PaymentStep
                  bookingId={bookingId}
                  total={total}
                  subtotal={subtotal}
                  discount={discount}
                  showtime={selectedShowtime}
                  movie={selectedMovie}
                  selectedSeats={selectedSeats}
                  selectedCombos={selectedCombos}
                  onPaymentSuccess={() => setCurrentStep(7)}
                  onExternalPaymentStart={() => {
                    skipCancelOnUnloadRef.current = true
                    setSkipCancelOnUnload(true)
                  }}
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
            </>
          )}
        </div>
        {/* Navigation & Summary */}
        <div className="px-6 py-4 border-t bg-gray-50 dark:bg-gray-900 flex items-center justify-between">
          <Button
            variant="outline"
            onClick={handleBack}
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
              isRedeemingPoints ||
              currentStep === 6 ||
              currentStep === 7
            }
            className="bg-blue-600 hover:bg-blue-700"
          >
            {currentStep === 7 ? "Done" : primaryCtaLabel}
            {currentStep !== 7 && <ChevronRight className="w-4 h-4 ml-2" />}
          </Button>
        </div>
      </Card>
    </div>
  )
}
