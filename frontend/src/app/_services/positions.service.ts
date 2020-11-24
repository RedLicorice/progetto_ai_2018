import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import * as L from 'leaflet';

import { Position } from '../_models/Position';
import PointInPolygon from 'point-in-polygon';
import { PurchaseRequest } from '../_models/PurchaseRequest';
import { Purchase } from '../_models/Purchase';

import { POSITIONS } from './mock-positions';

@Injectable()
export class PositionsService {
  private COST_PER_POSITION = 1;

  private dateFrom: number = null;
  private dateTo: number = null;
  private polygonCoordinates: L.LatLng[] = [];

  // internal stuff, used for routing
  canPurchase = false; // Why is this hardwired?

  constructor() {}

  getDateFrom(): number {
    return this.dateFrom;
  }

  setDateFrom(dateFrom: number): void {
    this.dateFrom = dateFrom;
  }

  getDateTo(): number {
    return this.dateTo;
  }

  setDateTo(dateTo: number): void {
    this.dateTo = dateTo;
  }

  getPolygonCoordinates(): L.LatLng[] {
    return this.polygonCoordinates;
  }

  setPolygonCoordinates(polygonCoordinates: L.LatLng[]): void {
    this.polygonCoordinates = polygonCoordinates;
  }

  private computePositions(): Position[] {
    return POSITIONS.filter(p => {
      // if (this.dateFrom === null || p.timestamp < this.dateFrom) {
      //   return false;
      // }
      //
      // if (this.dateTo === null || p.timestamp > this.dateTo) {
      //   return false;
      // }

      if (this.polygonCoordinates.length > 0) {
        const coords = this.polygonCoordinates.map(c => [c.lat, c.lng]);
        // coords = coords.slice(0, coords.length - 1);
        return PointInPolygon([p.latitude, p.longitude], coords);
      }

      return false;
    });
  }

  getPositionsNumber(): Observable<PurchaseRequest> {
    const positions: Position[] = this.computePositions();
    const request: PurchaseRequest = {
      number: positions.length,
      cost: this.COST_PER_POSITION * positions.length
    };

    return of(request);
  }

  purchasePositions(): Observable<Purchase> {
    const positions: Position[] = this.computePositions();
    const purchase: Purchase = {
      number: positions.length,
      cost: this.COST_PER_POSITION * positions.length,
      positions: positions
    };

    return of(purchase);
  }
}
