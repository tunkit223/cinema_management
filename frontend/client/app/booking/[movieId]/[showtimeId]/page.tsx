"use client"

import React, { useState, useEffect, useRef } from "react"
import Link from "next/link"
import { use } from "react"
import { ChevronLeft, ChevronRight } from "lucide-react"
import { generateSeats } from "@/lib/mock-data"
import { getMovieById, mapMovieForDisplay, getScreeningSeatsByScreeningId, mapScreeningSeatToSeat, getScreeningById, mapScreeningToShowtime, getCombos, mapComboForDisplay, getComboItemsByComboId, mapComboItemDetail } from "@/lib/api-movie"
import { createBooking, getBookingSummary, updateBookingCombos, redeemBookingPoints } from "@/services/bookingService"
import type { BookingSummaryResponse } from "@/services/bookingService"
import { getUserInfo, getToken } from "@/services/localStorageService"
import { getMyInfo, getCustomerLoyaltyPoints } from "@/services/customerService"
import type { Seat, ComboItem, Showtime } from "@/lib/types"
import SeatSelectionStep from "@/components/booking/seat-selection-step"
import ComboSelectionStep from "@/components/booking/combo-selection-step"
import ConfirmationStep from "@/components/booking/confirmation-step"
import PaymentStep from "@/components/booking/payment-step"
import SuccessStep from "@/components/booking/success-step"
import BookingTimer from "@/components/booking/booking-timer"

export default function BookingPage({
  params,
}: {
  params: Promise<{ movieId: string; showtimeId: string }>
}) {
  const { movieId, showtimeId } = use(params)
  
  const BOOKING_STORAGE_KEY = `booking_${movieId}_${showtimeId}`
  
  const [movie, setMovie] = useState<any>(null)
  const [showtime, setShowtime] = useState<Showtime | null>(null)
  const [loading, setLoading] = useState(true)
  const [seatsLoading, setSeatsLoading] = useState(true)
  const [seats, setSeats] = useState<Seat[]>([])

  const [currentStep, setCurrentStep] = useState(1)
  const [selectedSeats, setSelectedSeats] = useState<Seat[]>([])
  const [selectedCombos, setSelectedCombos] = useState<ComboItem[]>([])
  const [combos, setCombos] = useState<ComboItem[]>([])
  const [combosLoading, setCombosLoading] = useState(true)
  const [bookingSummary, setBookingSummary] = useState<BookingSummaryResponse | null>(null)
  const [isUpdatingCombos, setIsUpdatingCombos] = useState(false)
  const [isLoadingSummary, setIsLoadingSummary] = useState(false)
  const [customerPoints, setCustomerPoints] = useState(0)
  const [pointsUsed, setPointsUsed] = useState(0)
  const [pointsDiscount, setPointsDiscount] = useState(0)
  const [paymentSuccess, setPaymentSuccess] = useState(false)
  
  // Booking state
  const [bookingId, setBookingId] = useState<string | null>(null)
  const [bookingExpiredAt, setBookingExpiredAt] = useState<string | null>(null)
  const [isCreatingBooking, setIsCreatingBooking] = useState(false)
  const hasRunStep1Reset = useRef(false)

  // Save booking state to sessionStorage
  const saveBookingState = (state: any) => {
    try {
      sessionStorage.setItem(BOOKING_STORAGE_KEY, JSON.stringify(state))
    } catch (error) {
      console.error('Error saving booking state:', error)
    }
  }

  // Clear booking state from sessionStorage
  const clearBookingState = () => {
    try {
      sessionStorage.removeItem(BOOKING_STORAGE_KEY)
    } catch (error) {
      console.error('Error clearing booking state:', error)
    }
  }

  // Helper functions
  const fetchBookingSummary = async (id: string) => {
    setIsLoadingSummary(true)
    try {
      const summary = await getBookingSummary(id)
      setBookingSummary(summary)
      // Update expiredAt from summary if available
      if (summary.expiredAt && !bookingExpiredAt) {
        setBookingExpiredAt(summary.expiredAt)
      }
      // Convert discountAmount to points (1 point = 1000 VND)
      if (summary.discountAmount !== undefined && summary.discountAmount > 0) {
        const points = Math.floor(summary.discountAmount / 1000)
        setPointsUsed(points)
        setPointsDiscount(summary.discountAmount)
      }
    } catch (error: any) {
      console.error('Error fetching booking summary:', error?.response?.data || error.message || error)
    } finally {
      setIsLoadingSummary(false)
    }
  }

  const getMaxRedeemablePoints = () => {
    // Always use subtotal (before any discounts), never totalAmount
    const subtotalValue = Number(bookingSummary?.subTotal ?? subtotal)
    const fiftyPercentCap = Math.floor(subtotalValue * 0.5)
    // Cap in points: can't redeem more points than available, and discount (points * 1000) can't exceed subtotal or 50% cap
    const maxPointsBySubtotal = Math.floor(subtotalValue / 1000)
    const maxPointsByFiftyCap = Math.floor(fiftyPercentCap / 1000)
    return Math.max(0, Math.min(customerPoints, maxPointsBySubtotal, maxPointsByFiftyCap))
  }

  const handleApplyPoints = (points: number) => {
    const maxRedeem = getMaxRedeemablePoints()
    const safePoints = Math.min(points, maxRedeem)
    setPointsUsed(safePoints)
    // 1 point = 1000 VND discount
    setPointsDiscount(safePoints * 1000)
  }

  const handleBookingExpired = () => {
    alert('Booking has expired. Please select seats again.')
    // Clear saved state
    clearBookingState()
    // Reset state
    setBookingId(null)
    setBookingExpiredAt(null)
    setBookingSummary(null)
    setSelectedCombos([])
    setSelectedSeats([])
    setCurrentStep(1)
    setPointsUsed(0)
    setPointsDiscount(0)
  }

  // Restore booking state from sessionStorage on mount
  useEffect(() => {
    try {
      const savedState = sessionStorage.getItem(BOOKING_STORAGE_KEY)
      if (savedState) {
        const state = JSON.parse(savedState)
        
        // Only restore if step >= 2 (from combo selection onwards)
        if (state.currentStep && state.currentStep >= 2) {
          // Check if booking is not expired
          if (state.bookingExpiredAt) {
            const expiredAt = new Date(state.bookingExpiredAt)
            const now = new Date()
            
            if (now < expiredAt) {
              // Restore state
              if (state.bookingId) setBookingId(state.bookingId)
              if (state.bookingExpiredAt) setBookingExpiredAt(state.bookingExpiredAt)
              if (state.currentStep) setCurrentStep(state.currentStep)
              if (state.selectedSeats) setSelectedSeats(state.selectedSeats)
              if (state.selectedCombos) setSelectedCombos(state.selectedCombos)
              if (state.pointsUsed) setPointsUsed(state.pointsUsed)
              if (state.pointsDiscount) setPointsDiscount(state.pointsDiscount)
              console.log('Booking state restored from sessionStorage')
            } else {
              // Booking expired, clear storage
              clearBookingState()
              console.log('Saved booking has expired, cleared from storage')
            }
          }
        } else {
          // Step 1, clear any saved state
          clearBookingState()
        }
      }
    } catch (error) {
      console.error('Error restoring booking state:', error)
    }
  }, [])

  // Fetch movie from API
  useEffect(() => {
    const fetchMovie = async () => {
      try {
        setLoading(true)
        const data = await getMovieById(movieId)
        if (data) {
          const mappedMovie = mapMovieForDisplay(data)
          setMovie(mappedMovie)
        }
      } catch (error) {
        console.error('Error fetching movie:', error)
      } finally {
        setLoading(false)
      }
    }
    fetchMovie()
  }, [movieId])

  // Fetch showtime from API
  useEffect(() => {
    const fetchShowtime = async () => {
      try {
        const screeningData = await getScreeningById(showtimeId)
        if (screeningData) {
          const mappedShowtime = mapScreeningToShowtime(screeningData)
          setShowtime(mappedShowtime)
        }
      } catch (error) {
        console.error('Error fetching showtime:', error)
        setShowtime(null)
      }
    }
    if (showtimeId) {
      fetchShowtime()
    }
  }, [showtimeId])

  // Fetch combos from backend
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
              console.error('Error fetching combo items:', error)
              return { ...combo, items: [] }
            }
          })
        )

        setCombos(combosWithItems)
      } catch (error) {
        console.error('Error fetching combos:', error)
        setCombos([])
      } finally {
        setCombosLoading(false)
      }
    }

    fetchCombos()
  }, [])

  // Fetch showtime from API
  useEffect(() => {
    const fetchShowtime = async () => {
      try {
        const screeningData = await getScreeningById(showtimeId)
        if (screeningData) {
          const mappedShowtime = mapScreeningToShowtime(screeningData)
          setShowtime(mappedShowtime)
        }
      } catch (error) {
        console.error('Error fetching showtime:', error)
        setShowtime(null)
      }
    }
    if (showtimeId) {
      fetchShowtime()
    }
  }, [showtimeId])

  // Fetch seats from API
  useEffect(() => {
    const fetchSeats = async () => {
      try {
        setSeatsLoading(true)
        const seatData = await getScreeningSeatsByScreeningId(showtimeId)
        
        if (seatData && Array.isArray(seatData)) {
          // Map backend seats to Seat format and sort by row then seat number
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
          // Fallback to mock data if no seats from backend
          console.warn('No seat data from backend, using mock data')
          setSeats(generateSeats())
        }
      } catch (error) {
        console.error('Error fetching seats:', error)
        // Fallback to mock data
        setSeats(generateSeats())
      } finally {
        setSeatsLoading(false)
      }
    }

    if (showtimeId) {
      fetchSeats()
    }
  }, [showtimeId])

  // Save booking state whenever key states change (only from step 2 onwards)
  useEffect(() => {
    if (bookingId && currentStep >= 2 && currentStep < 5) {
      saveBookingState({
        bookingId,
        bookingExpiredAt,
        currentStep,
        selectedSeats,
        selectedCombos,
        pointsUsed,
        pointsDiscount,
      })
    } else if (currentStep === 1) {
      // Clear state when going back to step 1
      clearBookingState()
    }
  }, [bookingId, bookingExpiredAt, currentStep, selectedSeats, selectedCombos, pointsUsed, pointsDiscount])

  // Reset booking summary when going back to step 2 or step 1
  useEffect(() => {
    if (currentStep === 2) {
      // Reset booking summary when returning to combo selection
      // This allows real-time combo updates
      setBookingSummary(null)
      setPointsUsed(0)
      setPointsDiscount(0)
      console.log('Booking summary reset for step 2')
    } else if (currentStep === 1 && showtimeId) {
      // Skip the initial mount to avoid a duplicate seat fetch (causing timeout)
      if (!hasRunStep1Reset.current) {
        hasRunStep1Reset.current = true
        return
      }

      // Reset booking-related state without reloading seats to avoid timeout
      setBookingId(null)
      setBookingExpiredAt(null)
      setBookingSummary(null)
      setSelectedSeats([])
      setSelectedCombos([])
      setPointsUsed(0)
      setPointsDiscount(0)
      console.log('Booking state reset for step 1')
      
      // Reload seats to show updated availability
      const reloadSeats = async () => {
        try {
          setSeatsLoading(true)
          const seatData = await getScreeningSeatsByScreeningId(showtimeId)
          
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
            console.log('Seats reloaded for step 1')
          } else {
            console.warn('No seat data from backend')
            setSeats(generateSeats())
          }
        } catch (error) {
          console.error('Error reloading seats for step 1:', error)
          // Keep existing seats if reload fails
        } finally {
          setSeatsLoading(false)
        }
      }
      
      reloadSeats()
    }
  }, [currentStep, showtimeId])

  // Load booking summary only when entering confirmation step (step 3)
  useEffect(() => {
    if (currentStep === 3 && bookingId) {
      // Always fetch summary when entering step 3, or if we don't have summary yet
      if (!bookingSummary || bookingSummary.bookingId !== bookingId) {
        console.log('Fetching booking summary for confirmation step')
        fetchBookingSummary(bookingId)
      }
    }
  }, [currentStep, bookingId, bookingSummary])

  // Fetch customer loyalty points when entering confirmation step
  useEffect(() => {
    const fetchCustomerPoints = async () => {
      try {
        const token = getToken()
        if (!token) return

        let userInfo = getUserInfo()
        if (!userInfo || (!userInfo.id && !userInfo.customerId)) {
          userInfo = await getMyInfo()
        }

        const customerId = userInfo?.id || userInfo?.customerId
        if (customerId) {
          const points = await getCustomerLoyaltyPoints(customerId)
          setCustomerPoints(points)
        }
      } catch (error: any) {
        console.error('Error fetching customer loyalty points:', error)
      }
    }

    if (currentStep === 3) {
      fetchCustomerPoints()
    }
  }, [currentStep])

  // Update booking summary when combos change (step 2 onwards, if booking exists)
  useEffect(() => {
    const updateSummaryOnComboChange = async () => {
      if (currentStep >= 2 && bookingId && selectedCombos.length >= 0) {
        try {
          console.log('Fetching updated booking summary due to combo change')
          fetchBookingSummary(bookingId)
        } catch (error: any) {
          console.error('Error updating booking summary on combo change:', error)
        }
      }
    }

    updateSummaryOnComboChange()
  }, [selectedCombos, bookingId, currentStep])

  if (loading) {
    return (
      <div className="min-h-screen bg-background dark:bg-slate-950 flex items-center justify-center">
        <div className="text-center">
          <p className="text-lg">Loading...</p>
        </div>
      </div>
    )
  }

  if (!movie || !showtime) {
    return (
      <div className="min-h-screen bg-background dark:bg-slate-950 flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-3xl font-bold mb-4">Booking Not Found</h1>
          <p className="text-muted-foreground mb-4">Movie ID: {movieId}</p>
          <p className="text-muted-foreground mb-4">Showtime ID: {showtimeId}</p>
          <Link href="/" className="text-purple-600 hover:text-purple-700 font-semibold">
            Back to Home
          </Link>
        </div>
      </div>
    )
  }

  const seatPrice = showtime.price
  // Only use bookingSummary from step 3 onwards (confirmation step)
  const useBookingSummary = bookingSummary && currentStep >= 3
  const seatsTotal = useBookingSummary
    ? Number(bookingSummary.seatSubtotal ?? 0)
    : selectedSeats.reduce((sum, seat) => sum + (seat.price || seatPrice), 0)
  const comboTotal = useBookingSummary
    ? Number(bookingSummary.comboSubtotal ?? 0)
    : selectedCombos.reduce((sum, combo) => sum + (combo.price * (combo.quantity || 1)), 0)
  const subtotal = useBookingSummary ? Number(bookingSummary.subTotal ?? seatsTotal + comboTotal) : seatsTotal + comboTotal
  // Calculate total: prefer live pointsDiscount for immediate UI update; otherwise fallback to server discountAmount
  const discount = pointsDiscount > 0
    ? pointsDiscount
    : (useBookingSummary && bookingSummary?.discountAmount !== undefined
        ? Number(bookingSummary.discountAmount)
        : 0)
  const total = Math.max(0, subtotal - discount)

  const nextButtonLabel = isCreatingBooking
    ? 'Creating booking...'
    : isUpdatingCombos
      ? 'Saving combos...'
      : isLoadingSummary
        ? 'Loading summary...'
        : 'Next'

  const summarySeatCount = bookingSummary?.seats?.length ?? selectedSeats.length
  const summaryComboCount = bookingSummary?.combos?.length ?? selectedCombos.length

  const handleNextStep = async () => {
    if (currentStep === 1 && selectedSeats.length === 0) {
      return
    }

    // Nếu đang ở step 1 và chưa tạo booking, thì tạo booking trước
    if (currentStep === 1 && !bookingId && selectedSeats.length > 0) {
      try {
        setIsCreatingBooking(true)
        
        // Kiểm tra đăng nhập
        const token = getToken()
        console.log('Token:', token ? 'exists' : 'not found')
        
        if (!token) {
          alert('Please sign in to continue booking')
          return
        }

        // Lấy thông tin customer từ localStorage hoặc API
        let userInfo = getUserInfo()
        console.log('UserInfo from localStorage:', userInfo)
        
        // Nếu không có userInfo/id/customerId trong localStorage (trường hợp login bằng email/password), gọi API
        if (!userInfo || (!userInfo.id && !userInfo.customerId)) {
          console.log('Fetching user info from API...')
          try {
            userInfo = await getMyInfo()
            console.log('UserInfo from API:', userInfo)
          } catch (error: any) {
            console.error('Error fetching user info:', error)
            console.error('Error response:', error?.response?.data)
            alert(`Unable to fetch user info: ${error?.response?.data?.message || error.message || 'Please sign in again.'}`)
            return
          }
        }

        const customerId = userInfo?.id || userInfo?.customerId

        if (!customerId) {
          console.error('UserInfo still invalid (missing id/customerId):', userInfo)
          alert('User info not found. Please sign in again.')
          return
        }

        console.log('Creating booking with customerId:', customerId)

        // Tạo booking request
        const bookingRequest = {
          customerId,
          screeningId: showtimeId,
          screeningSeatIds: selectedSeats.map(seat => seat.id)
        }
        
        console.log('Booking request:', bookingRequest)

        const response = await createBooking(bookingRequest)
        console.log('Booking response:', response)
        
        setBookingId(response.id)
        setBookingExpiredAt(response.expiredAt)
        setBookingSummary(null)
        
        // Chuyển sang step tiếp theo
        setCurrentStep(currentStep + 1)
      } catch (error: any) {
        console.error('Error creating booking:', error)
        console.error('Error details:', error?.response?.data)
        
        const errorMessage = error?.response?.data?.message || error.message || 'Please try again.'
        
        // Handle specific error: seats not available
        if (errorMessage.includes('not available') || errorMessage.includes('seats')) {
          alert(`⚠️ Booking Error: ${errorMessage}\n\nSome seats may have been booked by other users. Please select seats again.`)
          
          // Reload seats from API
          try {
            setSeatsLoading(true)
            const seatData = await getScreeningSeatsByScreeningId(showtimeId)
            
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
            }
          } catch (reloadError) {
            console.error('Error reloading seats:', reloadError)
          } finally {
            setSeatsLoading(false)
          }
          
          // Clear selected seats
          setSelectedSeats([])
        } else {
          alert(`Unable to create booking: ${errorMessage}`)
        }
      } finally {
        setIsCreatingBooking(false)
      }
    } else if (currentStep === 2) {
      if (!bookingId) {
        alert('Please create a booking first by selecting seats.')
        return
      }

      try {
        setIsUpdatingCombos(true)

        const combosPayload = selectedCombos.map((combo) => ({
          comboId: combo.id,
          quantity: combo.quantity && combo.quantity > 0 ? combo.quantity : 1,
        }))

        await updateBookingCombos(bookingId, { combos: combosPayload })

        setCurrentStep(currentStep + 1)
      } catch (error: any) {
        console.error('Error updating combos:', error)
        alert(`Unable to update combos: ${error?.response?.data?.message || error.message || 'Please try again.'}`)
      } finally {
        setIsUpdatingCombos(false)
      }
    } else if (currentStep === 3) {
      if (!bookingId) {
        alert('Booking not found. Please go back and create booking again.')
        return
      }

      try {
        const maxRedeem = getMaxRedeemablePoints()
        const pointsToRedeem = Math.min(pointsUsed, maxRedeem)

        if (pointsToRedeem > 0) {
          setIsLoadingSummary(true)
          const summary = await redeemBookingPoints(bookingId, { pointsToRedeem })
          setBookingSummary(summary)
          setPointsUsed(pointsToRedeem)
          // 1 point = 1000 VND discount
          setPointsDiscount(pointsToRedeem * 1000)
        }

        setCurrentStep(currentStep + 1)
      } catch (error: any) {
        console.error('Error redeeming points:', error)
        alert(`Unable to redeem points: ${error?.response?.data?.message || error.message || 'Please try again.'}`)
      } finally {
        setIsLoadingSummary(false)
      }
    } else {
      // Các bước khác thì chỉ cần chuyển step, summary sẽ được gọi khi vào bước 3
      setCurrentStep(currentStep + 1)
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
      {bookingExpiredAt && currentStep > 1 && currentStep < 5 && (
        <div className="fixed top-16 md:top-20 left-1/2 -translate-x-1/2 z-50">
          <BookingTimer expiredAt={bookingExpiredAt} onExpired={handleBookingExpired} />
        </div>
      )}

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Movie Info */}
        <div className="mb-8">
          <div className="flex gap-6">
            <div className="w-24 h-36 rounded-lg overflow-hidden flex-shrink-0">
              {movie?.posterUrl && (
                <img src={movie.posterUrl} alt={movie.title} className="w-full h-full object-cover" />
              )}
            </div>
            <div className="flex-1">
              <h1 className="text-3xl font-bold mb-2">{movie?.title}</h1>
              <p className="text-muted-foreground mb-2">{movie?.genre}</p>
              <p className="text-sm text-muted-foreground">
                {showtime?.time} • {showtime?.format} • {showtime?.price.toLocaleString()} VND
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
              seatsLoading ? (
                <div className="bg-card dark:bg-slate-900 border border-border dark:border-slate-800 rounded-xl p-8 flex items-center justify-center min-h-[400px]">
                  <div className="text-center">
                    <div className="inline-block w-8 h-8 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
                    <p className="text-muted-foreground mt-4">Loading seats...</p>
                  </div>
                </div>
              ) : (
                <SeatSelectionStep
                  seats={seats}
                  selectedSeats={selectedSeats}
                  onSelectSeats={setSelectedSeats}
                  seatPrice={seatPrice}
                />
              )
            )}
            {currentStep === 2 && (
              combosLoading ? (
                <div className="bg-card dark:bg-slate-900 border border-border dark:border-slate-800 rounded-xl p-8 flex items-center justify-center min-h-[300px]">
                  <div className="text-center">
                    <div className="inline-block w-8 h-8 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
                    <p className="text-muted-foreground mt-4">Loading combos...</p>
                  </div>
                </div>
              ) : (
                <ComboSelectionStep combos={combos} selectedCombos={selectedCombos} onSelectCombos={setSelectedCombos} />
              )
            )}
            {currentStep === 3 && showtime && (
              <ConfirmationStep
                movie={movie}
                showtime={showtime}
                selectedSeats={selectedSeats}
                selectedCombos={selectedCombos}
                subtotal={subtotal}
                pointsUsed={pointsUsed}
                pointsDiscount={pointsDiscount}
                total={total}
                customerPoints={customerPoints}
                onApplyPoints={handleApplyPoints}
                bookingSummary={bookingSummary}
                isLoadingSummary={isLoadingSummary}
              />
            )}
            {currentStep === 4 && (
              <PaymentStep
                total={total}
                onPaymentSuccess={() => {
                  setPaymentSuccess(true)
                  setCurrentStep(5)
                  // Clear booking state on success
                  clearBookingState()
                }}
              />
            )}
            {currentStep === 5 && showtime && (
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
                  <span className="text-muted-foreground">Seats ({summarySeatCount})</span>
                  <span className="font-semibold">{seatsTotal.toLocaleString()} VND</span>
                </div>
                {summaryComboCount > 0 && (
                  <div className="flex justify-between text-sm">
                    <span className="text-muted-foreground">Combos ({summaryComboCount})</span>
                    <span className="font-semibold">{comboTotal.toLocaleString()} VND</span>
                  </div>
                )}
                {discount > 0 && (
                  <div className="flex justify-between text-sm text-green-600">
                    <span>Points Discount</span>
                    <span>-{discount.toLocaleString()} VND</span>
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
                    onClick={handleNextStep}
                    disabled={
                      (currentStep === 1 && selectedSeats.length === 0) ||
                      (currentStep === 2 && !bookingId) ||
                      (currentStep === 3 && total === 0) ||
                      isCreatingBooking ||
                      isUpdatingCombos ||
                      isLoadingSummary
                    }
                    className="w-full px-4 py-3 rounded-lg gradient-primary text-white font-semibold hover:shadow-lg transition-all flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {nextButtonLabel}
                    {!isCreatingBooking && !isUpdatingCombos && !isLoadingSummary && <ChevronRight size={20} />}
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
