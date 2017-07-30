import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import moment from 'moment';
import { allocate } from '../actions';
import _ from 'lodash';

class TransactionRow extends Component {
  constructor(props) {
    super(props);
    this.handleSelect = this.handleSelect.bind(this);
  }

  handleSelect(event) {
    this.props.allocate(this.props.data.id, parseInt(event.target.value));
  }

  renderAllocation(allocation) {
    return <option key={allocation.id} value={allocation.id}>{allocation.name}</option>;
  }

  renderAllocations() {
    return (<select className="form-control" value={this.props.data.allocationId} onChange={this.handleSelect}>
      <optgroup label="Budgets">
        {this.props.budgets.map(this.renderAllocation)}
      </optgroup>
      <optgroup label="Goals">
        {this.props.goals.map(this.renderAllocation)}
      </optgroup>
    </select>);
  }

  render() {
    const transaction = this.props.data;

    return (
      <tr key={transaction.id}>
        <td>{moment(transaction.postedDate).format('L')}</td>
        <td>{transaction.payee}</td>
        <td>${(transaction.amount/100).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,')}</td>
        <td>{transaction.note}</td>
        <td colSpan={2}>{this.renderAllocations()}</td>
      </tr>
    );
  }
}

function mapStateToProps({ goals, budgets }){
  return { goals, budgets };
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({ allocate }, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(TransactionRow);
