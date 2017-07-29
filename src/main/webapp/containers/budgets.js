import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { createBudget } from '../actions/index';
import _ from 'lodash';

class Budgets extends Component {
  constructor(props) {
    super(props);
    this.state = this.blankState();
    this.onChange = this.onChange.bind(this);
    this.renderField = this.renderField.bind(this);
    this.renderRow = this.renderRow.bind(this);
  }
  blankState() {
    const state = { //TODO do field management with props
      fields: ['name', 'saved', 'amount', 'cap']
    };

    _.forEach(state.fields, function(fieldName) {
      state[fieldName] = '';
    })
    return state;
  }
  onChange(field, event) {
    const stateUpdate = {};
    stateUpdate[field] = event.target.value;

    this.setState(stateUpdate);
  }

  createBudget() {
    this.props.createBudget(this.state);
    this.setState(this.blankState());
  }
  renderRow(budget) {
    return (<tr key={budget.id}>
      {this.state.fields.map((f) => {return <td key={f}>{budget[f]}</td>})}
      <td></td>
    </tr>);
  }
  renderField(fieldName) {
    return (<td key={fieldName}><input
      placeholder={fieldName}
      value={this.state[fieldName]}
      onChange={(event) => this.onChange(fieldName, event)}>
      </input></td>);
  }
  render() {
    return (
      <table className="table table-hover">
        <thead>
          <tr>
            <th>Name</th>
            <th>Saved</th>
            <th>Contribution</th>
            <th>Cap</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {this.props.budgets.map(this.renderRow)}
          <tr>
            {this.state.fields.map(this.renderField)}
            <td>
              <button
                className="btn btn-primary"
                onClick={() => this.createBudget()}>
                Create</button>
            </td>
          </tr>
        </tbody>
      </table>);
  }
}

function mapStateToProps({ budgets }){
  return { budgets };
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({ createBudget }, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(Budgets);
