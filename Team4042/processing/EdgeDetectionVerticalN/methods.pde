float[][] kcvFloat(PImage in, float[][] kernel) {
  float[][] result = new float[in.width][in.height];
  in.filter(GRAY);
  in.loadPixels(); //opens pixels for use
  //iterates through image applying convolution
  //skips outside to prevent out of bounds error
  for (int y = 1; y <= in.height - 1; y++) {
    for (int x = 1; x <= in.width - 1; x++) {
      float sum = 0;
      for (int ky = -1; ky <= 1; ky++) {
        for (int kx = -1; kx <= 1; kx++) {
          int pos = (y + ky) * in.width + (x + kx);
          float val = red(in.pixels[pos]); //channel read arbitrary, image greyscale
          sum += kernel[ky+1][kx+1] * val;
        }
      }
      sum = abs(sum);
      result[x][y] = sum;
    }
  }
  return result;
}