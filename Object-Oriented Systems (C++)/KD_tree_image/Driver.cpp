#include "Image.h"
#include "Bridges.h"
#include "Color.h"
#include "ColorGrid.h"
#include "KdTreeElement.h"

using namespace bridges;
using namespace img;

int main(){

    Bridges *bridges = new Bridges (2, "josh_vaz", "808628867398");
    bridges->setTitle("Module 6");

    string testFile = "/home/user/Pictures/mickey_mouse.ppm";
    Image *image1 = new Image(testFile);

    image1->display(*bridges);

}

