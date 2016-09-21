void setup() {
  pinMode(8, INPUT);
  Serial.begin(115200);
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
  static uint8_t cooldown = 0;
  
  // Delimiter
  Serial.write(255);

  // HSL
  Serial.write(potentiometerReading(3));
  Serial.write(potentiometerReading(4));
  Serial.write(potentiometerReading(5));

  // Buttons
  uint8_t buttons = 0;
  if(!was_pressed && pressed && cooldown == 0) {
    buttons = buttons | (1 << 0);
    cooldown = 20;
  }

  Serial.write(buttons);

  // Dial
  Serial.write(0);

  // Advance button state
  if(cooldown) cooldown -= 1;
  was_pressed = pressed;

  // Slight Delay
  delay(50);
}
