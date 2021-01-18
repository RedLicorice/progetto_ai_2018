import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {FlexLayoutModule} from '@angular/flex-layout';
import { NgModule } from '@angular/core';
import { SharedModule } from './shared/shared.module';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { LeafletDrawModule } from '@asymmetrik/ngx-leaflet-draw';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { ResultsComponent } from './results/results.component';
import { PurchaseComponent } from './purchase/purchase.component';
import { UserComponent } from './user/user.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { RegisterComponent} from './register/register.component';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { ReactiveFormsModule } from '@angular/forms';
import { AuthGuard } from './_guards/auth.guard';
import { JwtInterceptor } from './_helpers/jwt.interceptor';
import { AuthenticationService } from './_services/authentication.service';
import { UserService } from './_services/user.service';

import { MatButtonModule } from '@angular/material/button';
import { MatRippleModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NavbarComponent } from './components/navbar/navbar.component';
import { MatSidenavModule } from '@angular/material/sidenav';
import {MatMenuModule} from '@angular/material/menu';
import {MatDividerModule} from '@angular/material/divider';
import {MatIconModule} from '@angular/material/icon';
import {MatGridListModule} from '@angular/material/grid-list';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatDialogModule } from '@angular/material/dialog';


import { ChartsModule } from '@carbon/charts-angular';


import { ShopComponent } from './shop/shop.component';

import { ArchiveMapComponent } from './components/archive-map/archive-map.component';
import { TimeChartComponent } from './components/time-chart/time-chart.component';
import {ArchiveService} from './_services/archive.service';
import { InvoicesComponent } from './invoices/invoices.component';
import {StoreService} from './_services/store.service';
import { PayDialogComponent } from './invoices/pay-dialog/pay-dialog.component';
import { CancelDialogComponent } from './invoices/cancel-dialog/cancel-dialog.component';
import { DetailsDialogComponent } from './invoices/details-dialog/details-dialog.component';
import { ArchivesComponent } from './archives/archives.component';
import { ArchiveDetailComponent } from './archives/archive-detail/archive-detail.component';
import { ArchiveUploadComponent } from './archives/archive-upload/archive-upload.component';
import { ArchiveDeleteComponent } from './archives/archive-delete/archive-delete.component';

@NgModule({
  declarations: [AppComponent, ResultsComponent, PurchaseComponent,
    UserComponent, LoginComponent, HomeComponent, RegisterComponent, NavbarComponent, ArchiveMapComponent,
    TimeChartComponent, ShopComponent, InvoicesComponent, PayDialogComponent, CancelDialogComponent,
    DetailsDialogComponent, ArchivesComponent, ArchiveDetailComponent, ArchiveUploadComponent, ArchiveDeleteComponent],
  imports: [
    // angular
    BrowserModule,
    BrowserAnimationsModule,
    FlexLayoutModule,
    // angular material
    SharedModule,
    // HttpClient
    HttpClientModule,
    // features
    LeafletModule.forRoot(),
    LeafletDrawModule.forRoot(),
    AppRoutingModule,
    ReactiveFormsModule,
    // Modules for Material forms
    MatMenuModule,
    MatDividerModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatRippleModule,
    MatSnackBarModule,
    MatSidenavModule,
    MatGridListModule,
    MatTableModule,
    MatDialogModule,
    MatPaginatorModule,
    // Charts
    ChartsModule
  ],
  providers: [
    AuthGuard,
    AuthenticationService,
    UserService,
    ArchiveService,
    StoreService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
