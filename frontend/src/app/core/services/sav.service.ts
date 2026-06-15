import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SavCase, SavCaseStatus, SavCaseStatusHistory } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class SavService {
  private readonly API_URL = 'http://localhost:8080/sav-service/api/sav-cases';

  constructor(private http: HttpClient) {}

  createCase(data: Partial<SavCase>): Observable<SavCase> {
    return this.http.post<SavCase>(this.API_URL, data);
  }

  getCaseById(id: string): Observable<SavCase> {
    return this.http.get<SavCase>(`${this.API_URL}/${id}`);
  }

  getCasesByCustomer(customerId: string): Observable<SavCase[]> {
    return this.http.get<SavCase[]>(`${this.API_URL}/customer/${customerId}`);
  }

  getCasesByStatus(status: SavCaseStatus): Observable<SavCase[]> {
    return this.http.get<SavCase[]>(`${this.API_URL}/status/${status}`);
  }

  getAllCases(): Observable<SavCase[]> {
    return this.http.get<SavCase[]>(this.API_URL);
  }

  assignCase(id: string, data: { agentId?: string; technicianId?: string }): Observable<SavCase> {
    return this.http.put<SavCase>(`${this.API_URL}/${id}/assign`, data);
  }

  updateStatus(id: string, data: { status: SavCaseStatus; comment?: string }): Observable<SavCase> {
    return this.http.put<SavCase>(`${this.API_URL}/${id}/status`, data);
  }

  getCaseHistory(id: string): Observable<SavCaseStatusHistory[]> {
    return this.http.get<SavCaseStatusHistory[]>(`${this.API_URL}/${id}/history`);
  }
}
