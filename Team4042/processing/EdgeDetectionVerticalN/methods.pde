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
      
      float mus = 0;
      for (int ky = -1; ky <= 1; ky++) {
        for (int kx = -1; kx <= 1; kx++) {
          int pos = (y + ky) * target.width + (x + kx);
          float val = red(target.pixels[pos]); //red channel used because greyscale
          mus -= kernel[ky+1][kx+1] * val; //this time looks for edge going the other way
        }
      }
      
      sum = abs(sum) + abs(mus);
      
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

boolean[] filterColumns(PImage target) {
  //detects potential edges, adds them to array
  //note: must skip edges to keep kernel in image, so input edges are lost
  //convolutedResult.filter(GRAY);
  boolean[] xsWithEdges;
  xsWithEdges = new boolean[target.width];
  target.loadPixels();
  for(int x = 1; x < target.width - 1; x++) {
    double sum = 0;
    for(int y = 1; y < target.height - 1; y++) {
      int pos = y * target.width + x;
      sum += red(target.pixels[pos]);
    }
    target.pixels[x] = color(0, 0, 0);
    if(sum > COLUMN_SUM_THRESHOLD) xsWithEdges[x] = true; else xsWithEdges[x] = false;
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
      line(x, 1, x, convolutionImage.height - 1);
      line(x + width / 2, 1, x + width / 2, convolutionImage.height - 1);
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