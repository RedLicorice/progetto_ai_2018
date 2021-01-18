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
    this.reloadData()
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  goBack(): void {
    this.location.back();
  }

  reloadData(): void {
    const uploadedArchives = this.archiveService.getUploadedArchives();
    uploadedArchives.subscribe( items => {
        console.log('Uploaded archives retrieved', items);
        this.dataSource.data = items;
      },
      err => {
        console.log('Error retrieving invoices', err);
      });
  }

  uploadArchive(): void {
    const dialogRef = this.dialog.open(ArchiveUploadComponent);
    dialogRef.afterClosed()
      .subscribe(res => {
        if (res) {
          console.log('Upload confirm', res);
          return this.archiveService.uploadArchive(res).subscribe(upl => {
            console.log('Upload result', upl);
            this.reloadData();
          });
        }
      });
  }

  detailsArchive(archiveId: string): void {
    this.dialog.open(ArchiveDetailComponent, {
      data: archiveId
    });
  }

  deleteArchive(archiveId: string): void {
    const dialogRef = this.dialog.open(ArchiveDeleteComponent, {
      data: archiveId
    });
    dialogRef.afterClosed().subscribe(res => {
      if (res) {
        console.log('Deleting archive', archiveId);
        this.archiveService.deleteArchive(archiveId).subscribe(del => {
            this.reloadData();
          }
        );
      }
    });
  }

}
