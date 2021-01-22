import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {ArchiveService} from '../../_services/archive.service';
import {Location} from '@angular/common';
import {MatDialog} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ArchiveSummary} from '../../_models/Archive';
import {ArchiveDetailComponent} from '../archive-detail/archive-detail.component';
import {ArchiveDeleteComponent} from '../archive-delete/archive-delete.component';

@Component({
  selector: 'app-purchased-archives-table',
  templateUrl: './purchased-archives-table.component.html',
  styleUrls: ['./purchased-archives-table.component.css']
})
export class PurchasedArchivesTableComponent implements OnInit, AfterViewInit {
  columnNames: string[] = ['id', 'username', 'count', 'actions'];
  dataSource = new MatTableDataSource();

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private archiveService: ArchiveService,
    private location: Location,
    public dialog: MatDialog,
    private _snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.reloadData();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  reloadData(): void {
    const archivesReq = this.archiveService.getPurchasedArchives();
    archivesReq.subscribe(archives => {
      console.log('Refresh purchased archives', archives);
      this.dataSource.data = archives;
    });
  }

  appendArchive(data: ArchiveSummary): void {
    this.dataSource.data.push(data);
  }

  onArchiveDetails(archiveId: string): void {
    this.dialog.open(ArchiveDetailComponent, {
      data: archiveId
    });
  }

  onArchiveDownload(archiveId: string): void {
    const archive = this.archiveService.downloadArchive(archiveId);
    archive.subscribe(a => {
      const blob = new Blob([JSON.stringify(a)], { type: 'text/json' });
      const url = window.URL.createObjectURL(blob);
      window.open(url);
    });
  }

}

