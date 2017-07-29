import axios from 'axios';

const ROOT_URL = 'http://localhost:9000';
export const FETCH_TRANSACTIONS = 'FETCH_TRANSACTIONS';
export const FETCH_GOALS = 'FETCH_GOALS';
export const FETCH_BUDGETS = 'FETCH_BUDGETS';
export const ALLOCATE = 'ALLOCATE';
export const CREATE_BUDGET = 'CREATE_BUDGET';
export const CREATE_GOAL = 'CREATE_GOAL';

export function fetchTransactions() {
  const url = `${ROOT_URL}/transactions`;

  const request = axios.get(url);
  return {
    type: FETCH_TRANSACTIONS,
    payload: request
  };
}
export function fetchGoals() {
  const url = `${ROOT_URL}/allocations/goals`;

  const request = axios.get(url);
  return {
    type: FETCH_GOALS,
    payload: request
  };
}

export function fetchBudgets() {
  const url = `${ROOT_URL}/allocations/budgets`;

  const request = axios.get(url);
  return {
    type: FETCH_BUDGETS,
    payload: request
  };
}

export function allocate(transactionId, allocationId) {
  const url = `${ROOT_URL}/allocate`;
  const request = axios.post(url, {
    transactionId,
    allocationId
  });
  return {
    type: ALLOCATE,
    payload: request
  };
}

export function createBudget(budget) {
  const url = `${ROOT_URL}/allocations/budgets`;
  const res = {
    name: budget['name'],
    saved: parseInt(budget['saved']),
    amount: parseInt(budget['amount'])
  };

  if (budget['cap']) {
    res['cap'] = parseInt(budget['cap']);
  }

  const request = axios.post(url, res);
  return {
    type: CREATE_BUDGET,
    payload: request
  };
}

export function createGoal(goal) {
  const url = `${ROOT_URL}/allocations/goals`;
  const res = {
    name: goal['name'],
    saved: parseInt(goal['saved']),
    weight: parseInt(goal['weight'])
  };

  if (goal['cap']) {
    res['cap'] = parseInt(goal['cap']);
  }

  const request = axios.post(url, res);
  return {
    type: CREATE_GOAL,
    payload: request
  };
}
