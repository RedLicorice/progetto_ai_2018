import {Component, EventEmitter, Input, NgZone, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import * as L from 'leaflet';
import {Router} from '@angular/router';
import {Location} from '@angular/common';
import {ArchiveResource} from '../../_models/Archive';
import * as moment from 'moment';

@Component({
  selector: 'app-archive-display-map',
  templateUrl: './archive-display-map.component.html',
  styleUrls: ['./archive-display-map.component.css']
})
export class ArchiveDisplayMapComponent implements OnInit, OnChanges {
  @Input() archive: ArchiveResource;
  map: L.Map;
  markers: L.LayerGroup;

  mapOptions: any = {
    layers: [
      L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18, attribution: 'Open Street Map' }),
    ],
    zoom: 12,
    center: L.latLng(41.892824, 12.4948653)
  };

  constructor(
    private ngZone: NgZone
  ) {}

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    this.clearArchive();
    if ( changes.archive.currentValue ) {
      this.markers = this.setArchive(changes.archive.currentValue);
    }
  }

  onMapReady(map: L.Map): void {
    this.map = map;
  }

  strToColor(str): string {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
      // tslint:disable-next-line:no-bitwise
      hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    let colour = '#';
    for (let i = 0; i < 3; i++) {
      // tslint:disable-next-line:no-bitwise
      const value = (hash >> (i * 8)) & 0xFF;
      colour += ('00' + value.toString(16)).substr(-2);
    }
    return colour;
  }

  // Display a single archive
  // - Use a Polyline to display the path
  // - Add markers to show waypoints, with hover tooltip
  setArchive(archive: ArchiveResource): L.LayerGroup {
    // Determine color for archive
    const color = this.strToColor(archive.id + archive.username);
    // Create a LayerGroup containing all data relative to this archive
    const archiveLayer = new L.LayerGroup();
    // Build a polyline showing the archive path
    const polylinePoints = archive.measures.map(m => L.latLng(m.latitude, m.longitude));
    const polyline = L.polyline(polylinePoints, {'color': color}).addTo(archiveLayer);
    // At each waypoint (measure), add a marker with a tooltip showing the time
    // the measure was taken
    archive.measures.forEach(m => {
      const label = archive.username + ' (' + moment.unix(m.timestamp).format('DD/MM/YYYY HH:mm') + ')';
      const marker = L.circleMarker( L.latLng(m.latitude, m.longitude), {'color': color, 'fillColor': color}).addTo(archiveLayer);
      marker.bindPopup(label);
    });
    archiveLayer.addTo(this.map);
    // Fit the map viewport to the polyline bounds
    // Not very accurate but does the job
    this.map.fitBounds(polyline.getBounds());
    return archiveLayer;
  }

  clearArchive(): void {
    if ( this.markers ) {
      this.markers.clearLayers();
      this.map.removeLayer(this.markers);
    }
  }
}
