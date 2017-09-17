/*
PImage a;
PImage b;
int n = 0;
*/

/*
    if(n == 0) {
      a = cam;
      n++;
    } else {
      b = cam;
      a.filter(GRAY);
      b.filter(GRAY);
      a.loadPixels();
      b.loadPixels();
      for(int y = 0; y < a.height; y++) {
        for(int x = 0; x < a.width; x++) {
          int pos = a.width * y + x;
          float avalue = (float) (int) red(a.pixels[pos]);
          float bvalue = (float) (int) red(b.pixels[pos]);
          float value = ((n + 1) * avalue + bvalue) / (n + 2);
          a.pixels[pos] = color(value, value, value);
        }
      }
      a.updatePixels();
      b.updatePixels();
      n++;
    }
  }
  if(n > 20) {
    image(a, 0, 0);
    image(b, width / 2, 0);
    n = 0;
  }
  println(n);
  */