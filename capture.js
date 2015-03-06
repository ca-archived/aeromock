var casper = require('casper').create();

casper.start('http://localhost:3183/test', function(){
    this.capture('target/capture.png');
});

casper.run();
