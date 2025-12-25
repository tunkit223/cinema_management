"use client"

import { useState, useEffect } from "react"
import { use } from "react"
import Link from "next/link"
import { getMovieById, mapMovieForDisplay } from "@/lib/api-movie"
import MovieDetailClient from "./movie-detail-client"

export default function MovieDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params)
  const [movie, setMovie] = useState<any>(null)
  const [loading, setLoading] = useState(true)

  // Fetch movie khi component mount
  useEffect(() => {
    const fetchMovie = async () => {
      try {
        setLoading(true)
        console.log('🎬 Fetching movie ID:', id)

        const data = await getMovieById(id)
        console.log('📦 Raw data:', data)

        if (data) {
          const mappedMovie = mapMovieForDisplay(data)
          console.log('✅ Mapped movie:', mappedMovie)
          setMovie(mappedMovie)
        } else {
          console.error('❌ No data returned')
        }
      } catch (error) {
        console.error('❌ Error fetching movie:', error)
      } finally {
        setLoading(false)
      }
    }

    fetchMovie()
  }, [id])

  // Loading state
  if (loading) {
    return (
      <div className="min-h-screen bg-background dark:bg-slate-950 pt-20">
        <div className="container-max px-4 md:px-8 py-12">
          <div className="animate-pulse space-y-8">
            <div className="bg-gray-700 h-96 rounded-xl"></div>
            <div className="space-y-4">
              <div className="bg-gray-700 h-8 w-1/2 rounded"></div>
              <div className="bg-gray-700 h-4 w-full rounded"></div>
              <div className="bg-gray-700 h-4 w-3/4 rounded"></div>
            </div>
          </div>
        </div>
      </div>
    )
  }

  // Movie not found
  if (!movie) {
    return (
      <div className="min-h-screen bg-background dark:bg-slate-950 flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-3xl font-bold mb-4">Movie Not Found</h1>
          <p className="text-muted-foreground mb-6">ID: {id}</p>
          <Link href="/" className="text-purple-600 hover:text-purple-700 font-semibold">
            Back to Home
          </Link>
        </div>
      </div>
    )
  }

  return <MovieDetailClient movie={movie} />
}












