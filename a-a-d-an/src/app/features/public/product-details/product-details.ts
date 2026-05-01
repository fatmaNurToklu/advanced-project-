import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { ProductDetail } from '../../../shared/models/product.model';

@Component({
  selector: 'app-product-details',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './product-details.html',
  styleUrl: './product-details.css',
})
export class ProductDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private productService = inject(ProductService);
  private cartService = inject(CartService);
  auth = inject(AuthService);

  product = signal<ProductDetail | null>(null);
  selectedImage = signal(0);
  added = signal(false);
  addingToCart = signal(false);

  reviewStars = signal(5);
  reviewComment = signal('');
  reviewSubmitting = signal(false);
  reviewSuccess = signal(false);
  reviewError = signal('');

  avgRating = computed(() => {
    const p = this.product();
    if (!p || !p.reviews?.length) return 0;
    return p.reviews.reduce((sum, r) => sum + r.starRating, 0) / p.reviews.length;
  });

  stars = [1, 2, 3, 4, 5];

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id') ?? '';
    this.productService.getProduct(id).subscribe(p => this.product.set(p));
  }

  addToCart() {
    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/auth']);
      return;
    }
    const p = this.product();
    if (!p) return;
    this.addingToCart.set(true);
    this.cartService.addItem(p.productId).subscribe({
      next: () => {
        this.added.set(true);
        this.addingToCart.set(false);
        setTimeout(() => this.added.set(false), 2500);
      },
      error: () => this.addingToCart.set(false),
    });
  }

  submitReview() {
    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/auth']);
      return;
    }
    const p = this.product();
    if (!p || !this.reviewComment().trim()) return;
    this.reviewSubmitting.set(true);
    this.reviewError.set('');
    this.productService.submitReview(p.productId, {
      starRating: this.reviewStars(),
      comment: this.reviewComment(),
    }).subscribe({
      next: (review) => {
        this.reviewSubmitting.set(false);
        this.reviewSuccess.set(true);
        this.reviewComment.set('');
        this.reviewStars.set(5);
        const current = this.product();
        if (current) {
          this.product.set({ ...current, reviews: [review, ...(current.reviews ?? [])] });
        }
        setTimeout(() => this.reviewSuccess.set(false), 3000);
      },
      error: (err) => {
        this.reviewSubmitting.set(false);
        this.reviewError.set(err?.error?.message ?? 'Failed to submit review.');
      },
    });
  }
}
