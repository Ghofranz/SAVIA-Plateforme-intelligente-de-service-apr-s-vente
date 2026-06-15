import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { CreateCustomerRequest, CustomerResponse } from '../models/customer.model';

@Injectable({
providedIn: 'root'
})
export class CustomerApiService {
private readonly apiUrl = 'http://localhost:8080/customer-service/api/customers';

constructor(private readonly http: HttpClient) {}

  createCustomer(request: CreateCustomerRequest): Observable<CustomerResponse> {
    return this.http.post<CustomerResponse>(this.apiUrl, request);
  }

  getCustomerById(id: number): Observable<CustomerResponse> {
    return this.http.get<CustomerResponse>(`${this.apiUrl}/${id}`);
  }
}
