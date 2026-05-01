import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NotificationService } from '../../../core/services/notification.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-contact-support',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './contact-support.html',
  styleUrl: './contact-support.css',
})
export class ContactSupportComponent implements OnInit {
  private notif = inject(NotificationService);
  private auth = inject(AuthService);

  form = { name: '', email: '', subject: '', message: '' };
  sent = signal(false);
  loading = signal(false);
  activeCategory = signal('general');

  readonly categories = [
    { id: 'general',  label: 'General Inquiry',   icon: '💬' },
    { id: 'order',    label: 'Order Issue',        icon: '📦' },
    { id: 'payment',  label: 'Payment Problem',    icon: '💳' },
    { id: 'return',   label: 'Return & Refund',    icon: '↩️' },
    { id: 'tech',     label: 'Technical Support',  icon: '🔧' },
  ];

  ngOnInit() {
    const u = this.auth.currentUser();
    if (u) {
      this.form.name  = `${u.firstName} ${u.lastName}`;
      this.form.email = u.email;
    }
  }

  submit() {
    if (!this.form.name || !this.form.email || !this.form.message) return;
    this.loading.set(true);

    setTimeout(() => {
      this.sent.set(true);
      this.loading.set(false);

      // Push auto-reply notification
      if (this.auth.isLoggedIn()) {
        this.notif.pushLocal(
          'Message Received ✓',
          `We got your message about "${this.form.subject || this.activeCategory()}". Our support team will respond within 24 hours.`
        );
      }
    }, 900);
  }

  getCategoryLabel(): string {
    return this.categories.find(c => c.id === this.activeCategory())?.label ?? 'General Inquiry';
  }

  reset() {
    this.form = { name: '', email: '', subject: '', message: '' };
    this.sent.set(false);
    this.activeCategory.set('general');
  }
}
