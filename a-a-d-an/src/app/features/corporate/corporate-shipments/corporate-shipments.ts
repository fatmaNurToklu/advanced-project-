import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { ShipmentRequest } from '../../../shared/models/product.model';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../shared/models/order.model';

@Component({
  selector: 'app-corporate-shipments',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './corporate-shipments.html',
  styleUrl: './corporate-shipments.css',
})
export class CorporateShipmentsComponent implements OnInit {
  private productService = inject(ProductService);
  private orderService = inject(OrderService);

  orders = signal<Order[]>([]);
  loading = signal(false);
  showForm = signal(false);

  stats = { pending: 156, inTransit: 423, delivered: 1247, returned: 23 };

  form: ShipmentRequest = this.emptyForm();

  ngOnInit() { this.loadOrders(); }

  loadOrders() {
    this.loading.set(true);
    this.orderService.getCorporateOrders(0, 10, 'CONFIRMED').subscribe({
      next: res => { this.orders.set(res.content); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  openForm() { this.form = this.emptyForm(); this.showForm.set(true); }
  closeForm() { this.showForm.set(false); }

  submit() {
    this.productService.createShipment(this.form).subscribe(() => {
      this.showForm.set(false);
      this.loadOrders();
    });
  }

  private emptyForm(): ShipmentRequest {
    return {
      orderId: '',
      trackingNumber: '',
      carrierName: 'FedEx',
      modeOfShipment: 'GROUND',
      estimatedDelivery: '',
    };
  }

  badgeClass(status: string): string {
    return 'dp-badge badge-' + (status ?? '').toLowerCase().replace('_', '-');
  }
}
