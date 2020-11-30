import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { FiltersComponent } from './filters/filters.component';
import { ResultsComponent } from './results/results.component';
import { PurchaseComponent } from './purchase/purchase.component';
import { UserComponent } from './user/user.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { AuthGuard } from './_guards/auth.guard';
import {RegisterComponent} from './register/register.component';
import {ShopComponent} from './shop/shop.component';

const routes: Routes = [
  {
    path: 'user',
    component: UserComponent,
    canActivate: [AuthGuard] // To enforce authentication
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'register',
    component: RegisterComponent,
  },
  {
    path: '',
    component: HomeComponent,
  },
  {
    path: 'filter',
    component: FiltersComponent,
  },
  {
    path: 'results',
    component: ResultsComponent
  },
  {
    path: 'purchase',
    component: PurchaseComponent
  },
  {
    path: 'shop',
    component: ShopComponent
  }
];

@NgModule({
  exports: [RouterModule],
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })]
})
export class AppRoutingModule {}
