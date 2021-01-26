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
import {MatSnackBar} from '@angular/material/snack-bar';
import {ActivatedRoute} from "@angular/router";

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
    public dialog: MatDialog,
    private _snackBar: MatSnackBar,
    private activatedRoute: ActivatedRoute
  ) { }



  ngOnInit(): void {
    this.reload();
    this.activatedRoute.paramMap.subscribe(params => {
      const currentId = params.get('id');
      if ( currentId ) {
        console.log('Invoice parameters', params, currentId);
        this.storeService.getInvoice(currentId).subscribe(
          invoice => this.detailsInvoice(invoice),
          err => console.log('Error getting invoice', err)
        );
      }
    });
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  goBack(): void {
    this.location.back();
  }

  reload(): void {
    const invoices = this.storeService.getInvoices();
    invoices.subscribe( items => {
        console.log('Invoices retrieved', items);
        this.dataSource.data = items;
      },
      err => {
        console.log('Error retrieving invoices', err);
      });
  }

  detailsInvoice(invoice: Invoice): void {
    const dialogRef = this.dialog.open(DetailsDialogComponent, {
      data: invoice
    });
    dialogRef.afterClosed().subscribe(res => {
      if (res) {
        this.payInvoice(invoice);
      }
    });
  }

  payInvoice(invoice: Invoice): void {
    const dialogRef = this.dialog.open(PayDialogComponent, {
      data: invoice
    });
    dialogRef.afterClosed().subscribe( conf => {
      if (conf) {
        const resp = this.storeService.payInvoice(conf);
        resp.subscribe(res => {
          console.log('Invoice paid', res);
          this._snackBar.open('Invoice pagato!', 'Chiudi', {duration: 800});
          this.reload();
        },
        err => {
          this._snackBar.open('Errore durante il pagamento dell\'invoice: controlla di avere abbastanza Token!', 'Chiudi', {duration: 800});
        });
      }
    });
  }

  cancelInvoice(invoice: Invoice): void {
    const dialogRef = this.dialog.open(CancelDialogComponent, {
      data: invoice
    });
    dialogRef.afterClosed().subscribe( res => {
      if (res) {
        const resp = this.storeService.cancelInvoice(invoice.id);
        resp.subscribe(req => {
            this._snackBar.open('Invoice annullato!', 'Chiudi', {duration: 800});
            this.reload();
          },
          err => {
            this._snackBar.open('Errore durante l\'annullamento dell\'invoice!', 'Chiudi', {duration: 800});
          });
      }
    });
  }

  timestampToDate(ts): string {
    return moment.unix(ts).format('DD/MM/YYYY HH:mm');
  }
}
