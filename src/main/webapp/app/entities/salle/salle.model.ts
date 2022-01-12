import { INiveau } from 'app/entities/niveau/niveau.model';

export interface ISalle {
  id?: number;
  libelle?: string | null;
  capacite?: number | null;
  niveaus?: INiveau[] | null;
}

export class Salle implements ISalle {
  constructor(public id?: number, public libelle?: string | null, public capacite?: number | null, public niveaus?: INiveau[] | null) {}
}

export function getSalleIdentifier(salle: ISalle): number | undefined {
  return salle.id;
}
