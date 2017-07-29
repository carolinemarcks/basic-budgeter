import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { fetchTransactions } from '../actions/index';
import TransactionRow from './transaction-row';

class TransactionList extends Component {
  onPage(page) {
    this.props.fetchTransactions(page);
  }
  renderButton(page, text) {
    const cantPage = page === undefined;
    return (<button
      type="button"
      className="btn btn-secondary btn-sm"
      onClick={() => this.onPage(page)}
      disabled={cantPage}>{text}</button>);
  }
  render() {
    return (
        <table className="table table-hover">
          <thead>
            <tr>
              <th>Posted Date</th>
              <th>Payee</th>
              <th>Amount</th>
              <th>Note</th>
              <th>Allocation</th>
              <th>
                <div className="btn-group pagination">
                  {this.renderButton(this.props.pages[0], "<")}
                  {this.renderButton(this.props.pages[2], ">")}
                  {this.renderButton('', ">>")}
                </div>
              </th>
            </tr>
          </thead>
          <tbody>
            {this.props.transactions.map((transaction) =>
              <TransactionRow key={transaction.id} data={transaction} />
            )}
          </tbody>
        </table>);
  }
}

function mapStateToProps({ transactions, transactionsFilter }){
  const { pages } = transactionsFilter;
  return { pages, transactions };
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({ fetchTransactions }, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(TransactionList);
