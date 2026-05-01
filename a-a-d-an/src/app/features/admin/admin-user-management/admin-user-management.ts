import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { User } from '../../../shared/models/user.model';

@Component({
  selector: 'app-admin-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-user-management.html',
  styleUrl: './admin-user-management.css',
})
export class AdminUserManagementComponent implements OnInit {
  private userService = inject(UserService);

  users = signal<User[]>([]);
  currentPage = signal(0);
  totalPages = signal(0);
  search = '';

  ngOnInit() { this.loadUsers(); }

  loadUsers() {
    this.userService.getAllUsers(this.currentPage()).subscribe(res => {
      this.users.set(res.content);
      this.totalPages.set(res.totalPages);
    });
  }

  toggleStatus(userId: string, currentStatus: boolean) {
    this.userService.updateUserStatus(userId, !currentStatus).subscribe(updated =>
      this.users.update(list => list.map(u => u.userId === userId ? updated : u))
    );
  }

  deleteUser(userId: string) {
    this.userService.deleteUser(userId).subscribe(() =>
      this.users.update(list => list.filter(u => u.userId !== userId))
    );
  }

  goToPage(page: number) { this.currentPage.set(page); this.loadUsers(); }
  pages() {
    const total = this.totalPages(), cur = this.currentPage(), d = 3;
    const s = Math.max(0, cur - d), e = Math.min(total - 1, cur + d);
    return Array.from({ length: e - s + 1 }, (_, i) => s + i);
  }
}
