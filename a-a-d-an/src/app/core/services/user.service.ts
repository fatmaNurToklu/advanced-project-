import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  User, Address, AddressRequest, UpdateProfileRequest,
  AdminDashboard, StoreResponse, AuditLog, SystemConfig,
  StoreComparison, CategoryRequest
} from '../../shared/models/user.model';
import { PageResponse } from './product.service';
import { Review, Category } from '../../shared/models/product.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly API = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // --- Individual ---
  getMe(): Observable<User> {
    return this.http.get<User>(`${this.API}/users/profile`);
  }

  updateMe(data: UpdateProfileRequest): Observable<User> {
    return this.http.put<User>(`${this.API}/users/profile`, data);
  }

  getAddresses(): Observable<Address[]> {
    return this.http.get<Address[]>(`${this.API}/users/addresses`);
  }

  addAddress(address: AddressRequest): Observable<Address> {
    return this.http.post<Address>(`${this.API}/users/addresses`, address);
  }

  deleteAddress(addressId: string): Observable<void> {
    return this.http.delete<void>(`${this.API}/users/addresses/${addressId}`);
  }

  // --- Admin: Users ---
  getAllUsers(page = 0, size = 20): Observable<PageResponse<User>> {
    return this.http.get<PageResponse<User>>(`${this.API}/admin/users`, {
      params: { page, size },
    });
  }

  updateUserStatus(userId: string, status: boolean): Observable<User> {
    return this.http.patch<User>(`${this.API}/admin/users/${userId}/status`, null, {
      params: { status },
    });
  }

  deleteUser(userId: string): Observable<void> {
    return this.http.delete<void>(`${this.API}/admin/users/${userId}`);
  }

  getAdminDashboard(): Observable<AdminDashboard> {
    return this.http.get<AdminDashboard>(`${this.API}/admin/dashboard`);
  }

  // --- Admin: Stores ---
  getAllStores(page = 0, size = 20): Observable<PageResponse<StoreResponse>> {
    return this.http.get<PageResponse<StoreResponse>>(`${this.API}/admin/stores`, {
      params: { page, size },
    });
  }

  updateStoreStatus(storeId: string, status: string): Observable<StoreResponse> {
    return this.http.patch<StoreResponse>(`${this.API}/admin/stores/${storeId}/status`, null, {
      params: { status },
    });
  }

  // --- Admin: Reviews ---
  getAdminReviews(page = 0, size = 20): Observable<PageResponse<Review>> {
    return this.http.get<PageResponse<Review>>(`${this.API}/admin/reviews`, {
      params: { page, size },
    });
  }

  deleteAdminReview(reviewId: string): Observable<void> {
    return this.http.delete<void>(`${this.API}/admin/reviews/${reviewId}`);
  }

  // --- Admin: Categories ---
  createCategory(data: CategoryRequest): Observable<Category> {
    return this.http.post<Category>(`${this.API}/admin/categories`, data);
  }

  updateCategory(id: string, data: CategoryRequest): Observable<Category> {
    return this.http.put<Category>(`${this.API}/admin/categories/${id}`, data);
  }

  deleteCategory(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API}/admin/categories/${id}`);
  }

  // --- Admin: Analytics ---
  getStoreComparison(): Observable<StoreComparison[]> {
    return this.http.get<StoreComparison[]>(`${this.API}/admin/analytics/stores/comparison`);
  }

  // --- Admin: Config ---
  getSystemConfig(): Observable<SystemConfig[]> {
    return this.http.get<SystemConfig[]>(`${this.API}/admin/config`);
  }

  updateSystemConfig(data: Record<string, string>): Observable<SystemConfig[]> {
    return this.http.put<SystemConfig[]>(`${this.API}/admin/config`, data);
  }

  deleteConfigKey(key: string): Observable<void> {
    return this.http.delete<void>(`${this.API}/admin/config/${key}`);
  }

  // --- Admin: Audit Logs ---
  getAuditLogs(page = 0, size = 20, userId?: string, action?: string): Observable<PageResponse<AuditLog>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (userId) params = params.set('userId', userId);
    if (action) params = params.set('action', action);
    return this.http.get<PageResponse<AuditLog>>(`${this.API}/admin/audit-logs`, { params });
  }

  // --- Corporate: Store ---
  getMyStore(): Observable<StoreResponse> {
    return this.http.get<StoreResponse>(`${this.API}/corporate/store`);
  }

  updateMyStore(storeName: string, description: string): Observable<StoreResponse> {
    return this.http.put<StoreResponse>(`${this.API}/corporate/store`, null, {
      params: { storeName, description },
    });
  }
}
