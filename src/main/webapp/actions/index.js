import axios from 'axios';

const ROOT_URL = 'http://localhost:9000';
export const FETCH_TRANSACTIONS = 'FETCH_TRANSACTIONS';

export function fetchTransactions() {
  const url = `${ROOT_URL}/transactions`;

  const request = axios.get(url);
  return {
    type: FETCH_TRANSACTIONS,
    payload: request
  };
}
