import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../shared/models/order.model';
import { StoreDashboard } from '../../../shared/models/product.model';

@Component({
  selector: 'app-corporate-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, CurrencyPipe, DatePipe],
  templateUrl: './corporate-dashboard.html',
  styleUrl: './corporate-dashboard.css',
})
export class CorporateDashboardComponent implements OnInit {
  private orderService = inject(OrderService);

  dashboard = signal<StoreDashboard | null>(null);
  recentOrders = signal<Order[]>([]);
  loading = signal(true);

  exportMenuOpen = signal(false);

  readonly weekdays = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  readonly chartData = [4200, 3800, 5100, 6400, 5900, 7800, 6900];

  chartPath = computed(() => this.buildPath(this.chartData));
  chartArea = computed(() => this.buildArea(this.chartData));
  chartDots = computed(() => this.buildDots(this.chartData));
  chartYLabels = computed(() => this.buildYLabels(this.chartData));

  ngOnInit() {
    this.orderService.getCorporateDashboard().subscribe({
      next: d => { this.dashboard.set(d); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
    this.orderService.getCorporateOrders(0, 5).subscribe(res =>
      this.recentOrders.set(res.content)
    );
  }

  private buildPath(data: number[]): string {
    const { pts } = this.normalize(data);
    return 'M ' + pts.map(([x, y]) => `${x},${y}`).join(' L ');
  }

  private buildArea(data: number[]): string {
    const { pts, W, H } = this.normalize(data);
    const line = pts.map(([x, y]) => `${x},${y}`).join(' L ');
    return `M ${pts[0][0]},${H} L ${line} L ${pts[pts.length - 1][0]},${H} Z`;
  }

  private buildDots(data: number[]): { x: number; y: number }[] {
    return this.normalize(data).pts.map(([x, y]) => ({ x, y }));
  }

  private buildYLabels(data: number[]): { value: string; y: number }[] {
    const max = Math.max(...data);
    const H = 160;
    return [0, 0.25, 0.5, 0.75, 1].map(f => ({
      value: '$' + Math.round(max * f / 1000) + 'k',
      y: H - f * H + 10,
    }));
  }

  private normalize(data: number[], W = 560, H = 160) {
    const min = Math.min(...data) * 0.9;
    const max = Math.max(...data) * 1.05;
    const step = W / (data.length - 1);
    const pts = data.map((v, i): [number, number] => [
      i * step,
      H - ((v - min) / (max - min)) * H,
    ]);
    return { pts, W, H };
  }

  badgeClass(status: string): string {
    return 'dp-badge badge-' + status.toLowerCase().replace('_', '-');
  }

  exportCsv() {
    this.exportMenuOpen.set(false);
    const d = this.dashboard();
    const rows = [
      ['Metric', 'Value'],
      ['Total Revenue', d?.totalRevenue ?? 0],
      ['Total Orders', d?.totalOrders ?? 0],
      ['Completed Orders', d?.completedOrders ?? 0],
      ['Pending Orders', d?.pendingOrders ?? 0],
      ['Cancelled Orders', d?.cancelledOrders ?? 0],
      ['Total Products', d?.totalProducts ?? 0],
      ['Low Stock Items', d?.lowStockCount ?? 0],
      ['Avg Rating', d?.avgRating ?? 0],
    ];
    const csv = rows.map(r => r.join(',')).join('\n');
    this.downloadBlob(new Blob([csv], { type: 'text/csv' }), 'dashboard-report.csv');
  }

  exportJson() {
    this.exportMenuOpen.set(false);
    const payload = {
      exportedAt: new Date().toISOString(),
      dashboard: this.dashboard(),
      recentOrders: this.recentOrders(),
    };
    const blob = new Blob([JSON.stringify(payload, null, 2)], { type: 'application/json' });
    this.downloadBlob(blob, 'dashboard-report.json');
  }

  private downloadBlob(blob: Blob, filename: string) {
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
  }
}
