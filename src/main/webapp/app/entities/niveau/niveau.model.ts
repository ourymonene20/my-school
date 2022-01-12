import { ISalle } from 'app/entities/salle/salle.model';
import { IEnseignant } from 'app/entities/enseignant/enseignant.model';

export interface INiveau {
  id?: number;
  nom?: string | null;
  libelle?: string | null;
  salle?: ISalle | null;
  enseignants?: IEnseignant[] | null;
}

export class Niveau implements INiveau {
  constructor(
    public id?: number,
    public nom?: string | null,
    public libelle?: string | null,
    public salle?: ISalle | null,
    public enseignants?: IEnseignant[] | null
  ) {}
}

export function getNiveauIdentifier(niveau: INiveau): number | undefined {
  return niveau.id;
}
