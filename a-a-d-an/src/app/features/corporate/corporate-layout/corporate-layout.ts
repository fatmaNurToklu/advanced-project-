import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

interface NavItem {
  label: string;
  icon: string;
  route: string;
  badge?: string;
}

@Component({
  selector: 'app-corporate-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './corporate-layout.html',
  styleUrl: './corporate-layout.css',
})
export class CorporateLayoutComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  sidebarOpen = signal(true);

  navItems: NavItem[] = [
    { label: 'Dashboard',      icon: '⬛', route: '/corporate/dashboard' },
    { label: 'AI Assistant',   icon: '🤖', route: '/corporate/ai-assistant', badge: 'New' },
    { label: 'Analytics',      icon: '📊', route: '/corporate/analytics' },
    { label: 'Orders',         icon: '🛒', route: '/corporate/orders' },
    { label: 'Products',       icon: '📦', route: '/corporate/products' },
    { label: 'Customers',      icon: '👥', route: '/corporate/customers' },
    { label: 'Store Settings', icon: '🏪', route: '/corporate/settings' },
    { label: 'Shipments',      icon: '🚚', route: '/corporate/shipments' },
    { label: 'Reviews',        icon: '⭐', route: '/corporate/reviews' },
  ];

  toggleSidebar() { this.sidebarOpen.update(v => !v); }

  logout() { this.auth.logout(); }
}
