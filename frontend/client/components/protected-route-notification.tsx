"use client";

import { useEffect, useState } from "react";
import { usePathname } from "next/navigation";
import { isProtectedRoute, isAuthenticated } from "@/lib/auth-utils";
import { useAuthModalStore } from "@/store";

export function ProtectedRouteNotification() {
  const [showNotification, setShowNotification] = useState(false);
  const pathname = usePathname();

  // Zustand store
  const openLoginModal = useAuthModalStore((state) => state.openLoginModal);

  useEffect(() => {
    // Check if trying to access protected route without auth
    if (isProtectedRoute(pathname) && !isAuthenticated()) {
      setShowNotification(true);

      // Auto hide after 5 seconds
      const timer = setTimeout(() => {
        setShowNotification(false);
      }, 5000);

      return () => clearTimeout(timer);
    } else {
      setShowNotification(false);
    }
  }, [pathname]);

  if (!showNotification) return null;

  return (
    <div className="fixed top-20 right-4 z-[60] max-w-md animate-slide-in">
      <div className="bg-card border-2 border-yellow-500/20 rounded-lg shadow-xl p-4 flex items-start gap-3">
        <div className="flex-shrink-0">
          <svg
            className="w-6 h-6 text-yellow-500"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
            />
          </svg>
        </div>
        <div className="flex-1">
          <h3 className="font-semibold mb-1 text-yellow-600 dark:text-yellow-500">
            Login Required
          </h3>
          <p className="text-sm text-muted-foreground mb-3">
            Please login to continue booking tickets.
          </p>
          <button
            onClick={() => {
              setShowNotification(false);
              openLoginModal();
            }}
            className="px-4 py-2 bg-gradient-to-r from-purple-600 to-pink-500 text-white text-sm font-medium rounded-lg hover:shadow-lg transition-all"
          >
            Login Now
          </button>
        </div>
        <button
          onClick={() => setShowNotification(false)}
          className="flex-shrink-0 text-muted-foreground hover:text-foreground"
        >
          <svg
            className="w-5 h-5"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
        </button>
      </div>
    </div>
  );
}
