import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';
import {Location} from '@angular/common';
import {ArchiveService} from '../_services/archive.service';
import {ArchiveDetailComponent} from './archive-detail/archive-detail.component';
import {ArchiveDeleteComponent} from './archive-delete/archive-delete.component';
import {ArchiveUploadComponent} from './archive-upload/archive-upload.component';

@Component({
  selector: 'app-archives',
  templateUrl: './archives.component.html',
  styleUrls: ['./archives.component.css']
})
export class ArchivesComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['id', 'purchases', 'deleted', 'count', 'actions'];
  dataSource = new MatTableDataSource();

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private location: Location,
    private archiveService: ArchiveService,
    public dialog: MatDialog
  ) { }

  ngOnInit(): void {
    const uploadedArchives = this.archiveService.getUploadedArchives();
    uploadedArchives.subscribe( items => {
        console.log('Uploaded archives retrieved', items);
        this.dataSource.data = items;
      },
      err => {
        console.log('Error retrieving invoices', err);
      });
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  goBack(): void {
    this.location.back();
  }

  uploadArchive(): void {
    this.dialog.open(ArchiveUploadComponent);
  }

  detailsArchive(archiveId: string): void {
    this.dialog.open(ArchiveDetailComponent, {
      data: archiveId
    });
  }

  deleteArchive(archiveId: string): void {
    this.dialog.open(ArchiveDeleteComponent, {
      data: archiveId
    });
  }

}
