import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Product, ProductDetail, Category, InventoryItem, ProductRequest,
  ShipmentRequest, CustomerSegment, RevenueAnalytics, Review
} from '../../shared/models/product.model';

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
  last: boolean;
}

export interface ProductFilter {
  categoryId?: string;
  minPrice?: number;
  maxPrice?: number;
  keyword?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: string;
}

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly API = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getProducts(filter: ProductFilter = {}): Observable<PageResponse<Product>> {
    let params = new HttpParams();
    if (filter.categoryId) params = params.set('categoryId', filter.categoryId);
    if (filter.minPrice != null) params = params.set('minPrice', filter.minPrice);
    if (filter.maxPrice != null) params = params.set('maxPrice', filter.maxPrice);
    if (filter.keyword) params = params.set('keyword', filter.keyword);
    params = params.set('page', filter.page ?? 0);
    params = params.set('size', filter.size ?? 20);
    if (filter.sortBy) params = params.set('sortBy', filter.sortBy);
    if (filter.sortDir) params = params.set('sortDir', filter.sortDir);
    return this.http.get<PageResponse<Product>>(`${this.API}/products`, { params });
  }

  getProduct(id: string): Observable<ProductDetail> {
    return this.http.get<ProductDetail>(`${this.API}/products/${id}`);
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.API}/categories`);
  }

  getCategoryById(id: string): Observable<Category> {
    return this.http.get<Category>(`${this.API}/categories/${id}`);
  }

  createProduct(product: ProductRequest): Observable<Product> {
    return this.http.post<Product>(`${this.API}/corporate/products`, product);
  }

  updateProduct(productId: string, product: Partial<ProductRequest>): Observable<Product> {
    return this.http.put<Product>(`${this.API}/corporate/products/${productId}`, product);
  }

  deleteProduct(productId: string): Observable<void> {
    return this.http.delete<void>(`${this.API}/corporate/products/${productId}`);
  }

  addProductImage(productId: string, imageUrl: string, isPrimary = false): Observable<void> {
    return this.http.post<void>(`${this.API}/corporate/products/${productId}/images`, null, {
      params: { imageUrl, isPrimary }
    });
  }

  getInventory(page = 0, size = 20): Observable<PageResponse<InventoryItem>> {
    return this.http.get<PageResponse<InventoryItem>>(`${this.API}/corporate/inventory`, {
      params: { page, size }
    });
  }

  createShipment(data: ShipmentRequest): Observable<any> {
    return this.http.post<any>(`${this.API}/corporate/shipments`, data);
  }

  getRevenueAnalytics(period = 'MONTHLY', startDate?: string, endDate?: string): Observable<RevenueAnalytics> {
    let params = new HttpParams().set('period', period);
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    return this.http.get<RevenueAnalytics>(`${this.API}/corporate/analytics/revenue`, { params });
  }

  getCustomerSegments(period?: string): Observable<CustomerSegment[]> {
    let params = new HttpParams();
    if (period) params = params.set('period', period);
    return this.http.get<CustomerSegment[]>(`${this.API}/corporate/analytics/customers/segments`, { params });
  }

  getStoreReviews(page = 0, size = 10): Observable<PageResponse<Review>> {
    return this.http.get<PageResponse<Review>>(`${this.API}/corporate/reviews`, {
      params: { page, size }
    });
  }

  replyToReview(reviewId: string, reply: string): Observable<Review> {
    return this.http.post<Review>(`${this.API}/corporate/reviews/${reviewId}/reply`, null, {
      params: { reply }
    });
  }

  submitReview(productId: string, data: { starRating: number; comment: string }): Observable<Review> {
    return this.http.post<Review>(`${this.API}/products/${productId}/reviews`, data);
  }
}
