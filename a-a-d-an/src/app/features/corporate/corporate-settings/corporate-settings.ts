import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';

@Component({
  selector: 'app-corporate-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './corporate-settings.html',
  styleUrl: './corporate-settings.css',
})
export class CorporateSettingsComponent implements OnInit {
  private userService = inject(UserService);

  store = { name: '', description: '' };
  saved = signal(false);
  error = signal('');

  ngOnInit() {
    this.userService.getMyStore().subscribe(s => {
      this.store.name = s.storeName;
      this.store.description = s.description;
    });
  }

  saveStore() {
    this.userService.updateMyStore(this.store.name, this.store.description).subscribe({
      next: () => { this.saved.set(true); setTimeout(() => this.saved.set(false), 3000); },
      error: () => this.error.set('Mağaza bilgileri güncellenemedi.'),
    });
  }
}
