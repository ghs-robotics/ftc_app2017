PImage img;
int dist = 25;

void setup() {
  size(1280, 720);
  String imgString = Integer.toString(dist) + ".jpg";
  img = loadImage(imgString);
  kernelScale(kernel, KERNEL_SCALER);
  noLoop();
}

void draw() {
  println(distToPos(dist));
  image(img, 0, 0);
  image(arrToImg(findTape(distToPos(dist), img, kernel)),0,distToPos(dist));
}