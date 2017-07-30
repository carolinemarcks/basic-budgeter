import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import TransactionList from './transaction-list';
import Budgets from './budgets';
import Goals from './goals';
import Home from './home';
import { fetchTransactions, fetchGoals, fetchBudgets } from '../actions';
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';

class App extends Component {
  componentDidMount() {
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
            <Tab>Home</Tab>
            <Tab>Transactions</Tab>
            <Tab>Budgets</Tab>
            <Tab>Goals</Tab>
          </TabList>
          <TabPanel>
            <Home />
          </TabPanel>
          <TabPanel>
            <TransactionList />
          </TabPanel>
          <TabPanel>
            <Budgets />
          </TabPanel>
          <TabPanel>
            <Goals />
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
