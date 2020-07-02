import {Measure} from './Measure';
import {Position} from './Position';

/*
* These classes represent the different "views" corresponding to an archive resource as returned by the API.
*
* ArchiveResource models an archive in its "full form", with detailed measures.
*/
export class ArchiveResource {
  id: string;
  username: string;
  count: number;
  measures: Measure[];
}
/*
* This class models an archive in its public form, with approximate representations of measures, included measure count
* (which does not necessarily correspond to approximations count), and price.
* */
export class PublicArchiveResource {
  id: string;
  username: string;
  price: number;
  count: number;
  timestamps: number[];
  positions: Position[];
}
/*
* This class models the archive summary for all of the user's archives both purchased and uploaded
* */
export class ArchiveSummary {
  id: string;
  username: string;
  count: number;
}
/*
* This class models the archive summary for all of the user uploaded archives, which includes purchase count and deleted flag
* */
export class ArchiveOwnerSummary {
  id: string;
  username: string;
  count: number;
  purchases: number;
  deleted: boolean;
}
