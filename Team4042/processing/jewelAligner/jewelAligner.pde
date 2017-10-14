PImage img;
int dist = 31;

void setup() {
  //size(1280, 720);
  size(300, 200);
  String imgString = Integer.toString(dist) + ".jpg";
  img = loadImage(imgString);
  noLoop();
}

void draw() {
  //println(distToPos(dist));
  //PImage temp = img.get(0, 0, img.width, img
  //float[] tapeX = ezFindTape(img, dist);
  //image(img, 0, 0);
  //line(tapeX[0], 0, tapeX[0], height - 1);
  //line(tapes[0], 0, tapes[0], height - 1);
  //line(tapes[1], 0, tapes[1], height - 1);
  //line(0, distToPos(dist + fudge), width - 1, distToPos(dist + fudge));
  //JewelColor[] jewelColors = ezGetJewelColors(img, tapeX[0], dist + tapeX[1]);
  //println(jewelColorToString(jewelColors[0]) + ", " + jewelColorToString(jewelColors[1]));
  
  float start = millis();
  JewelColor[] jewelColors = sezGetJewelColors(img, dist);
  println(jewelColorToString(jewelColors[0]) + ", " + jewelColorToString(jewelColors[1]));
  println(millis() - start);
}