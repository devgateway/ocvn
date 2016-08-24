import Visualization from '../../visualization';
import backendYearFilterable from "../../backend-year-filterable";

class Table extends backendYearFilterable(Visualization){}

Table.DATE_FORMAT = {
  year: 'numeric',
  month: 'short',
  day: 'numeric'
};

Table.comparable = false;

export default Table;