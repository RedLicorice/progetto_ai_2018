import {Component, Input, OnInit} from '@angular/core';
import {PublicArchiveResource} from '../../_models/Archive';
import {GoogleChartInterface} from 'ng2-google-charts';

@Component({
  selector: 'app-time-chart',
  templateUrl: './time-chart.component.html',
  styleUrls: ['./time-chart.component.css']
})
export class TimeChartComponent implements OnInit {
  // Chart header data
  private chartDataHeader = [
    // Y Axis shows one row for each user
    {type: 'string', id: 'User', label: 'User'},
    {type: 'string', id: 'Archive', label: 'Archive'},
    // X Axis shows the dates
    {type: 'date', id: 'From', label: 'From'},
    {type: 'date', id: 'To', label: 'To'},
    // Custom column used to define a custom tooltip when the user hovers a point
    {type: 'string', role: 'tooltip', 'p': {'html': true}}
  ];

  // Chart data structure
  public chartData: GoogleChartInterface  = {
    // Chart type
    chartType: 'Timeline',
    // Chart data (initially empty)
    dataTable: [],
    options: {
      // Allow HTML custom tooltips
      allowHtml: true,
      // Customize the Y axis
      vAxis: {
        // No title
        title: 'Users',
        // Set color to white to blend with the background and make it invisible
        textStyle: {
          color: '#000',
        },
        // Set the initial tick to 0 (only integer values)
        ticks: [0]
      },
      // Define the tooltip type
      tooltip: {
        isHtml: true
      },
      // Hide the legend
      legend: {
        position: 'none'
      },
      // Set the chart to span the full width of the container element
      chartArea: {
        left: 0,
        width: '100%'
      }
    },
    formatters: [
      // Define a format for the date
      {
        columns: [0],
        type: 'DateFormat',
        options: {
          pattern: 'd MMM yyyy, H:m'
        }
      },
      // Allow only integer values on the Y axis
      {
        columns: [1],
        type: 'NumberFormat',
        options: {
          pattern: '#'
        }
      }
    ]
  };

  constructor() { }

  ngOnInit(): void {
    this.clear();
  }

  clear(): void {
    // Set the header row
    this.chartData.dataTable = [this.chartDataHeader];
  }

  setPublicArchives(archives: PublicArchiveResource[]): void {
    // Set the header row
    this.chartData.dataTable = [this.chartDataHeader];
    const users: string[] = archives.map(a => a.username); // Get a list of users
    const usersIndex = [];
    users.forEach(u => {
      if (usersIndex.indexOf(u) === -1) {
          usersIndex.push(u);
          this.chartData.options.vAxis.ticks.push(usersIndex.length);
        }
      });
    this.chartData.options.vAxis.ticks.push(usersIndex.length + 1);

    archives.forEach(a => {
      const from = new Date(Math.min.apply(null, a.timestamps));
      const to = new Date(Math.max.apply(null, a.timestamps));
      console.log("timeChart", a.username, a.id, from, to);
      this.chartData.dataTable.push([ a.username, a.id, from, to, this.createTooltip(a.id, from, to, a.username)]);
    });
  }

  // Creates a custom HTML tooltip for a point in the chart
  // The tooltip displays the owner and the date corresponding to the point
  // date: the date to be displayed in the tooltip
  // user: the user to be displayed in the tooltip
  createTooltip(id: string, from: Date, to: Date, username: string) {
    const fromDate = from.toLocaleDateString();
    const fromTime = from.toLocaleTimeString();
    const toDate = to.toLocaleDateString();
    const toTime = to.toLocaleTimeString();
    const tooltip =
      '<div style="padding: 10px">' +
      '<p><strong>' + username + '</strong></p>' +
      '<p><strong>Archive:</strong>&nbsp;' + id + '</p>' +
      '<p><strong>From:</strong>&nbsp;' + fromDate + ' ' + fromTime + '</p>' +
      '<p><strong>To:</strong>&nbsp;' + toDate + ' ' + toTime + '</p>' +
      '</div>';
    return tooltip;
  }

}
