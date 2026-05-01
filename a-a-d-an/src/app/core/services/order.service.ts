import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order, SpendingAnalytics } from '../../shared/models/order.model';
import { PageResponse } from './product.service';
import { StoreDashboard } from '../../shared/models/product.model';

export interface CouponValidation {
  valid: boolean;
  discountPercent: number;
  discountAmount: number;
  couponCode: string;
}

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly API = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getMyOrders(page = 0, size = 10): Observable<PageResponse<Order>> {
    return this.http.get<PageResponse<Order>>(`${this.API}/orders`, {
      params: { page, size },
    });
  }

  getOrder(orderId: string): Observable<Order> {
    return this.http.get<Order>(`${this.API}/orders/${orderId}`);
  }

  cancelOrder(orderId: string): Observable<Order> {
    return this.http.patch<Order>(`${this.API}/orders/${orderId}/cancel`, {});
  }

  exportOrders(format = 'CSV'): Observable<Blob> {
    return this.http.get(`${this.API}/orders/export`, {
      params: { format },
      responseType: 'blob',
    });
  }

  validateCoupon(code: string): Observable<CouponValidation> {
    return this.http.get<CouponValidation>(`${this.API}/coupons/validate`, {
      params: { code },
    });
  }

  getCorporateOrders(page = 0, size = 10, status?: string): Observable<PageResponse<Order>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<PageResponse<Order>>(`${this.API}/corporate/orders`, { params });
  }

  getCorporateDashboard(): Observable<StoreDashboard> {
    return this.http.get<StoreDashboard>(`${this.API}/corporate/dashboard`);
  }

  getSpendingAnalytics(): Observable<SpendingAnalytics> {
    return this.http.get<SpendingAnalytics>(`${this.API}/users/analytics/spending`);
  }
}
