import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import { Location } from '@angular/common';
import {ArchiveMapComponent} from '../components/archive-map/archive-map.component';
import {ArchiveService} from '../_services/archive.service';
import {Position} from '../_models/Position';
import {ArchiveTimeChartComponent} from './archive-time-chart/archive-time-chart.component';
import { FormControl } from '@angular/forms';
import * as moment from 'moment';
import {PublicArchiveResource} from '../_models/Archive';
import {ArchiveSearchMapComponent, ArchiveSearchResult} from './archive-search-map/archive-search-map.component';
import {TimeIntervalFilterComponent} from './time-interval-filter/time-interval-filter.component';
import {MatSnackBar} from "@angular/material/snack-bar";

// ToDo: Add datetimepickers for filter selection
// https://www.npmjs.com/package/@angular-material-components/datetime-picker

@Component({
  selector: 'app-shop',
  templateUrl: './shop.component.html',
  styleUrls: ['./shop.component.css']
})
export class ShopComponent implements OnInit, AfterViewInit {
  @ViewChild(ArchiveSearchMapComponent) mapComponent: ArchiveSearchMapComponent;
  @ViewChild(ArchiveTimeChartComponent) timeChartComponent: ArchiveTimeChartComponent;
  @ViewChild(TimeIntervalFilterComponent) timeIntervalFilterComponent: TimeIntervalFilterComponent;

  constructor(
    private location: Location,
    private archiveService: ArchiveService,
    private cdr: ChangeDetectorRef,
    private _snackBar: MatSnackBar
  ) { }

  shownUsers: string[] = [];
  shownArchives: PublicArchiveResource[] = [];
  selectedArchives: PublicArchiveResource[] = [];
  selectedUsers: string[] = [];
  resultingArchives: PublicArchiveResource[];
  polygonCreated = false;
  resultingCount = 0;
  dateBegin: number;
  dateEnd: number;
  topLeft: Position;
  bottomRight: Position;

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.dateBegin = this.timeIntervalFilterComponent.dateBegin;
    this.dateEnd = this.timeIntervalFilterComponent.dateEnd;
  }

  searchArea() {
    // Send request with current area to server
    console.log('Search request',
      this.topLeft,
      this.bottomRight,
      this.dateBegin,
      this.dateEnd);
    const archives = this.archiveService.searchArchives(
      this.topLeft,
      this.bottomRight,
      this.dateBegin,
      this.dateEnd);
    archives.subscribe(
      res => {
        this.shownArchives = res;
        this.shownUsers = [...new Set(res.map(arc => arc.username))]; // Use the ellipsis operator and Set to remove duplicates
        this.mapComponent.setArchives(res);
        this.timeChartComponent.setPublicArchives(res);
        this.cdr.detectChanges(); // force changes detections
      },
      err => {
        console.log('Error Getting Archives: ', err);
      });
  }

  onBoundsChanged(bounds: any): void {
    // console.log('position changed', bounds);
    this.topLeft = {
      latitude: bounds._southWest.lat,
      longitude: bounds._northEast.lng
    };
    this.bottomRight = {
      latitude: bounds._northEast.lat,
      longitude: bounds._southWest.lng
    };
    this.searchArea();
  }

  onTimeFilterChanged(times: number[]): void {
    this.dateBegin = times[0];
    this.dateEnd = times[1];
    this.searchArea();
  }

  onAreaSelected(searchResults: ArchiveSearchResult): void {
    console.log('area selected', searchResults);
    if ( searchResults ) {
      this.resultingArchives = searchResults.selected_archives;
      this.resultingCount = searchResults.position_count;
      this.polygonCreated = true;
      this.selectedUsers = [... new Set(searchResults.selected_archives.map(a => a.username))];
      this.selectedArchives = this.resultingArchives.filter(a => this.selectedUsers.includes(a.username));
    } else {
      this.selectedUsers = [];
      this.selectedArchives = [];
      this.resultingArchives = [];
      this.resultingCount = 0;
      this.polygonCreated = false;
    }
    this.cdr.detectChanges(); // force changes detections
  }

  clearPolygon(): void {
    this.mapComponent.clearPolygon();
  }

   isUserSelected(user: string): boolean {
    return this.selectedUsers.includes(user);
  }

  onSelectArchive(event: any): void {
    const option = event.option;
    if (option.selected) {
      this.selectedArchives.push(option.value);
    } else {
      const index = this.selectedArchives.indexOf(option.value);
      this.selectedArchives.splice(index, 1);
    }
    console.log('OnSelectArchive', option, this.selectedArchives);
    this.cdr.detectChanges();
  }

  onSelectUser(event: any) {
    const option = event.option;
    if (option.selected) {
      this.selectedUsers.push(option.value);
    } else {
      const index = this.selectedUsers.indexOf(option.value);
      this.selectedUsers.splice(index, 1);
    }
    this.selectedArchives = this.resultingArchives.filter(a => this.selectedUsers.includes(a.username));
    console.log('OnSelectUser', option, this.selectedUsers);
    this.cdr.detectChanges();
  }

  onConfirmPurchase() {
    console.log('confirm purchase', this.selectedArchives);
    const purchase = this.archiveService.buyArchives(this.selectedArchives.map(a => a.id));
    purchase.subscribe( res => {
        console.log('purchase success', res);
        this._snackBar.open('Ordine effettuato!');
      },
      err => {
        console.log('purchase error', err);
        this._snackBar.open('Errore durante l\'ordine!');
      });
  }

  goBack(): void {
    this.location.back();
  }
}
