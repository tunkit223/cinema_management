import httpClient from "@/configurations/httpClient";
import type { ApiResponse } from "@/utils/apiResponse";

// Runtime-available status map; keeps values aligned with backend enums
export const TicketStatus = {
  ACTIVE: "ACTIVE",
  USED: "USED",
  EXPIRED: "EXPIRED",
  CANCELLED: "CANCELLED",
} as const;

export type TicketStatus = (typeof TicketStatus)[keyof typeof TicketStatus];

export interface TicketResponse {
  id: string;
  ticketCode: string;
  movieTitle: string;
  startTime: string;
  qrContent: string;
  seatName: string;
  price: number;
  status: TicketStatus;
  expiresAt: string;
}

export const ticketService = {
  getTicketByCode: async (ticketCode: string): Promise<TicketResponse> => {
    const response = await httpClient.get<ApiResponse<TicketResponse>>(
      `/tickets/${ticketCode}`
    );
    return response.data.result;
  },

  checkInTicket: async (ticketCode: string): Promise<string> => {
    const response = await httpClient.post<ApiResponse<string>>(
      `/tickets/check-in/${ticketCode}`
    );
    return response.data.result;
  },

  getTicketsByBooking: async (bookingId: string): Promise<TicketResponse[]> => {
    const response = await httpClient.get<ApiResponse<TicketResponse[]>>(
      `/tickets/by-booking/${bookingId}`
    );
    return response.data.result;
  },

  getTicketsByCustomer: async (customerId: string): Promise<TicketResponse[]> => {
    const response = await httpClient.get<TicketResponse[]>(
      `/tickets/my-tickets/${customerId}`
    );
    return response.data;
  },
};
