import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-password-reset',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './password-reset.html',
  styleUrl: './password-reset.css',
})
export class PasswordResetComponent {
  email = '';
  sent = signal(false);
  loading = signal(false);
  error = signal('');

  submit() {
    if (!this.email) return;
    this.loading.set(true);
    this.error.set('');
    setTimeout(() => {
      this.sent.set(true);
      this.loading.set(false);
    }, 800);
  }
}
