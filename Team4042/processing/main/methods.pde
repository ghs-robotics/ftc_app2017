//runs a kernel convolution on a PImage and returns float
float[][] kcvFloat(PImage in, float[][] kernel) {
  float[][] result = new float[in.width][in.height];
  in.filter(GRAY);
  in.loadPixels(); //opens pixels for use
  //iterates through image applying convolution
  //skips outside to prevent out of bounds error
  for (int y = 1; y < in.height - 1; y++) {
    for (int x = 1; x < in.width - 1; x++) {
      float sum = 0;
      for (int ky = -1; ky <= 1; ky++) {
        for (int kx = -1; kx <= 1; kx++) {
          int pos = (y + ky) * in.width + (x + kx);
          float val = red(in.pixels[pos]); //channel read arbitrary, image greyscale
          sum += kernel[ky+1][kx+1] * val;
        }
      }
      sum = abs(sum); //detects edges going either way
      result[x][y] = sum;
    }
  }
  in.updatePixels();
  return result;
}

//sums columns from 2D float array
float[] sumcFloat(float[][] in) {
  float[] result = new float[in.length];
  for(int x = 1; x < in.length - 1; x++) {
    float sum = 0;
    for(int y = 1; y < in[0].length - 1; y++) {
      sum += in[x][y];
    }
    if(sum > COLUMN_SUM_THRESHOLD) result[x] = sum; else result[x] = 0;
  }
  return result;
}

//filters columns based off of sums
boolean[] fcFloat(float[] in) {
  boolean[] result = new boolean[in.length];
  for(int i = 1; i < in.length - 1; i++) {
    result[i] = in[i] > COLUMN_SUM_THRESHOLD;
  }
  return result;
}

//converts a 2D float array to a PImage 
PImage ftpi(float[][] in) {
  PImage result = createImage(in.length, in[0].length, RGB);
  result.loadPixels();
  for(int x = 1; x < in.length - 1; x++) {
    for(int y = 1; y < in[0].length - 1; y++) {
      result.pixels[y * in.length + x] = color(in[x][y], in[x][y], in[x][y]);
    }
  }
  result.updatePixels();
  return result;
}

//scales 2D array
float[][] scarr(float[][] in, float scaler) {
  float[][] result = new float[3][3];
  for(int x = 0; x < 3; x++) {
    for(int y = 0; y < 3; y++) {
      result[x][y] = in[x][y] * scaler;
    }
  }
  return result;
}

//draws camera, horizontal kernel convolution result, and maybe edges
void drawResults(PImage cam, float[][] conv, boolean[] edges) {
  image(cam, 0, 0);
  image(ftpi(conv), width / 2, 0);
  stroke(0x8000FF00);
  for(int i = 1; i < edges.length - 1; i++) {
    if(edges[i]) {
      line(i, 0, i, height - 1);
      line(i + width / 2, 0, i + width / 2, height - 1);
    }
  }
}