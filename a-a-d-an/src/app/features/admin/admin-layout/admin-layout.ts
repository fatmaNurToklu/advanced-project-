import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.css',
})
export class AdminLayoutComponent {
  private auth = inject(AuthService);
  sidebarOpen = signal(true);

  navItems = [
    { label: 'Dashboard',        icon: 'grid',    route: '/admin/dashboard' },
    { label: 'AI Assistant',     icon: 'chart',   route: '/admin/ai-assistant' },
    { label: 'User Management',  icon: 'users',   route: '/admin/users' },
    { label: 'Store Approvals',  icon: 'store',   route: '/admin/store-approvals' },
    { label: 'Review Moderation',icon: 'star',    route: '/admin/reviews' },
    { label: 'Categories',       icon: 'grid',    route: '/admin/categories' },
    { label: 'Revenue Analytics',icon: 'chart',   route: '/admin/revenue' },
    { label: 'Audit Logs',       icon: 'log',     route: '/admin/audit-logs' },
    { label: 'System Config',    icon: 'settings',route: '/admin/system' },
  ];

  toggleSidebar() { this.sidebarOpen.update(v => !v); }
  logout() { this.auth.logout(); }
}
