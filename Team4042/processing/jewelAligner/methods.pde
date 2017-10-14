//8001.3/(d - 1.880) + 301.5 = on screen pixel pos from top
int distToPos(float d) {
  return (int) (FUNCTION_A_A / (d - FUNCTION_A_B) + FUNCTION_A_C);
}
int distToLength(float d) {
  return (int) (FUNCTION_B_A / (d - FUNCTION_B_B) + FUNCTION_B_C);
}
int distToBallY(float d) {
  return (int) (FUNCTION_C_A / (d - FUNCTION_C_B) + FUNCTION_C_C);
}
int distToBallXOffset(float d) {
  return (int) (FUNCTION_D_A / (d - FUNCTION_D_B) + FUNCTION_D_C);
}

public enum JewelColor {
  red, blue, uncertain
}

JewelColor[] sezGetJewelColors(PImage img, float d) {
  float[] temp = ezFindTape(img, d);
  JewelColor[] jewelColors = ezGetJewelColors(img, temp[0], d + temp[1]);
  return jewelColors;
}

float[] ezFindTape(PImage img, float d) {
  float[][] skernel = kernelScale(KERNEL, KERNEL_SCALER);
  PImage temp = img.get(); //copies to a temporary image because findTape filters to grey
  float[] tapes = {0, 0};
  int fudge = 0;
  float[] result = new float[2];
  for(fudge = 0; fudge <= FUDGE_AMOUNT; fudge++) {
    tapes = findTape(d + fudge, temp, skernel);
    if(tapes[0] != 0 || tapes[1] != 0) {
      break;
    }
    tapes = findTape(d - fudge, temp, skernel);
    if(tapes[0] != 0 || tapes[1] != 0) {
      fudge = -1 * fudge;
      break;
    }
  }
  result[0] = (tapes[0] + tapes[1]) / 2;
  result[1] = fudge;
  return result;
}

JewelColor[] ezGetJewelColors(PImage img, float tc, float d) {
  return getJewelColors(img, tc, d, SEARCH_RADIUS, COLOR_CONTRAST_FACTOR);
}

String jewelColorToString(JewelColor in) {
  switch (in) {
    case blue: return "blue";
    case red: return "red";
    case uncertain: return "uncertain";
    default: return "invalid input";
  }
}

JewelColor[] getJewelColors(PImage img, float tc /* tape center */, float d /* distance from wall */, int sr /* search radius */, double acc) {
  JewelColor[] result = {JewelColor.uncertain, JewelColor.uncertain};
  double lrsum = 0;
  double lbsum = 0;
  double rrsum = 0;
  double rbsum = 0;
  int off = distToBallXOffset(d);
  int ycenter = distToBallY(d);
  int itc = (int) (tc);
  img.loadPixels();
  for(int x = itc - off - sr; x <= itc - off + sr; x++) {
    for(int y = ycenter - sr; y <= ycenter + sr; y++) {
      lrsum += red(img.pixels[y * img.width + x]);
      lbsum += blue(img.pixels[y * img.width + x]);
    }
  }
  //rect(itc - off - sr, ycenter - sr, sr * 2, sr * 2);
  for(int x = itc + off - sr; x <= itc + off + sr; x++) {
    for(int y = ycenter - sr; y <= ycenter + sr; y++) {
      rrsum += red(img.pixels[y * img.width + x]);
      rbsum += blue(img.pixels[y * img.width + x]);
    }
  }
  if(lrsum / lbsum > acc) {
    result[0] = JewelColor.red;
  }
  if(lbsum / lrsum > acc) {
    result[0] = JewelColor.blue;
  }
  if(rrsum / rbsum > acc) {
    result[1] = JewelColor.red;
  }
  if(rbsum / rrsum > acc) {
    result[1] = JewelColor.blue;
  }
  return result;
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
float[] findTape(float d, PImage in, float[][] kernel) {
  int yin = distToPos(d);
  float[] result = new float[2];
  float[][] kk = new float[in.width][in.height];
  in.filter(GRAY);
  in.loadPixels(); //opens pixels for use
  //iterates through image applying convolution
  //skips outside to prevent out of bounds error
  for (int y = yin - 2; y <= yin + 2; y++) {
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
      kk[x][y - yin + 2] = sum;
    }
  }
  in.updatePixels();
  int[] maybe = new int[img.width];
  int maybeI = 0;
  for(int x = 0; x < img.width; x++) {
    float sum = 0;
    for(int y = 0; y < 5; y++) {
      sum += kk[x][y];
    }
    if(sum > TAPE_THRESHOLD) {
      maybe[maybeI] = x;
      maybeI++;
    }
  }
  
  int a = 0;
  int b = 0;
  int len = distToLength(d);
  
  int count = 0;
  for(int i = 0; i <= maybeI; i++) {
    for(int j = i + 1; j <= maybeI; j++) {
      int dist = abs(maybe[i] - maybe[j]); 
      if(dist > len - TAPE_LENGTH_TOLERANCE && dist < len + TAPE_LENGTH_TOLERANCE) {
        a = maybe[i];
        b = maybe[j];
        count++;
      }
    }
  }
  //println(count);
  result[0] = a;
  result[1] = b;
  return result;
}

PImage arrToImg(float[][] in) {
  PImage result = createImage(img.width, 5, RGB);
  result.loadPixels();
  for(int y = 0; y < 5; y++) {
    for(int x = 0; x < img.width; x++) {
      result.pixels[y * img.width + x] = color(in[x][y], in[x][y], in[x][y]);
    }
  }
  return result;
}