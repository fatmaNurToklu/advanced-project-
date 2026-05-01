import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Order, CheckoutRequest } from '../../shared/models/order.model';

export interface CartItem {
  cartItemId: string;
  productId: string;
  productName: string;
  primaryImageUrl: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
}

export interface CartResponse {
  cartId: string;
  items: CartItem[];
  totalAmount: number;
  totalItems: number;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly CART_API = 'http://localhost:8080/api/cart';
  private readonly CHECKOUT_API = 'http://localhost:8080/api/checkout';

  cart = signal<CartResponse>({ cartId: '', items: [], totalAmount: 0, totalItems: 0 });
  items = computed(() => this.cart().items);
  totalPrice = computed(() => this.cart().totalAmount);
  totalItems = computed(() => this.cart().totalItems);

  constructor(private http: HttpClient) {}

  loadCart(): Observable<CartResponse> {
    return this.http.get<CartResponse>(this.CART_API).pipe(
      tap(cart => this.cart.set(cart))
    );
  }

  addItem(productId: string, quantity = 1): Observable<CartResponse> {
    return this.http.post<CartResponse>(`${this.CART_API}/items`, { productId, quantity }).pipe(
      tap(cart => this.cart.set(cart))
    );
  }

  removeItem(productId: string): Observable<CartResponse> {
    return this.http.delete<CartResponse>(`${this.CART_API}/items/${productId}`).pipe(
      tap(cart => this.cart.set(cart))
    );
  }

  clearCart(): Observable<void> {
    return this.http.delete<void>(this.CART_API);
  }

  checkout(request: CheckoutRequest): Observable<Order> {
    return this.http.post<Order>(this.CHECKOUT_API, request);
  }
}
