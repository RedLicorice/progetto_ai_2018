import { Pipe, PipeTransform } from '@angular/core';
import * as moment from 'moment';

/*
 * Transform a timestamp to a readable date
 * Usage:
 *   value | formatTimestamp
 * Example:
 *   {{ 1528189536 | formatTimestamp }}
 *   formats to: 05/06/2018
*/
@Pipe({ name: 'formatTimestamp' })
export class FormatTimestampPipe implements PipeTransform {
  transform(value: number): string {
    return moment.unix(value).format('DD/MM/YYYY');
  }
}
