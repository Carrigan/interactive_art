var ws;
var transactions = [];
var oscillators = [];
var scale_notes = [];
var sketchHeight = 600;
var sketchWidth = 710;

function add_transaction(amount, duration) {
  transactions.push({
    start: millis(),
    duration: duration * 1000,
    x: random(sketchHeight),
    y: random(sketchWidth),
    amount: amount.toFixed(2)
  });
}

function change_chord(intervals, start_note, octaves) {
  scale_notes = [];
  for(var octave_i = 0; octave_i < octaves; octave_i++) {
    for(var note_i = 0; note_i < intervals.length; note_i++) {
      scale_notes.push(start_note + octave_i * 12 + intervals[note_i]);
    }
  }
}

function play_btc(amount) {
  var log_amt = Math.log(amount);
  var max_v = Math.log(20000);
  var min_v = 0;

  // Make higher amounts map to lower pitched notes
  note = Math.round(map(log_amt, min_v, max_v, scale_notes.length - 1, 0));
  if (note < 0) note = 0;

  // Make higher amounts map to longer, louder notes
  var scaled = map(log_amt, min_v, max_v, 0.2, 1.0);
  if(scaled > 1.0) scaled = 1.0;

  // Play the note, returning the duration
  return play_note(scale_notes[note], 0.2 * scaled, 0, 2 * scaled, scaled);
}

function play_note(midi_key, a, s, r, v) {
  // Use a free oscillator to play the note
  var osc_obj = free_oscillators()[0];
  if(!osc_obj) return;

  // Set the in_use flag until the note is finished
  var osc = osc_obj.osc;
  osc_obj.in_use = true;
  setTimeout(function() { osc_obj.in_use = false; }, (a + s + r) * 1000);

  // Create an envelope
  var env = new p5.Env();
  env.setADSR(a, s, 1.0, r);
  env.setRange(v, 0.0);

  // Play the note
  osc.freq(midiToFreq(midi_key));
  osc.amp(env);
  env.play();

  // Return the duration
  return a + s + r;
}

function free_oscillators() {
  // Create an array of all free oscillators
  var freed = [];
  for(var i = 0; i < oscillators.length; i++) {
    osc = oscillators[i];
    if(osc.in_use == false) freed.push(osc);
  }
  return freed;
}

function initialize_oscillators(count) {
  for(var i = 0; i < count; i++) {
    osc = new p5.Oscillator();
    osc.setType('sine');
    osc.amp(0);
    osc.start();

    oscillators.push({ osc: osc, in_use: false });
  }
}

function preload() {
  ws = new WebSocket("wss://ws.blockchain.info/inv");

  ws.onopen = function(event) {
    ws.send('{"op": "unconfirmed_sub"}');
  };

  ws.onmessage = function(event) {
    var data = JSON.parse(event.data);
    var outputs = data.x.out;
    var amount = (outputs[outputs.length - 1].value / 100000000) * 450;
    var duration = play_btc(amount);

    add_transaction(amount, duration);
  };
}

function setup() {
  // Create the canvas
  var canvas = createCanvas(sketchWidth, sketchHeight);
  canvas.parent('slide-p5demo');
  textSize(32);

  // Start the oscillators
  initialize_oscillators(30);

  // Initialize the chord to a m7
  change_chord([0, 3, 7, 10], 42, 3);
}

function draw() {
  background(50);

  var next_transactions = [];
  for(var i = 0; i < transactions.length; i++) {
    var transaction = transactions[i];
    var end_time = transaction.start + transaction.duration;
    if(millis() < end_time) {
      var color = map(millis() - transaction.start, 0, transaction.duration, 250, 50);
      fill(color);
      text(transaction.amount, transaction.x, transaction.y);

      next_transactions.push(transaction);
    }
  }

  transactions = next_transactions;
}

/**
var x = [],
  y = [],
  segNum = 20,
  segLength = 18;

for (var i = 0; i < segNum; i++) {
  x[i] = 0;
  y[i] = 0;
}

function setup() {
  var canvas = createCanvas(740, 600);
  canvas.parent('slide-p5demo');

  strokeWeight(9);
  stroke(255, 100);
}

function draw() {
  background(0);
  dragSegment(0, mouseX, mouseY);
  for( var i=0; i<x.length-1; i++) {
    dragSegment(i+1, x[i], y[i]);
  }
}

function dragSegment(i, xin, yin) {
  var dx = xin - x[i];
  var dy = yin - y[i];
  var angle = atan2(dy, dx);
  x[i] = xin - cos(angle) * segLength;
  y[i] = yin - sin(angle) * segLength;
  segment(x[i], y[i], angle);
}

function segment(x, y, a) {
  push();
  translate(x, y);
  rotate(a);
  line(0, 0, segLength, 0);
  pop();
}
*/
