import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import {
CreateSavCaseRequest,
SavCaseResponse,
SavCaseStatusHistoryResponse,
UpdateSavCaseStatusRequest
} from '../models/sav-case.model';

@Injectable({
providedIn: 'root'
})
export class SavCaseApiService {
private readonly apiUrl = 'http://localhost:8080/sav-service/api/sav-cases';

constructor(private readonly http: HttpClient) {}

  createSavCase(request: CreateSavCaseRequest): Observable<SavCaseResponse> {
    return this.http.post<SavCaseResponse>(this.apiUrl, request);
  }

  getSavCasesByCustomerId(customerId: number): Observable<SavCaseResponse[]> {
    return this.http.get<SavCaseResponse[]>(`${this.apiUrl}/customer/${customerId}`);
  }

  getSavCasesByStatus(status: string): Observable<SavCaseResponse[]> {
    return this.http.get<SavCaseResponse[]>(`${this.apiUrl}/status/${status}`);
  }

  getSavCaseById(id: number): Observable<SavCaseResponse> {
    return this.http.get<SavCaseResponse>(`${this.apiUrl}/${id}`);
  }

  updateSavCaseStatus(id: number, request: UpdateSavCaseStatusRequest): Observable<SavCaseResponse> {
    return this.http.put<SavCaseResponse>(`${this.apiUrl}/${id}/status`, request);
  }

  getStatusHistory(id: number): Observable<SavCaseStatusHistoryResponse[]> {
    return this.http.get<SavCaseStatusHistoryResponse[]>(`${this.apiUrl}/${id}/history`);
  }
}
