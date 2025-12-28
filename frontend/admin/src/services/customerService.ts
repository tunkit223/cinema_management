import httpClient from "@/configurations/httpClient"
import type { ApiResponse } from "@/utils/apiResponse"

export interface Customer {
  id: string
  email: string
  firstName?: string
  lastName?: string
  phone?: string
  loyaltyPoints?: number
  customerId?: string
}

// Get current user info
export const getMyInfo = async (): Promise<Customer> => {
  try {
    const response = await httpClient.get<ApiResponse<Customer>>(
      "/customers/me"
    )
    return response.data.result
  } catch (error) {
    console.error("Failed to get user info:", error)
    throw error
  }
}

// Get customer loyalty points
export const getCustomerLoyaltyPoints = async (customerId: string): Promise<number> => {
  try {
    const response = await httpClient.get<ApiResponse<{ points: number }>>(
      `/customers/${customerId}/loyalty-points`
    )
    return response.data.result?.points || 0
  } catch (error) {
    console.error("Failed to get loyalty points:", error)
    throw error
  }
}

// Get customer by ID
export const getCustomerById = async (customerId: string): Promise<Customer> => {
  try {
    const response = await httpClient.get<ApiResponse<Customer>>(
      `/customers/${customerId}`
    )
    return response.data.result
  } catch (error) {
    console.error("Failed to get customer:", error)
    throw error
  }
}

// Get all customers
export const getAllCustomers = async (): Promise<Customer[]> => {
  try {
    const response = await httpClient.get<ApiResponse<Customer[]>>(
      "/customers"
    )
    return response.data.result || []
  } catch (error) {
    console.error("Failed to get customers:", error)
    throw error
  }
}

// Search customers
export const searchCustomers = async (query: string): Promise<Customer[]> => {
  try {
    const response = await httpClient.get<ApiResponse<Customer[]>>(
      `/customers/search?query=${encodeURIComponent(query)}`
    )
    return response.data.result || []
  } catch (error) {
    console.error("Failed to search customers:", error)
    throw error
  }
}
