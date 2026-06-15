export type UserRole = 'CLIENT' | 'AGENT' | 'TECHNICIAN' | 'MANAGER' | 'ADMIN';

export interface User {
  id: string;
  firstname: string;
  lastname: string;
  email: string;
  role: UserRole;
}

export interface AuthRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstname: string;
  lastname: string;
  email: string;
  password: string;
  role: UserRole;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface Customer {
  id: string;
  authUserId: string;
  firstname: string;
  lastname: string;
  email: string;
  phoneNumber: string;
  address: string;
}

export interface CustomerProduct {
  id: string;
  customerId: string;
  productName: string;
  brand: string;
  model: string;
  serialNumber: string;
  purchaseDate: string;
  warrantyEndDate: string;
}

export type SavCaseStatus = 'CREATED' | 'IN_REVIEW' | 'WAITING_CUSTOMER' | 'ASSIGNED_TO_TECHNICIAN' | 'IN_REPAIR' | 'RESOLVED' | 'CLOSED' | 'REJECTED';
export type SavCasePriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

export interface SavCase {
  id: string;
  caseReference: string;
  customerId: string;
  customerProductId: string;
  title: string;
  description: string;
  status: SavCaseStatus;
  priority: SavCasePriority;
  assignedAgentAuthUserId?: string;
  assignedTechnicianAuthUserId?: string;
  createdAt?: string;
}

export interface SavCaseStatusHistory {
  id: string;
  oldStatus: SavCaseStatus;
  newStatus: SavCaseStatus;
  changedByAuthUserId: string;
  comment: string;
  changedAt: string;
}
