import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {ArchiveService} from '../../_services/archive.service';
import {ArchiveResource} from '../../_models/Archive';
import {ArchiveDisplayMapComponent} from '../archive-display-map/archive-display-map.component';

@Component({
  selector: 'app-archive-detail',
  templateUrl: './archive-detail.component.html',
  styleUrls: ['./archive-detail.component.css']
})
export class ArchiveDetailComponent implements OnInit {
  @ViewChild(ArchiveDisplayMapComponent) mapComponent: ArchiveDisplayMapComponent;

  constructor(
    @Inject(MAT_DIALOG_DATA) public archiveId: string,
    private archiveService: ArchiveService
  ) { }

  archive: ArchiveResource;

  ngOnInit(): void {
    const archive = this.archiveService.downloadArchive(this.archiveId);
    archive.subscribe(a => {
      console.log('Retrieved archive', a);
      // this.mapComponent.setArchive(a);
      this.archive = a;
    });
  }

  downloadArchive(): void {
    const blob = new Blob([JSON.stringify(this.archive)], { type: 'text/json' });
    const url = window.URL.createObjectURL(blob);
    window.open(url);
  }

}
