import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface Category {
  categoryId: string;
  name: string;
  slug: string;
  iconUrl?: string;
  parentId?: string;
  children?: Category[];
}

@Component({
  selector: 'app-category-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './category-management.html',
  styleUrl: './category-management.css',
})
export class CategoryManagementComponent implements OnInit {
  private http = inject(HttpClient);
  private readonly API = 'http://localhost:8080/api/categories';

  categories = signal<Category[]>([]);
  loading = signal(false);
  showForm = signal(false);
  editTarget = signal<Category | null>(null);

  form = { name: '', slug: '', iconUrl: '', parentId: '' };

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading.set(true);
    this.http.get<Category[]>(this.API).subscribe({
      next: data => { this.categories.set(data); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  openCreate() {
    this.editTarget.set(null);
    this.form = { name: '', slug: '', iconUrl: '', parentId: '' };
    this.showForm.set(true);
  }

  openEdit(cat: Category) {
    this.editTarget.set(cat);
    this.form = { name: cat.name, slug: cat.slug, iconUrl: cat.iconUrl || '', parentId: cat.parentId || '' };
    this.showForm.set(true);
  }

  save() {
    const body = {
      name: this.form.name,
      slug: this.form.slug,
      iconUrl: this.form.iconUrl || null,
      parentId: this.form.parentId || null,
    };
    const target = this.editTarget();
    const req = target
      ? this.http.put<Category>(`${this.API}/${target.categoryId}`, body)
      : this.http.post<Category>(this.API, body);

    req.subscribe({ next: () => { this.showForm.set(false); this.load(); } });
  }

  delete(cat: Category) {
    if (!confirm(`"${cat.name}" kategorisini sil?`)) return;
    this.http.delete(`${this.API}/${cat.categoryId}`).subscribe({ next: () => this.load() });
  }

  autoSlug() {
    this.form.slug = this.form.name.toLowerCase().replace(/\s+/g, '-').replace(/[^a-z0-9-]/g, '');
  }

  flatList(cats: Category[]): Category[] {
    const result: Category[] = [];
    const flatten = (list: Category[]) => list.forEach(c => { result.push(c); if (c.children) flatten(c.children); });
    flatten(cats);
    return result;
  }
}
