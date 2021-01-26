import { Component, OnInit, NgZone } from '@angular/core';
import { Router } from '@angular/router';

import { UserService } from '../_services/user.service';
import { User } from '../_models/User';
import { Location } from '@angular/common';
import {Observable} from 'rxjs';
import {MatDialog} from '@angular/material/dialog';
import {TopupDialogComponent} from './topup-dialog/topup-dialog.component';
import {MatSnackBar} from "@angular/material/snack-bar";
import {ChangePasswordDialogComponent} from "./change-password-dialog/change-password-dialog.component";

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
    private dialog: MatDialog,
    private _snackBar: MatSnackBar
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
  }

  goBack(): void {
    this.location.back();
  }

  onTopup(): void {
    const dialogRef = this.dialog.open(TopupDialogComponent);
    dialogRef.afterClosed()
      .subscribe(res => {
        if (res && res >= 100 && res <= 1000) {
          console.log('Topup confirm', res);
          return this.userService.topup(res).subscribe(acc => {
              console.log('Topup result', acc);
              this._snackBar.open('Ricarica effettuata!', 'Chiudi', {duration: 800});
              this.user.wallet = acc.wallet;
            },
            err => this._snackBar.open('Ricarica Fallita!', 'Chiudi', {duration: 800}));
        }
      });
  }

  onChangePassword(): void {
    const dialogRef = this.dialog.open(ChangePasswordDialogComponent);
    dialogRef.afterClosed()
      .subscribe(res => {
        if (res) {
          console.log('Change Password', res);
          return this.userService.changePassword(res).subscribe(acc => {
              console.log('Change result', acc);
              this._snackBar.open('Password modificata!', 'Chiudi', {duration: 800})
                .afterDismissed().subscribe(
                  x => this.router.navigateByUrl('/login')
                );
            },
            err => this._snackBar.open('Password non modificata!', 'Chiudi', {duration: 800}));
        }
      });
  }
}
