import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CartService } from '../../../core/services/cart.service';
import { UserService } from '../../../core/services/user.service';
import { NotificationService } from '../../../core/services/notification.service';
import { NotificationType } from '../../../shared/models/notification.model';
import { Address } from '../../../shared/models/user.model';

@Component({
  selector: 'app-cart-checkout',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './cart-checkout.html',
  styleUrl: './cart-checkout.css',
})
export class CartCheckoutComponent implements OnInit {
  cart = inject(CartService);
  private userService = inject(UserService);
  private notif = inject(NotificationService);

  addresses = signal<Address[]>([]);
  selectedAddressId = signal<string | null>(null);
  paymentMethod = signal<string>('Credit_Card');
  step = signal<'cart' | 'address' | 'confirm'>('cart');
  loading = signal(false);

  ngOnInit() {
    this.cart.loadCart().subscribe();
    this.userService.getAddresses().subscribe(a => {
      this.addresses.set(a);
      const def = a.find(x => x.isDefault);
      if (def) this.selectedAddressId.set(def.addressId);
    });
  }

  checkout() {
    const addrId = this.selectedAddressId();
    if (!addrId) return;
    this.loading.set(true);
    this.cart.checkout({ addressId: addrId, paymentMethod: this.paymentMethod() }).subscribe({
      next: () => {
        this.step.set('confirm');
        this.loading.set(false);
        this.pushOrderNotifications();
      },
      error: () => this.loading.set(false),
    });
  }

  private pushOrderNotifications() {
    const itemCount = this.cart.items().length;

    this.notif.pushLocalTyped(
      NotificationType.ORDER_UPDATE,
      'Order Received ✓',
      `Your order of ${itemCount} item${itemCount !== 1 ? 's' : ''} has been confirmed. We're preparing it for shipment.`
    );

    // Simulate shipment update a few seconds later
    setTimeout(() => {
      this.notif.pushLocalTyped(
        NotificationType.SHIPMENT_UPDATE,
        'Order Shipped 🚚',
        'Your order is on its way! You can track your shipment from the Order History page.'
      );
    }, 8000);
  }
}
