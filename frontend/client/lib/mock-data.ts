import type { Movie, Facility, Testimonial, FAQItem, MembershipTier, Showtime, ComboItem, Seat } from "./types"

// Cinema/Theater data
export const cinemas = [
  {
    id: "cinema-1",
    name: "CINEPLEX Downtown",
    location: "123 Main St, City Center",
    phone: "0912-123-456",
    rooms: [
      {
        id: "room-1",
        name: "Hall 1",
        capacity: 96,
        seatLayout: "A-H x 12"
      },
      {
        id: "room-2",
        name: "Hall 2", 
        capacity: 120,
        seatLayout: "A-I x 12"
      }
    ]
  },
  {
    id: "cinema-2",
    name: "CINEPLEX East",
    location: "456 Park Ave, East District",
    phone: "0912-789-012",
    rooms: [
      {
        id: "room-3",
        name: "Premium Hall",
        capacity: 60,
        seatLayout: "A-F x 10"
      }
    ]
  }
]

// Screenings data
export const screenings = [
  {
    id: "screening-1",
    movieId: "1",
    roomId: "room-1",
    startTime: new Date(2024, 11, 23, 10, 0).toISOString(),
    endTime: new Date(2024, 11, 23, 12, 28).toISOString(),
    format: "2D",
    price: 50000
  },
  {
    id: "screening-2",
    movieId: "1",
    roomId: "room-1",
    startTime: new Date(2024, 11, 23, 13, 30).toISOString(),
    endTime: new Date(2024, 11, 23, 15, 58).toISOString(),
    format: "3D",
    price: 60000
  },
  {
    id: "screening-3",
    movieId: "1",
    roomId: "room-2",
    startTime: new Date(2024, 11, 23, 17, 0).toISOString(),
    endTime: new Date(2024, 11, 23, 19, 28).toISOString(),
    format: "IMAX",
    price: 75000
  }
]

export const nowShowingMovies: Movie[] = [
  {
    id: "1",
    title: "The Quantum Paradox",
    genre: ["Sci-Fi", "Thriller"],
    rating: "PG-13",
    duration: 148,
    releaseDate: "2024-10-15",
    poster: "/sci-fi-movie-poster-quantum.jpg",
    description: "A mind-bending journey through parallel dimensions.",
    director: "Christopher Nolan",
    cast: ["Leonardo DiCaprio", "Marion Cotillard"],
    trailerUrl: "https://www.youtube.com/embed/dQw4w9WgXcQ",
  },
  {
    id: "2",
    title: "Echoes of Tomorrow",
    genre: ["Drama", "Romance"],
    rating: "PG",
    duration: 132,
    releaseDate: "2024-10-20",
    poster: "/romantic-drama-movie-poster.jpg",
    description: "A touching story about love across time.",
    director: "Greta Gerwig",
    cast: ["Timothée Chalamet", "Zendaya"],
  },
  {
    id: "3",
    title: "Shadow Protocol",
    genre: ["Action", "Spy"],
    rating: "R",
    duration: 156,
    releaseDate: "2024-10-18",
    poster: "/action-spy-thriller-movie-poster.jpg",
    description: "High-stakes espionage in a digital age.",
    director: "Denis Villeneuve",
    cast: ["Tom Cruise", "Rebecca Ferguson"],
  },
  {
    id: "4",
    title: "Laughter & Chaos",
    genre: ["Comedy"],
    rating: "PG-13",
    duration: 104,
    releaseDate: "2024-10-22",
    poster: "/comedy-movie-poster-funny.jpg",
    description: "A hilarious adventure that will keep you laughing.",
    director: "Taika Waititi",
    cast: ["Ryan Reynolds", "Awkwafina"],
  },
  {
    id: "5",
    title: "The Last Guardian",
    genre: ["Fantasy", "Adventure"],
    rating: "PG",
    duration: 142,
    releaseDate: "2024-10-25",
    poster: "/fantasy-adventure-movie-poster.png",
    description: "An epic quest to save the realm.",
    director: "Peter Jackson",
    cast: ["Cate Blanchett", "Viggo Mortensen"],
  },
]

export const comingSoonMovies: Movie[] = [
  {
    id: "6",
    title: "Cosmic Collision",
    genre: ["Sci-Fi", "Action"],
    rating: "PG-13",
    duration: 160,
    releaseDate: "2024-11-15",
    poster: "/cosmic-space-sci-fi-movie-poster.jpg",
    description: "When two worlds collide, heroes must rise.",
    director: "James Cameron",
    cast: ["Zoe Saldana", "Sam Worthington"],
  },
  {
    id: "7",
    title: "Midnight Heist",
    genre: ["Crime", "Thriller"],
    rating: "R",
    duration: 138,
    releaseDate: "2024-11-20",
    poster: "/heist-crime-thriller-movie-poster.jpg",
    description: "The greatest heist ever planned.",
    director: "David Fincher",
    cast: ["Ryan Gosling", "Charlize Theron"],
  },
  {
    id: "8",
    title: "Whispers in the Wind",
    genre: ["Drama", "Mystery"],
    rating: "PG",
    duration: 125,
    releaseDate: "2024-11-25",
    poster: "/mystery-drama-movie-poster.jpg",
    description: "Secrets that could change everything.",
    director: "Yorgos Lanthimos",
    cast: ["Emma Stone", "Mark Ruffalo"],
  },
]

export const facilities: Facility[] = [
  {
    id: "1",
    name: "Premium Seating",
    description: "Luxury recliners with maximum comfort and legroom",
    icon: "🪑",
  },
  {
    id: "2",
    name: "4K Projection",
    description: "Crystal clear 4K resolution for immersive viewing",
    icon: "📽️",
  },
  {
    id: "3",
    name: "Dolby Atmos",
    description: "Immersive 3D surround sound experience",
    icon: "🔊",
  },
  {
    id: "4",
    name: "Gourmet Snacks",
    description: "Premium food and beverage selection",
    icon: "🍿",
  },
  {
    id: "5",
    name: "VIP Lounge",
    description: "Exclusive lounge for premium members",
    icon: "✨",
  },
  {
    id: "6",
    name: "Wheelchair Access",
    description: "Fully accessible facilities for all guests",
    icon: "♿",
  },
]

export const testimonials: Testimonial[] = [
  {
    id: "1",
    name: "Sarah Johnson",
    role: "Movie Enthusiast",
    content: "CINEPLEX offers the best cinema experience I've ever had. The sound quality and comfort are unmatched!",
    rating: 5,
    avatar: "/diverse-woman-avatar.png",
  },
  {
    id: "2",
    name: "Michael Chen",
    role: "Frequent Visitor",
    content: "The online booking system is so convenient. I love being able to reserve my seats in advance.",
    rating: 5,
    avatar: "/man-avatar.png",
  },
  {
    id: "3",
    name: "Emma Williams",
    role: "Premium Member",
    content: "The membership program is fantastic. Great discounts and exclusive perks make it worth every penny.",
    rating: 5,
    avatar: "/professional-woman-avatar.png",
  },
]

export const faqs: FAQItem[] = [
  {
    id: "1",
    question: "How do I book tickets online?",
    answer:
      "Simply select your movie, choose your preferred date and time, select your seats, and complete the payment. Your tickets will be sent to your email immediately.",
  },
  {
    id: "2",
    question: "Can I cancel or modify my booking?",
    answer:
      "Yes, you can cancel or modify your booking up to 2 hours before the showtime. Visit your account and select the booking you wish to change.",
  },
  {
    id: "3",
    question: "What payment methods do you accept?",
    answer: "We accept all major credit cards, debit cards, digital wallets, and bank transfers for your convenience.",
  },
  {
    id: "4",
    question: "Is there a membership program?",
    answer:
      "Yes! Our membership program offers exclusive discounts, early access to bookings, and special perks. Choose from Silver, Gold, or Platinum tiers.",
  },
  {
    id: "5",
    question: "Do you offer group discounts?",
    answer: "Groups of 10 or more receive special discounts. Contact our group sales team for more information.",
  },
  {
    id: "6",
    question: "What is your refund policy?",
    answer:
      "Full refunds are available for cancellations made at least 2 hours before showtime. No refunds for no-shows.",
  },
]

export const membershipTiers: MembershipTier[] = [
  {
    id: "1",
    name: "Silver",
    price: 9.99,
    benefits: ["10% discount on tickets", "5% discount on snacks", "Early booking access", "Monthly newsletter"],
  },
  {
    id: "2",
    name: "Gold",
    price: 19.99,
    benefits: [
      "20% discount on tickets",
      "15% discount on snacks",
      "Priority booking",
      "Free large popcorn monthly",
      "Exclusive events",
    ],
    featured: true,
  },
  {
    id: "3",
    name: "Platinum",
    price: 29.99,
    benefits: [
      "30% discount on tickets",
      "25% discount on snacks",
      "VIP lounge access",
      "Free premium snacks monthly",
      "Exclusive premiere events",
      "Personal concierge",
    ],
  },
]

export const showtimes: Record<string, Showtime[]> = {
  "1": [
    { id: "st1", movieId: "1", time: "10:00 AM", date: "2024-12-23", format: "2D", price: 50000, availableSeats: 45, roomId: "room-1", cinemaId: "cinema-1" },
    { id: "st2", movieId: "1", time: "1:30 PM", date: "2024-12-23", format: "3D", price: 60000, availableSeats: 32, roomId: "room-1", cinemaId: "cinema-1" },
    { id: "st3", movieId: "1", time: "5:00 PM", date: "2024-12-23", format: "IMAX", price: 75000, availableSeats: 28, roomId: "room-2", cinemaId: "cinema-1" },
    { id: "st4", movieId: "1", time: "8:30 PM", date: "2024-12-23", format: "2D", price: 50000, availableSeats: 15, roomId: "room-2", cinemaId: "cinema-1" },
    { id: "st1b", movieId: "1", time: "10:00 AM", date: "2024-12-24", format: "2D", price: 50000, availableSeats: 50, roomId: "room-1", cinemaId: "cinema-1" },
    { id: "st2b", movieId: "1", time: "1:30 PM", date: "2024-12-24", format: "3D", price: 60000, availableSeats: 45, roomId: "room-3", cinemaId: "cinema-2" },
  ],
  "2": [
    { id: "st5", movieId: "2", time: "11:00 AM", date: "2024-12-23", format: "2D", price: 50000, availableSeats: 50, roomId: "room-1", cinemaId: "cinema-1" },
    { id: "st6", movieId: "2", time: "2:00 PM", date: "2024-12-23", format: "2D", price: 50000, availableSeats: 38, roomId: "room-2", cinemaId: "cinema-1" },
    { id: "st7", movieId: "2", time: "6:00 PM", date: "2024-12-23", format: "3D", price: 60000, availableSeats: 22, roomId: "room-3", cinemaId: "cinema-2" },
    { id: "st8", movieId: "2", time: "9:00 PM", date: "2024-12-23", format: "2D", price: 50000, availableSeats: 10, roomId: "room-1", cinemaId: "cinema-1" },
  ],
  "3": [
    { id: "st9", movieId: "3", time: "10:30 AM", date: "2024-12-23", format: "2D", price: 50000, availableSeats: 48, roomId: "room-2", cinemaId: "cinema-1" },
    { id: "st10", movieId: "3", time: "1:00 PM", date: "2024-12-23", format: "IMAX", price: 75000, availableSeats: 25, roomId: "room-1", cinemaId: "cinema-1" },
    { id: "st11", movieId: "3", time: "4:30 PM", date: "2024-12-23", format: "2D", price: 50000, availableSeats: 35, roomId: "room-3", cinemaId: "cinema-2" },
    { id: "st12", movieId: "3", time: "7:30 PM", date: "2024-12-23", format: "3D", price: 60000, availableSeats: 18, roomId: "room-2", cinemaId: "cinema-1" },
  ],
  "4": [
    { id: "st13", movieId: "4", time: "9:00 AM", date: "2024-12-23", format: "2D", price: 50000, availableSeats: 52, roomId: "room-1", cinemaId: "cinema-1" },
    { id: "st14", movieId: "4", time: "12:00 PM", date: "2024-12-23", format: "2D", price: 50000, availableSeats: 40, roomId: "room-2", cinemaId: "cinema-1" },
    { id: "st15", movieId: "4", time: "3:30 PM", date: "2024-12-23", format: "2D", price: 50000, availableSeats: 30, roomId: "room-3", cinemaId: "cinema-2" },
    { id: "st16", movieId: "4", time: "6:30 PM", date: "2024-12-23", format: "3D", price: 60000, availableSeats: 20, roomId: "room-1", cinemaId: "cinema-1" },
  ],
  "5": [
    { id: "st17", movieId: "5", time: "10:00 AM", date: "2024-12-23", format: "2D", price: 50000, availableSeats: 46, roomId: "room-1", cinemaId: "cinema-1" },
    { id: "st18", movieId: "5", time: "1:30 PM", date: "2024-12-23", format: "IMAX", price: 75000, availableSeats: 24, roomId: "room-2", cinemaId: "cinema-1" },
    { id: "st19", movieId: "5", time: "5:00 PM", date: "2024-12-23", format: "2D", price: 50000, availableSeats: 33, roomId: "room-3", cinemaId: "cinema-2" },
    { id: "st20", movieId: "5", time: "8:00 PM", date: "2024-12-23", format: "3D", price: 60000, availableSeats: 16, roomId: "room-1", cinemaId: "cinema-1" },
  ],
}

export const combos: ComboItem[] = [
  { id: "c1", name: "Small Popcorn", price: 50000, icon: "🍿" },
  { id: "c2", name: "Large Popcorn", price: 80000, icon: "🍿" },
  { id: "c3", name: "Small Drink", price: 40000, icon: "🥤" },
  { id: "c4", name: "Large Drink", price: 60000, icon: "🥤" },
  { id: "c5", name: "Candy Pack", price: 70000, icon: "🍬" },
  { id: "c6", name: "Combo Deal (Popcorn + Drink)", price: 100000, icon: "🎬" },
]

export const generateSeats = (): Seat[] => {
  const seats: Seat[] = []
  const rows = ["A", "B", "C", "D", "E", "F", "G", "H"]
  const seatsPerRow = 12

  rows.forEach((row) => {
    for (let i = 1; i <= seatsPerRow; i++) {
      // VIP seats: last 2 columns (11, 12)
      const isVip = i >= 11
      // Couple seats: middle 4 seats per row (5-8)
      const isCouple = i >= 5 && i <= 8
      // Standard: rest
      const type: "standard" | "vip" | "couple" = isVip ? "vip" : isCouple ? "couple" : "standard"
      
      // 20% of seats are booked (not available), prefer VIP seats
      const isBooked = isVip ? Math.random() > 0.3 : Math.random() > 0.2
      
      seats.push({
        id: `${row}${i}`,
        row,
        number: i,
        isAvailable: !isBooked,
        type,
      })
    }
  })

  return seats
}
// Current user data
export const currentUser = {
  id: "user-1",
  email: "customer@example.com",
  name: "John Doe",
  phone: "0901-234-567",
  isAuthenticated: true,
}

// Mock customer data for booking
export const mockCustomers = [
  {
    id: "customer-1",
    name: "John Doe",
    email: "john.doe@example.com",
    phone: "0901-234-567",
    totalBookings: 5,
  },
  {
    id: "customer-2",
    name: "Jane Smith",
    email: "jane.smith@example.com",
    phone: "0902-345-678",
    totalBookings: 12,
  },
]

// Mock booking history
export const mockBookings = [
  {
    id: "booking-1",
    movieId: "1",
    movieTitle: "The Quantum Paradox",
    customerId: "customer-1",
    showtimeId: "st1",
    date: "2024-12-23",
    time: "10:00 AM",
    cinemaName: "CINEPLEX Downtown",
    roomName: "Hall 1",
    seats: ["A1", "A2"],
    combos: ["c6"],
    totalPrice: 200000,
    status: "COMPLETED",
    createdAt: "2024-12-20T10:30:00Z",
  },
  {
    id: "booking-2",
    movieId: "2",
    movieTitle: "Echoes of Tomorrow",
    customerId: "customer-1",
    showtimeId: "st5",
    date: "2024-12-23",
    time: "11:00 AM",
    cinemaName: "CINEPLEX Downtown",
    roomName: "Hall 1",
    seats: ["B5", "B6"],
    combos: ["c1", "c3"],
    totalPrice: 190000,
    status: "CONFIRMED",
    createdAt: "2024-12-22T14:20:00Z",
  },
  {
    id: "booking-3",
    movieId: "3",
    movieTitle: "Shadow Protocol",
    customerId: "customer-2",
    showtimeId: "st10",
    date: "2024-12-23",
    time: "1:00 PM",
    cinemaName: "CINEPLEX Downtown",
    roomName: "Hall 1",
    seats: ["C7", "C8", "C9", "C10"],
    combos: ["c6", "c6"],
    totalPrice: 500000,
    status: "CONFIRMED",
    createdAt: "2024-12-21T18:45:00Z",
  },
  {
    id: "booking-4",
    movieId: "1",
    movieTitle: "The Quantum Paradox",
    customerId: "customer-2",
    showtimeId: "st3",
    date: "2024-12-23",
    time: "5:00 PM",
    cinemaName: "CINEPLEX Downtown",
    roomName: "Hall 2",
    seats: ["E5", "E6"],
    combos: ["c2", "c4", "c5"],
    totalPrice: 360000,
    status: "PENDING",
    createdAt: "2024-12-23T08:15:00Z",
  }
]

// Mock payment methods
export const paymentMethods = [
  {
    id: "pm-1",
    type: "credit_card",
    name: "Visa",
    icon: "💳",
    description: "Pay with Visa credit/debit card"
  },
  {
    id: "pm-2",
    type: "credit_card",
    name: "Mastercard",
    icon: "💳",
    description: "Pay with Mastercard credit/debit card"
  },
  {
    id: "pm-3",
    type: "e_wallet",
    name: "MoMo",
    icon: "📱",
    description: "Pay with MoMo e-wallet"
  },
  {
    id: "pm-4",
    type: "e_wallet",
    name: "ZaloPay",
    icon: "📱",
    description: "Pay with ZaloPay e-wallet"
  },
  {
    id: "pm-5",
    type: "bank_transfer",
    name: "Bank Transfer",
    icon: "🏦",
    description: "Direct bank transfer"
  }
]

// Mock discount codes
export const discountCodes = [
  {
    code: "WELCOME10",
    type: "percentage",
    value: 10,
    description: "10% off for new customers",
    minAmount: 100000,
    maxDiscount: 50000,
    validUntil: "2025-12-31"
  },
  {
    code: "MOVIE20",
    type: "percentage",
    value: 20,
    description: "20% off on all movie tickets",
    minAmount: 150000,
    maxDiscount: 100000,
    validUntil: "2025-12-31"
  },
  {
    code: "FLAT50",
    type: "fixed",
    value: 50000,
    description: "Flat 50,000 VND off",
    minAmount: 200000,
    maxDiscount: 50000,
    validUntil: "2025-12-31"
  },
  {
    code: "COMBO15",
    type: "percentage",
    value: 15,
    description: "15% off on combo items",
    minAmount: 50000,
    maxDiscount: 30000,
    validUntil: "2025-12-31"
  }
]

// Mock reviews/ratings data
export const movieReviews = {
  "1": [
    {
      id: "rev-1",
      userId: "customer-1",
      userName: "John Doe",
      rating: 5,
      comment: "Mind-blowing! A masterpiece of modern cinema.",
      date: "2024-12-20",
      helpful: 24
    },
    {
      id: "rev-2",
      userId: "customer-2",
      userName: "Jane Smith",
      rating: 4,
      comment: "Great visuals and story, but a bit confusing at times.",
      date: "2024-12-19",
      helpful: 15
    }
  ],
  "2": [
    {
      id: "rev-3",
      userId: "customer-1",
      userName: "John Doe",
      rating: 5,
      comment: "Beautiful story that touched my heart.",
      date: "2024-12-21",
      helpful: 18
    }
  ],
  "3": [
    {
      id: "rev-4",
      userId: "customer-2",
      userName: "Jane Smith",
      rating: 4,
      comment: "Action-packed thriller with great performances.",
      date: "2024-12-22",
      helpful: 12
    }
  ]
}

// Mock notification data
export const notifications = [
  {
    id: "notif-1",
    userId: "customer-1",
    type: "booking_confirmed",
    title: "Booking Confirmed",
    message: "Your booking for The Quantum Paradox has been confirmed!",
    read: false,
    createdAt: "2024-12-22T14:20:00Z"
  },
  {
    id: "notif-2",
    userId: "customer-1",
    type: "reminder",
    title: "Movie Reminder",
    message: "Your movie starts in 2 hours. Don't forget!",
    read: false,
    createdAt: "2024-12-23T09:00:00Z"
  },
  {
    id: "notif-3",
    userId: "customer-1",
    type: "promotion",
    title: "Special Offer",
    message: "Get 20% off with code MOVIE20 - Valid until Dec 31!",
    read: true,
    createdAt: "2024-12-20T08:00:00Z"
  }
]

// Helper function to get available seats for a specific showtime
export const getAvailableSeatsForShowtime = (showtimeId: string): Seat[] => {
  const bookedSeats = mockBookings
    .filter(booking => booking.showtimeId === showtimeId)
    .flatMap(booking => booking.seats)

  const allSeats = generateSeats()
  
  return allSeats.map(seat => ({
    ...seat,
    isAvailable: !bookedSeats.includes(seat.id)
  }))
}