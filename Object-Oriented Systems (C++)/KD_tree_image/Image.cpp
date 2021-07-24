#include "Image.h"
#include "Bridges.h"
#include "Color.h"
#include "ColorGrid.h"
#include "KdTreeElement.h"
#include <iostream>
#include <fstream>


using namespace std;
using namespace bridges;
using namespace img;


Image::Image(string fileName)
{
    read(fileName);
}

//Read in PPm file to ColorGrid, calls buildImageTree
void Image::read(string fileName){

    int* image_array;
    int count = 0;
    string magic;
    ifstream file(fileName);
    int size;
    int maxVal;
    int value;
    int width;
    int height;

    //read file to int array
    if(file.is_open()){

        file >> magic;
        file >> width;
        file >> height;
        file >> maxVal;
        size = width * height * 3;
        image_array = new int[size];

        if(maxVal = 255){
            while(file){
                file >> image_array[count++];
            }
        }else{
            while(file){
                file >> value ;
                image_array[count++] = value/ maxVal * 255;
            }
        }

        file.close();

    }else cout << "Cannot open file";

    count = 0;

    // create a color grid from int array
    cg  = new ColorGrid(height, width);

    for (int k = 0; k < height; k++)
        for (int j = 0; j < width; j++){
            cg->set(k, j, Color(image_array[count],image_array[count+1],image_array[count+2]));
            count += 3;
        }
    int startingSize[4] = {0,width,0,height};
    int* region = startingSize;

    buildImageTree(region,0,true);
}

//Display image in bridges
void Image::display (Bridges& bridges){

    bridges.setDataStructure(cg);
    bridges.visualize();

}

//Builds KdTree of image
KdTreeElement<int, int>* Image::buildImageTree(int* region, int level, bool dim_flag){

    int dim;
    if (dim_flag) dim = 0;
    else dim = 1;

    KdTreeElement<int,int> *root = new KdTreeElement<int,int>(dim,PartitionRegion(region,dim_flag));

    bool homogeneous;
    homogeneous = IsRegionHomogeneousGeneral(region);

    //PARTITION
    if (level <= MAX_LEVEL && !homogeneous){

        int partitioner = PartitionRegion(region,dim_flag);
        int* leftRegion;
        int* rightRegion;


        if(dim_flag){
            int lregion[]= {region[0],partitioner,region[2],region[3]};
            int rregion[] = {partitioner,region[1],region[2],region[3]};
            leftRegion = lregion;
            rightRegion = rregion;
        }else{
            int lregion[] = {region[0],region[1],region[2],partitioner};
            int rregion[] = {region[0],region[1],partitioner,region[3]};
            leftRegion = lregion;
            rightRegion = rregion;
        }


        level += 1;
        root->setLeft(buildImageTree(leftRegion,level,!dim_flag));
        root->setRight(buildImageTree(rightRegion,level,!dim_flag));

        ColorRegion(region,dim_flag);

        return root;
    }

    ColorRegion(region);

    return nullptr;
}

//2 color Image - if any 2 pixels are different returns false
bool Image::IsRegionHomogeneousSimple(int* region){

    //If region width is less than 3 return homogenous to avoid degen cases
    static const int MIN_WIDTH = 2;
    if(region[1] - region[0] < MIN_WIDTH || region[3] - region[2] < MIN_WIDTH) return true;

    int red = cg->get(region[2],region[0]).getRed();
    int blue = cg->get(region[2],region[0]).getBlue();
    int green = cg->get(region[2],region[0]).getGreen();

    for(int i = region[2]; i < region[3]; i++)
        for(int j = region[0]; j < region[1]; j++){
            if(cg->get(i,j).getRed() != red) return false;
            else if(cg->get(i,j).getBlue() != blue) return false;
            else if(cg->get(i,j).getGreen() != green) return false;
        }

    return true;
}

//Multi color Image - if a color is over/under threshold value of average RGB values return false
bool Image::IsRegionHomogeneousGeneral(int* region){

    static const int THRESHOLD_VALUE = 5;

    //If region width is less than 2 return homogenous to avoid degen cases
    static const int MIN_WIDTH = 2;
    if(region[1] - region[0] < MIN_WIDTH || region[3] - region[2] < MIN_WIDTH) return true;

    int avgRed = 0;
    int avgBlue = 0;
    int avgGreen = 0;
    int numElements = 0;

    for(int i = region[2]; i < region[3]; i++)
        for(int j = region[0]; j < region[1]; j++){
            avgRed += cg->get(i,j).getRed();
            avgBlue += cg->get(i,j).getBlue();
            avgGreen += cg->get(i,j).getGreen();
            numElements++;
        }

    avgRed /= numElements;
    avgBlue /= numElements;
    avgGreen /= numElements;

    for(int i = region[2]; i < region[3]; i++)
        for(int j = region[0]; j < region[1]; j++){
            if(cg->get(i,j).getRed() > avgRed + THRESHOLD_VALUE)return false;
            if(cg->get(i,j).getRed() < avgRed - THRESHOLD_VALUE)return false;
            if(cg->get(i,j).getBlue() > avgBlue + THRESHOLD_VALUE)return false;
            if(cg->get(i,j).getBlue() < avgBlue - THRESHOLD_VALUE)return false;
            if(cg->get(i,j).getGreen() > avgGreen + THRESHOLD_VALUE)return false;
            if(cg->get(i,j).getGreen() < avgGreen - THRESHOLD_VALUE)return false;
        }

    return true;
}

//Colors region as an average of colors present
void Image::ColorRegion(int* region){

    int avgRed = 0;
    int avgBlue = 0;
    int avgGreen = 0;
    int numElements = 0;

    for(int i = region[2]; i < region[3]; i++)
        for(int j = region[0]; j < region[1]; j++){
            avgRed += cg->get(i,j).getRed();
            avgBlue += cg->get(i,j).getBlue();
            avgGreen += cg->get(i,j).getGreen();
            numElements++;
        }

    avgRed /= numElements;
    avgBlue /= numElements;
    avgGreen /= numElements;

    for(int i = region[2]; i < region[3]; i++)
        for(int j = region[0]; j < region[1]; j++){
            cg->set(i,j,Color(avgRed,avgGreen,avgBlue));
        }
}

//Colors partition lines
void Image::ColorRegion(int* region, bool dim_flag){

    //if dim_flag, draw x = partioned_region


     for (int k = region[2]; k < region[3]; k++){
        cg->set(k, region[0], Color("black"));
        cg->set(k, region[1] - 1, Color("black"));
     }
     for (int j = region[0]; j < region[1]; j++){
        cg->set(region[2], j, Color("black"));
        cg->set(region[3] - 1, j, Color("black"));
     }



}

//Compute avereage of distances
int Image::PartitionRegion(int* region, bool dim_flag){

    int avg;

    if(dim_flag) avg = region[0] + ((region[1] - region[0]) / 2);    //X Avg
    else avg = region[2] + ((region[3] - region[2]) / 2);            //Y Avg

    return avg;
}
