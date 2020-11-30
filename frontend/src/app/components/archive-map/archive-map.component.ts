import {Component, NgZone, OnInit, Output} from '@angular/core';
import * as L from 'leaflet';
import 'leaflet.heat/dist/leaflet-heat.js';
import {Router} from '@angular/router';
import {Location} from '@angular/common';
import * as moment from 'moment';
import {EventEmitter} from '@angular/core';
import {ArchiveResource, PublicArchiveResource} from '../../_models/Archive';


@Component({
  selector: 'app-archive-map',
  templateUrl: './archive-map.component.html',
  styleUrls: ['./archive-map.component.css']
})
export class ArchiveMapComponent implements OnInit {
  // Bubble up moveend event (also on map loading) so archives can be set by parent
  @Output() positionChanged = new EventEmitter();
  @Output() areaSelected = new EventEmitter();

  map: L.Map;
  markers: L.LayerGroup = new L.LayerGroup();
  polygonCreated = false;
  mapOptions: any = {
    layers: [
      L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18, attribution: 'Open Street Map' }),
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
    private router: Router,
    private location: Location) {}

  ngOnInit() {
  }

  onMapReady(map: L.Map): void {
    this.map = map;
    this.map.addLayer(this.featureGroups);
    this.markers.addTo(map);
    // Emit PositionChanged event on map load, so parent can load archives
    this.positionChanged.emit(map.getBounds());
    const that = this;
    // save the just drawn polygon coordinates
    // N.B: can't seem to figure out how the library exposes
    // the layers/coords stuff, so I need to access the
    // leaflet native ones, which are not exposed in the library interfaces
    this.map.on(L.Draw.Event.CREATED, e => {
      this.polygonCreated = true;
      // @ts-ignore
      const layer: L.Layer = e.layer;
      // @ts-ignore
      this.polygonCoordinates = layer.getLatLngs()[0];
      // Emit AreaSelected event on polygon drawn so parent can filter archives
      that.areaSelected.emit(this.polygonCoordinates);
      this.ngZone.run(() => {
        this.featureGroups.addLayer(layer);
        this.showControlLayer = false;
      });
    });
    // Request archives in new area when map is repositioned
    this.map.on('moveend', function(event) {
      // event.target is the map which has been moved,
      // getBounds returns the _southWest and _northEast geopoints pair
      // These will be used to request archives crossing the current viewport to the server
      console.log('map move end', event.target.getBounds());
      that.positionChanged.emit(event.target.getBounds());
    });
  }

  clearGeospatial(): void {
    this.ngZone.run(() => {
      this.featureGroups.clearLayers();
    });

    this.showControlLayer = true;
    this.polygonCoordinates = [];
  }

  goBack(): void {
    this.location.back();
  }

  hsvToRgb(h, s, v) {
    let r, g, b;
    let i;
    let f, p, q, t;

    // Make sure our arguments stay in-range
    h = Math.max(0, Math.min(360, h));
    s = Math.max(0, Math.min(100, s));
    v = Math.max(0, Math.min(100, v));

    // We accept saturation and value arguments from 0 to 100 because that's
    // how Photoshop represents those values. Internally, however, the
    // saturation and value are calculated from a range of 0 to 1. We make
    // That conversion here.
    s /= 100;
    v /= 100;

    if (s === 0) {
      // Achromatic (grey)
      r = g = b = v;
      // return [Math.round(r * 255), Math.round(g * 255), Math.round(b * 255)];
      return '#' + Math.round(r * 255).toString(16) + Math.round(g * 255).toString(16) + Math.round(b * 255).toString(16);
    }

    h /= 60; // sector 0 to 5
    i = Math.floor(h);
    f = h - i; // factorial part of h
    p = v * (1 - s);
    q = v * (1 - s * f);
    t = v * (1 - s * (1 - f));

    switch (i) {
      case 0:
        r = v;
        g = t;
        b = p;
        break;

      case 1:
        r = q;
        g = v;
        b = p;
        break;

      case 2:
        r = p;
        g = v;
        b = t;
        break;

      case 3:
        r = p;
        g = q;
        b = v;
        break;

      case 4:
        r = t;
        g = p;
        b = v;
        break;

      default: // case 5:
        r = v;
        g = p;
        b = q;
    }

    // return [Math.round(r * 255), Math.round(g * 255), Math.round(b * 255)];
    return '#' + Math.round(r * 255).toString(16) + Math.round(g * 255).toString(16) + Math.round(b * 255).toString(16);
  }

  // Use HSL to get a different color for each user
  getColors(count): string[] {
    const i = 360 / ( count - 1); // Evenly distribute colors among the hue range
    const result: string[] = [];
    for (let x = 0; x < count; x++) {
      result.push(this.hsvToRgb(i * x, 100, 100));
    }
    return result;
  }

  addMarker(lat, lng, color) {
    // ToDo: How to set the marker color?
    const marker = L.circleMarker([lat, lng], { color: color, fillColor: color});
    // const marker = L.marker([lat, lng], {
    //   icon: L.icon({
    //     iconSize: [25, 41],
    //     iconAnchor: [13, 41],
    //     iconUrl: 'assets/marker-icon.png',
    //     shadowUrl: 'assets/marker-shadow.png'
    //   })
    // });

    marker.addTo(this.markers);
  }

  clearMarkers() {
    this.markers.clearLayers();
    console.log('markers cleared');
  }

  setPublicArchives(archives: PublicArchiveResource[]): void {
    // Assign an unique color to each user, by index
    const users: string[] = archives.map(a => a.username); // Get a list of users
    const colors = this.getColors(users.length);
    const that = this;
    archives.forEach( a => {
      a.positions.forEach( p => {
        console.log('Add marker: ', p);
        that.addMarker(p.latitude, p.longitude, colors[users.indexOf(a.username)]);
      });
    });
  }

  setArchives(archives: ArchiveResource[]): void {
    // Assign an unique color to each user, by index
    const users: string[] = archives.map(a => a.username); // Get a list of users
    const colors = this.getColors(users.length);
    const that = this;
    archives.forEach( a => {
      a.measures.forEach( m => that.addMarker(m.latitude, m.longitude, colors[users.indexOf(a.username)]));
    });
  }
}
