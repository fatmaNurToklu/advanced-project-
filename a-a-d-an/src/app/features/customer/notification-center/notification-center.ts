import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../../core/services/notification.service';
import { NotificationType } from '../../../shared/models/notification.model';

@Component({
  selector: 'app-notification-center',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification-center.html',
  styleUrl: './notification-center.css',
})
export class NotificationCenterComponent {
  notifService = inject(NotificationService);

  typeLabel(type: NotificationType): string {
    const labels: Record<NotificationType, string> = {
      [NotificationType.ORDER_UPDATE]:    'Order',
      [NotificationType.SHIPMENT_UPDATE]: 'Shipment',
      [NotificationType.PROMOTION]:       'Promo',
      [NotificationType.SYSTEM]:          'System',
    };
    return labels[type] ?? type;
  }
}
