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

    const goalAlloc = _.reduce(this.props.history, function(sum, d) {
      const { month, spent, earned } = d
      return earned + sum;
    }, 0) / this.props.history.length - nextAlloc;


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
            <td>${this.formatCentsToMoney(goalAlloc)}</td>
          </tr>
        </tbody>
      </table>);
  }
}

function mapStateToProps({ budgets, history }){
  return { budgets, history };
}

export default connect(mapStateToProps)(Predictions);
