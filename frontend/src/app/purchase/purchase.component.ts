import { Component, OnInit, NgZone } from '@angular/core';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import * as L from 'leaflet';

import { PositionsService } from '../_services/positions.service';
import { Purchase } from '../_models/Purchase';

@Component({
  selector: 'app-purchase',
  templateUrl: './purchase.component.html',
  styleUrls: ['./purchase.component.css']
})
export class PurchaseComponent implements OnInit {
  purchase: Purchase;

  // map stuff
  mapOptions: any = {
    layers: [
      L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18, attribution: 'Open Street Map' })
    ],
    zoom: 12,
    center: L.latLng(41.892824, 12.4948653)
  };

  markers: L.Layer[] = [];

  constructor(
    private positionsService: PositionsService,
    private location: Location,
    private ngZone: NgZone,
    private router: Router
  ) {}

  ngOnInit() {
    if (!this.positionsService.canPurchase) {
      this.router.navigate(['']);
    } else {
      this.getPositions();
    }
  }

  getPositions(): void {
    this.positionsService.purchasePositions().subscribe(purchase => {
      this.purchase = purchase;

      this.ngZone.run(() => {
        this.purchase.positions.forEach(p => {
          const marker = L.marker([p.latitude, p.longitude], {
            icon: L.icon({
              iconSize: [25, 41],
              iconAnchor: [13, 41],
              iconUrl: 'assets/marker-icon.png',
              shadowUrl: 'assets/marker-shadow.png'
            })
          });

          this.markers.push(marker);
        });
      });
    });
  }

  goBack(): void {
    this.location.back();
  }
}
