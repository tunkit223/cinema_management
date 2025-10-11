"use client"

import type { ComboItem } from "@/lib/types"

interface ComboSelectionStepProps {
  combos: ComboItem[]
  selectedCombos: ComboItem[]
  onSelectCombos: (combos: ComboItem[]) => void
}

export default function ComboSelectionStep({ combos, selectedCombos, onSelectCombos }: ComboSelectionStepProps) {
  const handleComboClick = (combo: ComboItem) => {
    const isSelected = selectedCombos.some((c) => c.id === combo.id)
    if (isSelected) {
      onSelectCombos(selectedCombos.filter((c) => c.id !== combo.id))
    } else {
      onSelectCombos([...selectedCombos, combo])
    }
  }

  return (
    <div className="bg-card dark:bg-slate-900 border border-border dark:border-slate-800 rounded-xl p-8">
      <h2 className="text-2xl font-bold mb-2">Choose Your Combos</h2>
      <p className="text-muted-foreground mb-8">Add snacks and drinks to your booking</p>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {combos.map((combo) => {
          const isSelected = selectedCombos.some((c) => c.id === combo.id)
          return (
            <button
              key={combo.id}
              onClick={() => handleComboClick(combo)}
              className={`p-6 rounded-lg border-2 transition-all text-left ${
                isSelected
                  ? "border-purple-600 bg-purple-500/10 dark:bg-purple-900/20"
                  : "border-border dark:border-slate-800 bg-muted dark:bg-slate-800 hover:border-purple-600"
              }`}
            >
              <div className="flex items-start justify-between mb-2">
                <span className="text-3xl">{combo.icon}</span>
                <div
                  className={`w-6 h-6 rounded border-2 flex items-center justify-center ${
                    isSelected ? "border-purple-600 bg-purple-600" : "border-border dark:border-slate-600"
                  }`}
                >
                  {isSelected && <span className="text-white text-sm">✓</span>}
                </div>
              </div>
              <h3 className="font-bold mb-1">{combo.name}</h3>
              <p className="text-lg font-semibold text-purple-600">{combo.price.toLocaleString()} VND</p>
            </button>
          )
        })}
      </div>

      {selectedCombos.length > 0 && (
        <div className="mt-8 p-4 bg-purple-500/10 dark:bg-purple-900/20 border border-purple-500/30 rounded-lg">
          <p className="text-sm font-semibold mb-2">Selected Combos:</p>
          <ul className="text-sm space-y-1">
            {selectedCombos.map((combo) => (
              <li key={combo.id}>
                {combo.name} - {combo.price.toLocaleString()} VND
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  )
}
