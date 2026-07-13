import { Routes } from '@angular/router';
import { authGuard, loginGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent),
    canActivate: [loginGuard]
  },
  {
    path: '',
    loadComponent: () => import('./pages/layout/layout.component').then(m => m.LayoutComponent),
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'inventario', loadComponent: () => import('./pages/inventario/inventario.component').then(m => m.InventarioComponent) },
      { path: 'ventas', loadComponent: () => import('./pages/ventas/ventas.component').then(m => m.VentasComponent) },
      { path: 'proveedores', loadComponent: () => import('./pages/proveedores/proveedores.component').then(m => m.ProveedoresComponent) },
      { path: 'reportes', loadComponent: () => import('./pages/reportes/reportes.component').then(m => m.ReportesComponent) }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
