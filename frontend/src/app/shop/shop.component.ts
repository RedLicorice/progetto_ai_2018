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
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';

@Component({
  selector: 'app-shop',
  templateUrl: './shop.component.html',
  styleUrls: ['./shop.component.css']
})
export class ShopComponent implements OnInit {
  @ViewChild(ArchiveSearchMapComponent) mapComponent: ArchiveSearchMapComponent;
  @ViewChild(ArchiveTimeChartComponent) timeChartComponent: ArchiveTimeChartComponent;
  @ViewChild(TimeIntervalFilterComponent) timeIntervalFilterComponent: TimeIntervalFilterComponent;

  constructor(
    private location: Location,
    private router: Router,
    private archiveService: ArchiveService,
    private cdr: ChangeDetectorRef,
    private _snackBar: MatSnackBar
  ) { }

  shownArchives: PublicArchiveResource[] = []; // Archives shown on the map
  resultingArchives: PublicArchiveResource[]; // Archives containing at least one position within the polygon
  selectedArchives: PublicArchiveResource[] = []; // Archives selected for purchase
  shownUsers: string[] = []; // Users owning the archives shown on the map
  resultingUsers: string[] = []; // Users owning the archives containing at least one position within the polygon
  selectedUsers: string[] = []; // Selected users among the ones owning the archives containing at least one position within the polygon
  polygonCreated = false;
  resultingCount = 0;
  dateBegin: number;
  dateEnd: number;
  topLeft: Position;
  bottomRight: Position;

  ngOnInit(): void {
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
    console.log('time filter changed', times);
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
      this.resultingUsers = [... new Set(searchResults.selected_archives.map(a => a.username))];
      this.selectedUsers = [... new Set(searchResults.selected_archives.map(a => a.username))];
      this.selectedArchives = this.resultingArchives.filter(a => this.selectedUsers.includes(a.username));
    } else {
      this.selectedUsers = [];
      this.resultingUsers = [];
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
    this.cdr.detectChanges();
    const purchase = this.archiveService.buyArchives(this.selectedArchives.map(a => a.id));
    purchase.subscribe( res => {
        console.log('purchase success', res);
        this._snackBar.open('Ordine effettuato!', 'Chiudi', {duration: 800})
          .afterDismissed()
          .subscribe(
            x => this.router.navigateByUrl('/invoices/' + res.id)
          );
      },
      err => {
        console.log('purchase error', err);
        this._snackBar.open('Errore durante l\'ordine!', 'Chiudi', {duration: 800});
      });
  }

  goBack(): void {
    this.location.back();
  }
}
