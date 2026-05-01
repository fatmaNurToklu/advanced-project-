import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { AuditLog } from '../../../shared/models/user.model';

@Component({
  selector: 'app-audit-logs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './audit-logs.html',
  styleUrl: './audit-logs.css',
})
export class AuditLogsComponent implements OnInit {
  private userService = inject(UserService);

  logs = signal<AuditLog[]>([]);
  currentPage = signal(0);
  totalPages = signal(0);
  totalElements = signal(0);
  loading = signal(false);
  actionFilter = '';

  ngOnInit() { this.loadLogs(); }

  loadLogs() {
    this.loading.set(true);
    this.userService.getAuditLogs(
      this.currentPage(), 20,
      undefined,
      this.actionFilter || undefined
    ).subscribe({
      next: res => {
        this.logs.set(res.content);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  goToPage(p: number) { this.currentPage.set(p); this.loadLogs(); }

  pages(): number[] {
    const t = this.totalPages(), c = this.currentPage(), d = 2;
    const s = Math.max(0, c - d), e = Math.min(t - 1, c + d);
    return Array.from({ length: e - s + 1 }, (_, i) => s + i);
  }
}
