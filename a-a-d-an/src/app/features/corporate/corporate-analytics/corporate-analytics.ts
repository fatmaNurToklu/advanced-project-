import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { RevenueAnalytics, CustomerSegment } from '../../../shared/models/product.model';

@Component({
  selector: 'app-corporate-analytics',
  standalone: true,
  imports: [CommonModule, FormsModule, CurrencyPipe],
  templateUrl: './corporate-analytics.html',
  styleUrl: './corporate-analytics.css',
})
export class CorporateAnalyticsComponent implements OnInit {
  private productService = inject(ProductService);

  revenue = signal<RevenueAnalytics | null>(null);
  segments = signal<CustomerSegment[]>([]);
  period = 'MONTHLY';
  loading = signal(false);

  categoryKeys = computed(() =>
    Object.keys(this.revenue()?.revenueByCategory ?? {})
  );

  monthKeys = computed(() =>
    Object.keys(this.revenue()?.revenueByMonth ?? {}).slice(-6)
  );

  maxMonthRevenue = computed(() =>
    Math.max(...Object.values(this.revenue()?.revenueByMonth ?? { _: 1 }), 1)
  );

  segmentColors = ['teal', 'purple', 'orange', 'pink', 'green'];

  ngOnInit() { this.loadData(); }

  loadData() {
    this.loading.set(true);
    this.productService.getRevenueAnalytics(this.period).subscribe({
      next: r => { this.revenue.set(r); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
    this.productService.getCustomerSegments(this.period).subscribe(s =>
      this.segments.set(s)
    );
  }

  monthBarHeight(key: string): number {
    const val = this.revenue()?.revenueByMonth?.[key] ?? 0;
    return Math.round((val / this.maxMonthRevenue()) * 120);
  }
}
