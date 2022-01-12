import dayjs from 'dayjs/esm';
import { INiveau } from 'app/entities/niveau/niveau.model';

export interface IEnseignant {
  id?: number;
  nom?: string | null;
  prenom?: string | null;
  email?: string | null;
  dateNaiss?: dayjs.Dayjs | null;
  niveaus?: INiveau[] | null;
}

export class Enseignant implements IEnseignant {
  constructor(
    public id?: number,
    public nom?: string | null,
    public prenom?: string | null,
    public email?: string | null,
    public dateNaiss?: dayjs.Dayjs | null,
    public niveaus?: INiveau[] | null
  ) {}
}

export function getEnseignantIdentifier(enseignant: IEnseignant): number | undefined {
  return enseignant.id;
}
