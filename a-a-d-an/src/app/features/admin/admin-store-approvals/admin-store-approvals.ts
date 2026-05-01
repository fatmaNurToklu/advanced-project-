import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../core/services/user.service';
import { StoreResponse } from '../../../shared/models/user.model';

@Component({
  selector: 'app-admin-store-approvals',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-store-approvals.html',
  styleUrl: './admin-store-approvals.css',
})
export class AdminStoreApprovalsComponent implements OnInit {
  private userService = inject(UserService);

  stores = signal<StoreResponse[]>([]);
  totalPages = signal(0);
  currentPage = signal(0);

  ngOnInit() { this.loadStores(); }

  loadStores() {
    this.userService.getAllStores(this.currentPage()).subscribe(res => {
      this.stores.set(res.content);
      this.totalPages.set(res.totalPages);
    });
  }

  setStatus(storeId: string, status: string) {
    this.userService.updateStoreStatus(storeId, status).subscribe(updated =>
      this.stores.update(list => list.map(s => s.storeId === storeId ? updated : s))
    );
  }

  goToPage(page: number) { this.currentPage.set(page); this.loadStores(); }
  pages() {
    const total = this.totalPages(), cur = this.currentPage(), d = 3;
    const s = Math.max(0, cur - d), e = Math.min(total - 1, cur + d);
    return Array.from({ length: e - s + 1 }, (_, i) => s + i);
  }
}
