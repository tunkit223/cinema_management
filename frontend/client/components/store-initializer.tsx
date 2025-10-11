"use client";

import { useEffect, useRef } from "react";
import { useAuthStore } from "@/store";

/**
 * StoreInitializer - Initialize stores on app mount
 * This component should be placed high in the component tree (e.g., in layout)
 */
export function StoreInitializer({ children }: { children: React.ReactNode }) {
  const initialized = useRef(false);
  const checkAuth = useAuthStore((state) => state.checkAuth);

  useEffect(() => {
    if (!initialized.current) {
      // Initialize auth state on mount
      checkAuth();
      initialized.current = true;
    }
  }, [checkAuth]);

  return <>{children}</>;
}
