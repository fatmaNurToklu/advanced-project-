import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Review } from '../../../shared/models/product.model';

interface ReviewPage { content: Review[]; totalPages: number; totalElements: number; }

@Component({
  selector: 'app-review-management',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './review-management.html',
  styleUrl: './review-management.css',
})
export class ReviewManagementComponent implements OnInit {
  private http = inject(HttpClient);
  private readonly API = 'http://localhost:8080/api/admin/reviews';

  reviews = signal<Review[]>([]);
  totalPages = signal(0);
  currentPage = signal(0);

  ngOnInit() { this.loadReviews(); }

  loadReviews() {
    this.http.get<ReviewPage>(this.API, { params: { page: this.currentPage(), size: 20 } })
      .subscribe(res => { this.reviews.set(res.content); this.totalPages.set(res.totalPages); });
  }

  deleteReview(reviewId: string) {
    this.http.delete(`${this.API}/${reviewId}`).subscribe(() =>
      this.reviews.update(list => list.filter(r => r.reviewId !== reviewId))
    );
  }

  goToPage(page: number) { this.currentPage.set(page); this.loadReviews(); }
  pages() {
    const total = this.totalPages(), cur = this.currentPage(), d = 3;
    const s = Math.max(0, cur - d), e = Math.min(total - 1, cur + d);
    return Array.from({ length: e - s + 1 }, (_, i) => s + i);
  }
}
