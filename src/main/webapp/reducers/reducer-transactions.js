import { FETCH_TRANSACTIONS } from '../actions/index';

export default function(state = [], action) {
  switch (action.type) {
    case FETCH_TRANSACTIONS:
      return action.payload.data.transactions;
    default:
      return state;
    }
}
