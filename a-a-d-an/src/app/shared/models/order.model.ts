export interface OrderItem {
  orderItemId: string;
  productId: string;
  productName: string;
  quantity: number;
  unitPriceAtSale: number;
  totalItemPrice: number;
}

export interface Shipment {
  shipmentId: string;
  orderId: string;
  trackingNumber: string;
  carrierName: string;
  modeOfShipment: string;
  shippingStatus: string;
  estimatedDelivery: string;
}

export interface Payment {
  paymentId: string;
  paymentMethod: string;
  paymentStatus: string;
  transactionId: string;
  paidAt: string;
}

export interface Order {
  orderId: string;
  orderNumber: string;
  storeId: string;
  storeName: string;
  totalAmount: number;
  status: string;
  fulfilment: string;
  orderDate: string;
  items: OrderItem[];
  shipment?: Shipment | null;
  payment?: Payment | null;
}

export interface CheckoutRequest {
  addressId: string;
  paymentMethod: string;
  couponCode?: string;
}

export interface SpendingAnalytics {
  totalSpend: number;
  totalOrders: number;
  completedOrders: number;
  cancelledOrders: number;
  avgOrderValue: number;
  spendByCategory: Record<string, number>;
  recentOrders: Order[];
}
