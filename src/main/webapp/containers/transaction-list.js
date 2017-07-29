import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { fetchTransactions } from '../actions'

class TransactionList extends Component {
  componentDidMount() {
    this.props.fetchTransactions();
  }
  render() {
    return (
      <div>
        {this.props.transactions.map((transaction) =>
          <div key={transaction.id}>{JSON.stringify(transaction)}</div>
        )}
      </div>
    );
  }
}

function mapStateToProps({ transactions }){
  return { transactions };
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({ fetchTransactions }, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(TransactionList);
