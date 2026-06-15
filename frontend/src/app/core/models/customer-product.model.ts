export interface CreateCustomerProductRequest {
  customerId: number;
productName: string;
brand: string;
model: string | null;
serialNumber: string;
purchaseDate: string | null;
warrantyEndDate: string | null;
}

export interface CustomerProductResponse {
id: number;
customerId: number;
productName: string;
brand: string;
model: string | null;
serialNumber: string;
purchaseDate: string | null;
warrantyEndDate: string | null;
createdAt: string;
updatedAt: string;
}
