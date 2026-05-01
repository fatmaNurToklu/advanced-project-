import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';
import { corporateGuard } from './core/guards/corporate.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },

  // Public
  { path: 'home',          loadComponent: () => import('./features/public/home/home').then(m => m.HomeComponent) },
  { path: 'auth',          loadComponent: () => import('./features/public/authentication/authentication').then(m => m.AuthenticationComponent) },
  { path: 'products',      loadComponent: () => import('./features/public/product-list/product-list').then(m => m.ProductListComponent) },
  { path: 'products/:id',  loadComponent: () => import('./features/public/product-details/product-details').then(m => m.ProductDetailsComponent) },
  { path: 'contact',       loadComponent: () => import('./features/public/contact-support/contact-support').then(m => m.ContactSupportComponent) },
  { path: 'password-reset',loadComponent: () => import('./features/public/password-reset/password-reset').then(m => m.PasswordResetComponent) },
  { path: 'faq',           loadComponent: () => import('./features/public/faq/faq').then(m => m.FaqComponent) },

  // Customer (auth required)
  { path: 'ai-assistant',      canActivate: [authGuard], loadComponent: () => import('./features/chatbot/ai-data-assistant/ai-data-assistant').then(m => m.AiDataAssistantComponent) },
  { path: 'profile',           canActivate: [authGuard], loadComponent: () => import('./features/customer/user-profile/user-profile').then(m => m.UserProfileComponent) },
  { path: 'cart',              canActivate: [authGuard], loadComponent: () => import('./features/customer/cart-checkout/cart-checkout').then(m => m.CartCheckoutComponent) },
  { path: 'orders',            canActivate: [authGuard], loadComponent: () => import('./features/customer/order-history/order-history').then(m => m.OrderHistoryComponent) },
  { path: 'checkout-success',  canActivate: [authGuard], loadComponent: () => import('./features/customer/checkout-success/checkout-success').then(m => m.CheckoutSuccessComponent) },
  { path: 'notifications',     canActivate: [authGuard], loadComponent: () => import('./features/customer/notification-center/notification-center').then(m => m.NotificationCenterComponent) },

  // Corporate (nested layout with sidebar)
  {
    path: 'corporate',
    canActivate: [corporateGuard],
    loadComponent: () => import('./features/corporate/corporate-layout/corporate-layout').then(m => m.CorporateLayoutComponent),
    children: [
      { path: '',            redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard',   loadComponent: () => import('./features/corporate/corporate-dashboard/corporate-dashboard').then(m => m.CorporateDashboardComponent) },
      { path: 'ai-assistant',loadComponent: () => import('./features/chatbot/ai-data-assistant/ai-data-assistant').then(m => m.AiDataAssistantComponent) },
      { path: 'analytics',   loadComponent: () => import('./features/corporate/corporate-analytics/corporate-analytics').then(m => m.CorporateAnalyticsComponent) },
      { path: 'orders',      loadComponent: () => import('./features/corporate/corporate-orders/corporate-orders').then(m => m.CorporateOrdersComponent) },
      { path: 'products',    loadComponent: () => import('./features/corporate/corporate-products/corporate-products').then(m => m.CorporateProductsComponent) },
      { path: 'customers',   loadComponent: () => import('./features/corporate/corporate-customers/corporate-customers').then(m => m.CorporateCustomersComponent) },
      { path: 'settings',    loadComponent: () => import('./features/corporate/corporate-settings/corporate-settings').then(m => m.CorporateSettingsComponent) },
      { path: 'shipments',   loadComponent: () => import('./features/corporate/corporate-shipments/corporate-shipments').then(m => m.CorporateShipmentsComponent) },
      { path: 'reviews',     loadComponent: () => import('./features/corporate/corporate-reviews/corporate-reviews').then(m => m.CorporateReviewsComponent) },
      { path: 'inventory',   loadComponent: () => import('./features/corporate/store-inventory/store-inventory').then(m => m.StoreInventoryComponent) },
      { path: 'ai-insights', loadComponent: () => import('./features/chatbot/ai-insight-dashboard/ai-insight-dashboard').then(m => m.AiInsightDashboardComponent) },
    ],
  },

  // Admin (nested layout with sidebar)
  {
    path: 'admin',
    canActivate: [adminGuard],
    loadComponent: () => import('./features/admin/admin-layout/admin-layout').then(m => m.AdminLayoutComponent),
    children: [
      { path: '',                redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard',       loadComponent: () => import('./features/admin/admin-management/admin-management').then(m => m.AdminManagementComponent) },
      { path: 'users',           loadComponent: () => import('./features/admin/admin-user-management/admin-user-management').then(m => m.AdminUserManagementComponent) },
      { path: 'store-approvals', loadComponent: () => import('./features/admin/admin-store-approvals/admin-store-approvals').then(m => m.AdminStoreApprovalsComponent) },
      { path: 'reviews',         loadComponent: () => import('./features/admin/review-management/review-management').then(m => m.ReviewManagementComponent) },
      { path: 'audit-logs',      loadComponent: () => import('./features/admin/audit-logs/audit-logs').then(m => m.AuditLogsComponent) },
      { path: 'revenue',         loadComponent: () => import('./features/admin/revenue-analytics/revenue-analytics').then(m => m.RevenueAnalyticsComponent) },
      { path: 'system',          loadComponent: () => import('./features/admin/system-configuration/system-configuration').then(m => m.SystemConfigurationComponent) },
      { path: 'ai-assistant',    loadComponent: () => import('./features/chatbot/ai-data-assistant/ai-data-assistant').then(m => m.AiDataAssistantComponent) },
      { path: 'categories',      loadComponent: () => import('./features/admin/category-management/category-management').then(m => m.CategoryManagementComponent) },
    ],
  },

  // 404
  { path: '**', loadComponent: () => import('./shared/components/not-found/not-found').then(m => m.NotFoundComponent) },
];
