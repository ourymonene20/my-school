import dayjs from 'dayjs/esm';
import { ITuteurs } from 'app/entities/tuteurs/tuteurs.model';

export interface IEleve {
  id?: number;
  nom?: string | null;
  prenom?: string | null;
  email?: string | null;
  dateNaiss?: dayjs.Dayjs | null;
  tuteur?: ITuteurs | null;
}

export class Eleve implements IEleve {
  constructor(
    public id?: number,
    public nom?: string | null,
    public prenom?: string | null,
    public email?: string | null,
    public dateNaiss?: dayjs.Dayjs | null,
    public tuteur?: ITuteurs | null
  ) {}
}

export function getEleveIdentifier(eleve: IEleve): number | undefined {
  return eleve.id;
}
