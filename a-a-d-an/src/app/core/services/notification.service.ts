import { Injectable, signal } from '@angular/core';
import { Notification, NotificationType } from '../../shared/models/notification.model';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  notifications = signal<Notification[]>([]);
  unreadCount = signal<number>(0);

  private socket: WebSocket | null = null;
  private readonly WS_URL = 'ws://localhost:8080/ws/notifications';

  constructor(private authService: AuthService) {}

  connect(): void {
    const token = this.authService.getToken();
    if (!token) return;

    this.socket = new WebSocket(`${this.WS_URL}?token=${token}`);

    this.socket.onmessage = (event) => {
      const notification: Notification = JSON.parse(event.data);
      this.notifications.update(list => [notification, ...list]);
      this.unreadCount.update(c => c + 1);
    };

    this.socket.onclose = () => {
      setTimeout(() => this.connect(), 5000);
    };
  }

  disconnect(): void {
    this.socket?.close();
    this.socket = null;
  }

  markAllAsRead(): void {
    this.notifications.update(list =>
      list.map(n => ({ ...n, isRead: true }))
    );
    this.unreadCount.set(0);
  }

  pushLocal(title: string, message: string): void {
    this.pushLocalTyped(NotificationType.SYSTEM, title, message);
  }

  pushLocalTyped(type: NotificationType, title: string, message: string): void {
    const notif: Notification = {
      id: Date.now(),
      userId: 0,
      type,
      title,
      message,
      isRead: false,
      createdAt: new Date().toISOString(),
    };
    this.notifications.update(list => [notif, ...list]);
    this.unreadCount.update(c => c + 1);
  }
}
