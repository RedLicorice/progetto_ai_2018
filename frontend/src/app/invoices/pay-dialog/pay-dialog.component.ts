import { Component, OnInit, Inject } from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {Invoice} from '../../_models/Invoice';

@Component({
  selector: 'app-pay-dialog',
  templateUrl: './pay-dialog.component.html',
  styleUrls: ['./pay-dialog.component.css']
})
export class PayDialogComponent implements OnInit {

  constructor(@Inject(MAT_DIALOG_DATA) public data: Invoice) { }

  ngOnInit(): void {
  }

}
