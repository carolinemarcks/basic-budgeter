import { FETCH_BUDGETS, CREATE_BUDGET } from '../actions/index';
import _ from 'lodash';

export default function(state = [], action) {
  switch (action.type) {
    case FETCH_BUDGETS:
      return _.sortBy(action.payload.data, "name");
    case CREATE_BUDGET:
      return _.sortBy([ ...state, action.payload.data ], "name");
    default:
      return state;
    }
}
