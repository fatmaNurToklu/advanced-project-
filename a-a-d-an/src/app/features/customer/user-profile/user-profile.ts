import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import { OrderService } from '../../../core/services/order.service';
import { User, Address, AddressRequest } from '../../../shared/models/user.model';
import { SpendingAnalytics } from '../../../shared/models/order.model';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.css',
})
export class UserProfileComponent implements OnInit {
  private userService = inject(UserService);
  private orderService = inject(OrderService);

  user = signal<User | null>(null);
  addresses = signal<Address[]>([]);
  analytics = signal<SpendingAnalytics | null>(null);
  showAddForm = signal(false);
  activeTab = signal<'profile' | 'addresses' | 'analytics'>('profile');

  newAddr: AddressRequest = {
    addressTitle: '',
    addressLine: '',
    city: '',
    state: '',
    country: '',
    postalCode: '',
    isDefault: false,
  };

  categoryEntries = signal<[string, number][]>([]);

  ngOnInit() {
    this.userService.getMe().subscribe(u => this.user.set(u));
    this.userService.getAddresses().subscribe(a => this.addresses.set(a));
    this.orderService.getSpendingAnalytics().subscribe(a => {
      this.analytics.set(a);
      this.categoryEntries.set(Object.entries(a.spendByCategory ?? {}).sort((x, y) => y[1] - x[1]));
    });
  }

  addAddress() {
    this.userService.addAddress(this.newAddr).subscribe(a => {
      this.addresses.update(list => [...list, a]);
      this.showAddForm.set(false);
      this.newAddr = { addressTitle: '', addressLine: '', city: '', state: '', country: '', postalCode: '', isDefault: false };
    });
  }

  deleteAddress(addressId: string) {
    this.userService.deleteAddress(addressId).subscribe(() =>
      this.addresses.update(list => list.filter(a => a.addressId !== addressId))
    );
  }

  maxCategorySpend(): number {
    const entries = this.categoryEntries();
    return entries.length ? Math.max(...entries.map(e => e[1])) : 1;
  }
}
