import React, { Component } from 'react';
import moment from 'moment';

export default class TransactionRow extends Component {
  render() {
    const transaction = this.props.data;

    return (
      <tr key={transaction.id}>
        <td>{moment(transaction.postedDate).format('L')}</td>
        <td>{transaction.payee}</td>
        <td>{transaction.amount}</td>
        <td>{transaction.note}</td>
        <td>{transaction.allocationId}</td>
      </tr>
    );
  }
}
