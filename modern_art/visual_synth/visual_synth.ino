void setup() {
  Serial.begin(115200);

  while(Serial.available() == 0);
}

void loop() {
  Serial.write(100);
  Serial.write(0);
  Serial.write(100);
  Serial.write(0);
  Serial.write(0);
}
