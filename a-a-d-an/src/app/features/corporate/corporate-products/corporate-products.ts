import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { Product, Category, ProductRequest } from '../../../shared/models/product.model';

@Component({
  selector: 'app-corporate-products',
  standalone: true,
  imports: [CommonModule, FormsModule, CurrencyPipe],
  templateUrl: './corporate-products.html',
  styleUrl: './corporate-products.css',
})
export class CorporateProductsComponent implements OnInit {
  private productService = inject(ProductService);

  products = signal<Product[]>([]);
  categories = signal<Category[]>([]);
  totalPages = signal(0);
  totalElements = signal(0);
  currentPage = signal(0);
  loading = signal(false);
  showForm = signal(false);
  editProduct = signal<Product | null>(null);

  keyword = '';
  categoryId = '';

  form: ProductRequest = this.emptyForm();

  ngOnInit() {
    this.loadCategories();
    this.loadProducts();
  }

  loadCategories() {
    this.productService.getCategories().subscribe(c => this.categories.set(c));
  }

  loadProducts() {
    this.loading.set(true);
    this.productService.getProducts({
      keyword: this.keyword || undefined,
      categoryId: this.categoryId || undefined,
      page: this.currentPage(),
      size: 12,
    }).subscribe({
      next: res => {
        this.products.set(res.content);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  openAdd() { this.form = this.emptyForm(); this.editProduct.set(null); this.showForm.set(true); }

  openEdit(p: Product) {
    this.editProduct.set(p);
    this.form = {
      name: p.name, description: p.description, categoryId: p.categoryId,
      sku: p.sku, basePrice: p.basePrice, initialStock: p.quantity,
      lowStockThreshold: 5,
    };
    this.showForm.set(true);
  }

  closeForm() { this.showForm.set(false); }

  submit() {
    const ep = this.editProduct();
    if (ep) {
      this.productService.updateProduct(ep.productId, this.form).subscribe(() => {
        this.showForm.set(false);
        this.loadProducts();
      });
    } else {
      this.productService.createProduct(this.form).subscribe(() => {
        this.showForm.set(false);
        this.loadProducts();
      });
    }
  }

  deleteProduct(productId: string) {
    if (!confirm('Bu ürünü silmek istediğinize emin misiniz?')) return;
    this.productService.deleteProduct(productId).subscribe(() => this.loadProducts());
  }

  goToPage(page: number) { this.currentPage.set(page); this.loadProducts(); }

  pages(): number[] {
    const total = this.totalPages(), cur = this.currentPage(), d = 2;
    const s = Math.max(0, cur - d), e = Math.min(total - 1, cur + d);
    return Array.from({ length: e - s + 1 }, (_, i) => s + i);
  }

  private emptyForm(): ProductRequest {
    return { name: '', description: '', categoryId: '', sku: '', basePrice: 0, initialStock: 0, lowStockThreshold: 5 };
  }
}
