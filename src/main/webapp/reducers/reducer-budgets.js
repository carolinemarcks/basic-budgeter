import { FETCH_BUDGETS } from '../actions/index';

export default function(state = [], action) {
  switch (action.type) {
    case FETCH_BUDGETS:
      return action.payload.data;
    default:
      return state;
    }
}
