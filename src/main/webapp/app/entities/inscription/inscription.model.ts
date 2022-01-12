import dayjs from 'dayjs/esm';
import { IEleve } from 'app/entities/eleve/eleve.model';
import { INiveau } from 'app/entities/niveau/niveau.model';

export interface IInscription {
  id?: number;
  dateInscprion?: dayjs.Dayjs | null;
  anneeScolaire?: dayjs.Dayjs | null;
  eleve?: IEleve | null;
  niveau?: INiveau | null;
}

export class Inscription implements IInscription {
  constructor(
    public id?: number,
    public dateInscprion?: dayjs.Dayjs | null,
    public anneeScolaire?: dayjs.Dayjs | null,
    public eleve?: IEleve | null,
    public niveau?: INiveau | null
  ) {}
}

export function getInscriptionIdentifier(inscription: IInscription): number | undefined {
  return inscription.id;
}
