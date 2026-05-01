import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../shared/models/order.model';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './order-history.html',
  styleUrl: './order-history.css',
})
export class OrderHistoryComponent implements OnInit {
  private orderService = inject(OrderService);

  orders = signal<Order[]>([]);
  loading = signal(false);
  totalPages = signal(0);
  currentPage = signal(0);
  exportMenuOpen = signal(false);

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.loading.set(true);
    this.orderService.getMyOrders(this.currentPage()).subscribe(res => {
      this.orders.set(res.content);
      this.totalPages.set(res.totalPages);
      this.loading.set(false);
    });
  }

  goToPage(page: number) {
    this.currentPage.set(page);
    this.loadOrders();
  }

  pages() {
    const total = this.totalPages(), cur = this.currentPage(), d = 3;
    const s = Math.max(0, cur - d), e = Math.min(total - 1, cur + d);
    return Array.from({ length: e - s + 1 }, (_, i) => s + i);
  }

  exportCsv() {
    this.exportMenuOpen.set(false);
    this.orderService.exportOrders('CSV').subscribe(blob => {
      this.downloadBlob(blob, 'orders.csv', 'text/csv');
    });
  }

  exportJson() {
    this.exportMenuOpen.set(false);
    const data = this.orders().map(o => ({
      orderNumber: o.orderNumber,
      date: o.orderDate,
      status: o.status,
      total: o.totalAmount,
      items: o.items.map(i => ({ product: i.productName, qty: i.quantity, price: i.totalItemPrice })),
    }));
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    this.downloadBlob(blob, 'orders.json', 'application/json');
  }

  private downloadBlob(blob: Blob, filename: string, type: string) {
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
  }
}
