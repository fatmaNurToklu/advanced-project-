import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { InventoryItem } from '../../../shared/models/product.model';

@Component({
  selector: 'app-store-inventory',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './store-inventory.html',
  styleUrl: './store-inventory.css',
})
export class StoreInventoryComponent implements OnInit {
  private productService = inject(ProductService);

  inventory = signal<InventoryItem[]>([]);
  totalPages = signal(0);
  currentPage = signal(0);
  search = '';

  ngOnInit() { this.loadInventory(); }

  loadInventory() {
    this.productService.getInventory(this.currentPage()).subscribe(res => {
      this.inventory.set(res.content);
      this.totalPages.set(res.totalPages);
    });
  }

  goToPage(page: number) { this.currentPage.set(page); this.loadInventory(); }
  pages() {
    const total = this.totalPages(), cur = this.currentPage(), d = 3;
    const s = Math.max(0, cur - d), e = Math.min(total - 1, cur + d);
    return Array.from({ length: e - s + 1 }, (_, i) => s + i);
  }
}
