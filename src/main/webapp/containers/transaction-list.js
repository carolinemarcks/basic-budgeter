import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import TransactionRow from './transaction-row';
import { fetchTransactions } from '../actions/index';
import _ from 'lodash';

class TransactionList extends Component {
  constructor(props) {
    super(props);
    this.allocationFilter = this.allocationFilter.bind(this);
    this.payeeFilter = this.payeeFilter.bind(this);
    this.updatePayeeFilterSearch = this.updatePayeeFilterSearch.bind(this);
    this.state = { payeeFilterSearch: this.props.filters.payeeFilter };
  }
  onPage(page) {
    this.props.fetchTransactions(page, this.props.filters);
  }
  renderButton(page, text) {
    const cantPage = page === undefined;
    return (<button
      type="button"
      className="btn btn-secondary btn-sm"
      onClick={() => this.onPage(page)}
      disabled={cantPage}>{text}</button>);
  }
  renderAllocation(allocation) {
    return <option key={allocation.id} value={allocation.id}>{allocation.name}</option>;
  }
  allocationFilter(event) {
    const curFilter = _.cloneDeep(this.props.filters);
    const newFilter = _.assign(curFilter, { allocationFilter: event.target.value });

    this.props.fetchTransactions('', newFilter);
  }

  payeeFilter(event) {
    event.preventDefault();
    const curFilter = _.cloneDeep(this.props.filters);
    const newFilter = _.assign(curFilter, { payeeFilter: this.state.payeeFilterSearch });

    this.props.fetchTransactions('', newFilter);
  }

  updatePayeeFilterSearch(event) {
    this.setState({ payeeFilterSearch: event.target.value });
  }
  render() {
    const alloc = this.props.filters.allocationFilter;
    const allocationFilterValue = alloc ? alloc : '';
    return (
        <table className="table table-hover">
          <thead>
            <tr>
              <th>Posted Date</th>
              <th><form onSubmit={this.payeeFilter}><input placeholder="Payee" value={this.state.payeeFilterSearch} onChange={this.updatePayeeFilterSearch}></input></form></th>
              <th>Amount</th>
              <th>Note</th>
              <th>
                <select className="form-control" value={allocationFilterValue} onChange={this.allocationFilter}>
                  {this.renderAllocation({id: '', name: "All Allocations"})}
                  <optgroup label="Budgets">
                    {this.props.budgets.map(this.renderAllocation)}
                  </optgroup>
                  <optgroup label="Goals">
                    {this.props.goals.map(this.renderAllocation)}
                  </optgroup>
                </select>
              </th>
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

function mapStateToProps({ transactions, transactionsFilter, goals, budgets }){
  const { pages, filters } = transactionsFilter;
  return { pages, filters, transactions, goals, budgets };
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({ fetchTransactions }, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(TransactionList);
