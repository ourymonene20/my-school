import { IEleve } from 'app/entities/eleve/eleve.model';

export interface ITuteurs {
  id?: number;
  nom?: string | null;
  prenom?: string | null;
  telephone?: string;
  email?: string | null;
  eleves?: IEleve[] | null;
}

export class Tuteurs implements ITuteurs {
  constructor(
    public id?: number,
    public nom?: string | null,
    public prenom?: string | null,
    public telephone?: string,
    public email?: string | null,
    public eleves?: IEleve[] | null
  ) {}
}

export function getTuteursIdentifier(tuteurs: ITuteurs): number | undefined {
  return tuteurs.id;
}
