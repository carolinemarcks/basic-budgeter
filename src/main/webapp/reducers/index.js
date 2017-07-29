import { combineReducers } from 'redux';
import TransactionList from './reducer-transactions';

const rootReducer = combineReducers({
  transactions: TransactionList
});

export default rootReducer;
