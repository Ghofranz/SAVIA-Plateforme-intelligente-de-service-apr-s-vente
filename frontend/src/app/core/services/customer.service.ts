import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Customer, CustomerProduct } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private readonly CUSTOMER_URL = 'http://localhost:8080/customer-service/api/customers';
  private readonly PRODUCT_URL = 'http://localhost:8080/customer-service/api/customer-products';

  constructor(private http: HttpClient) {}

  createCustomer(data: Partial<Customer>): Observable<Customer> {
    return this.http.post<Customer>(this.CUSTOMER_URL, data);
  }

  getCustomerById(id: string): Observable<Customer> {
    return this.http.get<Customer>(`${this.CUSTOMER_URL}/${id}`);
  }

  getCustomerByAuthUserId(authUserId: string): Observable<Customer> {
    return this.http.get<Customer>(`${this.CUSTOMER_URL}/auth/${authUserId}`);
  }

  getAllCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(this.CUSTOMER_URL);
  }

  createCustomerProduct(data: Partial<CustomerProduct>): Observable<CustomerProduct> {
    return this.http.post<CustomerProduct>(this.PRODUCT_URL, data);
  }

  getProductsByCustomerId(customerId: string): Observable<CustomerProduct[]> {
    return this.http.get<CustomerProduct[]>(`${this.PRODUCT_URL}/customer/${customerId}`);
  }
}
