import {AfterViewInit, Component, OnInit, ViewChild, EventEmitter, Output} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {Location} from '@angular/common';
import {ArchiveService} from '../../_services/archive.service';
import {MatDialog} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ArchiveUploadComponent} from '../archive-upload/archive-upload.component';
import {ArchiveDetailComponent} from '../archive-detail/archive-detail.component';
import {ArchiveDeleteComponent} from '../archive-delete/archive-delete.component';
import {ArchiveOwnerSummary} from '../../_models/Archive';

@Component({
  selector: 'app-uploaded-archives-table',
  templateUrl: './uploaded-archives-table.component.html',
  styleUrls: ['./uploaded-archives-table.component.css']
})
export class UploadedArchivesTableComponent implements OnInit, AfterViewInit {
  columnNames: string[] = ['id', 'purchases', 'count', 'deleted', 'actions'];
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
    const archivesReq = this.archiveService.getUploadedArchives();
    archivesReq.subscribe(archives => {
      console.log('Refresh uploaded archives', archives);
      this.dataSource.data = archives;
    });
  }

  appendArchive(data: ArchiveOwnerSummary): void {
    this.dataSource.data.push(data);
  }

  onArchiveDetails(archiveId: string): void {
    this.dialog.open(ArchiveDetailComponent, {
      data: archiveId
    });
  }

  onArchiveDelete(archiveId: string): void {
    const dialogRef = this.dialog.open(ArchiveDeleteComponent, {
      data: archiveId
    });
    dialogRef.afterClosed().subscribe(res => {
      if (res) {
        console.log('Deleting archive', archiveId);
        this.archiveService.deleteArchive(archiveId).subscribe(
          del => {
            this._snackBar.open('Archivio eliminato!');
          },
          err => this._snackBar.open('Eliminazione fallita!')
        );
      }
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

