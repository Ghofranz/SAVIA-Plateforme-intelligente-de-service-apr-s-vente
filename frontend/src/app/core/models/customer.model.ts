export interface CreateCustomerRequest {
  firstname: string;
lastname: string;
email: string;
phoneNumber: string;
address: string;
}

export interface CustomerResponse {
id: number;
authUserId: number;
firstname: string;
lastname: string;
email: string;
phoneNumber: string;
address: string;
}
