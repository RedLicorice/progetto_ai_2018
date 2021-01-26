import {Component, NgZone, OnInit} from '@angular/core';
import {Location} from "@angular/common";
import {ArchiveService} from "../_services/archive.service";
import {MatDialog} from "@angular/material/dialog";
import {MatSnackBar} from "@angular/material/snack-bar";
import * as L from "leaflet";
import {MatTableDataSource} from "@angular/material/table";
import * as moment from "moment";
import {Measure} from "../_models/Measure";

@Component({
  selector: 'app-archive-editor',
  templateUrl: './archive-editor.component.html',
  styleUrls: ['./archive-editor.component.css']
})
export class ArchiveEditorComponent implements OnInit {
  displayedColumns: string[] = ['latitude', 'longitude', 'timestamp'];
  dataSource = new MatTableDataSource();
  map: L.Map;
  polylineCreated = false; // Wheter a polygon has been drawn on the map
  polylineLayerGroup = new L.FeatureGroup();
  dateStart: string;
  dateMax: string;
  tsStart: number;
  speedAvg = 4; // Movement speed in km/h
  archiveData: Measure[] = [];

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
      polygon: false,
      polyline: true,
      rectangle: false,
      circle: false,
      marker: false,
      circlemarker: false
    },
    edit: {
      polygon: false,
      featureGroups: this.polylineLayerGroup
    }
  };

  constructor(
    private location: Location,
    private archiveService: ArchiveService,
    private snackBar: MatSnackBar,
    private ngZone: NgZone
  ) { }

  ngOnInit(): void {
    const today = moment();
    const ten_days_ago = moment().subtract(10, 'days');
    this.dateStart = ten_days_ago.format('YYYY-MM-DD[T]hh:mm');
    this.tsStart = ten_days_ago.unix();
    this.dateMax = today.format('YYYY-MM-DD[T]hh:mm');
    this.dataSource.data = this.archiveData;
  }

  goBack(): void {
    this.location.back();
  }

  timestampToString(ts): string {
    return moment.unix(ts).format('DD/MM/YYYY HH:mm');
  }

  clear() {
    this.archiveData = [];
    this.polylineLayerGroup.clearLayers();
    this.dataSource.data = this.archiveData;
    this.polylineCreated = false;
  }

  onMapReady(map: L.Map): void {
    this.map = map;
    this.map.addLayer(this.polylineLayerGroup);
  }

  onDrawCreated(event: any) {
    this.polylineCreated = true;
    // @ts-ignore
    const layer: L.Layer = event.layer;
    // @ts-ignore
    const polylineCoordinates = layer.getLatLngs(); // First element is the polygon (it's the first object in the group)
    this.archiveData = this.buildMeasures(polylineCoordinates);
    this.dataSource.data = this.archiveData;
    console.log('path', this.archiveData);
    // Add polygon layer to the map
    this.ngZone.run(() => {
      this.polylineLayerGroup.addLayer(layer);
    });
  }

  onDrawDeleted(event: any) {
    this.clear();
  }

  onDateChanged(event: any) {
    const newValue = moment('YYYY-MM-DD[T]hh:mm', event.target.value).unix();
    console.log('date changed', event);
    this.tsStart = event.target.valueAsNumber / 1000;
  }

  createMeasure(coords: L.LatLng, ts: number): Measure {
    return {
      'latitude': coords.lat,
      'longitude': coords.lng,
      'timestamp': ts
    };
  }

  buildMeasures(coordinates: L.LatLng[]): Measure[] {
    const result: Measure[] = [];
    let nextTimestamp = this.tsStart;
    console.log('Archive creation start', nextTimestamp);
    coordinates.forEach((current, index) => {
      if (index > 0) {
        const distance = current.distanceTo(coordinates[index - 1]); // returns distance in meters
        const speed = (5 / 18) * this.speedAvg; // 5/10 is the rrate between km/h and m/s
        nextTimestamp += Math.floor(distance / speed);
        result.push(this.createMeasure(current, nextTimestamp));
        console.log('Measure ' + (index + 1), result[index]);
      } else {
        result.push(this.createMeasure(current, nextTimestamp));
        console.log('Measure 1', result[index]);
      }
    });
    return result;
  }

  confirm() {
    this.archiveService.uploadArchive(this.archiveData)
      .subscribe(
        res => {
          this.snackBar.open('Archivio Creato!', 'Chiudi', {
            duration: 800,
          }).afterOpened().subscribe( x => this.clear());
        },
        err => {
          this.snackBar.open('Errore durante la creazione dell\'archivio!', 'Chiudi', {
            duration: 2000,
          });
        }
      );
  }
}
