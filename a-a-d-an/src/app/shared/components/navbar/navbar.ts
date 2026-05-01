import { Component, computed, inject, signal, HostListener } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { CartService } from '../../../core/services/cart.service';
import { UserRole } from '../../models/user.model';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class NavbarComponent {
  auth = inject(AuthService);
  notif = inject(NotificationService);
  cart = inject(CartService);

  isAdmin = computed(() => this.auth.userRole() === UserRole.ADMIN);
  isCorporate = computed(() => this.auth.userRole() === UserRole.CORPORATE);
  isCustomer = computed(() => this.auth.userRole() === UserRole.CUSTOMER);
  unreadCount = this.notif.unreadCount;
  cartCount = computed(() => this.cart.totalItems());
  userDropdownOpen = signal(false);
  mobileMenuOpen = signal(false);

  userName = computed(() => {
    const u = this.auth.currentUser();
    if (u) return `${u.firstName} ${u.lastName}`;
    return 'Account';
  });

  userInitials = computed(() => {
    const u = this.auth.currentUser();
    if (u) return `${u.firstName[0] ?? ''}${u.lastName[0] ?? ''}`.toUpperCase();
    return 'U';
  });

  @HostListener('document:click', ['$event'])
  onDocumentClick(e: Event) {
    const target = e.target as HTMLElement;
    if (!target.closest('.user-menu')) this.userDropdownOpen.set(false);
    if (!target.closest('.mobile-toggle') && !target.closest('.nav-links')) this.mobileMenuOpen.set(false);
  }

  toggleUserDropdown() { this.userDropdownOpen.update(v => !v); }
  toggleMobileMenu() { this.mobileMenuOpen.update(v => !v); }

  logout() {
    this.auth.logout();
    this.userDropdownOpen.set(false);
  }
}
