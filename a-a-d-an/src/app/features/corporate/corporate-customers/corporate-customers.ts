import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { ProductService } from '../../../core/services/product.service';
import { CustomerSegment } from '../../../shared/models/product.model';

const MOCK_CUSTOMERS = [
  { name: 'Sarah Miller', email: 'sarah@mail.com', membership: 'Gold', totalSpend: 1284.8, orders: 14, status: 'active' },
  { name: 'James Wilson', email: 'james@mail.com', membership: 'Silver', totalSpend: 756.5, orders: 8, status: 'active' },
  { name: 'Emily Johnson', email: 'emily@mail.com', membership: 'Bronze', totalSpend: 432.9, orders: 5, status: 'offline' },
];

@Component({
  selector: 'app-corporate-customers',
  standalone: true,
  imports: [CommonModule, CurrencyPipe],
  templateUrl: './corporate-customers.html',
  styleUrl: './corporate-customers.css',
})
export class CorporateCustomersComponent implements OnInit {
  private productService = inject(ProductService);

  segments = signal<CustomerSegment[]>([]);
  customers = MOCK_CUSTOMERS;
  loading = signal(false);

  stats = { total: 3421, newThisMonth: 284, goldMembers: 892, avgLtv: 142 };

  segmentColors = ['teal', 'purple', 'orange', 'pink', 'green'];

  ngOnInit() {
    this.loading.set(true);
    this.productService.getCustomerSegments().subscribe({
      next: s => { this.segments.set(s); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  membershipClass(m: string): string {
    if (m === 'Gold') return 'badge-gold';
    if (m === 'Silver') return 'badge-silver';
    return 'badge-bronze';
  }
}
