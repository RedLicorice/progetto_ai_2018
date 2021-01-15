import {Component, OnInit, ViewChild} from '@angular/core';
import { Location } from '@angular/common';
import {ArchiveMapComponent} from '../components/archive-map/archive-map.component';
import {ArchiveService} from '../_services/archive.service';
import {Position} from '../_models/Position';
import {TimeChartComponent} from '../components/time-chart/time-chart.component';

// ToDo: Add datetimepickers for filter selection
// https://www.npmjs.com/package/@angular-material-components/datetime-picker

@Component({
  selector: 'app-shop',
  templateUrl: './shop.component.html',
  styleUrls: ['./shop.component.css']
})
export class ShopComponent implements OnInit {
  @ViewChild(ArchiveMapComponent) mapComponent: ArchiveMapComponent;
  @ViewChild(TimeChartComponent) timeChartComponent: TimeChartComponent;
  constructor(
    private location: Location,
    private archiveService: ArchiveService
  ) { }

  ngOnInit(): void {
  }

  onPositionChanged(bounds: any): void {
    console.log('position changed', bounds);
    // if (this.mapComponent) {
    //   this.mapComponent.addMarker(bounds._northEast.lat, bounds._northEast.lng, '#ff0000');
    // }
    const topLeft = new Position();
    topLeft.latitude = bounds._southWest.lat;
    topLeft.longitude = bounds._northEast.lng;
    const bottomRight = new Position();
    bottomRight.latitude = bounds._northEast.lat;
    bottomRight.longitude = bounds._southWest.lng;
    const archives = this.archiveService.searchArchives(topLeft, bottomRight, null, null, []);
    const that = this;
    archives.subscribe({
      next(a) {
        // a.forEach( x => {
        //   console.log('Got Archive: ', x.id, 'User: ', x.username, 'Archive: ', x);
        // });
        that.mapComponent.setPublicArchives(a);
        that.timeChartComponent.setPublicArchives(a);
      },
      error(msg) {
        console.log('Error Getting Archives: ', msg);
      }
    });
  }

  onAreaSelected(polygon: any): void {
    console.log('area selected', polygon);
  }

  clearMarkers(): void {
    this.mapComponent.clearMarkers();
  }

  clearGeo(): void {
    this.mapComponent.clearGeospatial();
  }

  goBack(): void {
    this.location.back();
  }
}
