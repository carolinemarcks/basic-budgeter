import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { createBudget } from '../actions/index';
import Allocations from '../components/allocations';
import _ from 'lodash';

class Budgets extends Component {
  render() {
    return (<Allocations
      fields={['name', 'saved','amount','cap']}
      create={this.props.createBudget}
      allocations={this.props.budgets}/>);
  }
}

function mapStateToProps({ budgets }){
  return { budgets };
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({ createBudget }, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(Budgets);
