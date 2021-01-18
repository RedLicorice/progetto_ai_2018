import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {ArchiveService} from '../../_services/archive.service';

@Component({
  selector: 'app-archive-delete',
  templateUrl: './archive-delete.component.html',
  styleUrls: ['./archive-delete.component.css']
})
export class ArchiveDeleteComponent implements OnInit {

  constructor(
    @Inject(MAT_DIALOG_DATA) public archiveId: string,
    private archiveService: ArchiveService
  ) { }

  ngOnInit(): void {
  }

}
