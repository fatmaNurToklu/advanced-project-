import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { UserService } from '../../../core/services/user.service';
import { StoreComparison } from '../../../shared/models/user.model';
import { AdminDashboard } from '../../../shared/models/user.model';

@Component({
  selector: 'app-revenue-analytics',
  standalone: true,
  imports: [CommonModule, CurrencyPipe],
  templateUrl: './revenue-analytics.html',
  styleUrl: './revenue-analytics.css',
})
export class RevenueAnalyticsComponent implements OnInit {
  private userService = inject(UserService);

  dashboard = signal<AdminDashboard | null>(null);
  storeComparison = signal<StoreComparison[]>([]);
  loading = signal(false);

  ngOnInit() {
    this.loading.set(true);
    this.userService.getAdminDashboard().subscribe(d => {
      this.dashboard.set(d);
      this.loading.set(false);
    });
    this.userService.getStoreComparison().subscribe(s =>
      this.storeComparison.set(s)
    );
  }

  maxRevenue(): number {
    return Math.max(...this.storeComparison().map(s => s.totalRevenue), 1);
  }

  revenueBarWidth(rev: number): number {
    return Math.round((rev / this.maxRevenue()) * 100);
  }
}
