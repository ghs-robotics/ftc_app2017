//8001.3/(d - 1.880) + 301.5 = on screen pixel pos from top

//these all use distance from wall as input (in inches) to calculate an estimated value for...
//    \/
//vertical position of tape in pixels from top
int distToPos(float d) {
  return (int) (FUNCTION_A_A / (d - FUNCTION_A_B) + FUNCTION_A_C);
}
//horizontal length of tape in pixels
int distToLength(float d) {
  return (int) (FUNCTION_B_A / (d - FUNCTION_B_B) + FUNCTION_B_C);
}
//y position of ball centers in pixels from top
int distToBallY(float d) {
  return (int) (FUNCTION_C_A / (d - FUNCTION_C_B) + FUNCTION_C_C);
}
//x offset from the middle of the tape to the middle of the balls in pixels
int distToBallXOffset(float d) {
  return (int) (FUNCTION_D_A / (d - FUNCTION_D_B) + FUNCTION_D_C);
}
//    /\


//this enum is for jewel colors
//uncertain means that a color could not be confirmed for sure. possible causes (in order of probability):
//  1. ball is not there, i.e. knocked off
//  2. threshold set too high
//  3. looking in wrong place for balls
public enum JewelColor {
  red, blue, uncertain
}

//Su-Per E-Z Get Jewel Color-z!
//simple "interface" to rest of code - takes image and distance from wall and calls the correct methods correctly
JewelColor[] sezGetJewelColors(PImage img, float d) {
  //finds the tape, [0] is tape center, [1] is amount of fudging used
  float[] temp = ezFindTape(img, d);
  //gets the jewel colors using the provided image, found tape position, and "corrected"/fudged distance
  JewelColor[] jewelColors = ezGetJewelColors(img, temp[0], d + temp[1]);
  //{left jewel color, right jewel color}
  return jewelColors;
}

//E-Z tape finder: uses an image and distance from wall
float[] ezFindTape(PImage img, float d) {
  //creates a scaled kernel (multiplied by scaler to increase/decrease strength) from the vertical sobol kernel 
  float[][] skernel = kernelScale(KERNEL, KERNEL_SCALER);
  //copies input image to a temporary one because when passing the images as arguments, a reference is passed
  //findTape filters to grey, so the color info needed for color detection is lost otherwise
  PImage temp = img.get();
  //creates array for left/right tape bounds
  float[] tapes = {0, 0};
  //fudge is amount added to distance until it finds a tape
  //basically fudges the data until it gets a result
  int fudge = 0;
  //creates array for actual returned result: {tape x center, fudging required to get that done}
  float[] result = new float[2];
  //iterates through fudge amounts attempting to locate tape each time up to fudge amount
  for(fudge = 0; fudge <= FUDGE_AMOUNT; fudge++) {
    //tries tape adding fudge
    tapes = findTape(d + fudge, temp, skernel);
    //if there was something found
    if(tapes[0] != 0 || tapes[1] != 0) {
      //uses a break :P
      break;
    }
    //this time subtracts
    tapes = findTape(d - fudge, temp, skernel);
    if(tapes[0] != 0 || tapes[1] != 0) {
      //because fudge was subtracted, the actual fudge was -fudge, so this does that
      fudge = -1 * fudge;
      break;
    }
  }
  //averages bounds to get center
  result[0] = (tapes[0] + tapes[1]) / 2;
  //also tacks on fudge amount
  result[1] = fudge;
  return result;
}

//E-Z "interface" to get jewel colors, really just tacks on the constants
JewelColor[] ezGetJewelColors(PImage img, float tc, float d) {
  return getJewelColors(img, tc, d, SEARCH_RADIUS, COLOR_CONTRAST_FACTOR);
}

//utility method for converting JewelColor enum to string for telemetry, degubbing, etc.
String jewelColorToString(JewelColor in) {
  switch (in) {
    case blue: return "blue";
    case red: return "red";
    case uncertain: return "uncertain";
    default: return "invalid input";
  }
}

//uses image, tape center, distance from wall, search radius for square (1/2 of width/height/whatever), and minimum contrast threshold
JewelColor[] getJewelColors(PImage img, float tc, float d, int sr, double acc) {
  //sets uncertain by default, changed if something interesting happens (jewel color confirmed)
  JewelColor[] result = {JewelColor.uncertain, JewelColor.uncertain};
  double lrsum = 0; //left red sum
  double lbsum = 0; //left blue sum
  double rrsum = 0; //right red sum
  double rbsum = 0; //right blue sum
  int off = distToBallXOffset(d); //gets x offset for ball
  int ycenter = distToBallY(d); //gets ball center y value
  int itc = (int) (tc); //casts tape center to integer so that it can be used with pixel array
  img.loadPixels(); //opens pixels for reading from image
  
  //these loops sum the blue and red channels for the left and right balls for comparison
  //left side, so tape center - offset, then goes to side by radius
  for(int x = itc - off - sr; x <= itc - off + sr; x++) {
    //vertical position same for both sides, so only search radius impacts where pixel read
    for(int y = ycenter - sr; y <= ycenter + sr; y++) {
      //pixels is a one dimensional array of the thing's pixels
      //the y + img.width + x gives the one dimensional position of that pixel from 2d coords
      lrsum += red(img.pixels[y * img.width + x]);
      lbsum += blue(img.pixels[y * img.width + x]);
    }
  }
  //rect(itc - off - sr, ycenter - sr, sr * 2, sr * 2); //<- this is debug code
  //same as earlier, just adds offset because looking at right
  for(int x = itc + off - sr; x <= itc + off + sr; x++) {
    for(int y = ycenter - sr; y <= ycenter + sr; y++) {
      rrsum += red(img.pixels[y * img.width + x]);
      rbsum += blue(img.pixels[y * img.width + x]);
    }
  }
  
  //compares ratios of blue and red to see if one clearly more prominant than other
  //ratios compared to threshold inputted (acc)
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

//scales a kernel for strength
float[][] kernelScale(float[][] kernel, float scale) {
  float[][] retkern = new float[3][3];
  for(int y = 0; y < 3; y++) {
    for(int x = 0; x < 3; x++) {
      retkern[x][y] = kernel[x][y] * scale;
    }
  }
  return retkern;
}

//yin is y for tape vertically, position from top
float[] findTape(float d, PImage in, float[][] kernel) {
  int yin = distToPos(d); //converts distance from wall to tape y "input"
  float[] result = new float[2]; //holds left and right bounds
  float[][] kk = new float[in.width][in.height]; //stores kernel convolution results in an array for higher "fidelity," whatever that means
  //filters gray - WARNING: THIS FILTERS THE IMAGE PASSED IN AS A REFERENCE. TO PRESERVE INPUT COLORS, COPY IT
  in.filter(GRAY);
  in.loadPixels(); //opens pixels for use
  //iterates through image applying convolution
  //skips outside to prevent out of bounds error
  //the -2, +2 are for searching rows above and below the one where the tape supposedly is to help manage noise and to give redundancy
  for (int y = yin - 2; y <= yin + 2; y++) {//                                                                      /\
    //goes through each row independently running the convolution
    for (int x = 1; x < in.width - 1; x++) {
      float sum = 0; //sum of convolution results for this pixel
      //actually convoluting the pixel, kx, ky are offsets in those directions to adjacent pixels
      for (int ky = -1; ky <= 1; ky++) {
        for (int kx = -1; kx <= 1; kx++) {
          int pos = (y + ky) * in.width + (x + kx); //converts 2d (x+kx, y+ky) to 1d image pixel coords
          float val = red(in.pixels[pos]); //channel read arbitrary, image filtered to greyscale
          sum += kernel[ky+1][kx+1] * val;
        }
      }
      sum = abs(sum); //detects edges going either way/removes negative sign
      //store the sum in the array of kernel convolution results
      //keeps in mind that the array is not for the whole image: just the 5 rows (with the extra rows searched, see /\
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