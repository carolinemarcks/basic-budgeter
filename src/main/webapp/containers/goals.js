import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { createGoal } from '../actions/index';
import Allocations from '../components/allocations';
import _ from 'lodash';

class Goals extends Component {
  render() {
    return (<Allocations
      fields={['name', 'saved', 'weight', 'cap']}
      create={this.props.createGoal}
      allocations={this.props.goals}/>);
  }
}

function mapStateToProps({ goals }){
  return { goals };
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({ createGoal }, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(Goals);
