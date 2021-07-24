#ifndef IMAGE_H
#define IMAGE_H

#include "Bridges.h"
#include "Color.h"
#include "ColorGrid.h"
#include "KdTreeElement.h"

using namespace bridges;

namespace img{

    class Image
    {
        public:
            Image(string fileName);
            KdTreeElement<int, int>* buildImageTree(int* region, int level, bool dim_flag);
            void read(string infile);
            void display(Bridges& bridges);

        private:
            static const int MAX_LEVEL = 14;
            ColorGrid *cg;

            bool IsRegionHomogeneousSimple(int* region);
            bool IsRegionHomogeneousGeneral(int* region);
            void ColorRegion(int* region);
            void ColorRegion(int* region, bool dim_flag);
            int PartitionRegion(int* region, bool dim_flag);
    };

}

#endif // IMAGE_H
