import React, { Component } from 'react';
import _ from 'lodash';
import { LineChart, Line, XAxis, YAxis, Tooltip, ReferenceLine } from 'recharts';

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
      <td>{this.renderChart(allocation)}</td>
    </tr>);
  }
  renderChart(allocation) {
    const monthNames = ["Jan", "Feb ", "Mar", "Apr", "May", "Jun",
      "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    ];
    const data = allocation.history.map(({ month, net }) => {
      return {
        month: monthNames[month - 1],
        net: net / 100
      }
    });
    const avg = _.reduce(data, function(sum, d) {
      return sum + d.net;
    }, 0) / data.length;
    return (
      <LineChart width={300} height={200} data={data}
        margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
        <XAxis dataKey="month" />
        <YAxis />
        <Tooltip />
        <ReferenceLine y={avg} stroke="#404E4D" strokeDasharray="3 3" />
        <Line type="monotone" dataKey="net" stroke="#20A4F3" />
      </LineChart>);
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
