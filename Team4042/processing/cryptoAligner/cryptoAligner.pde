PShader t;
PShader u;
PImage i;
PGraphics g;
boolean e;
void setup() {
  size(910,512,P2D);
  i=loadImage("cryptobox.jpg");
  t=loadShader("colfil.glsl");
  u=loadShader("edges.glsl");
  i.resize(width, 0);
  g=createGraphics(910,512,P2D);
  //frameRate(2);
}
void draw() {
  //t.set("vl", (float)mouseX/width);
  //t.set("vu", (float)mouseY/height);
  //println((float)mouseX/width,(float)mouseY/height);
  g.beginDraw();
  g.background(0);
  g.shader(t);
  g.image(i,0,0);
  g.endDraw();
  shader(u);
  /*
  if(e) shader(u);
  else resetShader();
  e = !e;
  */
  
  image(g, 0, 0);
}