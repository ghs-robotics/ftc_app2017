//8001.3/(d - 1.880) + 301.5 = on screen pixel pos from top
int distToPos(float d) {
  return (int) (FUNCTION_A / (d - FUNCTION_B) + FUNCTION_C);
}

float[][] kernelScale(float[][] kernel, float scale) {
  for(int y = 0; y < 3; y++) {
    for(int x = 0; x < 3; x++) {
      kernel[x][y] = kernel[x][y] * scale;
    }
  }
  return kernel;
}

//yin is y for tape vertically, position from top
float[][] findTape(int yin, PImage img, float[][] kernel) {
  float[] result = new float[2];
  
  if(yin < 3 || yin > img.height - 3) println("y is out of bounds");
  img.loadPixels();
  float[][] kk = new float[img.width][5];
  for(int y = yin - 2; y <= yin + 2; y++) {
    for(int x = 2; x < img.width - 2; x++) {
      int sum = 0;
      for(int xo = -1; xo <= 1; xo++) {
        for(int yo = -1; yo <= 1; yo++) {
          sum += kernel[xo + 1][yo + 1] * img.pixels[(y + yo) * img.width + x + xo];
        }
      }
      kk[x][y - yin + 2] = abs(sum);
    }
  }
  
  for(int x = 0; x < img.width; x++) {
    for(int y = 0; y < 5; y++) {
      
    }
  }
  
  return kk;
}

PImage arrToImg(float[][] in) {
  PImage result = createImage(img.width, 5, RGB);
  result.loadPixels();
  for(int y = 0; y < 5; y++) {
    for(int x = 0; x < img.width; x++) {
      result.pixels[y * img.width + x] = color(in[x][y], in[x][y], in[x][y]);
    }
  }
  for(int n = 0; n < 500; n++) {
  println(in[n][0]);
  }
  return result;
}