import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../../core/services/order.service';
import { ProductService } from '../../../core/services/product.service';
import { Order } from '../../../shared/models/order.model';

@Component({
  selector: 'app-corporate-orders',
  standalone: true,
  imports: [CommonModule, FormsModule, CurrencyPipe, DatePipe],
  templateUrl: './corporate-orders.html',
  styleUrl: './corporate-orders.css',
})
export class CorporateOrdersComponent implements OnInit {
  private orderService = inject(OrderService);
  private productService = inject(ProductService);

  orders = signal<Order[]>([]);
  currentPage = signal(0);
  totalPages = signal(0);
  totalElements = signal(0);
  loading = signal(false);
  statusFilter = '';
  selectedOrder = signal<Order | null>(null);

  ngOnInit() { this.loadOrders(); }

  loadOrders() {
    this.loading.set(true);
    const status = this.statusFilter || undefined;
    this.orderService.getCorporateOrders(this.currentPage(), 10, status).subscribe({
      next: res => {
        this.orders.set(res.content);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  goToPage(page: number) { this.currentPage.set(page); this.loadOrders(); }

  pages(): number[] {
    const total = this.totalPages(), cur = this.currentPage(), d = 2;
    const s = Math.max(0, cur - d), e = Math.min(total - 1, cur + d);
    return Array.from({ length: e - s + 1 }, (_, i) => s + i);
  }

  openOrder(order: Order) { this.selectedOrder.set(order); }
  closeOrder() { this.selectedOrder.set(null); }

  badgeClass(status: string): string {
    return 'dp-badge badge-' + status.toLowerCase().replace('_', '-');
  }

  createShipment(orderId: string) {
    const tracking = prompt('Tracking numarası giriniz:');
    if (!tracking) return;
    this.productService.createShipment({
      orderId,
      trackingNumber: tracking,
      carrierName: 'FedEx',
      modeOfShipment: 'GROUND',
      estimatedDelivery: new Date(Date.now() + 5 * 86400000).toISOString().split('T')[0],
    }).subscribe(() => this.loadOrders());
  }
}
