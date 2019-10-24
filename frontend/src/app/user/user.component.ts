import { Component, OnInit, NgZone } from '@angular/core';
import { Router } from '@angular/router';

import { UserService } from '../_services/user.service';
import { User } from '../_models/User';
import { Location } from '@angular/common';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  user: User;
  positions: Position[];

  constructor(
    private userService: UserService,
    private router: Router,
    private location: Location,
  ) {
    this.user = new User;
  }

  ngOnInit() {
    this.userService.me().subscribe(
      data => {
        this.user = data;
      },
      error => {
        console.log(error);
      }
    );
    this.userService.positions().subscribe(
      data => {
        this.positions = data;
      },
      error => {
        console.log(error);
      }
    );
  }

  goBack(): void {
    this.location.back();
  }
}
