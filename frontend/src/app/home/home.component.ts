import { Component, OnInit, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import {Observable} from 'rxjs';
import { AuthenticationService } from '../_services/authentication.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  constructor(
    private router: Router,
    private auth: AuthenticationService
  ) {}

  isLogin(): boolean {
    return this.auth.isLogin();
  }

  goFilter(): void {
    this.router.navigate(['/filter']);
  }
  goLogin(): void {
    this.router.navigate(['/login']);
  }
  goUser(): void {
    this.router.navigate(['/user']);
  }
  goRegister(): void {
    this.router.navigate(['/register']);
  }
}
