import React, { Component } from 'react';
import _ from 'lodash';

export default class Allocations extends Component {
  constructor(props) {
    super(props);
    this.state = this.blankState();
    this.onChange = this.onChange.bind(this);
    this.renderField = this.renderField.bind(this);
    this.renderRow = this.renderRow.bind(this);
  }
  blankState() {
    const state = {};
    _.forEach(this.props.fields, function(fieldName) {
      state[fieldName] = '';
    })
    return state;
  }
  onChange(field, event) {
    const stateUpdate = {};
    stateUpdate[field] = event.target.value;

    this.setState(stateUpdate);
  }

  create() {
    this.props.create(this.state);
    this.setState(this.blankState());
  }
  renderRow(allocation) {
    return (<tr key={allocation.id}>
      {this.props.fields.map((f) => {return <td key={f}>{allocation[f]}</td>})}
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
    //TODO headers
    return (
      <table className="table table-hover">
        <thead>
          <tr>
            {this.props.fields.map((f) => {return <th key={f}>{f}</th>})}
            <th></th>
          </tr>
        </thead>
        <tbody>
          {this.props.allocations.map(this.renderRow)}
          <tr>
            {this.props.fields.map(this.renderField)}
            <td>
              <button
                className="btn btn-primary"
                onClick={() => this.create()}>
                Create</button>
            </td>
          </tr>
        </tbody>
      </table>);
  }
}
