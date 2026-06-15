import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import {
CreateCustomerProductRequest,
CustomerProductResponse
} from '../models/customer-product.model';

@Injectable({
providedIn: 'root'
})
export class CustomerProductApiService {
private readonly apiUrl = 'http://localhost:8080/customer-service/api/customer-products';

constructor(private readonly http: HttpClient) {}

  createProduct(request: CreateCustomerProductRequest): Observable<CustomerProductResponse> {
    return this.http.post<CustomerProductResponse>(this.apiUrl, request);
  }

  getProductById(id: number): Observable<CustomerProductResponse> {
    return this.http.get<CustomerProductResponse>(`${this.apiUrl}/${id}`);
  }
}
