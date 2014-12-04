/** @jsx React.DOM */

(function () {
    var React = require('react');

    window.React = React;

    React.renderComponent(
        <div>Hello</div>,
        document.getElementById('app')
    );
})();