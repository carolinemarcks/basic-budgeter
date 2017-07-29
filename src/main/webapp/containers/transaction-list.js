import React, { Component } from 'react';
import { connect } from 'react-redux';
import TransactionRow from './transaction-row';

class TransactionList extends Component {
  render() {
    return (
      <table className="table table-hover">
        <thead>
          <tr>
            <th>Posted Date</th>
            <th>Payee</th>
            <th>Amount</th>
            <th>Note</th>
            <th>Allocation Id</th>
          </tr>
        </thead>
        <tbody>
          {this.props.transactions.map((transaction) =>
            <TransactionRow key={transaction.id} data={transaction} />
          )}
        </tbody>
      </table>
    );
  }
}

function mapStateToProps({ transactions }){
  return { transactions };
}

export default connect(mapStateToProps)(TransactionList);
