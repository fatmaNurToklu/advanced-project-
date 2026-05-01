import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { Review } from '../../../shared/models/product.model';

@Component({
  selector: 'app-corporate-reviews',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './corporate-reviews.html',
  styleUrl: './corporate-reviews.css',
})
export class CorporateReviewsComponent implements OnInit {
  private productService = inject(ProductService);

  reviews = signal<Review[]>([]);
  totalPages = signal(0);
  currentPage = signal(0);
  loading = signal(false);
  replyTarget = signal<Review | null>(null);
  replyText = '';

  ngOnInit() { this.loadReviews(); }

  loadReviews() {
    this.loading.set(true);
    this.productService.getStoreReviews(this.currentPage()).subscribe({
      next: res => { this.reviews.set(res.content); this.totalPages.set(res.totalPages); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  openReply(r: Review) { this.replyTarget.set(r); this.replyText = ''; }
  closeReply() { this.replyTarget.set(null); }

  sendReply() {
    const r = this.replyTarget();
    if (!r || !this.replyText.trim()) return;
    this.productService.replyToReview(r.reviewId, this.replyText).subscribe(() => {
      this.closeReply();
      this.loadReviews();
    });
  }

  stars(n: number): string { return '★'.repeat(Math.min(5, Math.max(0, n))) + '☆'.repeat(5 - Math.min(5, n)); }

  goToPage(p: number) { this.currentPage.set(p); this.loadReviews(); }
  pages(): number[] {
    const t = this.totalPages(), c = this.currentPage(), d = 2;
    const s = Math.max(0, c - d), e = Math.min(t - 1, c + d);
    return Array.from({ length: e - s + 1 }, (_, i) => s + i);
  }

  sentimentClass(s: string): string {
    if (s === 'POSITIVE') return 'badge-delivered';
    if (s === 'NEGATIVE') return 'badge-cancelled';
    return 'badge-pending';
  }
}
