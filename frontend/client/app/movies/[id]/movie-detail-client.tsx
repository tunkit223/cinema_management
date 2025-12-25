"use client";

import { useState, useMemo, useEffect } from "react";
import Link from "next/link";
import { ChevronLeft, Clock, Users, MapPin, DoorOpen, Calendar, ChevronRight } from "lucide-react";
import { formatCurrency, formatDate } from "@/lib/mock-booking-helper";
import { getScreeningsByMovieId, mapScreeningToShowtime } from "@/lib/api-movie";
import type { Movie, Showtime } from "@/lib/types";

interface MovieDetailClientProps {
  movie: Movie;
}

export default function MovieDetailClient({ movie }: MovieDetailClientProps) {
  const [movieShowtimes, setMovieShowtimes] = useState<Showtime[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedShowtime, setSelectedShowtime] = useState<Showtime | null>(null);
  const [selectedCinema, setSelectedCinema] = useState<string>("all");
  
  // Fetch screenings from backend
  useEffect(() => {
    async function fetchScreenings() {
      try {
        setLoading(true);
        const screenings = await getScreeningsByMovieId(movie.id);
        const showtimes = screenings
          .map(mapScreeningToShowtime)
          .filter((st: any) => st !== null)
          .sort((a: Showtime, b: Showtime) => {
            // Sort by date then time
            const dateCompare = (a.date || '').localeCompare(b.date || '');
            if (dateCompare !== 0) return dateCompare;
            return a.time.localeCompare(b.time);
          });
        setMovieShowtimes(showtimes);
      } catch (error) {
        console.error("Error fetching screenings:", error);
        setMovieShowtimes([]);
      } finally {
        setLoading(false);
      }
    }

    if (movie.id) {
      fetchScreenings();
    }
  }, [movie.id]);
  
  // Get unique cinemas from showtimes
  const cinemas = useMemo(() => {
    const uniqueCinemas = new Map();
    movieShowtimes.forEach((st: any) => {
      if (st.cinemaId && st.cinemaName && !uniqueCinemas.has(st.cinemaId)) {
        uniqueCinemas.set(st.cinemaId, {
          id: st.cinemaId,
          name: st.cinemaName,
        });
      }
    });
    return Array.from(uniqueCinemas.values());
  }, [movieShowtimes]);
  
  // Helper function to get today's date string (always fresh) - using local timezone
  const getTodayStr = () => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  // Helper function to check if a showtime is in the past
  const isShowtimePast = (date: string | undefined, time: string): boolean => {
    if (!date) return false;
    
    const todayStr = getTodayStr();
    
    // If the date is in the past, the showtime is definitely past
    if (date < todayStr) return true;
    
    // If the date is today, check if the time has passed
    if (date === todayStr) {
      const now = new Date();
      const currentTimeStr = String(now.getHours()).padStart(2, '0') + ':' + String(now.getMinutes()).padStart(2, '0');
      return time <= currentTimeStr;
    }
    
    // If the date is in the future, the showtime is not past
    return false;
  };
  
  // Calendar state
  const today = new Date();
  const [currentMonth, setCurrentMonth] = useState(today.getMonth());
  const [currentYear, setCurrentYear] = useState(today.getFullYear());
  
  // Available dates from showtimes
  const availableDates = useMemo(() => {
    const dates = [...new Set(movieShowtimes.map(st => st.date))];
    return dates;
  }, [movieShowtimes]);
  
  // Default selected date - chọn ngày gần nhất >= hôm nay
  const [selectedDate, setSelectedDate] = useState<string>(getTodayStr());
  
  // Update selected date when showtimes change
  useEffect(() => {
    if (movieShowtimes.length === 0) return;
    
    const todayStr = getTodayStr();
    const dates = [...new Set(movieShowtimes.map(st => st.date))].sort();
    const futureDate = dates.find(date => date >= todayStr);
    
    // Always update to a valid future date
    if (futureDate) {
      setSelectedDate(futureDate);
    }
  }, [movieShowtimes]);
  
  // Generate calendar days
  const calendarDays = useMemo(() => {
    const firstDay = new Date(currentYear, currentMonth, 1);
    const lastDay = new Date(currentYear, currentMonth + 1, 0);
    const startingDayOfWeek = firstDay.getDay();
    const monthLength = lastDay.getDate();
    
    const todayStr = getTodayStr();
    
    const days = [];
    
    // Add empty cells for days before the first day
    for (let i = 0; i < startingDayOfWeek; i++) {
      days.push(null);
    }
    
    // Add days of the month
    for (let day = 1; day <= monthLength; day++) {
      const dateStr = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
      const hasShowtime = availableDates.includes(dateStr);
      const isToday = dateStr === todayStr;
      const isSelected = dateStr === selectedDate;
      const isPast = dateStr < todayStr;
      
      days.push({
        day,
        dateStr,
        hasShowtime,
        isToday,
        isSelected,
        isPast
      });
    }
    
    return days;
  }, [currentMonth, currentYear, availableDates, selectedDate]);
  
  // Month navigation
  const goToPreviousMonth = () => {
    if (currentMonth === 0) {
      setCurrentMonth(11);
      setCurrentYear(currentYear - 1);
    } else {
      setCurrentMonth(currentMonth - 1);
    }
  };
  
  const goToNextMonth = () => {
    if (currentMonth === 11) {
      setCurrentMonth(0);
      setCurrentYear(currentYear + 1);
    } else {
      setCurrentMonth(currentMonth + 1);
    }
  };

  // Filter showtimes by selected cinema and date
  const filteredShowtimes = useMemo(() => {
    return movieShowtimes.filter(st => {
      const matchCinema = selectedCinema === "all" || st.cinemaId === selectedCinema;
      const matchDate = st.date === selectedDate;
      const notPast = !isShowtimePast(st.date, st.time);
      return matchCinema && matchDate && notPast;
    });
  }, [movieShowtimes, selectedCinema, selectedDate]);

  if (!movie) {
    return (
      <div className="min-h-screen bg-background dark:bg-slate-950 flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-3xl font-bold mb-4">Movie Not Found</h1>
          <Link href="/" className="text-purple-600 hover:text-purple-700 font-semibold">
            Back to Home
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background dark:bg-slate-950 pt-20">
      {/* Movie Hero */}
      <div className="relative h-96 md:h-[500px] overflow-hidden bg-gradient-to-b from-purple-900/20 to-background dark:to-slate-950">
        {/* Poster background */}
        <img
          src={movie.poster || "/placeholder.svg"}
          alt={movie.title}
          className="w-full h-full object-cover opacity-40"
          onError={(e) => { e.currentTarget.src = "/placeholder.svg" }}
        />

        {/* Gradient overlay */}
        <div className="absolute inset-0 bg-gradient-to-t from-background dark:from-slate-950 via-transparent" />

        {/* Back to Home Button — overlay trên poster */}
        <div className="absolute top-6 left-6 z-20">
          <Link
            href="/"
            className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-black/40 backdrop-blur-md
                       text-white font-medium hover:bg-black/60 transition-colors"
          >
            <ChevronLeft size={20} />
            Back to Home
          </Link>
        </div>
      </div>

      {/* Movie Info */}
      <div className="container-max px-4 md:px-8 -mt-32 relative z-10 mb-12">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* Poster */}
          <div className="flex justify-center md:justify-start">
            <img
              src={movie.poster || "/placeholder.svg"}
              alt={movie.title}
              className="w-48 h-72 rounded-xl shadow-2xl object-cover"
              onError={(e) => { e.currentTarget.src = "/placeholder.svg" }}
            />
          </div>

          {/* Details */}
          <div className="md:col-span-2 space-y-6">
            <div>
              <h1 className="text-4xl md:text-5xl font-bold mb-4">{movie.title}</h1>
              <div className="flex flex-wrap gap-2 mb-4">
                {movie.genre.map((g) => (
                  <span
                    key={g}
                    className="px-3 py-1 rounded-full bg-purple-500/20 text-purple-600 dark:text-purple-300 text-sm font-semibold"
                  >
                    {g}
                  </span>
                ))}
              </div>
            </div>

            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div className="bg-card dark:bg-slate-900 rounded-lg p-4 border border-border dark:border-slate-800">
                <p className="text-muted-foreground text-sm mb-1">Rating</p>
                <p className="text-2xl font-bold">{movie.rating}</p>
              </div>
              <div className="bg-card dark:bg-slate-900 rounded-lg p-4 border border-border dark:border-slate-800">
                <p className="text-muted-foreground text-sm mb-1">Duration</p>
                <p className="text-2xl font-bold flex items-center gap-2">
                  <Clock size={20} />
                  {movie.duration}m
                </p>
              </div>
              <div className="bg-card dark:bg-slate-900 rounded-lg p-4 border border-border dark:border-slate-800">
                <p className="text-muted-foreground text-sm mb-1">Release</p>
                <p className="text-lg font-bold">{new Date(movie.releaseDate).toLocaleDateString()}</p>
              </div>
              <div className="bg-card dark:bg-slate-900 rounded-lg p-4 border border-border dark:border-slate-800">
                <p className="text-muted-foreground text-sm mb-1">Director</p>
                <p className="text-lg font-bold">{movie.director}</p>
              </div>
            </div>

            <div>
              <h3 className="text-lg font-bold mb-2">Cast</h3>
              <p className="text-muted-foreground">{movie.cast.join(", ")}</p>
            </div>

            <div>
              <h3 className="text-lg font-bold mb-2">Synopsis</h3>
              <p className="text-muted-foreground leading-relaxed">{movie.description}</p>
            </div>

            {/* Trailer button */}
            {movie.trailerUrl && (
              <div>
                <a
                  href={movie.trailerUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center gap-2 px-6 py-3 rounded-lg bg-red-600 text-white font-semibold hover:bg-red-700 transition-colors"
                >
                  <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M8 5v14l11-7z" />
                  </svg>
                  Watch Trailer
                </a>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Showtimes Section */}
      <div className="container-max px-4 md:px-8 py-12">
        <h2 className="text-3xl font-bold mb-8">Select Showtime</h2>

        {loading ? (
          <div className="text-center py-12">
            <div className="inline-block w-8 h-8 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
            <p className="text-muted-foreground mt-4">Loading showtimes...</p>
          </div>
        ) : movieShowtimes.length === 0 ? (
          <div className="text-center py-12 bg-card dark:bg-slate-900 rounded-xl border border-border dark:border-slate-800">
            <p className="text-muted-foreground text-lg mb-2">No showtimes available</p>
            <p className="text-muted-foreground text-sm">Please check back later for updated showtimes.</p>
          </div>
        ) : (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          {/* Calendar Date Selection */}
          <div>
            <div className="flex items-center gap-2 mb-4">
              <Calendar size={20} className="text-purple-600" />
              <h3 className="text-lg font-semibold">Select Date</h3>
            </div>
            <div className="bg-card dark:bg-slate-900 border border-border dark:border-slate-800 rounded-xl p-4">
              {/* Calendar Header */}
              <div className="flex items-center justify-between mb-4">
                <button
                  onClick={goToPreviousMonth}
                  className="p-1.5 hover:bg-purple-500/10 rounded-lg transition-colors"
                >
                  <ChevronLeft size={18} />
                </button>
                <div className="text-base font-bold uppercase">
                  {new Date(currentYear, currentMonth).toLocaleDateString('en-US', { month: 'long', year: 'numeric' })}
                </div>
                <button
                  onClick={goToNextMonth}
                  className="p-1.5 hover:bg-purple-500/10 rounded-lg transition-colors"
                >
                  <ChevronRight size={18} />
                </button>
              </div>

              {/* Day of week headers */}
              <div className="grid grid-cols-7 gap-1 mb-2">
                {['S', 'M', 'T', 'W', 'T', 'F', 'S'].map((day, idx) => (
                  <div key={`${day}-${idx}`} className="text-center text-xs font-semibold text-muted-foreground py-1">
                    {day}
                  </div>
                ))}
              </div>

              {/* Calendar Grid */}
              <div className="grid grid-cols-7 gap-1">
                {calendarDays.map((dayObj, idx) => {
                  if (!dayObj) {
                    return <div key={`empty-${idx}`} className="aspect-square"></div>;
                  }

                  const { day, dateStr, hasShowtime, isToday, isSelected, isPast } = dayObj;
                  const isDisabled = !hasShowtime || isPast;

                  return (
                    <button
                      key={dateStr}
                      onClick={() => {
                        if (hasShowtime && !isPast) {
                          setSelectedDate(dateStr);
                          setSelectedShowtime(null);
                        }
                      }}
                      disabled={isDisabled}
                      className={`aspect-square rounded-lg flex items-center justify-center text-sm font-semibold transition-all ${
                        isSelected
                          ? "bg-purple-600 text-white shadow-md"
                          : isToday && hasShowtime && !isPast
                          ? "border-2 border-purple-600 text-purple-600 hover:bg-purple-500/10"
                          : hasShowtime && !isPast
                          ? "hover:bg-purple-500/10"
                          : "text-muted-foreground/30 cursor-not-allowed"
                      }`}
                    >
                      {day}
                    </button>
                  );
                })}
              </div>
            </div>
          </div>

          {/* Cinema Dropdown Selection */}
          <div>
            <div className="flex items-center gap-2 mb-4">
              <MapPin size={20} className="text-purple-600" />
              <h3 className="text-lg font-semibold">Select Cinema</h3>
            </div>
            <div className="relative">
              <select
                value={selectedCinema}
                onChange={(e) => {
                  setSelectedCinema(e.target.value);
                  setSelectedShowtime(null);
                }}
                className="w-full px-4 py-3 bg-card dark:bg-slate-900 border border-border dark:border-slate-800 rounded-xl appearance-none cursor-pointer hover:border-purple-600 transition-colors focus:outline-none focus:ring-2 focus:ring-purple-600 text-base"
              >
                <option value="all">All Cinemas - Show all locations</option>
                {cinemas.map((cinema: any) => (
                  <option key={cinema.id} value={cinema.id}>
                    {cinema.name}
                  </option>
                ))}
              </select>
              <div className="absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none">
                <ChevronRight className="rotate-90" size={20} />
              </div>
            </div>
            
            {/* Selected Cinema Info */}
            {selectedCinema !== "all" && (
              <div className="mt-4 p-4 bg-purple-500/10 dark:bg-purple-900/20 border border-purple-600/30 rounded-xl">
                <div className="flex items-start gap-3">
                  <div className="w-10 h-10 rounded-full bg-purple-600 flex items-center justify-center flex-shrink-0">
                    <MapPin size={20} className="text-white" />
                  </div>
                  <div>
                    <div className="font-semibold text-purple-600 dark:text-purple-300">
                      {cinemas.find((c: any) => c.id === selectedCinema)?.name}
                    </div>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Showtimes Grid */}
        <div>
          <h3 className="text-xl font-semibold mb-4">
            Available Showtimes
            <span className="text-muted-foreground text-base font-normal ml-2">
              ({formatDate(selectedDate)})
            </span>
          </h3>
          
          {filteredShowtimes.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              {filteredShowtimes.map((showtime) => {
                const showtimeData = showtime as any;
                return (
                  <button
                    key={showtime.id}
                    onClick={() => setSelectedShowtime(showtime)}
                    className={`p-4 rounded-xl border-2 transition-all text-left ${
                      selectedShowtime?.id === showtime.id
                        ? "border-purple-600 bg-purple-500/10 dark:bg-purple-900/20 shadow-lg"
                        : "border-border dark:border-slate-800 bg-card dark:bg-slate-900 hover:border-purple-600"
                    }`}
                  >
                    <p className="text-xl font-bold mb-2">{showtime.time}</p>
                    
                    {showtimeData.cinemaName && selectedCinema === "all" && (
                      <div className="flex items-center gap-1 text-xs text-muted-foreground mb-2">
                        <MapPin size={14} />
                        <span className="truncate">{showtimeData.cinemaName}</span>
                      </div>
                    )}
                    
                    {showtimeData.roomName && (
                      <div className="flex items-center gap-1 text-xs text-muted-foreground mb-2">
                        <DoorOpen size={14} />
                        <span>{showtimeData.roomName}</span>
                      </div>
                    )}
                    
                    <p className="text-sm text-purple-600 dark:text-purple-300 font-semibold mb-2">
                      {showtime.format}
                    </p>
                    
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-purple-600">
                        {formatCurrency(showtime.price)}
                      </span>
                      <div className="flex items-center gap-1 text-xs text-muted-foreground">
                        <Users size={14} />
                        {showtime.availableSeats}
                      </div>
                    </div>
                  </button>
                );
              })}
            </div>
          ) : (
            <div className="text-center py-12 bg-card dark:bg-slate-900 rounded-xl border border-border dark:border-slate-800">
              <p className="text-muted-foreground text-lg mb-2">
                No showtimes available
              </p>
              <p className="text-muted-foreground text-sm">
                {selectedCinema === "all" 
                  ? `No showtimes for ${formatDate(selectedDate)}`
                  : `No showtimes at ${cinemas.find((c: any) => c.id === selectedCinema)?.name} on ${formatDate(selectedDate)}`
                }
              </p>
            </div>
          )}
        </div>

        {selectedShowtime && (
          <div className="mt-12 flex justify-center">
            <Link
              href={`/booking/${movie.id}/${selectedShowtime.id}`}
              className="px-8 py-4 rounded-lg bg-gradient-to-r from-purple-600 to-purple-700 text-white font-semibold hover:shadow-lg transition-all"
            >
              Continue to Booking
            </Link>
          </div>
        )}
          </>
        )}
      </div>
    </div>
  )
}
