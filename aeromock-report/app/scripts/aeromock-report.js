'use strict';
// var lib = require('./components/components');
(function(scope){
    var polymer = scope.Polymer('aeromock-report', {
      /**
       * The `noevent` event is not actually fired from here,
       * we document it as an example of documenting events.
       *
       * @event noevent
       */

      /**
       * The `notitle` attribute does not yet have a purpose.
       *
       * @attribute notitle
       * @type string
       */
      notitle: '',

      /**
       * The `aProp` is a property that does something cool.
       *
       * @property aProp
       * @type bool
       */
      aProp: false,

      ready: function() {
      },

      /**
       * The `task` method does no work at this time.
       *
       * @method task
       * @return {Object} Returns undefined.
       * @param {String} dummy Serves no purpose today.
       */
      task: function(dummy) {
        return dummy;
      }

    });

    return polymer;
})(window);
