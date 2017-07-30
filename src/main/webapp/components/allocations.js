import React, { Component } from 'react';
import _ from 'lodash';
import { ResponsiveContainer, LineChart, Line, XAxis, YAxis, Tooltip, ReferenceLine } from 'recharts';

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
    return (<ResponsiveContainer width="100%" height={100}>
      <LineChart data={data}>
        <XAxis dataKey="month" />
        <YAxis />
        <Tooltip />
        <ReferenceLine y={avg} stroke="#404E4D" strokeDasharray="3 3" />
        <Line type="monotone" dataKey="net" stroke="#20A4F3" />
      </LineChart>
    </ResponsiveContainer>);
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
      <table className="table table-hover allocations">
        <thead>
          <tr>
            {this.props.fields.map((f) => {return <th className={f} key={f}>{f}</th>})}
            <th className="graph"></th>
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
