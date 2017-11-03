void displayImage(PImage img, int getX, int getY, int zoom) {
  PImage temp = img.get(getX, getY, img.width/zoom, img.width/zoom * height/width);
  temp.resize(width, 0);
  rect(0,0,width-1,height-1);
  image(temp, 0, 0);
}

int intClamp(int val, int min, int max) {
  return Math.max(min, Math.min(max, val));
}
//int mouseToImgSpace(