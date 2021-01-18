import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ArchiveResource} from '../../_models/Archive';
import {UserService} from '../../_services/user.service';
import {ArchiveMapComponent} from '../../components/archive-map/archive-map.component';
import {Measure} from '../../_models/Measure';

@Component({
  selector: 'app-archive-upload',
  templateUrl: './archive-upload.component.html',
  styleUrls: ['./archive-upload.component.css']
})
export class ArchiveUploadComponent implements OnInit {
  @ViewChild('fileInput') fileInput: ElementRef;
  @ViewChild(ArchiveMapComponent) mapComponent: ArchiveMapComponent;
  fileAttr = 'Scegli File';
  showPreview = false;
  measures: Measure[];

  constructor(userService: UserService) { }

  ngOnInit(): void {
  }

  parseMeasures(jsonMeasures: string) {
    // The Backend accepts an array of Measure objects
    // We want to handle both cases
    this.measures = JSON.parse(jsonMeasures);
    this.showPreview = true;
  }

  onFileSelected(event: any) {
    if (event.target.files && event.target.files[0]) {
      const file = event.target.files[0];
      this.fileAttr = file.name;

      // HTML5 FileReader API
      const reader = new FileReader();
      reader.onload = () => {
        // 'text' is the file content
        const text = reader.result;
        console.log('uploaded content', text);
        this.parseMeasures(text.toString());
      };
      reader.readAsText(file);
      // Reset if duplicate image uploaded again
      this.fileInput.nativeElement.value = '';
    } else {
      this.fileAttr = 'Scegli File';
    }
  }
}
