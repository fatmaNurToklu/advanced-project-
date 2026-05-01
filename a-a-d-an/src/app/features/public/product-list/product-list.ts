import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ProductService, ProductFilter } from '../../../core/services/product.service';
import { Product, Category } from '../../../shared/models/product.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './product-list.html',
  styleUrl: './product-list.css',
})
export class ProductListComponent implements OnInit {
  private productService = inject(ProductService);

  products = signal<Product[]>([]);
  categories = signal<Category[]>([]);
  totalPages = signal(0);
  totalElements = signal(0);
  currentPage = signal(0);
  loading = signal(false);

  filter: ProductFilter = { page: 0, size: 16 };
  search = '';
  expandedCategoryId = signal<string | null>(null);
  selectedCategoryId = signal<string | undefined>(undefined);

  // Filter state
  minPrice = '';
  maxPrice = '';
  sortOption = 'name-asc';

  readonly sortOptions = [
    { value: 'name-asc',        label: 'Name: A → Z' },
    { value: 'name-desc',       label: 'Name: Z → A' },
    { value: 'basePrice-asc',   label: 'Price: Low → High' },
    { value: 'basePrice-desc',  label: 'Price: High → Low' },
    { value: 'avgRating-desc',  label: 'Top Rated' },
  ];

  hasActiveFilters = computed(() =>
    !!this.selectedCategoryId() || !!this.minPrice || !!this.maxPrice || this.sortOption !== 'name-asc'
  );

  ngOnInit() {
    this.loadCategories();
    this.loadProducts();
  }

  loadCategories() {
    this.productService.getCategories().subscribe(c => this.categories.set(c));
  }

  loadProducts() {
    this.loading.set(true);
    const [sortBy, sortDir] = this.sortOption.split('-');
    this.productService.getProducts({
      ...this.filter,
      keyword: this.search || undefined,
      minPrice: this.minPrice ? +this.minPrice : undefined,
      maxPrice: this.maxPrice ? +this.maxPrice : undefined,
      sortBy,
      sortDir,
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

  onSearch() {
    this.filter.page = 0;
    this.currentPage.set(0);
    this.loadProducts();
  }

  applyFilters() {
    this.filter.page = 0;
    this.currentPage.set(0);
    this.loadProducts();
  }

  clearFilters() {
    this.search = '';
    this.minPrice = '';
    this.maxPrice = '';
    this.sortOption = 'name-asc';
    this.filter = { page: 0, size: 16 };
    this.selectedCategoryId.set(undefined);
    this.loadProducts();
  }

  selectCategory(categoryId?: string) {
    this.filter.categoryId = categoryId;
    this.filter.page = 0;
    this.currentPage.set(0);
    this.selectedCategoryId.set(categoryId);
    this.loadProducts();
  }

  toggleExpand(cat: Category, event: Event) {
    event.stopPropagation();
    const isExpanded = this.expandedCategoryId() === cat.categoryId;
    this.expandedCategoryId.set(isExpanded ? null : cat.categoryId);
  }

  getSortLabel(): string {
    return this.sortOptions.find(o => o.value === this.sortOption)?.label ?? '';
  }

  onSortChange() {
    this.filter.page = 0;
    this.currentPage.set(0);
    this.loadProducts();
  }

  goToPage(page: number) {
    this.filter.page = page;
    this.currentPage.set(page);
    this.loadProducts();
  }

  pages() {
    const total = this.totalPages();
    const cur = this.currentPage();
    const delta = 3;
    const start = Math.max(0, cur - delta);
    const end = Math.min(total - 1, cur + delta);
    return Array.from({ length: end - start + 1 }, (_, i) => start + i);
  }
}
