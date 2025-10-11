import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import { getToken } from "./localStorageService";

export interface CustomerInfo {
  id: string;
  fullName: string;
  email: string;
  phoneNumber?: string;
  address?: string;
  avatarUrl?: string;
  dateOfBirth?: string;
  gender?: string;
  noPassword?: boolean;
}

export interface UpdateCustomerRequest {
  fullName?: string;
  phoneNumber?: string;
  address?: string;
  dateOfBirth?: string;
  gender?: string;
  avatarUrl?: string;
}

// Get customer info
export const getMyInfo = async (): Promise<CustomerInfo> => {
  try {
    const response = await httpClient.get<{ result: CustomerInfo }>(API.MY_INFO, {
      headers: {
        Authorization: `Bearer ${getToken()}`,
      },
    });
    
    return response.data.result;
  } catch (error) {
    console.error("Failed to get customer info:", error);
    throw error;
  }
};

// Update customer info
export const updateMyInfo = async (
  customerId: string,
  data: UpdateCustomerRequest
): Promise<CustomerInfo> => {
  try {
    const url = API.UPDATE_CUSTOMER.replace("${customerId}", customerId);
    const response = await httpClient.put<{ result: CustomerInfo }>(url, data, {
      headers: {
        Authorization: `Bearer ${getToken()}`,
      },
    });
    
    return response.data.result;
  } catch (error) {
    console.error("Failed to update customer info:", error);
    throw error;
  }
};
