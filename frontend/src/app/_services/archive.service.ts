import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import * as L from 'leaflet';

import PointInPolygon from 'point-in-polygon';
import { PurchaseRequest } from '../_models/PurchaseRequest';
import { Purchase } from '../_models/Purchase';
import { ArchiveResource, PublicArchiveResource, ArchiveSummary, ArchiveOwnerSummary } from '../_models/Archive';
import { Measure } from '../_models/Measure';
import { Position } from '../_models/Position';
import { Invoice } from '../_models/Invoice';
import {environment} from '../../environments/environment';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {map} from 'rxjs/operators';

@Injectable()
export class ArchiveService {

  constructor(private http: HttpClient) {}

  getArchives(): Observable<ArchiveSummary[]> {
    return this.http.get<ArchiveSummary[]>(environment.archives_url);
  }

  getUploadedArchives(): Observable<ArchiveOwnerSummary[]> {
    return this.http.get<ArchiveOwnerSummary[]>(environment.archives_uploaded_url);
  }

  uploadArchive(measures: Measure[]): Observable<ArchiveResource> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-type': 'application/json; charset=utf-8',
      })
    };
    return this.http.post<ArchiveResource>(environment.archives_upload_url, JSON.stringify(measures), httpOptions);
  }

  downloadArchive(archiveId: string): Observable<ArchiveResource> {
    return this.http.get<ArchiveResource>(environment.archives_download_url.replace('{id}', archiveId));
  }

  downloadPublicArchive(archiveId: string): Observable<PublicArchiveResource> {
    return this.http.get<PublicArchiveResource>(environment.archives_public_url.replace('{id}', archiveId));
  }

  searchArchives(topLeft: Position, bottomRight: Position, from: number, to: number, users: string[]): Observable<PublicArchiveResource[]> {
    const requestBody = {
      'topLeft': topLeft,
      'bottomRight' : bottomRight,
      'from': from,
      'to': to,
      'users': users
    };
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-type': 'application/json; charset=utf-8',
      })
    };
    return this.http.post<PublicArchiveResource[]>(
      environment.archives_search_url,
      JSON.stringify(requestBody),
      httpOptions
    );
  }

  deleteArchive(archiveId: string): Observable<ArchiveOwnerSummary> {
    return this.http.delete<ArchiveOwnerSummary>(environment.archives_delete_url.replace('{id}', archiveId));
  }

  buyArchives(archiveIds: string[]): Observable<Invoice[]> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-type': 'application/json; charset=utf-8',
      })
    };
    return this.http.post<Invoice[]>(
      environment.archives_search_url,
      archiveIds.toString(),
      httpOptions
    );
  }
}
