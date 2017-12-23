float mass = 10;
float pos = 200;
float vel = 0;
float g = 4;
float targ = 500;
float f = .4;
float f2 = 75;
float l = 0;
float l2 = 0;
float p = 0;
float muppet = .25;

void setup() {
  size(640, 480);
  stroke(0);
  fill(63);
}

void draw() {
  background(127);
  if(millis() - l2 > muppet * 1000) {
    forces(true);
    l2 = millis();
  } else {
    forces(false);
  }
  disp();
}

void disp() {
  line(100, 200, 600, 200);
  line(targ, 150, targ, 250);
  ellipse(pos, 200, 20, 20);
  text(power(), 10, 10);
  text(pos, 10, 20);
  text(muppet, 10, 30);
}

void forces(boolean b) {
  float t = (millis() - l) / 1000;
  vel += g*t;
  if(b) p = power();
  vel += t*p*80 / mass;
  vel -= vel > 0 ? t*f*pow(.95,abs(vel)) : -t*f*pow(.95,abs(vel));
  vel -= vel > 0 ? vel*vel/f2 : -vel*vel/f2;
  pos += vel;
  l = millis();
}

float power() {
  return constrain((targ - pos)/50, -1, 1);
  //return mouseX/100.0-2;
}