import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {Invoice} from '../../_models/Invoice';
import * as moment from 'moment';

@Component({
  selector: 'app-details-dialog',
  templateUrl: './details-dialog.component.html',
  styleUrls: ['./details-dialog.component.css']
})
export class DetailsDialogComponent implements OnInit {

  constructor(@Inject(MAT_DIALOG_DATA) public data: Invoice) { }

  ngOnInit(): void {
  }

  timestampToDate(ts): string {
    return moment.unix(ts).format('DD/MM/YYYY HH:mm');
  }

}
