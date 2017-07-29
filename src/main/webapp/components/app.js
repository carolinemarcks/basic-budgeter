import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import TransactionList from '../containers/transaction-list';
import Budgets from '../containers/budgets';
import { fetchTransactions, fetchGoals, fetchBudgets } from '../actions';
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';

class App extends Component {
  componentDidMount() {
    //TODO eventually move these into their respective containers
    this.props.fetchTransactions();
    this.props.fetchBudgets();
    this.props.fetchGoals();
  }
  render() {
    return (
      <div>
        <h1>Budgeter</h1>
        <Tabs>
          <TabList>
            <Tab>Transactions</Tab>
            <Tab>Budgets</Tab>
          </TabList>
          <TabPanel>
            <TransactionList />
          </TabPanel>
          <TabPanel>
            <Budgets />
          </TabPanel>
        </Tabs>
      </div>
    );
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({ fetchTransactions, fetchGoals, fetchBudgets }, dispatch);
}

export default connect(null, mapDispatchToProps)(App);
