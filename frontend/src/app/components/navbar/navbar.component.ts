import {Component, OnInit, Output} from '@angular/core';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../_services/authentication.service';
import {MenuItem} from './menu-item';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  constructor(
    private router: Router,
    private auth: AuthenticationService
  ) {
  }
  menuItems: MenuItem[] = [
    {
      label: 'Home',
      icon: 'home',
      link: '/',
      login: null
    },
    {
      label: 'Registrazione',
      icon: 'person_add',
      link: '/register',
      login: false
    },
    {
      label: 'Login',
      icon: 'login',
      link: '/login',
      login: false
    },
    {
      label: 'User',
      icon: 'account_circle',
      link: '/user',
      login: true
    },
    {
      label: 'Archivi',
      icon: 'archive',
      link: '/archives',
      login: true
    },
    {
      label: 'Negozio',
      icon: 'shopping_bag',
      link: '/shop',
      login: true
    },
    {
      label: 'Ordini',
      icon: 'receipt',
      link: '/invoices',
      login: true
    },
    {
      label: 'Logout',
      icon: 'exit_to_app',
      link: '/login',
      login: true
    },
  ];

  ngOnInit(): void {
  }

  isLogin(): boolean {
    return this.auth.isLogin();
  }

  getMenuItems(): MenuItem[] {
    // tslint:disable-next-line:max-line-length
    return this.menuItems.filter((item: MenuItem) => item.login === null || item.login === false && !this.isLogin() || item.login === true && this.isLogin() );
  }

}
