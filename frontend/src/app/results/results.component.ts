import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import * as moment from 'moment';

import { PositionsService } from '../_services/positions.service';
import { PurchaseRequest } from '../_models/PurchaseRequest';

@Component({
  selector: 'app-results',
  templateUrl: './results.component.html',
  styleUrls: ['./results.component.css'],
  providers: [PositionsService]
})
export class ResultsComponent implements OnInit {
  purchaseRequest: PurchaseRequest;

  constructor(private positionsService: PositionsService, private location: Location, private router: Router) {}

  ngOnInit() {
    if (!this.positionsService.canPurchase) {
      console.error("PositionService denied purchase!");
      this.router.navigate(['']);
    } else {
      this.getPositionsNumber();
    }
  }

  getPositionsNumber(): void {
    this.positionsService.getPositionsNumber().subscribe(purchaseRequest => {
      this.purchaseRequest = purchaseRequest;
    });
  }

  purchase(): void {
    this.router.navigate(['purchase']);
  }

  goBack(): void {
    this.positionsService.canPurchase = false;
    this.location.back();
  }
}
