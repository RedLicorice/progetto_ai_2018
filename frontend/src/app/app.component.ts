import {Component, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {AuthenticationService} from './_services/authentication.service';
import * as moment from 'moment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  constructor(
    private router: Router,
    private auth: AuthenticationService
  ) {}

  isLogin(): boolean {
    return this.auth.isLogin();
  }

  ngOnInit() {
    // const expiry = this.auth.getLoginExpiry();
    // if ( expiry ) {
    //   const timeout = (expiry.unix() - moment().unix());
    //   setTimeout(() => {
    //     this.auth.refresh();
    //   }, (timeout - 60) * 1000);
    // }
    this.auth.checkLogin();
    this.auth.setExpiryTimeout();
  }
}
