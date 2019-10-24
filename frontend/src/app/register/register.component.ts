import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import {FormBuilder, FormControl, FormGroup, FormGroupDirective, NgForm, Validators} from '@angular/forms';
import {MatSnackBar} from '@angular/material';
import { Location } from '@angular/common';
import { first } from 'rxjs/operators';

import { AuthenticationService } from '../_services/authentication.service';
import {JWT} from '../_models/JWT';
import {User} from '../_models/User';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  loading = false;
  submitted = false;
  returnUrl: string;
  error = '';

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authenticationService: AuthenticationService,
    private location: Location,
    private snackBar: MatSnackBar) {}

  ngOnInit() {
    this.registerForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  openSnackBar(message: string) {
    return this.snackBar.open(message, 'Chiudi', {
      duration: 2000,
    });
  }

  // convenience getter for easy access to form fields
  get f() { return this.registerForm.controls; }

  onSubmit() {
    this.submitted = true;
    // stop here if form is invalid
    if (this.registerForm.invalid) {
      this.openSnackBar('Form is invalid.');
      return;
    }
    this.loading = true;
    this.authenticationService.getRegisterToken()
      .pipe(first())
      .subscribe(
        data => {
          const token = localStorage.getItem('registerToken');
          this.authenticationService.register(token, this.f.username.value, this.f.password.value)
            .pipe(first())
            .subscribe(
              data1 => {
                this.openSnackBar('Registrazione effettuata!').afterDismissed().subscribe(
                  () => {
                    // Snackbar dismissed
                    this.router.navigate(['/login']);
                  }
                );
              },
              error => {
                this.error = error;
                this.loading = false;
                this.openSnackBar(error.toString());
              }
            );
        },
        error => {
          this.error = error;
          this.loading = false;
          this.openSnackBar(error.toString());
        }
      );
  }

  goBack(): void {
    this.location.back();
  }
}
