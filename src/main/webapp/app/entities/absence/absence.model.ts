import dayjs from 'dayjs/esm';
import { IEleve } from 'app/entities/eleve/eleve.model';

export interface IAbsence {
  id?: number;
  dateAbsence?: dayjs.Dayjs | null;
  eleve?: IEleve | null;
}

export class Absence implements IAbsence {
  constructor(public id?: number, public dateAbsence?: dayjs.Dayjs | null, public eleve?: IEleve | null) {}
}

export function getAbsenceIdentifier(absence: IAbsence): number | undefined {
  return absence.id;
}
