export enum UserRole {
  CUSTOMER = 'CUSTOMER',
  CORPORATE = 'CORPORATE',
  ADMIN = 'ADMIN',
}

export interface User {
  userId: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  gender: string;
  role: string;
  status: boolean;
  age: number;
  city: string;
  membershipType: string;
  totalSpend: number;
  satisfactionLevel: number;
  lastLogin: string;
  createdAt: string;
}

export interface Address {
  addressId: string;
  addressTitle: string;
  addressLine: string;
  city: string;
  state: string;
  country: string;
  postalCode: string;
  isDefault: boolean;
  isActive: boolean;
}

export interface AddressRequest {
  addressTitle: string;
  addressLine: string;
  city: string;
  state: string;
  country: string;
  postalCode: string;
  isDefault: boolean;
}

export interface UpdateProfileRequest {
  firstName: string;
  lastName: string;
  phone: string;
  gender: string;
  age: number;
  city: string;
  membershipType: string;
}

export interface AdminDashboard {
  totalUsers: number;
  totalCustomers: number;
  totalCorporateUsers: number;
  totalStores: number;
  openStores: number;
  closedStores: number;
  totalOrders: number;
  pendingOrders: number;
  platformRevenue: number;
  totalProducts: number;
}

export interface StoreResponse {
  storeId: string;
  storeName: string;
  description: string;
  status: string;
  storeRating: number;
  ownerName: string;
  createdAt: string;
}

export interface AuditLog {
  logId: string;
  userId: string;
  userEmail: string;
  action: string;
  targetType: string;
  targetId: string;
  description: string;
  ipAddress: string;
  createdAt: string;
}

export interface SystemConfig {
  configKey: string;
  configValue: string;
  description: string;
  updatedAt: string;
}

export interface StoreComparison {
  storeId: string;
  storeName: string;
  totalRevenue: number;
  totalOrders: number;
  avgRating: number;
  totalProducts: number;
}

export interface CategoryRequest {
  name: string;
  slug: string;
  parentId?: string;
}
