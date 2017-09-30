/*
PImage kernelConvolution(PImage target, float[][] kernel) {
  target.filter(GRAY);
  target.loadPixels();
  
  PImage result = createImage(target.width, target.height, RGB);
  
  //iterates through pixels of target
  //skips outer rows/columns to avoid out of bounds convolution
  for (int y = 1; y < target.height - 1; y++) {
    for (int x = 1; x < target.width - 1; x++) {
      
      float sum = 0;
      for (int ky = -1; ky <= 1; ky++) {
        for (int kx = -1; kx <= 1; kx++) {
          int pos = (y + ky) * target.width + (x + kx);
          float val = red(target.pixels[pos]); //red channel used because greyscale
          sum += kernel[ky+1][kx+1] * val;
        }
      }
      /*
      float mus = 0;
      for (int ky = -1; ky <= 1; ky++) {
        for (int kx = -1; kx <= 1; kx++) {
          int pos = (y + ky) * target.width + (x + kx);
          float val = red(target.pixels[pos]); //red channel used because greyscale
          mus -= kernel[ky+1][kx+1] * val;
        }
      }
      *//*
      sum = abs(sum);// + abs(mus);
      
      // For this pixel in the new image, set the gray value
      // based on the sum from the kernel
      result.pixels[y * target.width + x] = color(sum, sum, sum);
    }
  }
  result.updatePixels();
  return result;
}

double columnSum(PImage target, int x) {
  
  target.filter(GRAY);
  target.loadPixels();
  
  double sum = 0;
  for(int y = 0; y < target.height; y++) {
    int pos = y * target.width + x;
    sum += red(target.pixels[pos]);
  }
  return sum;
}

float[] sumColumns(PImage target) {
  //detects potential edges, adds them to array
  //note: must skip edges to keep kernel in image, so input edges are lost
  //convolutedResult.filter(GRAY);
  float[] xsWithEdges;
  xsWithEdges = new float[target.width];
  target.loadPixels();
  for(int x = 1; x < target.width - 1; x++) {
    float sum = 0;
    for(int y = 1; y < target.height - 1; y++) {
      int pos = y * target.width + x;
      sum += red(target.pixels[pos]);
    }
    target.pixels[x] = color(0, 0, 0);
    if(sum > COLUMN_SUM_THRESHOLD) xsWithEdges[x] = sum; else xsWithEdges[x] = 0;
  }
  target.updatePixels();
  
  return xsWithEdges;
}


void drawCamKernelConvolutionHighlights(PImage convolutionImage, PImage camera, boolean[] xsWithEdges) {
  stroke(0, 255, 0);
  image(convolutionImage, 0, 0);
  image(camera, width / 2, 0);
  for(int x = 0; x < convolutionImage.width; x++) {
    if(xsWithEdges[x]) {
      //line(x, 1, x, convolutionImage.height - 1);
      //line(x + width / 2, 1, x + width / 2, convolutionImage.height - 1);
    }
  }
}

float[][] scaleKernel(float[][] toScale, float scaler) {
  float[][] scaledKernel = new float[3][3];
  
  for(int x = 0; x < 3; x++) {
    for(int y = 0; y < 3; y++) {
      scaledKernel[x][y] = toScale[x][y] * scaler;
    }
  }
  return scaledKernel;
}

PImage edgeLengths(boolean[] edges, PImage conImg, PImage camImg) {
  float[][] result = new float[conImg.width][2];
  float[] kernel = new float[] {-1,-2,0,2,1};
  PImage newCon = createImage(conImg.width, conImg.height, RGB);
  
  extraReturnMax = 0;
  
  conImg.loadPixels();
  newCon.loadPixels();
  camImg.loadPixels();
  for(int i = 1; i <= conImg.width - 1; i++) { //iterate through edge list
    if(edges[i]) { //if there is an edge maybe at that x
      for(int j = 3; j <= conImg.height - 3; j++) { //iterate vertically through running horizontal edge convolution
        int sum = 0;
        for(int k = 0; k <= 4; k++) {
          int pos = (j + k - 2) * conImg.width + i;
          sum += red(camImg.pixels[pos]) * kernel[k];
        }
        if(abs(sum) > extraReturnMax) {
          extraReturnMax = abs(sum);
        }
        newCon.pixels[j * conImg.width + i] = color(abs(sum), abs(sum), abs(sum));
        if(abs(sum) < VERTICAL_EDGE_THRESHOLD) {
          newCon.pixels[j * conImg.width + i] = color(0, 0, 0);
        }
      }
    }
  }
  
  return newCon;
}
*/