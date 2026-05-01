import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserRole } from '../../shared/models/user.model';

export const corporateGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const role = auth.getRole();
  if (auth.isLoggedIn() && (role === UserRole.CORPORATE || role === UserRole.ADMIN)) return true;

  return router.createUrlTree(['/home']);
};
