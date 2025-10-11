"use client";

import { useEffect } from "react";
import { getToken } from "@/services/localStorageService";
import { useRouter } from "next/navigation";

export default function MyTicketsPage() {
  const router = useRouter();

  useEffect(() => {
    const token = getToken();
    if (!token) {
      router.push("/");
    }
  }, [router]);

  return (
    <div className="min-h-screen pt-20 pb-12 px-4">
      <div className="container mx-auto max-w-6xl">
        <h1 className="text-3xl font-bold mb-8">My Tickets</h1>

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

        <div className="mt-8 grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Example ticket cards - you can remove these or fetch from API */}
          <div className="bg-card border border-border rounded-lg p-6 opacity-50">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h3 className="font-semibold text-lg">Example Movie Title</h3>
                <p className="text-sm text-muted-foreground">Cinema Hall 1</p>
              </div>
              <span className="px-3 py-1 bg-green-500/10 text-green-600 rounded-full text-xs font-medium">
                Upcoming
              </span>
            </div>
            <div className="space-y-2 text-sm">
              <p>
                <span className="text-muted-foreground">Date:</span> Jan 15,
                2025
              </p>
              <p>
                <span className="text-muted-foreground">Time:</span> 7:30 PM
              </p>
              <p>
                <span className="text-muted-foreground">Seats:</span> A1, A2
              </p>
            </div>
            <button className="mt-4 w-full py-2 border border-border rounded-lg hover:bg-muted transition-colors">
              View Details
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
