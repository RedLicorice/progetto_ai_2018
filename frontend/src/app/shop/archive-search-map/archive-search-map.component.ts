import {Component, EventEmitter, Input, NgZone, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import * as L from 'leaflet';
import {Router} from '@angular/router';
import {Location} from '@angular/common';
import {ArchiveResource, PublicArchiveResource} from '../../_models/Archive';
import * as moment from 'moment';

export interface ArchiveSearchResult {
  polygon: L.Polygon;
  position_count: number;
  selected_archives: PublicArchiveResource[];
}

@Component({
  selector: 'app-archive-search-map',
  templateUrl: './archive-search-map.component.html',
  styleUrls: ['./archive-search-map.component.css']
})
export class ArchiveSearchMapComponent implements OnInit {
  @Output() positionChanged = new EventEmitter();
  @Output() areaSelected = new EventEmitter();

  displayedArchives: PublicArchiveResource[];
  map: L.Map;
  archiveLayers: Object = {}; // Dict which will contain L.LayerGroup instances (one for each archive!)
  polygonLayerGroup = new L.FeatureGroup();
  polygonCoordinates: L.LatLng[] = [];
  showControlLayer = true; // Wheter to show the control layer (for drawing polygons)
  polygonCreated = false; // Wheter a polygon has been drawn on the map

  mapOptions: any = {
    layers: [
      L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18, attribution: 'Open Street Map' }),
    ],
    zoom: 12,
    center: L.latLng(41.892824, 12.4948653)
  };

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
      featureGroups: this.polygonLayerGroup
    }
  };

  constructor(
    private ngZone: NgZone,
  ) {}

  ngOnInit() {
  }

  setCurrentPosition(): void {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition( position => {
        this.map.flyTo([position.coords.latitude, position.coords.longitude], 6);
      });
    }
    // flyTo already triggers moveend event so there's no need to do this
    // this.positionChanged.emit(this.map.getBounds());
  }

  // On map ready, save map instance and add draw layer
  onMapReady(map: L.Map): void {
    this.map = map;
    this.map.addLayer(this.polygonLayerGroup);
    // Set current location as map center (not very accurate on PC but changes things on mobile)
    this.setCurrentPosition();
  }

  onDrawCreated(event: any) {
    this.polygonCreated = true;
    // @ts-ignore
    const layer: L.Layer = event.layer;
    // @ts-ignore
    this.polygonCoordinates = layer.getLatLngs()[0]; // First element is the polygon (it's the first object in the group)
    // Add polygon layer to the map and disable editing
    this.ngZone.run(() => {
      this.polygonLayerGroup.addLayer(layer);
      this.showControlLayer = false;
    });
    // Count Markers in polygon
    const polygon = L.polygon(this.polygonCoordinates);
    const selectedData = this.getSelectedArchives(polygon);

    console.log('Geo selection result', selectedData);
    // Emit AreaSelected event on polygon drawn so parent can handle it
    this.areaSelected.emit(selectedData);
  }

  onMapMoveEnd(event: any) {
    // event.target is the map which has been moved,
    // getBounds returns the _southWest and _northEast geopoints pair
    // These will be used to request archives crossing the current viewport to the server
    // console.log('map move end', event.target.getBounds());
    this.positionChanged.emit(event.target.getBounds());
  }

  clearPolygon(): void {
    this.ngZone.run(() => {
      this.polygonLayerGroup.clearLayers();
    });

    this.showControlLayer = true;
    this.polygonCoordinates = [];
    // Clear selected area
    this.areaSelected.emit(null);
  }

  // Generate a color hashcode using a string as seed
  // We use it to assign a different color for each user's markers
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

  // Remove all of an archive's markers from the map and the archives index
  removeDisplayedArchive(archiveId: string) {
    if (archiveId in this.archiveLayers) {
      // Retrieve existing L.LayerGroup instance
      const oldGroup = this.archiveLayers[archiveId];
      // Clear markers from the group..
      oldGroup.clearLayers();
      // Remove layer from the map
      this.map.removeLayer(oldGroup);
      // Delete the archive's entry from archives' index
      delete this.archiveLayers[archiveId];
      // console.log('Removed archive', archiveId);
    } // else {
    //   console.log('New archive', archiveId);
    // }
  }

  // Display markers for an approximated archive
  displayArchive(archive: PublicArchiveResource, showPopup: boolean = true): L.LayerGroup {
    // If a layer already exists for this archive, clear it and remove it from the map
    this.removeDisplayedArchive(archive.id);
    // Instantiate a LayerGroup for this archive
    const group = new L.LayerGroup();
    // Get color for this user
    const color = this.strToColor(archive.id + archive.username);
    // Create a marker for each (approximate) position in the archive and add it to the LayerGroup
    archive.positions.forEach( p => {
      // Add a little bit of randomness to displayed coordinates (avoids full overlaps)
      // This shouldn't cause an issue since geospatial coordinates are approximated to the third decimal
      const lat = p.latitude + ( 5 * Math.random() / 100000);
      const lng = p.longitude + (5 * Math.random() / 100000);
      const marker = L.circleMarker([lat, lng], { color: color, fillColor: color});
      // Bind a popup (shown when the marker is clicked) - OPTIONAL
      if (showPopup) {
        const label = archive.id + ' (' + archive.username + ')';
        marker.bindPopup(label);
      }
      marker.addTo(group);
    });
    // Save this archive's LayerGroup in the key:value dict for later operations
    this.archiveLayers[archive.id] = group;
    // Add the group to the map
    group.addTo(this.map);
    return group;
  }

  // Clear all displayed archives
  clearArchives(): void {
    const keys = Object.keys(this.archiveLayers);
    keys.forEach( archiveId => this.removeDisplayedArchive(archiveId));
  }

  // Refresh displayed archives when input is updated
  setArchives(archives: PublicArchiveResource[]): void {
    this.clearArchives();
    archives.forEach(a => this.displayArchive(a));
    this.displayedArchives = archives;
  }

  // Count number of (displayed) position in a given polygon and get a list of archiveID's
  getSelectedArchives(polygon: L.Polygon): ArchiveSearchResult {
    const displayedArchiveIds = Object.keys(this.archiveLayers);
    let positionCount = 0;
    const selectedIds = new Set();
    displayedArchiveIds.forEach( archiveId => {
      const archiveLayer = this.archiveLayers[archiveId];
      archiveLayer.eachLayer( markerLayer => {
        // If it is a circle marker (the one we use to mark positions) and it's in the polygon bounds
        if (markerLayer instanceof L.CircleMarker && polygon.getBounds().contains(markerLayer.getLatLng())) {
          positionCount += 1;
          selectedIds.add(archiveId);
        }
      });
    });
    // Get Archives from selected Ids

    const selArchives = this.displayedArchives.filter(a => selectedIds.has(a.id));
    return {'polygon': polygon, 'position_count': positionCount, 'selected_archives': selArchives};
  }
}
