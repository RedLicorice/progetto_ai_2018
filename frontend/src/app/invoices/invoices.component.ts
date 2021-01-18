import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {Location} from '@angular/common';
import {StoreService} from '../_services/store.service';
import {MatDialog} from '@angular/material/dialog';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';
import {Invoice} from '../_models/Invoice';
import { PayDialogComponent } from './pay-dialog/pay-dialog.component';
import { CancelDialogComponent } from './cancel-dialog/cancel-dialog.component';
import { DetailsDialogComponent } from './details-dialog/details-dialog.component';
import * as moment from 'moment';

const MOCK_DATA: Invoice[] = [
  {'id': 'mockinvoice1', 'username': 'Test', 'amount': 10, 'itemId': 'mockitem1', 'isPaid': true, 'createdAt': 1610903485},
  {'id': 'mockinvoice2', 'username': 'Test', 'amount': 5, 'itemId': 'mockitem2', 'isPaid': false, 'createdAt': 1610903485},
  {'id': 'mockinvoice3', 'username': 'Test', 'amount': 8, 'itemId': 'mockitem3', 'isPaid': true, 'createdAt': 1610903485},
  {'id': 'mockinvoice4', 'username': 'Test', 'amount': 42, 'itemId': 'mockitem4', 'isPaid': false, 'createdAt': 1610903485},
  {'id': 'mockinvoice5', 'username': 'Test', 'amount': 420, 'itemId': 'mockitem5', 'isPaid': true, 'createdAt': 1610903485},
  {'id': 'mockinvoice6', 'username': 'Test', 'amount': 69, 'itemId': 'mockitem6', 'isPaid': false, 'createdAt': 1610903485}
];

@Component({
  selector: 'app-invoices',
  templateUrl: './invoices.component.html',
  styleUrls: ['./invoices.component.css']
})
export class InvoicesComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['id', 'itemId', 'createdAt', 'amount', 'isPaid', 'actions'];
  dataSource = new MatTableDataSource();

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private location: Location,
    private storeService: StoreService,
    public dialog: MatDialog
  ) { }



  ngOnInit(): void {
    const invoices = this.storeService.getInvoices();
    invoices.subscribe( items => {
      console.log('Invoices retrieved', items);
      this.dataSource.data = items;
    },
    err => {
      console.log('Error retrieving invoices', err);
    });
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  goBack(): void {
    this.location.back();
  }

  detailsInvoice(invoice: Invoice): void {
    this.dialog.open(DetailsDialogComponent, {
      data: invoice
    });
  }

  payInvoice(invoice: Invoice): void {
    const dialogRef = this.dialog.open(PayDialogComponent, {
      data: invoice
    });
    dialogRef.afterClosed().subscribe( res => {
      if (res) {
        this.storeService.payInvoice(invoice.id);
      }
    });
  }

  cancelInvoice(invoice: Invoice): void {
    const dialogRef = this.dialog.open(CancelDialogComponent, {
      data: invoice
    });
    dialogRef.afterClosed().subscribe( res => {
      if (res) {
        this.storeService.cancelInvoice(invoice.id);
      }
    });
  }

  timestampToDate(ts): string {
    return moment.unix(ts).format('DD/MM/YYYY HH:mm');
  }
}
