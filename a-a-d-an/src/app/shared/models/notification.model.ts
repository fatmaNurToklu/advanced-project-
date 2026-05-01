export enum NotificationType {
  ORDER_UPDATE = 'ORDER_UPDATE',
  SHIPMENT_UPDATE = 'SHIPMENT_UPDATE',
  PROMOTION = 'PROMOTION',
  SYSTEM = 'SYSTEM',
}

export interface Notification {
  id: number;
  userId: number;
  type: NotificationType;
  title: string;
  message: string;
  isRead: boolean;
  createdAt: string;
}
