import {OptionValueEntry} from "./option-value-entry";

export class OfferOptionEntry {
  id?: number;
  title?: string;
  name?: string;
  values?: OptionValueEntry[] = [];
  selectedValue?: OptionValueEntry;
  editMode?: boolean;
}
