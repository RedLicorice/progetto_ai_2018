import {Component, Input, OnInit, Output, EventEmitter} from '@angular/core';
import * as moment from 'moment';

@Component({
  selector: 'app-time-interval-filter',
  templateUrl: './time-interval-filter.component.html',
  styleUrls: ['./time-interval-filter.component.css']
})
export class TimeIntervalFilterComponent implements OnInit {
  set dateBegin(ts: number) {
    if (ts) {
      this._dateBegin = moment.unix(ts).format('YYYY-MM-DD[T]hh:mm');
    }
  }
  get dateBegin(): number {
    return moment(this._dateBegin, 'YYYY-MM-DD[T]hh:mm').unix();
  }

  set dateEnd(ts: number) {
    if (ts) {
      this._dateEnd = moment.unix(ts).format('YYYY-MM-DD[T]hh:mm');
    }
  }
  get dateEnd(): number {
    return moment(this._dateEnd, 'YYYY-MM-DD[T]hh:mm').unix();
  }

  @Output() changed = new EventEmitter<[number, number]>();

  _dateBegin: string;
  _dateEnd: string;
  _dateEndMax: string;

  constructor() { this.clear(); }

  ngOnInit(): void {}

  clear(): void {
    const today = moment();
    const last_year = moment().subtract(2, 'years');
    this._dateBegin = last_year.format('YYYY-MM-DD[T]hh:mm');
    this._dateEnd = today.format('YYYY-MM-DD[T]hh:mm');
    this._dateEndMax = today.format('YYYY-MM-DD[T]hh:mm');
    this.changed.emit([this.dateBegin, this.dateEnd]);
  }

  onDateBeginChange(event: any) {
    const newValue = moment('YYYY-MM-DD[T]hh:mm', event.target.value).unix();
    console.log('Date begin changed', newValue, event);
    this.changed.emit([this.dateBegin, this.dateEnd]);
  }

  onDateEndChange(event: any) {
    const newValue = moment('YYYY-MM-DD[T]hh:mm', event.target.value).unix();
    console.log('Date end changed', newValue, event);
    this.changed.emit([this.dateBegin, this.dateEnd]);
  }
}
