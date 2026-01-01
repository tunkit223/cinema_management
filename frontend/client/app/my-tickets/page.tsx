"use client";

import { useEffect, useState } from "react";
import { getToken, getUserInfo } from "@/services/localStorageService";
import { useRouter } from "next/navigation";
import { getTicketsByCustomer, TicketResponse } from "@/services/ticketService";
import { getMyInfo } from "@/services/customerService";

export default function MyTicketsPage() {
  const router = useRouter();
  const [tickets, setTickets] = useState<TicketResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const token = getToken();
    if (!token) {
      router.push("/");
      return;
    }

    const fetchTickets = async () => {
      try {
        setLoading(true);
        
        // Try to get user info from localStorage first
        let userInfo = getUserInfo();
        
        // If not available in localStorage, fetch from API
        if (!userInfo || (!userInfo.id && !userInfo.customerId)) {
          try {
            userInfo = await getMyInfo();
          } catch (error: any) {
            console.error("Error fetching user info:", error);
            setError("Unable to fetch user information. Please sign in again.");
            return;
          }
        }
        
        // Get customer ID (prefer 'id' field, fallback to 'customerId')
        const customerId = userInfo?.id || userInfo?.customerId;
        
        if (!customerId) {
          setError("Customer information not found");
          return;
        }

        const fetchedTickets = await getTicketsByCustomer(customerId);
        setTickets(fetchedTickets);
        setError(null);
      } catch (err) {
        console.error("Error fetching tickets:", err);
        setError("Failed to load tickets. Please try again.");
      } finally {
        setLoading(false);
      }
    };

    fetchTickets();
  }, [router]);

  return (
    <div className="min-h-screen pt-20 pb-12 px-4">
      <div className="container mx-auto max-w-6xl">
        <h1 className="text-3xl font-bold mb-8">My Tickets</h1>

        {loading ? (
          <div className="bg-card border border-border rounded-2xl p-8">
            <div className="text-center py-12">
              <div className="flex justify-center items-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-500"></div>
              </div>
              <p className="mt-4 text-muted-foreground">Loading your tickets...</p>
            </div>
          </div>
        ) : error ? (
          <div className="bg-card border border-border rounded-2xl p-8">
            <div className="text-center py-12">
              <div className="w-24 h-24 mx-auto mb-4 rounded-full bg-red-500/10 flex items-center justify-center">
                <svg
                  className="w-12 h-12 text-red-600"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M12 8v4m0 4v.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
              </div>
              <h2 className="text-xl font-semibold mb-2">Error</h2>
              <p className="text-muted-foreground mb-6">{error}</p>
              <button
                onClick={() => window.location.reload()}
                className="px-6 py-3 rounded-lg gradient-primary text-white font-semibold hover:shadow-lg transition-all"
              >
                Try Again
              </button>
            </div>
          </div>
        ) : tickets.length === 0 ? (
          <div className="bg-card border border-border rounded-2xl p-8">
            <div className="text-center py-12">
              <div className="w-24 h-24 mx-auto mb-4 rounded-full bg-gradient-to-r from-purple-500 to-pink-500 flex items-center justify-center">
                <svg
                  className="w-12 h-12 text-white"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z"
                  />
                </svg>
              </div>
              <h2 className="text-xl font-semibold mb-2">No Tickets Yet</h2>
              <p className="text-muted-foreground mb-6">
                You haven't booked any tickets yet. Start booking to see your
                tickets here!
              </p>
              <button
                onClick={() => router.push("/movies")}
                className="px-6 py-3 rounded-lg gradient-primary text-white font-semibold hover:shadow-lg transition-all"
              >
                Browse Movies
              </button>
            </div>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {tickets.map((ticket) => (
              <div key={ticket.id} className="bg-card border border-border rounded-lg p-6 hover:shadow-lg transition-shadow">
                <div className="flex justify-between items-start mb-4">
                  <div className="flex-1">
                    <h3 className="font-bold text-xl text-purple-600 dark:text-purple-400 mb-2">{ticket.movieTitle}</h3>
                    <p className="text-sm text-slate-600 dark:text-slate-300"><span className="font-semibold">Ticket:</span> {ticket.ticketCode}</p>
                    <p className="text-sm text-slate-600 dark:text-slate-300"><span className="font-semibold">Seat:</span> {ticket.seatName}</p>
                  </div>
                  <span
                    className={`px-3 py-1 rounded-full text-xs font-medium ${
                      ticket.status === "ACTIVE"
                        ? "bg-green-500/10 text-green-600"
                        : ticket.status === "USED"
                        ? "bg-blue-500/10 text-blue-600"
                        : "bg-red-500/10 text-red-600"
                    }`}
                  >
                    {ticket.status}
                  </span>
                </div>
                <div className="space-y-2 text-base mb-4 pb-4 border-b border-border dark:border-slate-700">
                  <p>
                    <span className="font-semibold text-slate-700 dark:text-slate-300">🎬 Showtime:</span> <span className="text-slate-600 dark:text-slate-300">{new Date(ticket.startTime).toLocaleString()}</span>
                  </p>
                  <p>
                    <span className="font-semibold text-slate-700 dark:text-slate-300">💰 Price:</span> <span className="text-purple-600 dark:text-purple-400 font-semibold">{ticket.price.toLocaleString()} VND</span>
                  </p>
                  <p>
                    <span className="font-semibold text-slate-700 dark:text-slate-300">⏰ Expires:</span> <span className="text-orange-600 dark:text-orange-400">{new Date(ticket.expiresAt).toLocaleString()}</span>
                  </p>
                </div>
                {ticket.qrContent && (
                  <div className="mb-4 p-4 bg-white rounded-lg flex justify-center">
                    <img 
                      src={`https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=${encodeURIComponent(ticket.qrContent)}`}
                      alt="QR Code"
                      className="w-32 h-32"
                    />
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
