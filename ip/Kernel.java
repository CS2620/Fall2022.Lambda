package ip;

public class Kernel {
  int sizeX;
  int sizeY;
  float[][] kernel;
  
  public Kernel(float[][] kernel){
    this.kernel = kernel;
    sizeY = this.kernel.length;
    sizeX = this.kernel[0].length;
    
  }

  public float get(int x, int y){
    //x and y should be in the range [-(size-1)/2,(size-1)/2]
    //We need to change that to be [0, size)
    int kx = x + (sizeX-1)/2;
    int ky = y + (sizeY-1)/2;
    return kernel[ky][kx];
  }

  public Kernel normalize(){
    float sum = 0;
    for(int ky = 0; ky < sizeY; ky++){
      for(int kx = 0; kx < sizeX; kx++){
        sum += kernel[ky][kx];
      }
    }

    for(int ky = 0; ky < sizeY; ky++){
      for(int kx = 0; kx < sizeX; kx++){
        kernel[ky][kx]/=sum;
      }
    }
    return this;
  }
  
}
