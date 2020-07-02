import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Invoice } from '../_models/Invoice';
import {environment} from '../../environments/environment';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {map} from 'rxjs/operators';

@Injectable()
export class StoreService {

  constructor(private http: HttpClient) {}

  getInvoices(): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(environment.store_invoices_url);
  }
  viewInvoice(invoiceId: string): Observable<Invoice> {
    return this.http.get<Invoice>(environment.store_invoice_detail_url);
  }
  payInvoice(invoiceId: string): Observable<Invoice> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-type': 'application/json; charset=utf-8',
      })
    };
    return this.http.post<Invoice>(
      environment.store_invoice_pay_url.replace('{id}', invoiceId),
      null,
      httpOptions
    );
  }
}
