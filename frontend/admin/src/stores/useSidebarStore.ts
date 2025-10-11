import { create } from 'zustand';
import { devtools } from 'zustand/middleware';

interface SidebarState {
  // State
  isCollapsed: boolean;
  settingsOpen: boolean;
  
  // Actions
  toggleCollapse: () => void;
  setCollapsed: (collapsed: boolean) => void;
  toggleSettings: () => void;
  setSettingsOpen: (open: boolean) => void;
}

export const useSidebarStore = create<SidebarState>()(
  devtools(
    (set) => ({
      // Initial state
      isCollapsed: false,
      settingsOpen: false,
      
      // Actions
      toggleCollapse: () => set((state) => ({ isCollapsed: !state.isCollapsed })),
      
      setCollapsed: (collapsed) => set({ isCollapsed: collapsed }),
      
      toggleSettings: () => set((state) => ({ settingsOpen: !state.settingsOpen })),
      
      setSettingsOpen: (open) => set({ settingsOpen: open }),
    }),
    { name: 'SidebarStore' }
  )
);

// Selectors
export const selectIsCollapsed = (state: SidebarState) => state.isCollapsed;
export const selectSettingsOpen = (state: SidebarState) => state.settingsOpen;
