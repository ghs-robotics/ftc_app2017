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
float muppet = .01;
float m = 0;
int bc = 127;

void setup() {
  size(640, 480);
  stroke(0);
  fill(63);
  frameRate(25);
}

void draw() {
  m = frameCount * 40;
  background(bc);
  if(m - l2 > muppet * 1000) {
    forces(true);
    l2 = m;
  } else {
    forces(false);
  }
  disp();
  if(frameCount < 500) {
    //saveFrame("####.png");
  } else {
    bc = 255;
  }
}

void disp() {
  line(100, 200, 600, 200);
  line(targ, 150, targ, 250);
  ellipse(pos, 200, 20, 20);
  text(power(), 10, 10);
  text(pos, 10, 20);
  text(muppet, 10, 30);
  text(frameCount, 10, 40);
}

void forces(boolean b) {
  float t = (m - l) / 1000;
  vel += g*t;
  if(b) p = power();
  vel += t*p*80 / mass;
  vel -= vel > 0 ? t*f*pow(.95,abs(vel)) : -t*f*pow(.95,abs(vel));
  vel -= vel > 0 ? vel*vel/f2 : -vel*vel/f2;
  pos += vel;
  l = m;
}

float power() {
  return constrain((targ - pos)/50, -1, 1);
  //return mouseX/100.0-2;
}