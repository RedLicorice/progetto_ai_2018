import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-topup-dialog',
  templateUrl: './topup-dialog.component.html',
  styleUrls: ['./topup-dialog.component.css']
})
export class TopupDialogComponent implements OnInit {
  amount = 100;
  exchangeRate = 0.1;

  constructor() { }

  ngOnInit(): void {
  }

}
