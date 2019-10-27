import { Component, OnInit, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import * as L from 'leaflet';
import * as moment from 'moment';
import { Location } from '@angular/common';
import { PositionsService } from '../_services/positions.service';

@Component({
  selector: 'app-filters',
  templateUrl: './filters.component.html',
  styleUrls: ['./filters.component.css'],
  providers: [PositionsService]
})
export class FiltersComponent implements OnInit {
  // slider stuff
  sliderMin: number;
  sliderMax: number;

  dateFrom: number;
  dateTo: number;

  // map stuff
  map: L.Map;
  polygonCreated: boolean = false;
  mapOptions: any = {
    layers: [
      L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18, attribution: 'Open Street Map' })
    ],
    zoom: 12,
    center: L.latLng(41.892824, 12.4948653)
  };

  featureGroups = new L.FeatureGroup();
  polygonCoordinates: L.LatLng[] = [];
  showControlLayer = true;

  mapDrawOptions: any = {
    position: 'bottomleft',
    draw: {
      polygon: true,
      polyline: false,
      rectangle: false,
      circle: false,
      marker: false,
      circlemarker: false
    },
    edit: {
      polygon: false,
      featureGroups: this.featureGroups
    }
  };

  constructor(
    private ngZone: NgZone,
    private positionsService: PositionsService,
    private router: Router,
    private location: Location) {}

  ngOnInit() {
    this.fetchDates();
  }

  fetchDates(): void {
    this.sliderMin = 0;
    this.sliderMax = +moment().format('X');
  }

  clearTemporal(): void {
    this.dateFrom = null;
    this.dateTo = null;
  }

  onMapReady(map: L.Map): void {
    this.map = map;
    this.map.addLayer(this.featureGroups);

    // save the just drawn polygon coordinates
    // N.B: can't seem to figure out how the library exposes
    // the layers/coords stuff, so I need to access the
    // leaflet native ones, which are not exposed in the library interfaces
    this.map.on(L.Draw.Event.CREATED, e => {
      this.polygonCreated=true;
      // @ts-ignore
      const layer: L.Layer = e.layer;
      // @ts-ignore
      this.polygonCoordinates = layer.getLatLngs()[0];

      this.ngZone.run(() => {
        this.featureGroups.addLayer(layer);
        this.showControlLayer = false;
      });
    });
  }

  clearGeospatial(): void {
    this.ngZone.run(() => {
      this.featureGroups.clearLayers();
    });

    this.showControlLayer = true;
    this.polygonCoordinates = [];
  }

  searchPositions(): void {
    this.positionsService.setDateFrom(this.dateFrom);
    this.positionsService.setDateTo(this.dateTo);
    this.positionsService.setPolygonCoordinates(this.polygonCoordinates);

    this.positionsService.canPurchase = true;
    this.router.navigate(['results']);
  }

  searchButtonDisabled(): boolean {
    if (!this.dateFrom || !this.dateTo || !(this.dateFrom < this.dateTo) || !this.polygonCreated  ) {
      return true;
    }

    return false;
  }

  goBack(): void {
    this.location.back();
  }
}
