import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { fetchHistory } from '../actions';
import _ from 'lodash';

class History extends Component {
  componentDidMount() {
    this.props.fetchHistory();
  }
  render() {
    return (<p>{JSON.stringify(this.props.history)}</p>);
  }
}

function mapStateToProps({ history }){
  return { history };
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({ fetchHistory }, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(History);
