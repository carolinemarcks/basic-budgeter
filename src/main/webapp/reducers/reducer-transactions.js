import { FETCH_TRANSACTIONS, ALLOCATE } from '../actions/index';
import _ from 'lodash';

export default function(state = [], action) {
  switch (action.type) {
    case FETCH_TRANSACTIONS:
      return action.payload.data.transactions;
    case ALLOCATE:
      const newTransaction = action.payload.data;
      return _.reduce(state, function(newState, oldTransaction) {
        if (oldTransaction.id == newTransaction.id) {
          newState.push(newTransaction)
        } else {
          newState.push(oldTransaction)
        }
        return newState;
      }, []);
    default:
      return state;
    }
}
