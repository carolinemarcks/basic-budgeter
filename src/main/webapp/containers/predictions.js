import React, { Component } from 'react';
import { connect } from 'react-redux';
import _ from 'lodash';

class Predictions extends Component {
  formatCentsToMoney(m) {
    return (m/100).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
  }
  render() {
    const maxAlloc = _.reduce(this.props.budgets, function(sum, d) {
      return sum + d.amount;
    }, 0);
    const nextAlloc = _.reduce(this.props.budgets, function(sum, d) {
      if (d.cap) {
        return sum + Math.min(d.cap - d.saved, d.amount);
      } else {
        return sum + d.amount;
      }
    }, 0);

    const totalGoalAlloc = _.reduce(this.props.history, function(sum, d) {
      return d.earned + sum;
    }, 0) / this.props.history.length - nextAlloc;

    const goalStones = _.reduce(this.props.goals, function(sum, d) {
      return d.weight + sum;
    }, 0);

    const goalAllocs = _.reduce(this.props.goals, function(accum, goal) {
      const { name, weight, cap, saved } = goal;
      if (weight === 0 || saved >= cap) {
        return accum;
      } else {
        const { stonesLeft, amountLeft, deltas } = accum;
        const toAdd = cap ? Math.min(amountLeft * weight / stonesLeft, cap - saved) : amountLeft * weight / stonesLeft;

        return {
          stonesLeft : stonesLeft - weight,
          amountLeft : amountLeft - toAdd,
          deltas: [...deltas, { name, toAdd }]
        };
      }
    },{stonesLeft: goalStones, amountLeft: totalGoalAlloc, deltas: []}).deltas.map(({ name, toAdd }) => {
        return (<tr key={name}>
          <td>{name}</td>
          <td>${this.formatCentsToMoney(toAdd)}</td>
        </tr>);
    });


    return (
      <table className="table">
        <tbody>
          <tr>
            <td>Max amount allocted each month:</td>
            <td>${this.formatCentsToMoney(maxAlloc)}</td>
          </tr>
          <tr>
            <td>Predicted allocation next month:</td>
            <td>${this.formatCentsToMoney(nextAlloc)}</td>
          </tr>
          <tr>
            <td>Predicted $ towards goals:</td>
            <td>${this.formatCentsToMoney(totalGoalAlloc)}</td>
          </tr>
          {goalAllocs}
        </tbody>
      </table>);
  }
}

function mapStateToProps({ budgets, history, goals }){
  return { budgets, history, goals };
}

export default connect(mapStateToProps)(Predictions);
