import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import { AdminDashboard } from '../../../shared/models/user.model';

@Component({
  selector: 'app-admin-management',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-management.html',
  styleUrl: './admin-management.css',
})
export class AdminManagementComponent implements OnInit {
  private userService = inject(UserService);

  dashboard = signal<AdminDashboard | null>(null);

  ngOnInit() {
    this.userService.getAdminDashboard().subscribe(d => this.dashboard.set(d));
  }
}
