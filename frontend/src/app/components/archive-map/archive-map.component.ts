import {Component, NgZone, OnInit, Output} from '@angular/core';
import * as L from 'leaflet';
import 'leaflet.heat/dist/leaflet-heat.js';
import {Router} from '@angular/router';
import {Location} from '@angular/common';
import * as moment from 'moment';
import {EventEmitter} from '@angular/core';
import {ArchiveResource, PublicArchiveResource} from '../../_models/Archive';
import {Measure} from '../../_models/Measure';


@Component({
  selector: 'app-archive-map',
  templateUrl: './archive-map.component.html',
  styleUrls: ['./archive-map.component.css']
})
export class ArchiveMapComponent implements OnInit {
  // Bubble up moveend event (also on map loading) so archives can be set by parent
  @Output() positionChanged = new EventEmitter();
  @Output() areaSelected = new EventEmitter();
  @Output() mapReady = new EventEmitter();

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
    this.mapReady.emit();
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

  addMarker(lat, lng, color) {
    const marker = L.circleMarker([lat, lng], { color: color, fillColor: color});
    marker.addTo(this.markers);
  }

  clearMarkers() {
    this.markers.clearLayers();
    console.log('Markers cleared');
  }

  setPublicArchives(archives: PublicArchiveResource[]): void {
    // Assign an unique color to each user, by index
    const users: string[] = archives.map(a => a.username); // Get a list of users
    // const colors = this.getColors(users.length);
    const colors = users.map( u => this.strToColor(u));

    archives.forEach( a => {
      a.positions.forEach( p => {
        console.log('Add marker: ', p);
        this.addMarker(p.latitude, p.longitude, colors[users.indexOf(a.username)]);
      });
    });
  }

  // Reggio Calabria - Lat: 38.110X Lng: 15.661X
  setArchives(archives: ArchiveResource[]): L.LayerGroup[] {
    return archives.map( a => this.setArchive(a));
  }

  // Display a single archive
  // - Use a Polyline to display the path
  // - Add markers to show waypoints, with hover tooltip
  // ToDo: use a different color for each user (should be easy since we hash the username to get a color)
  // ToDo: hold an index of shown LayerGroups in order to be able to selectively show/hide each archive
  setArchive(archive: ArchiveResource): L.LayerGroup {
    // Create a LayerGroup containing all data relative to this archive
    const archiveLayer = new L.LayerGroup();
    // Build a polyline showing the archive path
    const polylinePoints = archive.measures.map(m => L.latLng(m.latitude, m.longitude));
    const polyline = L.polyline(polylinePoints).addTo(archiveLayer);
    // At each waypoint (measure), add a marker with a tooltip showing the time
    // the measure was taken
    archive.measures.forEach(m => {
      const label = archive.username + ' (' + moment.unix(m.timestamp).format('DD/MM/YYYY HH:mm') + ')';
      const marker = L.circleMarker( L.latLng(m.latitude, m.longitude)).addTo(archiveLayer);
      marker.bindPopup(label);
    });
    archiveLayer.addTo(this.map);
    // Pan the map to the path's centroid and set an adequate zoom level
    // We could use flyTo to do so in a single operation, but that also
    // includes an animation which gets cringey
    this.map.panTo(polyline.getBounds().getCenter());
    this.map.setZoom(16);
    // Disable control layer (don't allow drawing on the map, since we're just showing an archive)
    this.showControlLayer = false;
    return archiveLayer;
  }
}
