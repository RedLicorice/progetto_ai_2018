import {Component, Input, OnInit} from '@angular/core';
import {PublicArchiveResource} from '../../_models/Archive';

@Component({
  selector: 'app-time-chart',
  templateUrl: './time-chart.component.html',
  styleUrls: ['./time-chart.component.css']
})
export class TimeChartComponent implements OnInit {
  // Chart options
  options = {
    'title': 'Scatter (time series)',
    'axes': {
      'bottom': {
        'title': 'Measures',
        'scaleType': 'time',
        'mapsTo': 'date',
      },
      'left': {
        'scaleType': 'labels',
        'mapsTo': 'value'
      }
    },
    'height': '400px',
    'legend': {
      'enabled': false,
    }
  };

  data = []; // Displayed data table

  constructor() { }

  ngOnInit(): void {
  }

  // Since JavaScript does not include date formatting utilities
  // This method converts a Spring timestamp (in seconds) to a Date object,
  // then proceeds in formatting it to the format required by Carbon-charts
  timestampToString(ts): string {
    const date = new Date(ts * 1000); // JS's timestamp is in MILLISECONDS (s*1000)
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hour = date.getHours();
    const min = date.getMinutes();
    const sec = date.getSeconds();
    // Required format: '2019-01-14T23:00:00.000Z'
    const datestr = date.getFullYear() + '-' +
      ( month <= 9 ? '0' + month : month) + '-' +
      ( day <= 9 ? '0' + day : day) + 'T' +
      ( hour <= 9 ? '0' + hour : hour) + ':' +
      ( min <= 9 ? '0' + min : min) + ':' +
      ( sec <= 9 ? '0' + sec : sec) + '.000Z';
    // console.log('timeChart date', datestr, date);
    return datestr;
  }

  setPublicArchives(archives: PublicArchiveResource[]): void {
    // Grab unique users list, in order to assign unique value (line on the y axis)
    // to each of them
    const users: string[] = archives.map(a => a.username); // Get a list of users
    const usersIndex = [];
    users.forEach(u => {
      if (usersIndex.indexOf(u) === -1) {
          usersIndex.push(u);
        }
      });
    // Empty the data table
    this.data = [];
    // Add a point to the data table for each measure in each of the archives
    archives.forEach(a => {
      a.timestamps.forEach(ts => {
        const datestr = this.timestampToString(ts);
        // console.log('timeChart date', datestr);
        // The data format is given by Carbon-charts API
        this.data.push({
          'group': a.username, // Groups allow for color assignment, this is independent from the archive-map component
          'date': datestr,
          'value': usersIndex.indexOf(a.username) + 1, // Each user has a separate value so it is represented on its own line
          'surplus': 0 // Not used in the data visualization, but npm complains if it is missing
        });
      });
    });
    // ToDO: Force view update
  }
}
