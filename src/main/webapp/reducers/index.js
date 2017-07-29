import { combineReducers } from 'redux';
import Transactions from './reducer-transactions';
import TransactionsFilter from './reducer-transactions-filter';
import Goals from './reducer-goals';
import Budgets from './reducer-budgets';

const rootReducer = combineReducers({
  transactions: Transactions,
  goals: Goals,
  budgets: Budgets,
  transactionsFilter: TransactionsFilter
});

export default rootReducer;
