import React, { Component } from 'react';
import TransactionList from '../containers/transaction-list';

export default class App extends Component {
  render() {
    return (
      <div>
      <h1>Budgeter</h1>
      <TransactionList />
      </div>
    );
  }
}
