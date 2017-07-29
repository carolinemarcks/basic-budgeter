import { combineReducers } from 'redux';
import Transactions from './reducer-transactions';
import Goals from './reducer-goals';
import Budgets from './reducer-budgets';

const rootReducer = combineReducers({
  transactions: Transactions,
  goals: Goals,
  budgets: Budgets
});

export default rootReducer;
