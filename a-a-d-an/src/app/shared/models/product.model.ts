export interface Product {
  productId: string;
  name: string;
  description: string;
  basePrice: number;
  categoryId: string;
  categoryName: string;
  storeId: string;
  storeName: string;
  sku: string;
  isActive: boolean;
  primaryImageUrl: string;
  avgRating: number;
  reviewCount: number;
  inventoryStatus: string;
  quantity: number;
}

export interface ProductDetail extends Product {
  imageUrls: string[];
  reviews: Review[];
}

export interface Review {
  reviewId: string;
  userId: string;
  userName: string;
  productId: string;
  starRating: number;
  sentiment: string;
  comment: string;
  helpfulVotes: number;
  isVerifiedPurchase: boolean;
  createdAt: string;
}

export interface Category {
  categoryId: string;
  name: string;
  slug: string;
  iconUrl: string;
  parentId: string;
  children: Category[];
}

export interface InventoryItem {
  inventoryId: string;
  productId: string;
  productName: string;
  quantity: number;
  lowStockThreshold: number;
  binLocation: string;
  status: string;
  isLowStock: boolean;
  lastStockUpdate: string;
}

export interface StoreDashboard {
  totalRevenue: number;
  totalOrders: number;
  pendingOrders: number;
  completedOrders: number;
  cancelledOrders: number;
  totalProducts: number;
  lowStockCount: number;
  avgRating: number;
  totalReviews: number;
}

export interface RevenueAnalytics {
  totalRevenue: number;
  periodRevenue: number;
  revenueByMonth: Record<string, number>;
  revenueByCategory: Record<string, number>;
  avgOrderValue: number;
  totalOrders: number;
}

export interface ProductRequest {
  name: string;
  description: string;
  categoryId: string;
  sku: string;
  basePrice: number;
  initialStock: number;
  lowStockThreshold: number;
}

export interface ShipmentRequest {
  orderId: string;
  trackingNumber: string;
  carrierName: string;
  modeOfShipment: string;
  estimatedDelivery: string;
}

export interface CustomerSegment {
  segment: string;
  count: number;
  revenue: number;
  avgOrderValue: number;
  percentage: number;
}
