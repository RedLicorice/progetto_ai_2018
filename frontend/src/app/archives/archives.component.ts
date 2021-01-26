import {Component, OnInit, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Location} from '@angular/common';
import {ArchiveService} from '../_services/archive.service';
import {ArchiveUploadComponent} from './archive-upload/archive-upload.component';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-archives',
  templateUrl: './archives.component.html',
  styleUrls: ['./archives.component.css']
})
export class ArchivesComponent implements OnInit {
  constructor(
    private location: Location,
    private archiveService: ArchiveService,
    public dialog: MatDialog,
    private _snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
  }

  goBack(): void {
    this.location.back();
  }

  uploadArchive(): void {
    const dialogRef = this.dialog.open(ArchiveUploadComponent);
    dialogRef.afterClosed()
      .subscribe(res => {
        if (res) {
          console.log('Upload confirm', res);
          return this.archiveService.uploadArchive(res).subscribe(upl => {
            console.log('Upload result', upl);
            this._snackBar.open('Upload completato!', 'Chiudi', {duration: 800});
          },
          err => this._snackBar.open('Upload fallito!', 'Chiudi', {duration: 800}));
        }
      });
  }
}
