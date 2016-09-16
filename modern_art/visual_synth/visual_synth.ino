void setup() {
  pinMode(8, INPUT);
  Serial.begin(115200);
  while(Serial.available() == 0);
}

int potentiometerReading(int pin) {
  int reading = analogRead(pin);
  reading = reading >> 2;

  if(reading == 255)
    return 254;

  return reading;
}

void loop() {
  static bool was_pressed = false;
  bool pressed = (digitalRead(8) == LOW);
  
  // Delimiter
  Serial.write(255);

  // HSL
  Serial.write(potentiometerReading(0));
  Serial.write(potentiometerReading(1));
  Serial.write(potentiometerReading(2));

  // Buttons
  uint8_t buttons = 0;
  if(!was_pressed && pressed) {
    buttons = buttons | (1 << 0);
  }

  Serial.write(buttons);

  // Dial
  Serial.write(0);

  // Slight delay
  was_pressed = pressed;
  delay(50);
}
