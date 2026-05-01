import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CartService } from '../../../core/services/cart.service';

@Component({
  selector: 'app-authentication',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './authentication.html',
  styleUrl: './authentication.css',
})
export class AuthenticationComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  private cartService = inject(CartService);

  mode = signal<'login' | 'register'>('login');
  accountType = signal<'CUSTOMER' | 'CORPORATE'>('CUSTOMER');
  error = signal('');
  loading = signal(false);

  email = '';
  password = '';
  firstName = '';
  lastName = '';
  phone = '';
  storeName = '';

  submit() {
    this.error.set('');
    this.loading.set(true);

    const obs = this.mode() === 'login'
      ? this.auth.login({ email: this.email, password: this.password })
      : this.auth.register({
          email: this.email,
          password: this.password,
          firstName: this.firstName,
          lastName: this.lastName,
          phone: this.phone,
          role: this.accountType(),
          storeName: this.accountType() === 'CORPORATE' ? this.storeName : undefined,
        });

    obs.subscribe({
      next: (res) => {
        this.cartService.loadCart().subscribe();
        const role = res.role;
        if (role === 'ADMIN') this.router.navigate(['/admin']);
        else if (role === 'CORPORATE') this.router.navigate(['/corporate']);
        else this.router.navigate(['/home']);
      },
      error: () => {
        this.error.set('Geçersiz bilgiler. Lütfen tekrar deneyin.');
        this.loading.set(false);
      },
    });
  }
}
